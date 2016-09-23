package com.yifanli.mtgdecktracker.deck_data_classes;

import android.util.Log;

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
    private static final String LOG_TAG = CardImagesMap.class.getSimpleName();
    public static Hashtable<String, byte[]> urlCardTable;
    public static Type hashTableType = new TypeToken<Hashtable<String, byte[]>>(){}.getType();
    private static CardImagesMap singletonInstance = new CardImagesMap();

    public static CardImagesMap getSingletonInstance(){
        return singletonInstance;

    }

    private CardImagesMap(){
        urlCardTable = new Hashtable<>();
    }

    public static String serializeAsJSON(){
        Gson gson = new Gson();
        String result = gson.toJson(urlCardTable);
        Log.i(LOG_TAG, result);
        return result;
    }

    public static void desealizeFromJSON(Reader reader){
        Gson gson = new Gson();
        Hashtable<String, byte[]> storage = gson.fromJson(reader, hashTableType);
        if(storage != null){
            urlCardTable = storage;
        }
    }


}
