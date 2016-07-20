package com.example.yifan.mtgdecktracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical.SavedDecksActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Yifan on 5/21/2016.
 */
public class NonLand extends Card {

    public boolean initImage = false;
    private ArrayList<Edition> editions;
    private int currentEditionIndex = -1;

    public NonLand(String name, int cmc, String cost, String imageURL) {
        this.name = name;
        this.cmc = cmc;
        this.cost = cost;
        this.imageURL = imageURL;
        this.total = 1;
    }

    public NonLand(JSONObject jsonCard) throws JSONException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");

    }

    public NonLand(JSONObject jsonCard, int total) throws JSONException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        //this.imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");
        this.editions = fillEditionArray(jsonCard.getJSONArray("editions"));
        this.total = total;
    }

    public NonLand(Parcel in) {
        name = in.readString();
        cmc = in.readInt();
        total = in.readInt();
        inDeck = in.readInt();
        notInDeck = in.readInt();
        cost = in.readString();
        imageURL = in.readString();
        cardImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        initImage = in.readByte() != 0; //initImage == true if byte != 0
        if(in.readByte() == 0x01){
            in.readList(getEditions(), Edition.class.getClassLoader());
        }


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(cmc);
        dest.writeInt(total);
        dest.writeInt(inDeck);
        dest.writeInt(notInDeck);
        dest.writeString(cost);
        dest.writeString(imageURL);
        dest.writeValue(cardImage);
        dest.writeByte((byte) (initImage ? 1 : 0)); //if initImage == true, byte == 1
        if(getEditions() == null){
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(getEditions());
        }

    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new NonLand(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new NonLand[size];
        }
    };

    public void initializeImage(Fragment fragment, final int recyclerViewPosition, final Context context, final boolean mainboardCard, int editionIndex) throws IOException, URISyntaxException {
        currentEditionIndex = editionIndex;
        Glide.with(fragment)
                .load(editions.get(currentEditionIndex).imageURL) //edition index is which printing of the card to load, usually editionIndex is called with 0 to load the most recent
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        cardImage = resource;
                        initImage = true;
                        if (context instanceof SavedDecksActivity) {
                            ((SavedDecksActivity) context).initCardImageCallback(recyclerViewPosition, mainboardCard);
                        }
                    }

                });

    }

    public void setTotal(int newTotal) {
        this.total = newTotal;
        this.inDeck = newTotal;
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

    public Bitmap getCardImage() {
        return cardImage;
    }

    public ArrayList<Edition> getEditions() {
        return editions;
    }

    public void setCurrentEditionIndex(int currentEditionIndex) {
        this.currentEditionIndex = currentEditionIndex;
    }

    public int getCurrentEditionIndex(){
        return currentEditionIndex;
    }

    @Override
    public String toString() {
        return "name: " + name + " total: " + total;

    }

    public static class Edition implements Serializable, Parcelable{
        String set;
        String imageURL;

        public Edition(JSONObject editionJSON) throws JSONException {
            this.set = editionJSON.getString("set");
            this.imageURL = editionJSON.getString("image_url");
        }

        protected Edition(Parcel in){
            set = in.readString();
            imageURL = in.readString();
        }

        public String getSet() {
            return set;
        }

        public String getImageURL() {
            return imageURL;
        }

        @Override
        public String toString() {
            return "set: " + set + " imageURL: " + imageURL;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(set);
            dest.writeString(imageURL);
        }

        public static final Creator<Edition> CREATOR = new Creator<Edition>() {
            public Edition createFromParcel(Parcel in) {
                return new Edition(in);
            }

            public Edition[] newArray(int size) {
                return new Edition[size];
            }
        };
    }

    public ArrayList<Edition> fillEditionArray(JSONArray editionsJSONArr) throws JSONException {
        ArrayList<Edition> editionsReturn = new ArrayList<>();
        for(int i = editionsJSONArr.length() - 1; i >= 0; i--){ //add in reverse order, the card at the beginning of the arraylist oldest release and least likely to be a blank
            editionsReturn.add(new Edition(editionsJSONArr.getJSONObject(i)));
        }
        return editionsReturn;
    }


}
