package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical;

import com.example.yifan.mtgdecktracker.Card;

import java.util.ArrayList;

/**
 * Created by Yifan on 6/16/2016.
 */
public interface FragmentActivityAdapterCommunicator {
    //// FIXME: 7/6/2016 needs to account for which tab
    void setCardCountCallback(int newCount, int position, boolean mainboardChange); //ModifyCardEntryFragment communicates EditDeckFragment through its host activity

    void respondToAdapterEditDeckButton(ArrayList<Card> mainboardContents, ArrayList<Card> sideboardContents, String deckName, int deckIndex); //allows host activity to respond to RecyclerView's button click

    void getModifiedDeck(ArrayList<Card> mainboard, ArrayList<Card> sideboard, int deckIndex, String deckName); //EditDeckFragment gives back a modified deck to host activity

    void initCardImageCallback(int position, boolean mainboardCard); //called within NonLand class by initializeImage inorder to have the horizontal adapter show the card images

    void openDrawer(int gravity); //opens drawer based on the drawer's gravity

    void closeDrawer(int gravity); //closes drawer based on the drawer's gravity

    void lockOrUnlockdrawer(int lockmode, int gravity); //locks or unlocks drawer based on the drawer's gravity

}


/*
Todo:
- SavedDecksActivity save decks                                                                             DONE
- SavedDecksActivity on rotation removes everything (implement onSaveInstanceState())                       DONE
- Actually have images, (init images) - glide api?                                                          DONE
- crop card images                                                                                          DONE
- save images(bitmaps) somehow                                                                              DONE
- delete deck when last card is deleted + option to delete decks                                            DONE
- have deck names                                                                                           DONE
- saved decks needs to show both number and name of card                                                    DONE
- able to sort decks (cmc)                                                                                  DONE (add cards in cmc order)
- on saving new deck wipe create deck list                                                                  DONE
- handle attempting to add 2 decks with same name
- fix the inconcistency detetected bug. (passing by reference causes this, clone object)                    DONE
- ability to have sideboard                                                                                 DONE
- fix images not loading until ui is refreshed if trying to load too many                                   DONE
 */
