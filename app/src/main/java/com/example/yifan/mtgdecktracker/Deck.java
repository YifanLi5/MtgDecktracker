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

    public Deck(){
        mainBoard = new ArrayList<>(DEFAULT_AMT);
        sideBoard = new ArrayList<>(SIDEBOARD_AMT);
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






    /*
    Need comparator classes to sort
    - alphabetically
    - cmc
    - color(s) ps: need to decide how to handle multiple card colors. Or can omit this field entirely
    - type of card
    - TBD
     */
}
