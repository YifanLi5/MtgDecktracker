package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yifan on 7/22/2016.
 */
public class BasicLand extends Card {
    public BasicLand(JSONObject jsonCard, int total) throws JSONException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.editions = fillEditionArray(jsonCard.getJSONArray("editions"));
        this.total = total;
        this.inDeck = total;
    }

    public BasicLand(Parcel in) {
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
            return new BasicLand(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new BasicLand[size];
        }
    };

    @Override
    public String toString() {
        return "name: " + name + " total: " + total;

    }
}
