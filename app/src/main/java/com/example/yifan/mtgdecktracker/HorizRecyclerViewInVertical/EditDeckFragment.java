package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;


public class EditDeckFragment extends Fragment implements ConfirmResetDialogFragment.ConfirmResetDialogCallbacks{
    private static final String LOG_TAG = EditDeckFragment.class.getSimpleName();
    private View rootView;
    private JSONObject selectedJSON;
    private String mDeckName;
    private Button mAddButton;
    private Button mSaveButton;
    private Button mResetButton;
    private AutoCompleteTextView mAutoCompleteEntryField;
    private ArrayAdapter<String> mAutoCompleteAdapter;
    private EditText mDeckNameField;
    private EditText mQuantityToAdd;
    private TabHost tabHost;

    private ListView mMainboardAddedCards;
    private ListView mSideboardAddedCards;
    private ArrayAdapter<Card> mMainboardAdapter;
    private ArrayAdapter<Card> mSideboardAdapter;
    private ArrayList<Card> mMainboardOriginal; //copy of main and sideboards are used to undo all edited changes. When saving original is cloned from to copy
    private ArrayList<Card> mMainboardCopy;
    private ArrayList<Card> mSideboardOriginal;
    private ArrayList<Card> mSideboardCopy;
    private HashSet<String> mMainboardSet; //used to prevent duplicate cards from being entered
    private HashSet<String> mSideboardSet;
    private TextView mMainboardTotalCardsTV;
    private TextView mSideboardTotalCardsTV;
    private int mainboardCardCounter;
    private int sideboardCardCounter;
    private static final String CARD_COUNT_STARTER = "Card count: ";

    private ModifyCardEntryFragment modifyCardFragment;
    private static EditDeckFragment singletonInstance;
    private FragmentActivityAdapterCommunicator hostActivity;

    //strings for bundle keys
    private static final String EDIT_EXISTING_DECK = "EditExistingDeck";
    private static final String DECK_NAME = "DeckName";
    private static final String MAINBOARD_CONTENTS = "MainContents";
    private static final String SIDEBOARD_CONTENTS = "SideboardContents";
    private static final String DECK_INDEX = "DeckIndex";

    public EditDeckFragment() {
        // Required empty public constructor
    }

    /*
    the getInstance() overloaded methods follow a Finite State Machine. This fragment can be used to edit an existing deck or create a new deck, an existing deck would initialize the listview to already contain cards
    if we are creating a new deck and we hide this fragment...
        - when we press the create new deck button the unfinished deck's cards should be shown
        - when we press the edit existing deck button we swap out the unfinished deck's fragment with a new one and populate it with the cards of the existing deck
    if we are editing a current deck and we hide this fragment...
        - when we press the create new deck button we presently don't save changes (as save button was not pressed, pending reconsideration later [autosave?])
        - when we press the edit existing deck button we swap edited deck with a new one unless the one we try to re-edit the same same deck. 
     */

    //used when editing an existing deck

    public static EditDeckFragment getInstance(ArrayList<Card> mainboardContents, ArrayList<Card> sideboardContents, String deckName, int deckIndex){
        Log.d("Testing FSM", "edit existing deck");
        if(singletonInstance == null){ //fragment isn't created meaning we are in starting state, therefore create it to get to edit deck state as that was what was selected by user
            singletonInstance = new EditDeckFragment();
            Bundle args = new Bundle();
            args.putInt(DECK_INDEX, deckIndex);
            args.putBoolean(EDIT_EXISTING_DECK, true);  //bundle arguement for whether we the fragment's state is to edit an exsting deck
            args.putString(DECK_NAME, deckName);
            args.putParcelableArrayList(MAINBOARD_CONTENTS, mainboardContents);
            args.putParcelableArrayList(SIDEBOARD_CONTENTS, sideboardContents);
            singletonInstance.setArguments(args);
            return singletonInstance;
        }
        else{
            Bundle lastArgs = singletonInstance.getArguments();
            if(lastArgs.getBoolean(EDIT_EXISTING_DECK) &&  lastArgs.getString("DeckName").equals(deckName)){ //check if the last state was editing an existing deck and if we are rediting the same deck
                return singletonInstance; //then no changes necessary.
            }
            else{ //fragment already exists but editing new deck
                singletonInstance = new EditDeckFragment();
                Bundle args = new Bundle();
                args.putInt(DECK_INDEX, deckIndex);
                args.putBoolean(EDIT_EXISTING_DECK, true);
                args.putString(DECK_NAME, deckName);
                args.putParcelableArrayList(MAINBOARD_CONTENTS, mainboardContents);
                args.putParcelableArrayList(SIDEBOARD_CONTENTS, sideboardContents);
                singletonInstance.setArguments(args);
                return singletonInstance;
            }
        }
    }

