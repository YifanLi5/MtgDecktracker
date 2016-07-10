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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Yifan on 5/21/2016.
 */
public class NonLand extends Card {

    public boolean initImage = false;

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
        initImage = in.readByte() != 0; //initImage == true if byte != 0
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

    public void initializeImage(Fragment fragment, final int recyclerViewPosition, final Context context, final boolean mainboardCard) throws IOException, URISyntaxException {
        Glide.with(fragment)
                .load(imageURL)
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

    @Override
    public String toString() {
        return "name: " + name + " total: " + total;

    }


}
