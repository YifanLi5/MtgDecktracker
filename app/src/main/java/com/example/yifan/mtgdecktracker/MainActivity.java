package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private Button mEnter;
    private EditText mCardSearch;
    private TextView mCardText;
    private ImageView mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEnter = (Button) findViewById(R.id.enterCard);
        mCardSearch = (EditText) findViewById(R.id.searchCard);
        mCardText = (TextView) findViewById(R.id.cardText);
        mCardView = (ImageView) findViewById(R.id.cardView);

        mCardSearch.setText("Archangel%20Avacyn");
        //for testing purposes the textfield is hardcoded to be initially some card
        //this textfields can be manually set but need to follow proper url encoding.
        //the %20 is because urls cannot have spaces. URL encoding normally replaces a space with a plus (+) sign or with %20.
        //todo: write wrapper to take care of this

        mEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new getCardTask().execute(mCardSearch.getText().toString());
            }
        });
    }

    private class getCardTask extends AsyncTask<String, Void, JsonCardParser> {
        StringBuilder cardInfoBuilder = new StringBuilder();
        @Override
        protected JsonCardParser doInBackground(String... params) {
            JSONArray cardsArray = JsonFetcher.getJSONArrFromSubname(params[0]);
            JsonCardParser parsedCard = new JsonCardParser(cardsArray);
            try {
                NonLand displayCard = parsedCard.getCardAtIndex(0);
                cardInfoBuilder.append("name: " + displayCard.getName() + "\n");
                cardInfoBuilder.append("cmc: " + displayCard.getCmc() + "\n");
                cardInfoBuilder.append("cost: " + displayCard.getCost() + "\n");

                displayCard.initializeImage();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return parsedCard;
        }

        @Override
        protected void onPostExecute(JsonCardParser jsonCardParser) {
            super.onPostExecute(jsonCardParser);
            mCardText.setText(cardInfoBuilder.toString());
            mCardView.setImageBitmap(jsonCardParser.getCard().cardImage);
        }
    }
}
