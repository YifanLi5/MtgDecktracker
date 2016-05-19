package com.example.yifan.mtgdecktracker;

import android.graphics.Bitmap;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card { //to be extended by Land, Creature, Sorcery, Artifact classes as these are all "cards"

    int cmc; //converted mana cost
    int totalCardNum; //total number of this specific card; totalCardNum = InDeck + notInDeck
    int InDeck; //number of this card remaining in deck
    int notInDeck; //number not in deck, i.e: in graveyard, hand, field, exile
    String cost
    Bitmap cardImage; //image of card, get from passed in url

    abstract boolean initImageFromURL(String rawURL);




}
