package com.example.yifan.mtgdecktracker.PlayDeckActivityClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.Deck;
import com.example.yifan.mtgdecktracker.R;
import com.example.yifan.mtgdecktracker.SavedDecksActivityClasses.SavedDecksActivity;

import java.util.ArrayList;

public class PlayDeckActivity extends AppCompatActivity implements PlayDeckActivityCommunicator{
    private static final String LOG_TAG = PlayDeckActivity.class.getSimpleName();
    private Deck playingDeck;
    private ArrayList<Card> mInDeckCards;
    private ArrayList<Card> mNotInDeckCards;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_deck);

        Intent intent = getIntent();
        playingDeck = intent.getParcelableExtra(SavedDecksActivity.INTENT_KEY);
        playingDeck = Deck.getCopy(playingDeck);


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
        mNotInDeckRV = (RecyclerView) findViewById(R.id.out_of_deck_recycler_view);
        mInDeckRV.setHasFixedSize(true);
        mNotInDeckRV.setHasFixedSize(true);

        mInDeckCards = playingDeck.getMainBoard();
        mNotInDeckCards = new ArrayList<>();
        mInDeckAdapter = PlayDeckContentsDataAdapter.getInDeckAdapter(getApplicationContext(), mInDeckCards, playingDeck.getTotalCardCount());
        mNotInDeckAdapter = PlayDeckContentsDataAdapter.getOutOfDeckAdapter(getApplicationContext(), mNotInDeckCards, playingDeck.getTotalCardCount());

        mInDeckRV.setAdapter(mInDeckAdapter);
        mNotInDeckRV.setAdapter(mNotInDeckAdapter);
        mInDeckRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mNotInDeckRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ItemTouchHelper inDeckHelper = new ItemTouchHelper(new InDeckCardSwipeCallback(this));
        ItemTouchHelper notInDeckHelper = new ItemTouchHelper(new NotInDeckCardSwipeCallback(this));
        inDeckHelper.attachToRecyclerView(mInDeckRV);
        notInDeckHelper.attachToRecyclerView(mNotInDeckRV);
    }

    //used by a swipe listener to move cards from one recycler view to another, card objects are never removed from mInDeckRV rather the card object swiped modifies its inDeck and notInDeck variables.
    public void moveFromInDeckToOutOfDeck(int position){
        Card targetCard = mInDeckCards.get(position);
        targetCard.moveOutOfDeck();
        //lists store references therefore can just add the reference into mNotInDeckCards list. Any changes to the card object is reflected in the card object referenced by mInDeckCards and mNotInDeckCards
        mNotInDeckCards.add(targetCard);
        mInDeckAdapter.notifyDataSetChanged();
        mNotInDeckAdapter.notifyDataSetChanged(); //the adapter knows to differentiate between creating a not in deck vs in deck card view
    }

    public void moveFromOutOfDeckToInDeck(int position){
        Card targetCard = mNotInDeckCards.get(position);
        targetCard.moveIntoDeck();
        mInDeckAdapter.notifyDataSetChanged();
        if(targetCard.getNotInDeck() == 0){ //remove not in deck cards if the card objects notInDeck variable is 0 indicating all cards of that name went back into the deck
            mNotInDeckAdapter.notifyItemRemoved(position);
        }
        else{
            mNotInDeckAdapter.notifyItemChanged(position);
        }

    }

}
