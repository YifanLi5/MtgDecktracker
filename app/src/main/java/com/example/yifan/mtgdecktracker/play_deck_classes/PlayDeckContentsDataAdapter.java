package com.example.yifan.mtgdecktracker.play_deck_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Yifan on 7/27/2016.
 * Adapter used for both the inDeck and notInDeck recyclerviews
 */
public class PlayDeckContentsDataAdapter extends RecyclerView.Adapter<PlayDeckContentsDataAdapter.SingleItem>{

    private static final String LOG_TAG = PlayDeckContentsDataAdapter.class.getSimpleName();
    private ArrayList<Card> cards;
    private Context mContext;
    private boolean usedForInDeck; //if adapter used for In Deck, then we need to show the draw probability in the cardProbabilityTV, otherwise don't show anything in that TV
    private int cardsRemainingInDeck;

    public static PlayDeckContentsDataAdapter getInDeckAdapter(Context context, ArrayList<Card> cards, int cardsRemainingInDeck){
        PlayDeckContentsDataAdapter instance = new PlayDeckContentsDataAdapter(context, cards, cardsRemainingInDeck);
        instance.usedForInDeck = true;
        return instance;
    }

    public static PlayDeckContentsDataAdapter getOutOfDeckAdapter(Context context, ArrayList<Card> cards){
        PlayDeckContentsDataAdapter instance = new PlayDeckContentsDataAdapter(context, cards, 0);
        instance.usedForInDeck = false;
        return instance;
    }

    private PlayDeckContentsDataAdapter(Context context, ArrayList<Card> cards, int cardsRemainingInDeck){
        this.mContext = context;
        this.cards = cards;
        this.cardsRemainingInDeck = cardsRemainingInDeck;
    }

    public void incrementTotalCardCount(){
        cardsRemainingInDeck++;
        if(mContext instanceof PlayDeckActivity){
            ((PlayDeckActivityCommunicator) mContext).changeCardsRemaining(cardsRemainingInDeck);
        }
    }

    public void decrementTotalCardCount(){
        if(cardsRemainingInDeck != 0){
            cardsRemainingInDeck--;
            if(mContext instanceof PlayDeckActivity){
                ((PlayDeckActivityCommunicator) mContext).changeCardsRemaining(cardsRemainingInDeck);
            }
        }
    }

    @Override
    public SingleItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_deck_single_card, parent, false);
        return new SingleItem(v);
    }

    @Override
    public void onBindViewHolder(SingleItem holder, int position) {
        Card singleItem = cards.get(position);
        holder.setCardImage(singleItem.getCardImage());
        holder.setCardNameTVText(singleItem.getName());

        if(usedForInDeck){
            double probability = ((double)singleItem.getInDeck() / cardsRemainingInDeck) * 100; //both num and denom are ints, need to cast one to make division return a double
            Log.d(LOG_TAG, "inDeck: " + singleItem.getInDeck() + "\ncardsRemainingInDeck: " + cardsRemainingInDeck + "\nprobability: " + String.format(Locale.ENGLISH, "%.2f", probability));

            holder.setCardQuantityTVText(singleItem.getInDeck());
            holder.setCardProbabilityTVText(String.format(Locale.ENGLISH, "%.2f", probability)); //draw % = amount in deck / total cards in deck
        }
        else{
            holder.setCardQuantityTVText(singleItem.getNotInDeck());
            //the out of deck arraylist is the same as the indeck arraylist, however if a card object has 0 out of deck, it is not shown in the out of deck recyclerview
            //this allows the same cards outside of deck to be matched beneath the cards inside of deck
            if(singleItem.getNotInDeck() == 0){
                holder.hideItem();
            }
            else if(singleItem.getNotInDeck() >= 1){
                holder.showItem();
            }

        }
    }

    @Override
    public int getItemCount() {
        return (cards != null ? cards.size() : 0);
    }

    public class SingleItem extends RecyclerView.ViewHolder{

        private ImageView cardImage;
        private TextView cardNameTV;
        private TextView cardQuantityTV;
        private TextView cardProbabilityTV;
        private View singleItemView;

        public SingleItem(View view) {
            super(view);
            this.cardImage = (ImageView) view.findViewById(R.id.card_image);
            this.cardNameTV = (TextView) view.findViewById(R.id.card_name);
            this.cardQuantityTV = (TextView) view.findViewById(R.id.card_quantity);
            this.cardProbabilityTV = (TextView) view.findViewById(R.id.card_draw_probability);
            this.singleItemView = view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void hideItem(){
            singleItemView.setVisibility(View.INVISIBLE);
        }
        public void showItem(){
            singleItemView.setVisibility(View.VISIBLE);
        }
        public void setCardImage(Bitmap bm){
            this.cardImage.setImageBitmap(bm);
        }
        public void setCardNameTVText(String cardName){
            this.cardNameTV.setText(cardName);
        }
        public void setCardQuantityTVText(int quantity){
            String formatedQuantity = "x" + quantity;
            this.cardQuantityTV.setText(formatedQuantity);
        }
        public void setCardProbabilityTVText(String probability){
            String formatedProbability = probability + "%";
            this.cardProbabilityTV.setText(formatedProbability);
        }
    }

}
