package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Yifan on 5/21/2016.
 */
public class NonLand extends Card {

    public boolean didTask = false;


    public NonLand(String name, int cmc, String cost, String imageURL) throws  IOException{
        this.name = name;
        this.cmc = cmc;
        this.cost = cost;
        this.imageURL = imageURL;
        this.total = 1;
    }

    public NonLand(JSONObject jsonCard)throws JSONException, IOException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");
        new fetchImageTask().execute();
    }

    public NonLand(JSONObject jsonCard, int total)throws JSONException {
        this.name = jsonCard.getString("name");
        this.cmc = jsonCard.getInt("cmc");
        this.cost = jsonCard.getString("cost");
        this.imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");
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

    private class fetchImageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                didTask = initializeImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

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

    @Override
    public String toString(){
        return "name: " + name + " total: " + total;

    }

}