    //used when creating a new deck
    public static EditDeckFragment getInstance(String deckName, int newDeckNumber){
        Log.d("Testing FSM", "testing create new deck");
        if(singletonInstance == null){ //fragment isn't created, create it in create deck mode with empty mMainboard
            singletonInstance = new EditDeckFragment();
            Bundle args = new Bundle();
            args.putBoolean(EDIT_EXISTING_DECK, false);
            args.putString(DECK_NAME, deckName + newDeckNumber);
            singletonInstance.setArguments(args);
            return singletonInstance;
        }
        else{
            if(!singletonInstance.getArguments().getBoolean(EDIT_EXISTING_DECK)){ //if the last fragment state was in create deck mode then we are resuming deck creation, return the singleton
                String temp = deckName + newDeckNumber;
                singletonInstance.mDeckNameField.setText(temp); //do not use bundle as we are not reinstantiating singletonInstance
                return singletonInstance;
            }
            else{ //otherwise the last state was edit existing deck, create new instance of singleton
                singletonInstance = new EditDeckFragment();
                Bundle args = new Bundle();
                args.putBoolean(EDIT_EXISTING_DECK, false);
                args.putString(DECK_NAME, deckName + newDeckNumber);
                singletonInstance.setArguments(args);
                return singletonInstance;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "creating fragment " + LOG_TAG);
        rootView = inflater.inflate(R.layout.fragment_edit_deck, container, false);
        mQuantityToAdd = (EditText) rootView.findViewById(R.id.card_quantity_field);
        mDeckNameField = (EditText) rootView.findViewById(R.id.deck_name_field);
        hostActivity = (FragmentActivityAdapterCommunicator) getActivity();

        // TODO: 7/11/2016 see if this part can be simplified
        if(savedInstanceState != null){
            this.mDeckName = savedInstanceState.getString(DECK_NAME);
            this.mMainboardOriginal = savedInstanceState.getParcelableArrayList(MAINBOARD_CONTENTS);
            this.mMainboardSet = new HashSet<>();
            if(mMainboardOriginal == null){
                this.mMainboardOriginal = new ArrayList<>();

            }
            else{
                for(Card card: mMainboardOriginal){
                    mMainboardSet.add(card.getName());
                }
            }

            this.mSideboardOriginal = savedInstanceState.getParcelableArrayList(SIDEBOARD_CONTENTS);
            this.mSideboardSet = new HashSet<>();
            if(mSideboardOriginal == null){
                this.mSideboardOriginal = new ArrayList<>();
            }
            else{
                for(Card card: mSideboardOriginal){
                    mSideboardSet.add(card.getName());
                }
            }
        }
        else{
            this.mDeckName = getArguments().getString(DECK_NAME);
            this.mDeckNameField.setText(mDeckName);
            if(getArguments().containsKey(MAINBOARD_CONTENTS)){
                this.mMainboardOriginal = getArguments().getParcelableArrayList(MAINBOARD_CONTENTS);
                this.mMainboardSet = new HashSet<>();
                for(Card card: mMainboardOriginal){
                    mMainboardSet.add(card.getName());
                }
            }

            if(getArguments().containsKey(SIDEBOARD_CONTENTS)){
                this.mSideboardOriginal = getArguments().getParcelableArrayList(SIDEBOARD_CONTENTS);
                this.mSideboardSet = new HashSet<>();
                for(Card card: mSideboardOriginal){
                    mSideboardSet.add(card.getName());
                }
            }
        }

        if(mMainboardOriginal != null){
            mMainboardCopy = (ArrayList<Card>) mMainboardOriginal.clone();
        }
        else{
            mMainboardOriginal = new ArrayList<>();
            mMainboardCopy = new ArrayList<>();
            mMainboardSet = new HashSet<>();
        }

        if(mSideboardOriginal != null){
            mSideboardCopy = (ArrayList<Card>) mSideboardOriginal.clone();
        }
        else{
            mSideboardOriginal = new ArrayList<>();
            mSideboardCopy = new ArrayList<>();
            mSideboardSet = new HashSet<>();
        }

        cardCounterSetup();
        autoCompleteSetUp();
        buttonsSetUp();
        tabsSetUp();
        listViewSetUp();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MAINBOARD_CONTENTS, mMainboardOriginal); //this will make a flip overwrite changes
        outState.putString(DECK_NAME, mDeckName);

    }

