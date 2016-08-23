package com.yifanli.mtgdecktracker.deck_data_classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yifan on 5/21/2016.
 */
public class NonBasicLand extends Card {

    public NonBasicLand(String name, int cmc, String cost, String imageURL) {
        this.name = name;
        this.cmc = cmc;
        this.cost = cost;
        this.imageURL = imageURL;
        this.total = 1;
    }

    public NonBasicLand(JSONObject jsonCard) throws JSONException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");

    }

    public NonBasicLand(JSONObject jsonCard, int total) throws JSONException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.editions = fillEditionArray(jsonCard.getJSONArray("editions"));
        this.total = total;
        this.inDeck = total;
    }

    public NonBasicLand(Parcel in) {
        name = in.readString();
        cmc = in.readInt();
        total = in.readInt();
        inDeck = in.readInt();
        notInDeck = in.readInt();
        cost = in.readString();
        imageURL = in.readString();
        cardImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        imageInitialized = in.readByte() != 0; //imageInitialized == true if byte != 0
        if(in.readByte() == 0x01){
            editions = new ArrayList<>();
            in.readList(editions, Edition.class.getClassLoader());
        }
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new NonBasicLand(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new NonBasicLand[size];
        }
    };



    @Override
    public String toString() {
        return "name: " + name + " total: " + total;

    }

}
