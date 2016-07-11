package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card implements Parcelable, Serializable{

    String name;
    int cmc; //converted mana cost
    int total; //total number of this specific card; total = InDeck + notInDeck
    int inDeck; //number of this card remaining in deck
    int notInDeck; //number not in deck, i.e: in graveyard, hand, field, exile
    String cost;
    String imageURL;
    Bitmap cardImage;

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
    }

    private class BitmapDataObject implements Serializable {
        public byte[] imageByteArray;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeInt(total);
        out.writeObject(cost);
        out.writeObject(imageURL);

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
        this.cost = (String)in.readObject();
        this.imageURL = (String)in.readObject();

        BitmapDataObject bitmapDataObject = (BitmapDataObject)in.readObject();
        this.cardImage = BitmapFactory.decodeByteArray(bitmapDataObject.imageByteArray, 0, bitmapDataObject.imageByteArray.length);
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.name = "NoData";
        this.total = 0;
        this.cost = "NoData";
        this.imageURL = "NoData";
        this.cardImage = null;
    }
}
