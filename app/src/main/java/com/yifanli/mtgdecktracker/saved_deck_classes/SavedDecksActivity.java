package com.yifanli.mtgdecktracker.saved_deck_classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yifanli.mtgdecktracker.R;
import com.yifanli.mtgdecktracker.deck_data_classes.Card;
import com.yifanli.mtgdecktracker.deck_data_classes.Deck;
import com.yifanli.mtgdecktracker.deck_data_classes.JsonSerialerDeSerializer;
import com.yifanli.mtgdecktracker.play_deck_classes.PlayDeckActivity;
import com.yifanli.mtgdecktracker.statics_and_constants.StaticUtilityMethodsAndConstants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavedDecksActivity extends AppCompatActivity implements SavedDeckActivityCommunicator, ConfirmResetDialogFragment.ConfirmResetDialogCallbacks {

    private static final String SAVED_INSTANCE_STATED_DECKS = "SavedInstanceStateDecks";
    private static final String LOG_TAG = SavedDecksActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ArrayList<Deck> savedDecks;
    private EditDeckFragment mEditDeckFragment = null;
    private DecksVerticalRecyclerAdapter verticalRecyclerAdapter;
    private RecyclerView mDecksRecycler;
    private BigCardImageFragment bigCardImageFragment;
    private int recyclerViewDeletionIndex;

    public static final String INTENT_KEY = "IntentKey";

    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_decks);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.saved_deck_activity_toolbar);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT); //when the left drawer (EditDeckFragment) is closed the right drawer (ModifyCardEntryFragment) also needs to close as the right operates on the left

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        if(savedInstanceState != null){
            Log.i(LOG_TAG, "restore savedDecks from savedInstanceState");
            savedDecks = savedInstanceState.getParcelableArrayList(SAVED_INSTANCE_STATED_DECKS);
            Log.d(LOG_TAG, savedDecks.toString());
        }
        else{
            Log.i(LOG_TAG, "attempt to restore savedDecks from memory");
            try{
                savedDecks = loadSavedDecks();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Saved Decks");
            toolbar.setTitleTextColor(0xFFFFFFFF);
        }

        mDecksRecycler = (RecyclerView) findViewById(R.id.decks_recycler_view);
        mDecksRecycler.setHasFixedSize(true);
        verticalRecyclerAdapter = new DecksVerticalRecyclerAdapter(this, savedDecks);
        mDecksRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDecksRecycler.setAdapter(verticalRecyclerAdapter);
        mDecksRecycler.addItemDecoration(new VerticalItemDecoration(this, R.drawable.recycler_view_divider));
        loadStoredImages();

    }

    //divider (gray line) for the vertical recycler view (containing decks)
    private class VerticalItemDecoration extends RecyclerView.ItemDecoration {
        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable mDivider;

        public VerticalItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //create the fragment initially so I don't need to have check whether I need to add or replace in fragment manager
        //handle properly resuming activity, i.e check if the fragment has been previously created so not to attempt to create duplicate and crash
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.edit_deck_fragment_container);

        if(frag == null || !(frag instanceof EditDeckFragment)){
            mEditDeckFragment = EditDeckFragment.getInstance("InitialPlaceHolderInstance", -1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.edit_deck_fragment_container, mEditDeckFragment)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "attempt save");
        for(Deck deck: savedDecks){
            deck.compressCardsInDeck();
        }
        saveDeckData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_INSTANCE_STATED_DECKS, savedDecks);
    }

    @Override
    public void setCardCountCallback(int newCount, int position, boolean mainboardChange) {
        if(mainboardChange){
            mEditDeckFragment.changeMainboardCardQuantityCallback(newCount, position);
        }
        else{
            mEditDeckFragment.changeSideboardCardQuantityCallback(newCount, position);
        }

    }

    @Override
    public void respondToAdapterEditDeckButton(ArrayList<Card> mainboardContents, ArrayList<Card> sideboardContents, String deckName, int deckIndex) {
        mEditDeckFragment = EditDeckFragment.getInstance(mainboardContents, sideboardContents, deckName, deckIndex);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.edit_deck_fragment_container, mEditDeckFragment)
                .commit();
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getModifiedDeck(ArrayList<Card> mainboard, ArrayList<Card> sideboard, int deckIndex, String deckName, int totalMainboardCardCount) {
        //create seperate objects to prevent undesired changes from hanging references
        ArrayList<Card> mainboardCopy = (ArrayList<Card>) mainboard.clone();
        ArrayList<Card> sideboardCopy = (ArrayList<Card>) sideboard.clone();

        if(mainboard.isEmpty() && sideboard.isEmpty()){
            savedDecks.remove(deckIndex);

        }
        else if(deckIndex == -1){ //making new deck, add to end
            Deck newDeck = new Deck(mainboardCopy, sideboardCopy, deckName);
            newDeck.setTotalCardCount(totalMainboardCardCount);
            savedDecks.add(newDeck);

        }
        else{ //editing existing deck, change by completing replacing
            Deck newDeck = new Deck(mainboardCopy, sideboardCopy, deckName);
            newDeck.setTotalCardCount(totalMainboardCardCount);
            savedDecks.set(deckIndex, newDeck);
        }
        Log.d(LOG_TAG, savedDecks.toString());
        verticalRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public void initCardImageCallback(final int position, final boolean mainboardCard) { //after the image is ready this is called from the Nonland class or changed in the BigCardImageFragment
        runOnUiThread(new Runnable() { //this method does a ui update therefore runs ought to on ui thread
            @Override
            public void run() {
                if(mainboardCard){
                    DeckHorizontalContentsDataAdapter mainboardRecyclerViewAdapter = verticalRecyclerAdapter.getMainboardDataAdapter();
                    mainboardRecyclerViewAdapter.notifyItemChanged(position);
                }
                else{
                    DeckHorizontalContentsDataAdapter sideboardRecyclerViewAdapter = verticalRecyclerAdapter.getSideboardDataAdapter();
                    sideboardRecyclerViewAdapter.notifyItemChanged(position);
                }
            }
        });

    }

    @Override
    public void openDrawer(int gravity) {
        mDrawerLayout.openDrawer(gravity);
    }

    @Override
    public void closeDrawer(int gravity) {
        mDrawerLayout.closeDrawer(gravity);
    }

    @Override
    public void lockOrUnlockdrawer(int lockmode, int gravity) {
        mDrawerLayout.setDrawerLockMode(lockmode, gravity);
    }

    @Override
    public void respondToCardImageClick(Card selectedCard, int recyclerViewIndex, boolean mainboardCard, int startingEditionIndex) {
        bigCardImageFragment = BigCardImageFragment.getInstance(selectedCard, recyclerViewIndex, mainboardCard, startingEditionIndex);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.big_card_image_fragment_container, bigCardImageFragment)
                .commit();
    }

    @Override
    public void respondToDeleteDeckRequest(int recyclerViewIndex) {
        recyclerViewDeletionIndex = recyclerViewIndex;
        ConfirmResetDialogFragment dialog = ConfirmResetDialogFragment.getInstanceForActivity("Confirm delete deck?");
        dialog.show(getSupportFragmentManager(), "FromActivity");
    }

    @Override
    public void startPlayDeckActivity(int index) {
        Intent playDeckActivityIntent = PlayDeckActivity.getStartingIntent(getApplicationContext(), index);
        startActivity(playDeckActivityIntent);
    }

    //add in button for creating new deck
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.saved_decks_toolbar_menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_new_deck_icon:
                mEditDeckFragment = EditDeckFragment.getInstance("New Deck ", savedDecks.size() + 1);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.edit_deck_fragment_container, mEditDeckFragment)
                        .commit();

                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //used for delete deck confirmation
    @Override
    public void onPositiveClick(DialogFragment dialog) {
        savedDecks.remove(recyclerViewDeletionIndex);
        verticalRecyclerAdapter.notifyItemRemoved(recyclerViewDeletionIndex);
    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {
        //do nothing
    }

    private void saveDeckData(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Card.class, new JsonSerialerDeSerializer());
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type savedDecksType = new TypeToken<ArrayList<Deck>>(){}.getType();
        String gsonStr = gson.toJson(savedDecks, savedDecksType);
        Log.i("gson debug", "what is saved\n" + gsonStr);
        FileOutputStream outputStream = null;
        try{
            outputStream = openFileOutput(StaticUtilityMethodsAndConstants.INTERNAL_STORAGE_FILENAME, MODE_PRIVATE);
            outputStream.write(gsonStr.getBytes());
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "no file?");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "unable to convert gsonStr to bytes");
            e.printStackTrace();
        }
        finally {
            try{
                if(outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @SuppressWarnings("unchecked")
    private ArrayList<Deck> loadSavedDecks() {
        //get the gson string file
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(Card.class, new JsonSerialerDeSerializer());
        Gson gson = builder.create();
        Type savedDecksType = new TypeToken<ArrayList<Deck>>(){}.getType();
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        try{
            inputStream = openFileInput(StaticUtilityMethodsAndConstants.INTERNAL_STORAGE_FILENAME);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            return gson.fromJson(reader, savedDecksType);
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
        Log.i(LOG_TAG, "no deck loaded");
        return new ArrayList<>();
    }

    private void loadStoredImages(){
        for(Deck deck: savedDecks){
            ArrayList<Card> cards = deck.getMainBoard();
            for(int i = 0; i < cards.size(); i++){
                cards.get(i).initializeImage(getApplicationContext(), true, i);
            }
            cards = deck.getSideBoard();
            for(int i = 0; i < cards.size(); i++){
                cards.get(i).initializeImage(getApplicationContext(), false, i);
            }
        }
    }
}