package com.example.yifan.mtgdecktracker;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Yifan on 5/7/2016.
 */
public class Deck {
    ArrayList<Card> land;
    ArrayList<Card> creature;
    ArrayList<Card> instant;
    ArrayList<Card> sorcery;
    ArrayList<Card> enchantment;
    ArrayList<Card> artifact;
    ArrayList<Card> plainswalker;
    ArrayList<ArrayList<Card>> deckList; //arraylist containing every above arraylist.

    public Deck(){
        land = new ArrayList<>();
        creature = new ArrayList<>();
        instant = new ArrayList<>();
        sorcery = new ArrayList<>();
        enchantment = new ArrayList<>();
        artifact = new ArrayList<>();
        plainswalker = new ArrayList<>();

    }

    public void addCard(Card card){

    }

      /*
    Need comparator classes to sort
    - alphabetically
    - cmc
    - color(s) ps: need to decide how to handle multiple card colors. Or can omit this field entirely
    - type of card
    - TBD
     */


    private void finishDeck(){ //do not do this in constructor. Everytime arrayList expands will need to copy everything.
        deckList = new ArrayList<>();
        deckList.add(land);
        deckList.add(creature);
        deckList.add(instant);
        deckList.add(sorcery);
        deckList.add(enchantment);
        deckList.add(artifact);
        deckList.add(plainswalker);
    }







}
