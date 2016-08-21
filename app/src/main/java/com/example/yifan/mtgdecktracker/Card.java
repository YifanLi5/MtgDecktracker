package com.example.yifan.mtgdecktracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.yifan.mtgdecktracker.saved_deck_classes.SavedDecksActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card implements Parcelable, Serializable{

    private static final String LOG_TAG = Card.class.getSimpleName();
    String name;
    int cmc; //converted mana cost
    int total; //total number of this specific card; total = InDeck + notInDeck
    transient int inDeck; //number of this card remaining in deck
    transient int notInDeck; //number not in deck, i.e: in graveyard, hand, field, exile
    String cost;

    String imageURL;
    Bitmap cardImage;

    public boolean imageInitialized = false;
    ArrayList<Edition> editions;
    int currentEditionIndex = -1;

    public static Card getCardSubclassInstance(JSONObject jsonCard, int total) throws JSONException {
        //some cards don't have a supertype, check if it does and also if said supertype denotes it is a basic land
        if(jsonCard.has("supertypes") && jsonCard.getJSONArray("supertypes").getString(0).equals("basic")){
            return new BasicLand(jsonCard, total);
        }
        else{
            return new NonBasicLand(jsonCard, total);
        }
    }

    public Bitmap getCardImage() {
        return cardImage;
    }

    public void setCardImage(Bitmap cardImage) {
        this.cardImage = cardImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCmc() {
        return cmc;
    }

    public void setCmc(int cmc) {
        this.cmc = cmc;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.inDeck = total;
    }

    public int getInDeck() {
        return inDeck;
    }

    public void setInDeck(int inDeck) {
        this.inDeck = inDeck;
    }

    public int getNotInDeck() {
        return notInDeck;
    }

    public void setNotInDeck(int notInDeck) {
        this.notInDeck = notInDeck;
    }

    public boolean moveOutOfDeck(){
        //operation only valid is there is at least 1 card inDeck
        if(inDeck >= 1){
            inDeck--;
            notInDeck++;
            return true;
        }
        else{
            Log.e(LOG_TAG, "attempt to remove card when there isn't one to remove");
            return false;
        }
    }

    public boolean moveIntoDeck(){
        if(notInDeck >= 1){
            inDeck++;
            notInDeck--;
            return true;
        }
        else{
            Log.e(LOG_TAG, "attempt to add card when there isn't one to add");
            return false;
        }
    }

    public void initializeImage(Fragment fragment, final int recyclerViewPosition, final Context context, final boolean mainboardCard, int editionIndex) throws IOException, URISyntaxException {
        currentEditionIndex = editionIndex;
        Glide.with(fragment)
                .load(editions.get(currentEditionIndex).imageURL) //edition index is which printing of the card to load, usually editionIndex is called with 0 to load the most recent
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        cardImage = resource;
                        imageInitialized = true;
                        if (context instanceof SavedDecksActivity) {
                            ((SavedDecksActivity) context).initCardImageCallback(recyclerViewPosition, mainboardCard);
                        }
                    }

                });

    }

    private class BitmapDataObject implements Serializable {
        public byte[] imageByteArray;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeInt(total);
        out.writeInt(inDeck);
        out.writeObject(cost);
        out.writeObject(imageURL);
        out.writeObject(editions);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(cardImage != null){
            cardImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            BitmapDataObject bitmapDataObject  = new BitmapDataObject();
            bitmapDataObject.imageByteArray = byteArrayOutputStream.toByteArray();
            out.writeObject(bitmapDataObject);
        }

    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.name = (String)in.readObject();
        this.total = in.readInt();
        this.inDeck = in.readInt();
        this.cost = (String)in.readObject();
        this.imageURL = (String)in.readObject();
        this.editions = (ArrayList<Edition>) in.readObject();

        BitmapDataObject bitmapDataObject = (BitmapDataObject)in.readObject();
        this.cardImage = BitmapFactory.decodeByteArray(bitmapDataObject.imageByteArray, 0, bitmapDataObject.imageByteArray.length);
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.name = "NoData";
        this.total = 0;
        this.inDeck = 0;
        this.cost = "NoData";
        this.imageURL = "NoData";
        this.cardImage = null;
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
        dest.writeByte((byte) (imageInitialized ? 1 : 0)); //if imageInitialized == true, byte == 1
        if(editions == null){
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(editions);
        }

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

    public ArrayList<Edition> fillEditionArray(JSONArray editionsJSONArr) throws JSONException {
        ArrayList<Edition> editionsReturn = new ArrayList<>();
        for(int i = editionsJSONArr.length() - 1; i >= 0; i--){ //add in reverse order, the card at the beginning of the arraylist oldest release and least likely to be a blank
            editionsReturn.add(new Edition(editionsJSONArr.getJSONObject(i)));
        }
        return editionsReturn;
    }


}
