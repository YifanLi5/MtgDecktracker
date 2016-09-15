package com.yifanli.mtgdecktracker.saved_deck_classes;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yifanli.mtgdecktracker.R;
import com.yifanli.mtgdecktracker.deck_data_classes.Card;
import com.yifanli.mtgdecktracker.deck_data_classes.Deck;

import java.util.ArrayList;

//Vertical RecyclerAdapter for the Vertical rows of decks
public class VerticalRVAdapterRewrite extends RecyclerView.Adapter<VerticalRVAdapterRewrite.DeckHolder> {

    private ArrayList<Deck> savedDecks;
    private Context mContext;
    private static final String LOG_TAG = VerticalRVAdapterRewrite.class.getSimpleName();

    public VerticalRVAdapterRewrite(Context context, ArrayList<Deck> savedDecks) {
        this.savedDecks = savedDecks;
        this.mContext = context;

    }

    @Override
    public DeckHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_item_layout, viewGroup, false);
        return new DeckHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final DeckHolder deckHolder, int position) {

        final String deckName = savedDecks.get(position).getDeckName();
        ArrayList<Card> mainboard = savedDecks.get(position).getMainBoard();
        ArrayList<Card> sideboard = savedDecks.get(position).getSideBoard();

        deckHolder.deckNameTV.setText(deckName);
        deckHolder.viewType = position;

        deckHolder.mainboardDataAdapter = new DeckHorizontalContentsDataAdapter(mContext, mainboard, true, deckHolder);
        deckHolder.sideboardDataAdapter = new DeckHorizontalContentsDataAdapter(mContext, sideboard, false, deckHolder);

        deckHolder.mainboard_recycler_view.setHasFixedSize(true);
        deckHolder.mainboard_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        deckHolder.mainboard_recycler_view.setAdapter(deckHolder.mainboardDataAdapter);

        deckHolder.sideboard_recycler_view.setHasFixedSize(true);
        deckHolder.sideboard_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        deckHolder.sideboard_recycler_view.setAdapter(deckHolder.sideboardDataAdapter);

        //set the listener for the dropdown menu to show the menu
        deckHolder.dropdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof SavedDecksActivity){
                    deckHolder.dropdownMenu = new PopupMenu(mContext, v);
                    deckHolder.dropdownMenu.inflate(R.menu.deck_dropdown_menu_items);
                    deckHolder.dropdownMenu.show();

                    //set the listener for each of the componenets inside the dropdown menu
                    deckHolder.dropdownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.play_deck:
                                    ((SavedDecksActivity) mContext).startPlayDeckActivity(deckHolder.getAdapterPosition());
                                    return true;
                                case R.id.edit_deck:
                                    ((SavedDecksActivity) mContext).respondToAdapterEditDeckButton(savedDecks.get(deckHolder.getAdapterPosition()).getMainBoard(), savedDecks.get(deckHolder.getAdapterPosition()).getSideBoard(), deckName, deckHolder.viewType);
                                    return true;
                                case R.id.delete_deck:
                                    ((SavedDecksActivity) mContext).respondToDeleteDeckRequest(deckHolder.getAdapterPosition());
                                    return true;
                            }
                            return false;
                        }
                    });

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != savedDecks ? savedDecks.size() : 0);
    }

    public class DeckHolder extends RecyclerView.ViewHolder {

        protected DeckHorizontalContentsDataAdapter mainboardDataAdapter;
        protected DeckHorizontalContentsDataAdapter sideboardDataAdapter;
        protected RecyclerView mainboard_recycler_view;
        protected RecyclerView sideboard_recycler_view;
        protected TextView deckNameTV;
        protected ImageButton dropdownBtn;
        protected PopupMenu dropdownMenu;
        protected int viewType;

        public DeckHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.deckNameTV = (TextView) view.findViewById(R.id.itemTitle);
            this.mainboard_recycler_view = (RecyclerView) view.findViewById(R.id.mainboard_recycler_view);
            this.sideboard_recycler_view = (RecyclerView) view.findViewById(R.id.sideboard_recycler_view);
            this.dropdownBtn = (ImageButton) view.findViewById(R.id.dropdown_menu_btn);
        }


    }

}