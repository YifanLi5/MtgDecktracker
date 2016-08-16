package com.example.yifan.mtgdecktracker.saved_deck_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;

/**
 * Adapter for the horizontal recycler views inside SavedDecksActivity. i.e: the RVs that show the individual cards
 */
public class DeckHorizontalContentsDataAdapter extends RecyclerView.Adapter<DeckHorizontalContentsDataAdapter.SingleItemRowHolder> {

    private ArrayList<Card> deckContents;
    private Context mContext;
    private boolean mainboardAdapter;

    public DeckHorizontalContentsDataAdapter(Context context, ArrayList<Card> contents, boolean mainboardAdapter) {
        this.deckContents = contents;
        this.mContext = context;
        this.mainboardAdapter = mainboardAdapter;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_decks_single_card, viewGroup, false);
        return new SingleItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        Card singleItem = deckContents.get(i);

        String cardDetails = singleItem.getName() + "\nx" + singleItem.getTotal();
        holder.cardNameTV.setText(cardDetails);
        holder.setCardImage(singleItem.getCardImage());
        holder.index = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return (null != deckContents ? deckContents.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView cardNameTV;
        protected ImageView cardImage;
        protected int index;

        public SingleItemRowHolder(View view) {
            super(view);
            this.cardNameTV = (TextView) view.findViewById(R.id.card_details);
            this.cardImage = (ImageView) view.findViewById(R.id.card_image);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof SavedDecksActivity){
                        Card clickedCard = deckContents.get(index);
                        ((SavedDecksActivity) mContext).respondToCardImageClick(clickedCard, index, mainboardAdapter, clickedCard.getCurrentEditionIndex());
                    }
                }
            });
        }

        public void setCardImage(Bitmap bm){
            this.cardImage.setImageBitmap(bm);
        }
    }

}