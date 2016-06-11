package com.example.yifan.mtgdecktracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Yifan on 5/7/2016.
 */
public class Deck {
    private static final int DEFAULT_AMT = 60;
    private static final int SIDEBOARD_AMT = 15;
    private ArrayList<Card> mainBoard;
    private ArrayList<Card> sideBoard;



    private String deckName;

    public Deck(){
        mainBoard = new ArrayList<>(DEFAULT_AMT);
        sideBoard = new ArrayList<>(SIDEBOARD_AMT);
    }

    public ArrayList<Card> getMainBoard() {
        return mainBoard;
    }

    public void setDeckName(String deckName){
        this.deckName = deckName;
    }

    public String getDeckName() {
        return deckName;
    }

    public void addToMainboard(Card card){
        mainBoard.add(card);
    }

    public void addToSideBoard(Card card){
        sideBoard.add(card);
    }

    public void sortListCmc(){
        Collections.sort(mainBoard, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                int rhsCmc = rhs.getCmc();
                int lhsCmc = lhs.getCmc();
                if(lhsCmc < rhsCmc){
                    return 1;
                }
                else{
                    return -1;
                }
            }
        });
    }


}
