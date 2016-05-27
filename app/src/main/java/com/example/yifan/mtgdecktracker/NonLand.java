package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Yifan on 5/21/2016.
 */
public class NonLand extends Card {
    public NonLand(String name, int cmc, String cost, String imageURL) throws  IOException{
        this.name = name;
        this.cmc = cmc;
        this.cost = cost;
        this.imageURL = imageURL;
        initializeImage();
    }

    public NonLand(JSONObject jsonCard)throws JSONException, IOException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");
        initializeImage();
    }

    public boolean initializeImage() throws IOException {
        InputStream imageStream = new URL(getImageURL()).openStream();
        cardImage = BitmapFactory.decodeStream(imageStream);
        if(cardImage != null){
            return true;
        }
        else{
            return false;
        }
    }

    public void setTotal(int newTotal){
        this.total = newTotal;
        this.inDeck = newTotal;
    }

    public boolean moveOutOfDeck(){
        if(this.inDeck > 0){
            this.inDeck--;
            this.notInDeck++;
            return true;
        }
        else{
            return false;
        }
    }

    public String getName() {
        return this.name;
    }

    public int getCmc() {
        return cmc;
    }

    public String getCost() {
        return cost;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Bitmap getCardImage(){
        return cardImage;
    }

}
