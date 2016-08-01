package com.example.yifan.mtgdecktracker.SavedDecksActivityClasses;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.Deck;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;

//Vertical RecyclerAdapter for the Vertical rows of decks
public class DecksVerticalRecyclerAdapter extends RecyclerView.Adapter<DecksVerticalRecyclerAdapter.ItemRowHolder> {

    private ArrayList<Deck> savedDecks;
    private Context mContext;
    private DeckHorizontalContentsDataAdapter mainboardDataAdapter;
    private DeckHorizontalContentsDataAdapter sideboardDataAdapter;
    private static final String LOG_TAG = DecksVerticalRecyclerAdapter.class.getSimpleName();

    public DecksVerticalRecyclerAdapter(Context context, ArrayList<Deck> savedDecks) {
        this.savedDecks = savedDecks;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_item_layout, null);

        ItemRowHolder mh = new ItemRowHolder(v, i);
        return mh;
    }

    public DeckHorizontalContentsDataAdapter getMainboardDataAdapter() {
        return mainboardDataAdapter;
    }

    public DeckHorizontalContentsDataAdapter getSideboardDataAdapter() {
        return sideboardDataAdapter;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {

        final String deckName = savedDecks.get(i).getDeckName();

        ArrayList<Card> mainboard = savedDecks.get(i).getMainBoard();
        ArrayList<Card> sideboard = savedDecks.get(i).getSideBoard();

        itemRowHolder.itemTitle.setText(deckName);
        itemRowHolder.deckIndex = i;

        mainboardDataAdapter = new DeckHorizontalContentsDataAdapter(mContext, mainboard, true);
        sideboardDataAdapter = new DeckHorizontalContentsDataAdapter(mContext, sideboard, false);

        itemRowHolder.mainboard_recycler_view.setHasFixedSize(true);
        itemRowHolder.mainboard_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        itemRowHolder.mainboard_recycler_view.setAdapter(mainboardDataAdapter);

        itemRowHolder.sideboard_recycler_view.setHasFixedSize(true);
        itemRowHolder.sideboard_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        itemRowHolder.sideboard_recycler_view.setAdapter(sideboardDataAdapter);

        //set the listener for the dropdown menu to show the menu
        itemRowHolder.dropdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof SavedDecksActivity){
                    itemRowHolder.dropdownMenuSetUp(v);
                    itemRowHolder.dropdownMenu.show();

                    //set the listener for each of the componenets inside the dropdown menu
                    itemRowHolder.dropdownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.play_deck:
                                    Log.i(LOG_TAG, "click on playdeck, to be implemented");
                                    return true;
                                case R.id.edit_deck:
                                    ((SavedDecksActivity) mContext).respondToAdapterEditDeckButton(savedDecks.get(i).getMainBoard(), savedDecks.get(i).getSideBoard(), deckName, itemRowHolder.deckIndex);
                                    return true;

                                // TODO: 7/12/2016 move this nested listener outside, also have confirmation before deleting deck
                                case R.id.delete_deck:
                                    ((SavedDecksActivity) mContext).respondToDeleteDeckRequest(i);
                                    return true;
                            }
                            return false;
                        }
                    });

                }
            }
        });


    }

    public void deleteDeck(int i){

    }

    @Override
    public int getItemCount() {
        return (null != savedDecks ? savedDecks.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;
        protected RecyclerView mainboard_recycler_view;
        protected RecyclerView sideboard_recycler_view;
        protected ImageButton dropdownBtn;
        protected PopupMenu dropdownMenu;
        protected int deckIndex;

        public ItemRowHolder(View view, int deckIndex) {
            super(view);
            this.deckIndex = deckIndex;
            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.mainboard_recycler_view = (RecyclerView) view.findViewById(R.id.mainboard_recycler_view);
            this.sideboard_recycler_view = (RecyclerView) view.findViewById(R.id.sideboard_recycler_view);
            this.dropdownBtn = (ImageButton) view.findViewById(R.id.dropdown_menu_btn);

        }

        public RecyclerView getMainboard_recycler_view() {
            return mainboard_recycler_view;
        }

        private void dropdownMenuSetUp(View v) {
            dropdownMenu = new PopupMenu(mContext, v);
            dropdownMenu.inflate(R.menu.deck_dropdown_menu_items);
        }


    }

}