package com.example.yifan.mtgdecktracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFetcher {
    private JsonFetcher(){} //force to be static method library, uninstantiable class

    private final static String partialMatchURLStarter = "https://api.deckbrew.com/mtg/cards?name="; //append subname of card's name to get JsonArray containing all cards that contain that subname
    private final static String autoCompleteURLStarter = "https://api.deckbrew.com/mtg/cards/typeahead?q="; //^ but for cards that start with subname

    public static JSONArray getJSONArrFromSubname(String subname){
        String url = partialMatchURLStarter + subname;
        return getJsonArrFromURL(url);
    }

    public static JSONArray getJSONArrFromAutoComplete(String subname){
        String url = autoCompleteURLStarter + subname;
        return getJsonArrFromURL(url);
    }

    public static JSONArray getJsonArrFromURL(String url){
        InputStream is;
        try {
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray jsonArray = new JSONArray(jsonText);
            is.close();
            return jsonArray;
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

}