package com.example.yifan.mtgdecktracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonFetcher {
    private JsonFetcher(){} //force to be static method library, uninstantiable class

    private final static String PARTIAL_MATCH_URL_START = "https://api.deckbrew.com/mtg/cards?name="; //append subname of card's name to get JsonArray containing all cards that contain that subname
    private final static String AUTO_COMPLETE_URL_START = "https://api.deckbrew.com/mtg/cards/typeahead?q="; //^ but for cards that start with some string

    private static JSONArray cardsJsonArray;

    public static ArrayList<String> getCardsFromPartialMatch(String subname) throws UnsupportedEncodingException {
        String url = (PARTIAL_MATCH_URL_START + subname).replaceAll(" ", "-"); //may need some better way to encodeURLs, URLencoder class doesn't work here
        return getJsonArrFromURL(url);
    }

    public static ArrayList<String> getCardsFromAutoComplete(String subname) throws UnsupportedEncodingException {
        String url = (AUTO_COMPLETE_URL_START + subname).replaceAll(" ", "-");
        return getJsonArrFromURL(url);
    }

    private static ArrayList<String> getJsonArrFromURL(String url){
        InputStream is;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            cardsJsonArray = new JSONArray(jsonText);
            is.close();
            return parseJsonArray(cardsJsonArray);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static ArrayList<String> parseJsonArray(JSONArray jsonCards) throws JSONException {
        ArrayList<String> cardsList = new ArrayList<>();
        for(int i = 0; i < jsonCards.length(); i++){
            JSONObject card = jsonCards.getJSONObject(i);
            cardsList.add(card.getString("name"));
        }
        return cardsList;
    }

    public static JSONArray getCardsJsonArray() {
        return cardsJsonArray;
    }
}