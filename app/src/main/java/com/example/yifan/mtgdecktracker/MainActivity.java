package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView mAutoCompleteEntryField;
    private ImageView mCardView;
    private ArrayAdapter<String> autoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAutoCompleteEntryField = (AutoCompleteTextView) findViewById(R.id.autoCompleteEntryField);
        mCardView = (ImageView) findViewById(R.id.cardView);
        initAutoComplete();
    }

    private void autoCompleteUpdater(ArrayList<String> suggestions){
        if(autoCompleteAdapter != null){
            autoCompleteAdapter.clear();
        }
        autoCompleteAdapter.addAll(suggestions);

        //debug
        String info = "searching: " + mAutoCompleteEntryField.getText().toString() +
                    "\nnum fields in arraylist(suggestions): " + suggestions.size()
                        +"\nnum fields in adapter(autoCompleteAdapter): " + autoCompleteAdapter.getCount();
        Log.d("autocomplete info", info);
    }

    private void initAutoComplete(){
        autoCompleteAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item);
        autoCompleteAdapter.setNotifyOnChange(true);

        mAutoCompleteEntryField.setThreshold(3); //how many characters before looking for matches
        mAutoCompleteEntryField.getText().clear();
        mAutoCompleteEntryField.setAdapter(autoCompleteAdapter);
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
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> cardSuggestions;
            try{
                cardSuggestions = JsonFetcher.getCardsFromAutoComplete(params[0]);
            } catch(IOException e){
                Log.e("MainActivity", "IOException in autoCompleteTask");
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

    private class initSelectedCardTask extends AsyncTask<Integer, Void, NonLand>{ //gets the card selected from auto complete and downlaods the image
        @Override
        protected NonLand doInBackground(Integer... params) {
            JSONArray cardsJsonArray = JsonFetcher.getCardsJsonArray();
            try {
                JSONObject selectedCard = cardsJsonArray.getJSONObject(params[0]);
                NonLand newCard = new NonLand(selectedCard);
                return newCard;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("initSelectedCardTask", "JSONException occured");
                return null;
            } catch (IOException e){
                e.printStackTrace();
                Log.e("initSelectedCardTask", "IOException occured: likely image dl problems");
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
