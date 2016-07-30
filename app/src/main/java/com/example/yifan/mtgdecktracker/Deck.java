package com.example.yifan.mtgdecktracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Yifan on 5/7/2016.
 */
public class Deck implements Serializable, Parcelable{

    private ArrayList<Card> mainBoard;
    private ArrayList<Card> sideBoard;
    private String deckName;
    private int totalCardCount; //total number of cards in mainboard, NOT the same as mainBoard.size(). Card objects have a variable of how many copies of that certain card there are and is not considered by size().

    public Deck(){
        mainBoard = new ArrayList<>();
        sideBoard = new ArrayList<>();
    }

    public Deck(ArrayList<Card> mainBoard, ArrayList<Card> sideBoard, String deckName){
        this.mainBoard = mainBoard;
        this.sideBoard = sideBoard;
        this.deckName = deckName;
    }

    public Deck(ArrayList<Card> mainBoard, ArrayList<Card> sideBoard, String deckName, int totalCardCount){
        this.mainBoard = mainBoard;
        this.sideBoard = sideBoard;
        this.deckName = deckName;
        this.totalCardCount = totalCardCount;
    }

    public static Deck getCopy(Deck orig){
        return new Deck((ArrayList<Card>)orig.mainBoard.clone(), (ArrayList<Card>)orig.sideBoard.clone(), orig.deckName, orig.totalCardCount);
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

    public int getTotalCardCount() {
        return totalCardCount;
    }

    public void setTotalCardCount(int totalCardCount) {
        this.totalCardCount = totalCardCount;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for(Card card: mainBoard){
            sb.append("Deck");
            sb.append(i);
            sb.append(": ");
            sb.append(card.getName());
            sb.append(" ");
        }
        return sb.toString();
    }

    protected Deck(Parcel in) {
        if (in.readByte() == 0x01) {
            mainBoard = new ArrayList<>();
            in.readList(mainBoard, Card.class.getClassLoader());
        } else {
            mainBoard = null;
        }
        if (in.readByte() == 0x01) {
            sideBoard = new ArrayList<>();
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
