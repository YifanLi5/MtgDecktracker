package com.example.yifan.mtgdecktracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Yifan on 5/7/2016.
 */
public class Deck implements Serializable, Parcelable{
    private static final int DEFAULT_AMT = 60;
    private static final int SIDEBOARD_AMT = 15;
    private ArrayList<Card> mainBoard;
    private ArrayList<Card> sideBoard;
    private String deckName;

    public Deck(){
        mainBoard = new ArrayList<>(DEFAULT_AMT);
        sideBoard = new ArrayList<>(SIDEBOARD_AMT);
    }

    public Deck(ArrayList<Card> mainBoard, ArrayList<Card> sideBoard, String deckName){
        this.mainBoard = mainBoard;
        this.sideBoard = sideBoard;
        this.deckName = deckName;
    }

    public ArrayList<Card> getMainBoard() {
        return mainBoard;
    }

    public ArrayList<Card> getSideBoard() {
        return sideBoard;
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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for(Card card: mainBoard){
            sb.append("Deck " + i + ": ");
            sb.append(card.getName() + " ");
        }
        return sb.toString();
    }

    protected Deck(Parcel in) {
        if (in.readByte() == 0x01) {
            mainBoard = new ArrayList<Card>();
            in.readList(mainBoard, Card.class.getClassLoader());
        } else {
            mainBoard = null;
        }
        if (in.readByte() == 0x01) {
            sideBoard = new ArrayList<Card>();
            in.readList(sideBoard, Card.class.getClassLoader());
        } else {
            sideBoard = null;
        }
        deckName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mainBoard == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mainBoard);
        }
        if (sideBoard == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(sideBoard);
        }
        dest.writeString(deckName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
        @Override
        public Deck createFromParcel(Parcel in) {
            return new Deck(in);
        }

        @Override
        public Deck[] newArray(int size) {
            return new Deck[size];
        }
    };


}
