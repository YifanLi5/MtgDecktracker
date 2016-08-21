package com.example.yifan.mtgdecktracker.play_deck_classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.Deck;
import com.example.yifan.mtgdecktracker.R;
import com.example.yifan.mtgdecktracker.saved_deck_classes.SavedDecksActivity;
import com.example.yifan.mtgdecktracker.statics_and_constants.StaticUtilityMethodsAndConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import aligningrecyclerview.AligningRecyclerView;
import aligningrecyclerview.AlignmentManager;

public class PlayDeckActivity extends AppCompatActivity implements PlayDeckActivityCommunicator{
    private static final String LOG_TAG = PlayDeckActivity.class.getSimpleName();
    private Deck playingDeck;
    private ArrayList<Card> playingCards;
    private AligningRecyclerView mInDeckRV;
    private AligningRecyclerView mNotInDeckRV;

    //listeners used to synchronize scrolling of mInDeckRV and mNotInDeckRV
    private RecyclerView.OnScrollListener mInDeckOSL = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            Log.d(LOG_TAG, "scroll inDeck by: " + dx);
            if(dx != 0){ //starting the activity triggers this listener for some reason with dx of 0. This needs to be here to prevent mNotInDeckOSL from being removed initially. IDK why this happens.
                /*
                need to remove listener for other RV, otherwise triggering one scroll listener scrolls the other RV.
                Scrolling the other RV triggers the original scroll listener which then again scrolls the other RV.
                (similar to mutually recursive functions)
                 */
                mNotInDeckRV.removeOnScrollListener(mNotInDeckOSL);
                mNotInDeckRV.scrollBy(dx,0); //scroll the other RV
            }
            super.onScrolled(recyclerView, dx, dy);

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            /*
            when the RV stops scrolling we re-add the listener for other RV, preventing the listeners from chaining together scrolling.
             */
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                Log.d(LOG_TAG, "adding new mNotInDeckOSL");
                mNotInDeckRV.addOnScrollListener(mNotInDeckOSL);
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    private RecyclerView.OnScrollListener mNotInDeckOSL = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            Log.d(LOG_TAG, "scroll notInDeck by: " + dx);
            if(dx != 0){
                mInDeckRV.removeOnScrollListener(mInDeckOSL);
                mInDeckRV.scrollBy(dx,0);
            }
            super.onScrolled(recyclerView, dx, dy);

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                Log.d(LOG_TAG, "adding new mInDeckOSL");
                mInDeckRV.addOnScrollListener(mInDeckOSL);
            }
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    private PlayDeckContentsDataAdapter mInDeckAdapter;
    private PlayDeckContentsDataAdapter mNotInDeckAdapter;
    private TextView mCardsRemainingTV;

    private static final String PLAYING_DECK_KEY = "PlayingDeckKey";

    //used to package up proper intent to start this activity
    public static Intent getStartingIntent(Context context, int index){
        Intent startIntent = new Intent(context, PlayDeckActivity.class);
        startIntent.putExtra(SavedDecksActivity.INTENT_KEY, index);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "playdeck activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_deck);
        mCardsRemainingTV = (TextView) findViewById(R.id.cards_remaining);

        if(savedInstanceState != null){
            playingDeck = savedInstanceState.getParcelable(PLAYING_DECK_KEY);

        }
        else{
            Intent intent = getIntent();
            int index = intent.getIntExtra(SavedDecksActivity.INTENT_KEY, -1);
            if(index != -1){ //-1 means error, no index to look for deck
                playingDeck = getSelectedDeck(index);
                Log.d(LOG_TAG, playingDeck.toString());
            }
        }
        changeCardsRemaining(playingDeck.getTotalCardCount());
        toolbarSetup();
        recyclerViewSetup();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_deck_toolbar_menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.life_counter_icon:
                Log.d(LOG_TAG, "force scroll");
                mInDeckRV.scrollBy(10, 0);
                break;

            case R.id.extra_menu_icon:
                mInDeckRV.scrollBy(-10, 0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PLAYING_DECK_KEY, playingDeck);
    }

    private void toolbarSetup(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.play_deck_activity_toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            String deckName = "Playing Deck: ";
            if(playingDeck != null){
                deckName += playingDeck.getDeckName();
            }
            else{
                deckName += "no name";
            }
            getSupportActionBar().setTitle(deckName);
            toolbar.setTitleTextColor(0xFFFFFFFF);
        }
    }

    private void recyclerViewSetup(){
        mInDeckRV = (AligningRecyclerView) findViewById(R.id.in_deck_recycler_view);
        mInDeckRV.setHasFixedSize(true);

        playingCards = playingDeck.getMainBoard(); //both adapters refer to the same arraylist but display different attributes of the card objects
        mInDeckAdapter = PlayDeckContentsDataAdapter.getInDeckAdapter(this, playingCards, playingDeck.getTotalCardCount());
        mInDeckRV.setAdapter(mInDeckAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mInDeckRV.setLayoutManager(linearLayoutManager);

        mNotInDeckRV = (AligningRecyclerView) findViewById(R.id.out_of_deck_recycler_view);
        mNotInDeckRV.setHasFixedSize(true);

        mNotInDeckAdapter = PlayDeckContentsDataAdapter.getOutOfDeckAdapter(this, playingCards);
        mNotInDeckRV.setAdapter(mNotInDeckAdapter);
        mNotInDeckRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //setup for swiping to remove cards
        ItemTouchHelper inDeckHelper = new ItemTouchHelper(new InDeckCardSwipeCallback(this));
        ItemTouchHelper notInDeckHelper = new ItemTouchHelper(new NotInDeckCardSwipeCallback(this));
        inDeckHelper.attachToRecyclerView(mInDeckRV);
        notInDeckHelper.attachToRecyclerView(mNotInDeckRV);

        mInDeckRV.addOnScrollListener(mInDeckOSL);

        mNotInDeckRV.addOnScrollListener(mNotInDeckOSL);
    }


    //used by a swipe listener to move cards from one recycler view to another, card objects are never removed from mInDeckRV rather the card object swiped modifies its inDeck and notInDeck variables.
    public void moveFromInDeckToOutOfDeck(int position){
        Card targetCard = playingCards.get(position);
        if(targetCard.moveOutOfDeck()){
            mInDeckAdapter.decrementTotalCardCount();
            mInDeckAdapter.notifyDataSetChanged();
            mNotInDeckAdapter.notifyDataSetChanged();
        }
        else{ //this is so that if the user swipes a card that doesn't have any indeck, it isn't deleted
            mInDeckAdapter.notifyItemChanged(position);
        }

    }

    public void moveFromOutOfDeckToInDeck(int position){
        Card targetCard = playingCards.get(position);
        if(targetCard.moveIntoDeck()){
            mInDeckAdapter.incrementTotalCardCount();
            mInDeckAdapter.notifyDataSetChanged();
            mNotInDeckAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void changeCardsRemaining(int cardsRemaining) {
        String cardsRemainingString = "Cards Remaining: " + cardsRemaining;
        mCardsRemainingTV.setText(cardsRemainingString);
    }

    //retrieve the deck object from the ArrayList<Deck> stored on the device
    @SuppressWarnings("unchecked")
    private Deck getSelectedDeck(int index){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(StaticUtilityMethodsAndConstants.INTERNAL_STORAGE_FILENAME);
            ois = new ObjectInputStream(fis);
            ArrayList<Deck> decks = (ArrayList<Deck>) ois.readObject();
            return decks.get(index);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try{
                if(fis != null){
                    fis.close();
                }
                if(ois != null){
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //no saved decks
        return null;
    }


}