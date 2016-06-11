package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVerticalTest;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.yifan.mtgdecktracker.ArrayAdapterNoFilter;
import com.example.yifan.mtgdecktracker.JsonFetcher;
import com.example.yifan.mtgdecktracker.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class NewDeckFragment extends Fragment {


    private Button mAddButton;
    private Button mSaveButton;
    private AutoCompleteTextView mAutoCompleteEntryField;
    private ArrayAdapter<String> mAutoCompleteAdapter;
    private EditText mCurrentMainBoard;
    private EditText mQuantityToAdd;

    private boolean mStartedAddingCards = false;

    View rootView;

    public NewDeckFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("NewDeckFragment", "creating fragment");
        rootView = inflater.inflate(R.layout.fragment_add_card, container, false);
        mCurrentMainBoard = (EditText) rootView.findViewById(R.id.current_mainboard);
        mQuantityToAdd = (EditText) rootView.findViewById(R.id.card_quantity_field);
        autoCompleteSetUp();
        buttonSetUp();
        mCurrentMainBoard = (EditText) rootView.findViewById(R.id.current_mainboard);

        // Inflate the layout for this fragment
        return rootView;



    }

    private void autoCompleteUpdater(ArrayList<String> suggestions) {
        if (mAutoCompleteAdapter != null) {
            mAutoCompleteAdapter.clear();
        }

        mAutoCompleteAdapter.addAll(suggestions);
    }

    private void buttonSetUp(){
        mAddButton = (Button) rootView.findViewById(R.id.add_card);
        mSaveButton = (Button) rootView.findViewById(R.id.save_button);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numToAdd = mQuantityToAdd.getText().toString(); //make sure user has entered a number into the field, if not just add 1 copy of the card
                if(!isInteger(numToAdd)){
                    numToAdd = "1";
                }
                String cardEntry = String.format("%1$-10s %2$10s", mAutoCompleteEntryField.getText().toString(), "x" + numToAdd) + "\n";

                mCurrentMainBoard.setSelection(mCurrentMainBoard.getText().length());
                mCurrentMainBoard.append(cardEntry);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void autoCompleteSetUp(){
        mAutoCompleteEntryField = (AutoCompleteTextView) rootView.findViewById(R.id.auto_complete_field);
        mAutoCompleteAdapter = new ArrayAdapterNoFilter(this.getActivity(), android.R.layout.select_dialog_item);
        mAutoCompleteAdapter.setNotifyOnChange(true);

        mAutoCompleteEntryField.setThreshold(3); //how many characters before looking for matches
        mAutoCompleteEntryField.getText().clear();
        mAutoCompleteEntryField.setAdapter(mAutoCompleteAdapter);
        /*mAutoCompleteEntryField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
        mAutoCompleteEntryField.addTextChangedListener(new TextWatcher() {
            final static long DELAY = 1000; //delay for user to input before autocomplete attempt in ms
            Timer showResultsTimer;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //runs instant before the text is changed
                //not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //runs during the text changing
                if(showResultsTimer != null){ //cancels the task if user has typed before the second has passed
                    showResultsTimer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { //runs immediately after the text is changed
                if(s.length() >= 3){
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
            try{
                cardSuggestions = JsonFetcher.getCardsFromAutoComplete(params[0]);
            } catch(IOException e){
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

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}
