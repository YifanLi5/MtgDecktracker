package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVerticalTest;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yifan.mtgdecktracker.ArrayAdapterNoFilter;
import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.JsonFetcher;
import com.example.yifan.mtgdecktracker.NonLand;
import com.example.yifan.mtgdecktracker.R;
import com.example.yifan.mtgdecktracker.StaticUtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


public class AddCardsToDeckFragment extends Fragment {

    private static final int DEFAULT_CARD_COUNT = 60;
    private static final String LOG_TAG = AddCardsToDeckFragment.class.getSimpleName();
    private String deckName;
    private Button mAddButton;
    private Button mSaveButton;
    private AutoCompleteTextView mAutoCompleteEntryField;
    private ArrayAdapter<String> mAutoCompleteAdapter;
    private EditText mQuantityToAdd;
    private ListView mMainboardAddedCards;
    private ArrayAdapter<Card> mListViewAdapter;
    private View rootView;
    private ArrayList<Card> mMainboard;
    private JSONObject selectedJSON;
    private HashSet<String> mMainboardSet; //used to prevent duplicate cards from being entered
    private boolean existingDeck;
    private ModifyCardEntryFragment modifyCardFragment;
    private static AddCardsToDeckFragment singletonInstance;

    public AddCardsToDeckFragment() {
        // Required empty public constructor
    }

    //used when editing an existing deck
    public static AddCardsToDeckFragment getInstance(ArrayList<Card> existingDeck){
        singletonInstance = new AddCardsToDeckFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("ExistingDeck", existingDeck);
        singletonInstance.setArguments(args);
        singletonInstance.existingDeck = true;
        return singletonInstance;
    }

    //used when createing a new deck
    //todo: store created decklist when fragment closes, delete it if user goes from edit deck to create deck or vice versa. Do not delete if go from create deck to create deck
    public static AddCardsToDeckFragment getInstance(String deckName){
        if(singletonInstance.existingDeck == false){
            return singletonInstance;
        }
        else{
            singletonInstance = new AddCardsToDeckFragment();
            Bundle args = new Bundle();
            args.putString("DeckName", deckName);
            singletonInstance.existingDeck = false;
            return singletonInstance;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "creating fragment " + LOG_TAG);
        rootView = inflater.inflate(R.layout.fragment_add_card, container, false);
        mQuantityToAdd = (EditText) rootView.findViewById(R.id.card_quantity_field);
        autoCompleteSetUp();
        buttonsSetUp();
        listViewSetUp();
        return rootView;
    }

    private void autoCompleteUpdater(ArrayList<String> suggestions) {
        if (mAutoCompleteAdapter != null) {
            mAutoCompleteAdapter.clear();
        }
        assert mAutoCompleteAdapter != null;
        mAutoCompleteAdapter.addAll(suggestions);
    }

