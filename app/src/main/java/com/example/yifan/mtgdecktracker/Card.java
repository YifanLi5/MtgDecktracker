package com.example.yifan.mtgdecktracker;

/**
 * Created by Yifan on 5/7/2016.
 */
public abstract class Card { //to be extended by Land, Creature, Sorcery, Artifact classes as these are all "cards"

    public enum CurrentLocation {
        DECK,
        HAND,
        GRAVEYARD,
        EXILE
    }

    int cmc; //converted mana cost
    int remainingInDeck;


    public boolean toHand(CurrentLocation location){
        if(location == CurrentLocation.DECK){
            remainingInDeck--;
        }

    }

    public boolean toGraveYard(){
        remainingInDeck--;
    }

    public boolean toExile(){
        remainingInDeck--;
    }

    public boolean toDeck(){
        remainingInDeck++;
    }

}
