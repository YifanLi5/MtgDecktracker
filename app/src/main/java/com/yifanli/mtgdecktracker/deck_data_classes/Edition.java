package com.yifanli.mtgdecktracker.deck_data_classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Yifan on 7/22/2016.
 */
public class Edition implements Serializable, Parcelable {
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

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Edition> CREATOR = new Parcelable.Creator<Edition>() {
        @Override
        public Edition createFromParcel(Parcel in) {
            return new Edition(in);
        }
        @Override
        public Edition[] newArray(int size) {
            return new Edition[size];
        }
    };
}