    private void cardCounterSetup(){
        mMainboardTotalCardsTV = (TextView) rootView.findViewById(R.id.current_mainboard_card_count);
        mSideboardTotalCardsTV = (TextView) rootView.findViewById(R.id.current_sideboard_card_count);
        if(mMainboardOriginal.isEmpty()){
            mainboardCardCounter = 0;
        }
        else{
            for(Card card: mMainboardOriginal){
                mainboardCardCounter += card.getTotal();
            }
        }

        if(mSideboardOriginal.isEmpty()){
            sideboardCardCounter = 0;
        }
        else{
            for(Card card: mSideboardOriginal){
                sideboardCardCounter += card.getTotal();
            }
        }

        String temp = CARD_COUNT_STARTER + mainboardCardCounter;
        mMainboardTotalCardsTV.setText(temp);
        temp = CARD_COUNT_STARTER + sideboardCardCounter;
        mSideboardTotalCardsTV.setText(temp);

    }

    private void autoCompleteUpdater(ArrayList<String> suggestions) {
        if (mAutoCompleteAdapter != null) {
            mAutoCompleteAdapter.clear();
        }
        assert mAutoCompleteAdapter != null;
        mAutoCompleteAdapter.addAll(suggestions);
    }

    //callback methods used to edit quantities or delete already added cards
    public void changeMainboardCardQuantityCallback(int newQuantity, int positionClicked) {
        //listviews comes from data in mMainboard or mSideboard, therefore can just modify this arraylist then call notifyDataChanged()
        if (newQuantity == 0) {
            deleteMainboardCard(positionClicked);
        }
        else {
            Card targetCard = mMainboardCopy.get(positionClicked);
            int quantityDelta = newQuantity - targetCard.getTotal(); //if quantityDelta is negative, card count decreased, vice versa for positive
            mainboardCardCounter += quantityDelta; //therefore simply add
            String temp = CARD_COUNT_STARTER + mainboardCardCounter;
            mMainboardTotalCardsTV.setText(temp);
            targetCard.setTotal(newQuantity);
            mMainboardAdapter.notifyDataSetChanged();
        }
    }

    private void deleteMainboardCard(int positionClicked) {
        //remove from arraylist and set, remove from set FIRST b/c set requires arraylist to get the item
        mainboardCardCounter -= mMainboardCopy.get(positionClicked).getTotal();
        String temp = CARD_COUNT_STARTER + mainboardCardCounter;
        mMainboardTotalCardsTV.setText(temp);
        mMainboardSet.remove(mMainboardCopy.get(positionClicked).getName());
        mMainboardCopy.remove(positionClicked);
        mMainboardAdapter.notifyDataSetChanged();
    }

    public void changeSideboardCardQuantityCallback(int newQuantity, int positionClicked) {
        if(newQuantity == 0){
            deleteSideboardCard(positionClicked);
        }
        else{
            Card targetCard = mSideboardCopy.get(positionClicked);
            int quantityDelta = newQuantity - targetCard.getTotal();
            sideboardCardCounter += quantityDelta;
            String temp = CARD_COUNT_STARTER + sideboardCardCounter;
            mSideboardTotalCardsTV.setText(temp);
            targetCard.setTotal(newQuantity);
            mSideboardAdapter.notifyDataSetChanged();
        }
    }

    private void deleteSideboardCard(int positionClicked){
        sideboardCardCounter -= mSideboardCopy.get(positionClicked).getTotal();
        String temp = CARD_COUNT_STARTER + sideboardCardCounter;
        mSideboardTotalCardsTV.setText(temp);
        mSideboardSet.remove(mSideboardCopy.get(positionClicked).getName());
        mSideboardCopy.remove(positionClicked);
        mSideboardAdapter.notifyDataSetChanged();
    }

