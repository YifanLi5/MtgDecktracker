package com.example.yifan.mtgdecktracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Yifan on 5/7/2016.
 */
public class Deck {
    private static final int STARTING_CARD_AMT = 60;
    ArrayList<Card> cardList;

    public Deck(){
        cardList = new ArrayList<>();
    }

    public void addCard(JSONObject jsonCard){
        try{
            String name = jsonCard.getString("name");
            int cmc = jsonCard.getInt("cmc");
            String cost = jsonCard.getString("cost");
            String imageURL = jsonCard.getJSONArray("editions").getJSONObject(0).getString("image_url");

            //todo: set up how card objects work
            Card newCard = null;

            //todo: should extend arrayList and override add to insert using insertion sort, all cards ordered by cmc as added.
            cardList.add(newCard);


        }
        catch(JSONException e){
            e.printStackTrace();
        }


    }

    public boolean removeCardCopyFromDeck(Card card){
        //todo: because list is order better to write own version of remove(Object) using binary search
        return cardList.remove(card);
    }

    public boolean saveDeck(){
        //figure out how to do this
    }

    /*
    Need comparator classes to sort
    - alphabetically
    - cmc
    - color(s) ps: need to decide how to handle multiple card colors. Or can omit this field entirely
    - type of card
    - TBD
     */










}
