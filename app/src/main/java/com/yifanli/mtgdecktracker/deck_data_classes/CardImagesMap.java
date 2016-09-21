package com.yifanli.mtgdecktracker.deck_data_classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Hashtable;

/**
 * Created by Yifan on 9/21/2016.
 * Used to map URLs of card images to their actual image
 * seperates serialization of card images from card class to allow async loading using the card's url.
 */
public class CardImagesMap {
    public static Hashtable<String, byte[]> urlCardTable = new Hashtable<>();
    public static Type hashTableType = new TypeToken<Hashtable<String, byte[]>>(){}.getType();

    private CardImagesMap(){
        throw new UnsupportedOperationException("No instances.");
    }

    public static String serializeAsJSON(){
        Gson gson = new Gson();
        return gson.toJson(urlCardTable);
    }

    public static void desealizeFromJSON(Reader reader){
        Gson gson = new Gson();
        urlCardTable = gson.fromJson(reader, hashTableType);
    }


}