    private void listViewSetUp() {
        mMainboardAddedCards = (ListView) rootView.findViewById(R.id.current_mainboard);
        mMainboardAdapter = new DeckCreationAdapter(getContext(), mMainboardCopy);
        mMainboardAddedCards.setAdapter(mMainboardAdapter);
        mMainboardAddedCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //open ModifyCardFragment, pass in the card's name and card's current quantity (arguments)
                //set up arguments
                Card targetCard = mMainboardCopy.get(position);
                //launch fragment or replace it
                //if modifyCardFragment doesn't exist, create it. If it does exist recreate it (a new item in the listview may have been pressed therefore need a new instance of the fragment) and replace it.
                if(modifyCardFragment == null){
                    Log.d(LOG_TAG, "creating fragment ModifyCardEntryFragment for mainboard card");
                    modifyCardFragment = ModifyCardEntryFragment.newInstance(targetCard, position, true);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.modify_card_entry_fragment_container, modifyCardFragment)
                            .commit();
                    hostActivity.openDrawer(Gravity.END);
                }
                else{
                    Log.d(LOG_TAG, "replacing fragment ModifyCardEntryFragment for mainboard card");
                    modifyCardFragment = ModifyCardEntryFragment.newInstance(targetCard, position, true);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.modify_card_entry_fragment_container, modifyCardFragment)
                            .commit();
                    hostActivity.openDrawer(Gravity.END);
                }

            }
        });

        mSideboardAddedCards = (ListView) rootView.findViewById(R.id.current_sideboard);
        mSideboardAdapter = new DeckCreationAdapter(getContext(), mSideboardCopy);
        mSideboardAddedCards.setAdapter(mSideboardAdapter);
        mSideboardAddedCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card targetCard = mSideboardCopy.get(position);
                if(modifyCardFragment == null){
                    Log.d(LOG_TAG, "creating fragment ModifyCardEntryFragment for mainboard card");
                    modifyCardFragment = ModifyCardEntryFragment.newInstance(targetCard, position, false);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.modify_card_entry_fragment_container, modifyCardFragment)
                            .commit();
                    hostActivity.openDrawer(Gravity.END);
                }
                else{
                    Log.d(LOG_TAG, "replacing fragment ModifyCardEntryFragment for mainboard card");
                    modifyCardFragment = ModifyCardEntryFragment.newInstance(targetCard, position, false);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.modify_card_entry_fragment_container, modifyCardFragment)
                            .commit();
                    hostActivity.openDrawer(Gravity.END);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void buttonsSetUp() {
        mAddButton = (Button) rootView.findViewById(R.id.add_card);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);
        mResetButton = (Button) rootView.findViewById(R.id.reset_button); //if editing an existing deck, this will allow user to undo all edits

        if(getArguments().getBoolean(EDIT_EXISTING_DECK)){ //do not need the reset button if creating a new deck
            mResetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmResetDialogFragment dialog = new ConfirmResetDialogFragment();
                    dialog.setTargetFragment(EditDeckFragment.this, 1);
                    dialog.show(getFragmentManager(), "ConfirmResetDialogFragment");
                }
            });
        }
        else{
            mResetButton.setVisibility(View.GONE);
        }

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
                    //reset text fields
                    mAutoCompleteEntryField.setText("");
                    mQuantityToAdd.setText("");
                    NonLand newEntry = new NonLand(selectedJSON, numToAdd);
                    if(tabHost.getCurrentTab() == 0){ //in mainboard tab, add to mainboard
                        if (mMainboardSet.add(newEntry.getName())) { //check if adding duplicates
                            insertInCmcOrder(mMainboardCopy, newEntry);
                            StaticUtilityMethods.hideKeyboardFrom(getContext(), rootView); //listview only updates after keyboard is pulled down, use this method
                            mainboardCardCounter += numToAdd;
                            String temp = CARD_COUNT_STARTER + mainboardCardCounter;
                            mMainboardTotalCardsTV.setText(temp);
                            mMainboardAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getContext(), "Card already added into Mainboard, modify quantities by touching the list.", Toast.LENGTH_LONG).show();
                        }

                    }
                    else if(tabHost.getCurrentTab() == 1){ //in sideboard tab, add to sideboard
                        if(mSideboardSet.add(newEntry.getName())){
                            insertInCmcOrder(mSideboardCopy, newEntry);
                            StaticUtilityMethods.hideKeyboardFrom(getContext(), rootView);
                            sideboardCardCounter += numToAdd;
                            String temp = CARD_COUNT_STARTER + mainboardCardCounter;
                            mSideboardTotalCardsTV.setText(temp);
                            mSideboardAdapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(getContext(), "Card already added into Sideboard, modify quantities by touching the list.", Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, "JSONException when adding card");
                    Toast.makeText(getContext(), "JSONException " + mAutoCompleteEntryField.getText(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch(NullPointerException e){
                    Toast.makeText(getContext(), "NullPointerException for some reason" + mAutoCompleteEntryField.getText(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Log.d(LOG_TAG, mMainboardCopy.toString());
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMainboardCopy.isEmpty() && mSideboardCopy.isEmpty()){
                    mMainboardOriginal = null;
                    mSideboardOriginal = null;
                    Toast.makeText(getContext(), "Deck is Empty, if this was an existing deck it was deleted", Toast.LENGTH_SHORT).show();
                    if(getArguments().getBoolean(EDIT_EXISTING_DECK)){
                        hostActivity.getModifiedDeck(mMainboardCopy, mSideboardCopy, getArguments().getInt(DECK_INDEX), mDeckName);
                    }
                }
                else{
                    //clone main and sideboards over as they changed and we can overwrite
                    mMainboardOriginal = (ArrayList<Card>) mMainboardCopy.clone();
                    mSideboardOriginal = (ArrayList<Card>) mSideboardCopy.clone(); 
                    mDeckName = mDeckNameField.getText().toString();

                    saveButtonInitImageHelper(); //implement make deck (initialize all images)

                    if(getArguments().containsKey(DECK_INDEX)){ //check if fragment has index of where it is in the host activity's vertical recyclerview, this means that we are modifying an existing deck
                        hostActivity.getModifiedDeck(mMainboardCopy, mSideboardCopy, getArguments().getInt(DECK_INDEX), mDeckName);
                    }

                    else{ //creating new deck
                        hostActivity.getModifiedDeck(mMainboardCopy, mSideboardCopy, -1, mDeckName); //3rd argument is index in vertical adapter, -1 means creating new deck
                        mMainboardAdapter.clear(); //after saving a new deck clear its contents from this fragment's listview to prepare to accept new deck as old data is still inside (user create subsequent new deck)
                        mQuantityToAdd.setText("");
                        mAutoCompleteEntryField.setText("");
                        mDeckNameField.setText("");

                    }

                }

                hostActivity.closeDrawer(Gravity.START);
            }

        });
    }

    private void insertInCmcOrder(ArrayList<Card> targetBoard, Card newEntry){
        int insertionPos = Collections.binarySearch(targetBoard, newEntry, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                int rhsCmc = rhs.getCmc();
                int lhsCmc = lhs.getCmc();
                if(lhsCmc > rhsCmc){
                    return 1;
                }
                else{
                    return -1;
                }
            }
        });
        if(insertionPos < 0){
            targetBoard.add(-insertionPos - 1, newEntry);
        }
    }

    private void saveButtonInitImageHelper(){
        for(int i = 0; i < mMainboardCopy.size(); i++){
            Card card = mMainboardCopy.get(i);
            if(!(card instanceof NonLand)){
                continue;
            }
            if(!((NonLand)card).initImage){
                try {
                    ((NonLand) card).initializeImage(EditDeckFragment.this, i, getActivity(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    Log.e(LOG_TAG, "URISyntaxException- bad uri for card: " + card.getName());
                    e.printStackTrace();
                }
            }
        }

        for(int i = 0; i < mSideboardCopy.size(); i++){
            Card card = mSideboardCopy.get(i);
            if(!(card instanceof NonLand)){
                continue;
            }
            if(!((NonLand)card).initImage){
                try {
                    ((NonLand) card).initializeImage(EditDeckFragment.this, i, getActivity(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    Log.e(LOG_TAG, "URISyntaxException- bad uri for card: " + card.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private void tabsSetUp(){
        tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("MainboardTab");
        spec.setContent(R.id.mainboard_tab);
        spec.setIndicator("Mainboard");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("SideboardTab");
        spec.setContent(R.id.sideboard_tab);
        spec.setIndicator("Sideboard");
        tabHost.addTab(spec);

        TextView tabname;
        tabname = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tabname.setTextSize(12);

        tabname = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tabname.setTextSize(12);
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) { //reset button uses yes/no dialog fragment to confirm. Yes in dialog fragment this is called and resets changes
        mMainboardAdapter.clear(); //don't need to sideboard = mainboard.clone() because clearing adapter also clears the contained arraylist
        mMainboardAdapter.addAll(mMainboardOriginal);
        mMainboardSet.clear();
        for(Card card: mMainboardOriginal){
            mMainboardSet.add(card.getName());
        }

        mSideboardAdapter.clear();
        mSideboardAdapter.addAll(mSideboardOriginal);
        mSideboardSet.clear();
        for(Card card: mSideboardOriginal){
            mSideboardSet.add(card.getName());
        }

    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {
        //do nothing
        Log.i(LOG_TAG, "no clicked");
    }

    private void autoCompleteSetUp() {
        mAutoCompleteEntryField = (AutoCompleteTextView) rootView.findViewById(R.id.auto_complete_field);
        mAutoCompleteAdapter = new ArrayAdapterNoFilter(this.getActivity(), R.layout.autocomplete_suggestion_item_layout, R.id.autocomplete_suggestion);
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
            final static long DELAY = 300; //delay for user to input before autocomplete attempt in ms
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
