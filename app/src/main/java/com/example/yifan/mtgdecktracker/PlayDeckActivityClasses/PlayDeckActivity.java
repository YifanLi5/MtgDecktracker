package com.example.yifan.mtgdecktracker.playDeckActivityClasses;

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
import android.widget.Toast;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.Deck;
import com.example.yifan.mtgdecktracker.R;
import com.example.yifan.mtgdecktracker.savedDecksActivityClasses.SavedDecksActivity;
import com.example.yifan.mtgdecktracker.staticMethods.StaticUtilityMethodsAndConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
//lalalalalaalalalalaall

public class PlayDeckActivity extends AppCompatActivity implements PlayDeckActivityCommunicator{
    private static final String LOG_TAG = PlayDeckActivity.class.getSimpleName();
    private Deck playingDeck;
    private ArrayList<Card> playingCards;
    private RecyclerView mInDeckRV;
    private RecyclerView mNotInDeckRV;
    private PlayDeckContentsDataAdapter mInDeckAdapter;
    private PlayDeckContentsDataAdapter mNotInDeckAdapter;

    //used to package up proper intent to start this activity
    public static Intent getStartingIntent(Context context, Deck deck){
        Intent startIntent = new Intent(context, PlayDeckActivity.class);
        startIntent.putExtra(SavedDecksActivity.INTENT_KEY, (Parcelable) deck);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "playdeck activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_deck);

        Intent intent = getIntent();
        int index = intent.getIntExtra(SavedDecksActivity.INTENT_KEY, -1);
        if(index != -1){
            playingDeck = getSelectedDeck(index);
            Log.d(LOG_TAG, playingDeck.toString());
        }

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
                Toast.makeText(getApplicationContext(), "life counter WIP", Toast.LENGTH_SHORT).show();
                break;
            // TODO: 7/27/2016 change this icon to something better
            case R.id.extra_menu_icon:
                Toast.makeText(getApplicationContext(), "extra menu WIP", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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

        playingCards = playingDeck.getMainBoard(); //both adapters refer to the same arraylist, the adapter based on usage
        mInDeckAdapter = PlayDeckContentsDataAdapter.getInDeckAdapter(getApplicationContext(), playingCards, playingDeck.getTotalCardCount());
        mInDeckRV.setAdapter(mInDeckAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mInDeckRV.setLayoutManager(linearLayoutManager);

        mNotInDeckRV = (RecyclerView) findViewById(R.id.out_of_deck_recycler_view);
        mNotInDeckRV.setHasFixedSize(true);

        mNotInDeckAdapter = PlayDeckContentsDataAdapter.getOutOfDeckAdapter(getApplicationContext(), playingCards);
        mNotInDeckRV.setAdapter(mNotInDeckAdapter);
        mNotInDeckRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ItemTouchHelper inDeckHelper = new ItemTouchHelper(new InDeckCardSwipeCallback(this));
        ItemTouchHelper notInDeckHelper = new ItemTouchHelper(new NotInDeckCardSwipeCallback(this));
        inDeckHelper.attachToRecyclerView(mInDeckRV);
        notInDeckHelper.attachToRecyclerView(mNotInDeckRV);

        mInDeckAdapter.notifyDataSetChanged();
    }

    //used by a swipe listener to move cards from one recycler view to another, card objects are never removed from mInDeckRV rather the card object swiped modifies its inDeck and notInDeck variables.
    public void moveFromInDeckToOutOfDeck(int position){
        Card targetCard = playingCards.get(position);
        if(targetCard.moveOutOfDeck()){
            mInDeckAdapter.decrementTotalCardCount();
            mInDeckAdapter.notifyDataSetChanged();
            mNotInDeckAdapter.notifyDataSetChanged(); //the adapter knows to differentiate between creating a not in deck vs in deck card view
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

    //this activity recieves the index of the deck (as an intent extra) to play which is stored in the phones internal storage
    //this method retrieves that deck
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
