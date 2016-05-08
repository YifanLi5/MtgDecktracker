package com.example.yifan.mtgdecktracker;

import java.util.ArrayList;

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

    /*
    Need comparator methods to sort
    - alphabetically
    - cmc
    - color(s) ps: need to decide how to handle multiple card colors. Or can omit this field entirely
    - type of card
    - TBD
     */



}