    //callback methods used to edit quantities or delete already added cards
    public void changeCardQuantityCallback(int newQuantity, int positionClicked) {
        //listview comes from data in mMainboard, therefore can just modify this arraylist then call notifyDataChanged()
        if (newQuantity == 0) {
            deleteCardCallback(positionClicked);
        } else {
            Card targetCard = mMainboard.get(positionClicked);
            targetCard.setTotal(newQuantity);
            Log.d(LOG_TAG, mMainboard.toString());
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    private void deleteCardCallback(int positionClicked) {
        //remove from arraylist and set, remove from set FIRST b/c set requires arraylist to get the item
        mMainboardSet.remove(mMainboard.get(positionClicked).getName());
        mMainboard.remove(positionClicked);
        mListViewAdapter.notifyDataSetChanged();
    }

    private void listViewSetUp() {
        mMainboard = new ArrayList<>(DEFAULT_CARD_COUNT);
        mMainboardSet = new HashSet<>(DEFAULT_CARD_COUNT);
        mMainboardAddedCards = (ListView) rootView.findViewById(R.id.current_mainboard);
        mListViewAdapter = new DeckCreationAdapter(getContext(), mMainboard);
        mMainboardAddedCards.setAdapter(mListViewAdapter);
        mMainboardAddedCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //open ModifyCardFragment, pass in the card's name and card's current quantity (arguements)
                //set up arguments
                Card targetCard = mMainboard.get(position);
                //launch fragment or replace it
                //todo: pass in the position
                //if modifyCardFragment doesn't exist, create it. If it does exist recreate it (a new item in the listview may have been pressed therefore need a new instance of the fragment) and replace it.
                if(modifyCardFragment == null){
                    modifyCardFragment = ModifyCardEntryFragment.newInstance(targetCard, position);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.modify_card_entry_fragment_container, modifyCardFragment)
                            .commit();
                }
                else{
                    modifyCardFragment = ModifyCardEntryFragment.newInstance(targetCard, position);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.modify_card_entry_fragment_container, modifyCardFragment)
                            .commit();
                }

            }
        });
    }

    private void buttonsSetUp() {
        mAddButton = (Button) rootView.findViewById(R.id.add_card);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create nonland object, add it into mMainboard, update the listview to show the cards and respective quantities.

                String quantityFieldEntry = mQuantityToAdd.getText().toString(); //make sure user has entered a number into the field, if not just add 1 copy of the card
                int numToAdd;
                if (!StaticUtilityMethods.isInteger(quantityFieldEntry)) {
                    numToAdd = 1;
                } else {
                    numToAdd = Integer.parseInt(quantityFieldEntry);
                }

                //using constructor NonLand(JsonObject, int)
                try {
                    NonLand newEntry = new NonLand(selectedJSON, numToAdd);
                    if (mMainboardSet.add(newEntry.getName())) { //check if adding duplicates
                        mMainboard.add(newEntry);
                        StaticUtilityMethods.hideKeyboardFrom(getContext(), rootView); //listview only updates after keyboard is pulled down, use this method
                        mListViewAdapter.notifyDataSetChanged();


                    } else {
                        Toast.makeText(getContext(), "Card already added, modify quantities by touching the list.", Toast.LENGTH_LONG).show();
                    }

                    Log.d(LOG_TAG, mMainboard.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException when adding card");
                    Toast.makeText(getContext(), "ERROR: unable to add card" + mAutoCompleteEntryField.getText(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch(NullPointerException e){
                    Toast.makeText(getContext(), "ERROR: unable get JSON" + mAutoCompleteEntryField.getText(), Toast.LENGTH_SHORT).show();
                }


            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement make deck (initialize all images) and save, display it in host activity


                //used as temp debug
                Log.d(LOG_TAG, "mainboard: " + mMainboard.toString());
                Log.d(LOG_TAG, "listview at position: " + modifyCardFragment.getPositionClicked());
            }
        });

    }

    private void autoCompleteSetUp() {
        mAutoCompleteEntryField = (AutoCompleteTextView) rootView.findViewById(R.id.auto_complete_field);
        mAutoCompleteAdapter = new ArrayAdapterNoFilter(this.getActivity(), android.R.layout.select_dialog_item);
        mAutoCompleteAdapter.setNotifyOnChange(true);
        mAutoCompleteEntryField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedJSON = JsonFetcher.getJSONArrayOfCards().getJSONObject(position); //used when adding a card to determine which of the suggestions was selected
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        });
        mAutoCompleteEntryField.setThreshold(3); //how many characters before looking for matches
        mAutoCompleteEntryField.getText().clear();
        mAutoCompleteEntryField.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteEntryField.addTextChangedListener(new TextWatcher() {
            final static long DELAY = 500; //delay for user to input before autocomplete attempt in ms
            Timer showResultsTimer;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //runs instant before the text is changed
                //not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //runs during the text changing
                if (showResultsTimer != null) { //cancels the autocomplete task if user has typed in the delay period.
                    showResultsTimer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { //runs immediately after the text is changed
                if (s.length() >= 3) {
                    showResultsTimer = new Timer();
                    showResultsTimer.schedule(new TimerTask() { //set timer to run the autocomplete task if 1000ms passed before user has typed something
                        @Override
                        public void run() { //runs autocompletetask after some ms set by DELAY variable, i.e: only after some DELAY after user stops typing does autocomplete run
                            new AutoCompleteTask().execute(mAutoCompleteEntryField.getText().toString());
                        }
                    }, DELAY);
                }

            }
        });
    }

    private class AutoCompleteTask extends AsyncTask<String, Void, ArrayList<String>> { //populates the autocomplete
        private final String LOG_TAG = AutoCompleteTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> cardSuggestions;
            try {
                cardSuggestions = JsonFetcher.getCardsFromAutoComplete(params[0]);
                Log.d(LOG_TAG, "Num of elements: " + String.valueOf(JsonFetcher.getJSONArrayOfCards().length()));
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException in autoCompleteTask");
                return null;
            }
            return cardSuggestions;
        }

        @Override
        protected void onPostExecute(ArrayList<String> cardSuggestions) {
            super.onPostExecute(cardSuggestions);
            autoCompleteUpdater(cardSuggestions);
        }
    }
}
