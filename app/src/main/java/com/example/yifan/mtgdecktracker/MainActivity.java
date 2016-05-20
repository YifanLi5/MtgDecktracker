package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    String testURLAvacyn = "https://api.deckbrew.com/mtg/cards/archangel-avacyn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new getJsonTask((ImageView) findViewById(R.id.cardImage)).execute(testURLAvacyn);

    }

    private class getJsonTask extends AsyncTask<String, Void, JsonCardParser> {
        ImageView card;

        public getJsonTask(ImageView card){
            this.card = card;
        }

        @Override
        protected JsonCardParser doInBackground(String... params) {
            JSONObject temp = JsonFromURL.getJsonArrFromURL(params[0]);
            JsonCardParser avacyn = new JsonCardParser(temp);
            return avacyn;
        }

        @Override
        protected void onPostExecute(JsonCardParser jsonCardParser) { //runs on ui thread, need to change to do in background
            super.onPostExecute(jsonCardParser);
            card.setImageBitmap(jsonCardParser.getCardBm());
        }
    }
}
