package com.example.yifan.mtgdecktracker.saved_deck_classes;

import com.example.yifan.mtgdecktracker.Card;

import java.util.ArrayList;

/**
 * Created by Yifan on 6/16/2016.
 * Interface for SavedDeckActivity to manage fragments and have buttons inside adapter relay messages to SavedDeckActivity through callbacks
 */
public interface SavedDeckActivityCommunicator {
    void setCardCountCallback(int newCount, int position, boolean mainboardChange); //ModifyCardEntryFragment communicates EditDeckFragment through its host activity

    void respondToAdapterEditDeckButton(ArrayList<Card> mainboardContents, ArrayList<Card> sideboardContents, String deckName, int deckIndex); //allows host activity to respond to RecyclerView's button click

    void getModifiedDeck(ArrayList<Card> mainboard, ArrayList<Card> sideboard, int deckIndex, String deckName, int totalMainboardCardCount); //EditDeckFragment gives back a modified deck to host activity

    void initCardImageCallback(int position, boolean mainboardCard); //called within NonBasicLand class by initializeImage inorder to have the horizontal adapter show the card images

    void openDrawer(int gravity); //opens drawer based on the drawer's gravity

    void closeDrawer(int gravity); //closes drawer based on the drawer's gravity

    void lockOrUnlockdrawer(int lockmode, int gravity); //locks or unlocks drawer based on the drawer's gravity

    void respondToCardImageClick(Card selectedCard, int recyclerViewIndex, boolean mainboardCard, int startingEditionIndex);

    void respondToDeleteDeckRequest(int recyclerViewIndex);

    void startPlayDeckActivity(int index);

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
- able to sort cards in decks (cmc)                                                                         DONE (just add cards in cmc order)
- on saving new deck wipe create deck list                                                                  DONE
- handle attempting to add 2 decks with same name                                                           DONE (decks can have same name, fixed so that it doesn't matter anymore)
- fix the inconcistency detetected bug. (passing by reference causes this, clone object)                    DONE
- ability to have sideboard                                                                                 DONE
- fix images not loading until ui is refreshed if trying to load too many                                   DONE
- add confirmation before deleting deck in dropdown menu                                                    DONE
- when clicking on card image in SavedDecksActivity open a fragment showing a bigger image                  DONE
- inside the above mentioned fragment ^, give user ability to switch card image to one of different set     DONE
- have BigCardImageFragment implement onSaveInstanceState()
 */
