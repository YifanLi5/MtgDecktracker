package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card {

    String name;
    int cmc; //converted mana cost
    int total; //total number of this specific card; total = InDeck + notInDeck
    int inDeck; //number of this card remaining in deck
    int notInDeck; //number not in deck, i.e: in graveyard, hand, field, exile
    String cost;
    String imageURL;
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
}
