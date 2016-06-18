package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;
import android.os.Parcelable;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card implements Parcelable{

    String name;
    int cmc; //converted mana cost
    int total; //total number of this specific card; total = InDeck + notInDeck
    int inDeck; //number of this card remaining in deck
    int notInDeck; //number not in deck, i.e: in graveyard, hand, field, exile
    String cost;
    String imageURL;

    public Bitmap getCardImage() {
        return cardImage;
    }

    public void setCardImage(Bitmap cardImage) {
        this.cardImage = cardImage;
    }

    Bitmap cardImage;

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
}
