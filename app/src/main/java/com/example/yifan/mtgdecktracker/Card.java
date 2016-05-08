package com.example.yifan.mtgdecktracker;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card { //to be extended by Land, Creature, Sorcery, Artifact classes as these are all "cards"
    int cmc; //converted mana cost
    int remainingInDeck;

    abstract boolean toHand();
    abstract boolean toGraveYard();
    abstract boolean toExile();
    abstract boolean toDeck();

}
