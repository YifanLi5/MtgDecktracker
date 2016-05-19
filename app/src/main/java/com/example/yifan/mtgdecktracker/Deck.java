package com.example.yifan.mtgdecktracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Yifan on 5/7/2016.
 */
public class Deck {
    private static final int STARTING_CARD_AMT = 60;
    ArrayList<Card> cardList;
    HashMap<String, Integer> cardListMap;
    /*
    All cards user initially enters stored in cardListWIP as Strings
    Only when deck is confirmed by user do we use this list to send all JSON requests at one time to fill out cardList.

    HashMap is used due to possibility of double adding card strings, easier to compact them together.
    ex) adding <"card1", 1> and <"card1", 2> would result in a final entry of <"card1", 3>
    */


    public Deck(){
        cardListMap = new HashMap<>();
    }

    public void addCard(String cardName, int count){ //assuming taking card name as string from textfield
        if(cardListMap.containsKey(cardName)){
            cardListMap.put(cardName, cardListMap.get(cardName) + count);
        }
        else{
            cardListMap.put(cardName, Integer.valueOf(count));
        }
    }

    //testing new branch

    public void finalizeDeck(){
        for(HashMap.Entry<String, Integer> entry : cardListMap.entrySet()){
            String cardName = entry.getKey();
            int cardCount = entry.getValue().intValue();

        }
    }

    public boolean removeFromDeck(Card card){

    }

    public void sortByCMC(){

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
