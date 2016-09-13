package com.yifanli.mtgdecktracker.play_deck_classes;

/**
 * Created by Yifan on 7/30/2016.
 * used for message relaying (callbacks) between fragments, cardTouchHelper and PlayDeckActivity
 */
public interface PlayDeckActivityCommunicator {
    void moveFromInDeckToOutOfDeck(int position);
    void moveFromOutOfDeckToInDeck(int position);
    void changeCardsRemaining(int cardsRemaining);
    void initCardImageCallback(int position);
}


/* TODO: 7/30/2016
- pass in location of cards in phone storage rather than pass images of cards (goes over Parcelable limit)                          DONE
- Make swipe able to move cards from inDeckRV to notInDeckRV and have statistics on the cards change (draw % and amount in deck)    DONE
- Have the cards in inDeckRV show correct quantity in deck, and draw probabilities                                                  DONE
- Have cards in notInDeckRV show quantites not in deck                                                                              DONE
- Make the indeck and outofdeck RVs scroll together                                                                                 DONE
- implement life counter fragment
- implement the more options menu to give the option to sideboard, restart and end game. functionality is seperate                  DONE
 */