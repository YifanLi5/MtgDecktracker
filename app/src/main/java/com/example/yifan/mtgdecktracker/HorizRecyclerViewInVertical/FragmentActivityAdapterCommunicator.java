package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical;

import com.example.yifan.mtgdecktracker.Card;

import java.util.ArrayList;

/**
 * Created by Yifan on 6/16/2016.
 */
public interface FragmentActivityAdapterCommunicator {
    public void setCardCountCallback(int newCount, int position); //ModifyCardEntryFragment communicates AddCardsToDeckFragment through its host activity

    public void respondToAdapterEditDeckButton(ArrayList<Card> deckContents, String deckName, int deckIndex); //allows host activity to respond to RecyclerView's button click

    public void getModifiedDeck(ArrayList<Card> deck, int deckIndex, String deckName); //AddCardsToDeckFragment gives back a modified deck to host activity

    public void initCardImageCallback(int position); //called within NonLand class by initializeImage inorder to have the horizontal adapter show the card images

    public void openDrawer(int gravity); //opens drawer based on the drawer's gravity

    public void closeDrawer(int gravity); //closes drawer based on the drawer's gravity

    public void lockOrUnlockdrawer(int lockmode, int gravity); //locks or unlocks drawer based on the drawer's gravity

}


/*
Todo:
- SavedDecksActivity save decks                                                                             DONE
- SavedDecksActivity on rotation removes everything (implement onSaveInstanceState())                       DONE
- Actually have images, (init images) - glide api?                                                          DONE
- crop card images                                                                                          DONE
- save images(bitmaps) somehow                                                                              DONE
- delete deck when last card is deleted + option to delete decks
- have deck names                                                                                           DONE
- saved decks needs to show both number and name of card                                                    DONE
- able to sort decks (start with alphabetical)
- on saving new deck wipe create deck list                                                                  DONE
- handle attempting to add 2 decks with same name
-
 */
