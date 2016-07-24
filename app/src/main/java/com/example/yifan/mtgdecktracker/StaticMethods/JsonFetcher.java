package com.example.yifan.mtgdecktracker.StaticMethods;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JsonFetcher {
    private JsonFetcher(){} //force to be static method library, uninstantiable class
    private final static String LOG_TAG = JsonFetcher.class.getSimpleName();
    private final static String PARTIAL_MATCH_URL_START = "https://api.deckbrew.com/mtg/cards?name="; //append subname of card's name to get JsonArray containing all cards that contain that subname
    private final static String AUTO_COMPLETE_URL_START = "https://api.deckbrew.com/mtg/cards/typeahead?q="; //^ but for cards that start with some string

    private static JSONArray JSONArrayOfCards;

    public static ArrayList<String> getCardsFromPartialMatch(String subname) throws UnsupportedEncodingException {
        String url = (PARTIAL_MATCH_URL_START + subname).replaceAll(" ", "-"); //may need some better way to encodeURLs, URLencoder class doesn't work here
        return getCardNamesArrayListFromURL(url);
    }

    public static ArrayList<String> getCardsFromAutoComplete(String subname) throws UnsupportedEncodingException {
        String url = (AUTO_COMPLETE_URL_START + subname).replaceAll(" ", "-");
        return getCardNamesArrayListFromURL(url);
    }

    public static ArrayList<String> getCardNamesArrayListFromURL(String url){
        Log.d(LOG_TAG, url);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            URL targetURL = new URL(url);
            urlConnection = (HttpURLConnection) targetURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            if(is == null){
                return null; //nothing recieved
            }
            reader = new BufferedReader(new InputStreamReader(is));
            String jsonText = readAll(reader);
            JSONArrayOfCards = new JSONArray(jsonText);
            return getCardNamesFromJSONArray(JSONArrayOfCards);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "MalformedURLException: error in url");
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException: error in getting data from url");
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException: error in getting json");
            return null;
        }

        finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IOException: error in closing reader");
                }
            }
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static ArrayList<String> getCardNamesFromJSONArray(JSONArray jsonCards) throws JSONException {
        ArrayList<String> cardsList = new ArrayList<>();
        for(int i = 0; i < jsonCards.length(); i++){
            JSONObject card = jsonCards.getJSONObject(i);
            cardsList.add(card.getString("name"));
        }
        return cardsList;
    }

    public static JSONArray getJSONArrayOfCards() {
        return JSONArrayOfCards;
    }
}