package com.example.yifan.mtgdecktracker;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TestAutoCompleteActivity extends AppCompatActivity {
    private AutoCompleteTextView mAutoCompleteEntryField;
    private ImageView mCardView;
    private ArrayAdapter<String> mAutoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auto_complete);

        mAutoCompleteEntryField = (AutoCompleteTextView) findViewById(R.id.autoCompleteEntryField);
        mCardView = (ImageView) findViewById(R.id.cardView);
        initAutoComplete();
    }

    private void autoCompleteUpdater(ArrayList<String> suggestions){
        if(mAutoCompleteAdapter != null){
            mAutoCompleteAdapter.clear();
        }

        mAutoCompleteAdapter.addAll(suggestions);

        /* mAutoCompleteAdapter = new ArrayAdapter<>(TestAutoCompleteActivity.this, android.R.layout.select_dialog_item, suggestions);
        mAutoCompleteAdapter.notifyDataSetChanged();*/

        //debug
        String info = "searching: " + mAutoCompleteEntryField.getText().toString() +
                    "\nnum fields in arraylist(suggestions): " + suggestions
                        +"\nnum fields in adapter(mAutoCompleteAdapter): " + mAutoCompleteAdapter.getCount();
        Log.d("autocomplete info", info);
    }

    private void initAutoComplete(){
        //mAutoCompleteAdapter = new ArrayAdapter<>(TestAutoCompleteActivity.this, android.R.layout.select_dialog_item);
        mAutoCompleteAdapter = new ArrayAdapterNoFilter(TestAutoCompleteActivity.this, android.R.layout.select_dialog_item);
        mAutoCompleteAdapter.setNotifyOnChange(true);

        mAutoCompleteEntryField.setThreshold(3); //how many characters before looking for matches
        mAutoCompleteEntryField.getText().clear();
        mAutoCompleteEntryField.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteEntryField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new initSelectedCardTask().execute(position);
            }
        });
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
                        public void run() {
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

    private class initSelectedCardTask extends AsyncTask<Integer, Void, NonLand>{ //gets the card selected from auto complete and downloads the image
        private final String LOG_TAG = initSelectedCardTask.class.getSimpleName();
        @Override
        protected NonLand doInBackground(Integer... params) {
            JSONArray cardsJsonArray = JsonFetcher.getJSONArrayOfCards();
            try {
                JSONObject selectedCard = cardsJsonArray.getJSONObject(params[0]);
                NonLand newCard = new NonLand(selectedCard);
                return newCard;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "JSONException occured");
                return null;
            }
        }

        @Override
        protected void onPostExecute(NonLand card) {
            super.onPostExecute(card);
            mCardView.setImageBitmap(card.getCardImage());
        }
    }
}
