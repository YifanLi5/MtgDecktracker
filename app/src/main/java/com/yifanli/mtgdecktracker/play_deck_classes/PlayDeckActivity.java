package com.yifanli.mtgdecktracker.play_deck_classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yifanli.mtgdecktracker.R;
import com.yifanli.mtgdecktracker.deck_data_classes.Card;
import com.yifanli.mtgdecktracker.deck_data_classes.Deck;
import com.yifanli.mtgdecktracker.deck_data_classes.JsonSerialerDeSerializer;
import com.yifanli.mtgdecktracker.saved_deck_classes.SavedDecksActivity;
import com.yifanli.mtgdecktracker.statics_and_constants.StaticUtilityMethodsAndConstants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PlayDeckActivity extends AppCompatActivity implements PlayDeckActivityCommunicator{
    private static final String LOG_TAG = PlayDeckActivity.class.getSimpleName();
    private Deck playingDeck;
    private ArrayList<Card> playingCards;
    private RecyclerView mInDeckRV;
    private RecyclerView mNotInDeckRV;

    //listener used to synchronize scrolling of mInDeckRV and mNotInDeckRV
    private RecyclerView.OnScrollListener mInDeckOSL = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mNotInDeckRV.scrollBy(dx,0); //scroll the other RV
            super.onScrolled(recyclerView, dx, dy);

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
        mInDeckRV = (RecyclerView) findViewById(R.id.in_deck_recycler_view);
        mInDeckRV.setHasFixedSize(true);

        playingCards = playingDeck.getMainBoard(); //both adapters refer to the same arraylist but display different attributes of the card objects
        mInDeckAdapter = PlayDeckContentsDataAdapter.getInDeckAdapter(this, playingCards, playingDeck.getTotalCardCount());
        mInDeckRV.setAdapter(mInDeckAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mInDeckRV.setLayoutManager(linearLayoutManager);

        mNotInDeckRV = (RecyclerView) findViewById(R.id.out_of_deck_recycler_view);
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
        loadStoredImages();
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
        int difference = mInDeckRV.computeHorizontalScrollOffset() - mNotInDeckRV.computeHorizontalScrollOffset();
        mNotInDeckRV.smoothScrollBy(difference, 0);


    }

    public void moveFromOutOfDeckToInDeck(int position){
        Card targetCard = playingCards.get(position);
        if(targetCard.moveIntoDeck()){
            mInDeckAdapter.incrementTotalCardCount();
            mInDeckAdapter.notifyDataSetChanged();
            mNotInDeckAdapter.notifyItemChanged(position);
        }
        int difference = mInDeckRV.computeHorizontalScrollOffset() - mNotInDeckRV.computeHorizontalScrollOffset();
        mNotInDeckRV.smoothScrollBy(difference, 0);
    }

    @Override
    public void changeCardsRemaining(int cardsRemaining) {
        String cardsRemainingString = "Cards Remaining: " + cardsRemaining;
        mCardsRemainingTV.setText(cardsRemainingString);
    }

    @Override
    public void initCardImageCallback(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNotInDeckAdapter.notifyItemChanged(position);
                mInDeckAdapter.notifyItemChanged(position);
            }
        });


    }

    private void loadStoredImages(){

        for(int i = 0; i < playingCards.size(); i++){
            playingCards.get(i).initializeImage(PlayDeckActivity.this, true, i, 0);
        }
    }

    //retrieve the deck object from the ArrayList<Deck> stored on the device
    @SuppressWarnings("unchecked")
    private Deck getSelectedDeck(int index){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Card.class, new JsonSerialerDeSerializer());
        Gson gson = builder.create();
        Type savedDecksType = new TypeToken<ArrayList<Deck>>(){}.getType();
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        try{
            inputStream = openFileInput(StaticUtilityMethodsAndConstants.INTERNAL_STORAGE_FILENAME);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            ArrayList<Deck> decks = gson.fromJson(reader, savedDecksType);
            Deck selectedDeck = decks.get(index);
            selectedDeck.setInDeckQuantities();
            return selectedDeck;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "no file found");
        } catch (JsonSyntaxException e){
            e.printStackTrace();
            Log.e(LOG_TAG, "invalid json");
        }
        finally {
            try{
                if(inputStream != null){
                    inputStream.close();
                }
                if(reader != null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "error closing stream");
            }
        }
        return null;
    }
}
