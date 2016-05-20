package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Yifan on 5/20/2016.
 */
public class JsonCardParser {
    String name;
    int cmc;
    String cost;
    String imageURL;
    Bitmap cardBm;


    public JsonCardParser(JSONObject JsonArr){
        initializeCardFields(JsonArr);
    }

    public void initializeCardFields(JSONObject jsonCard){
        try {
            name = jsonCard.getString("name");
            cmc = jsonCard.getInt("cmc");
            cost = jsonCard.getString("cost");
            imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");
            InputStream in = new URL(imageURL).openStream();
            cardBm = BitmapFactory.decodeStream(in);
            in.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCost() {
        return cost;
    }

    public int getCmc() {
        return cmc;
    }

    public String getName() {
        return name;
    }

    public Bitmap getCardBm() {
        return cardBm;
    }
}
