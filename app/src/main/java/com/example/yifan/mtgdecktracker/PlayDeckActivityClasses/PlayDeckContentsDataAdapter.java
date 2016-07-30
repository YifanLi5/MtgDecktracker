package com.example.yifan.mtgdecktracker.PlayDeckActivityClasses;

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
 * Created by Yifan on 7/27/2016.
 */
public class PlayDeckContentsDataAdapter extends RecyclerView.Adapter<PlayDeckContentsDataAdapter.SingleItem>{

    private ArrayList<Card> cards;
    private Context mContext;
    private boolean usedForInDeck; //if adapter used for In Deck, then we need to show the draw probability in the cardProbabilityTV, otherwise don't show anything in that TV
    private int cardsRemainingInDeck;

    public static PlayDeckContentsDataAdapter getInDeckAdapter(Context context, ArrayList<Card> cards, int cardsRemainingInDeck){
        PlayDeckContentsDataAdapter instance = new PlayDeckContentsDataAdapter(context, cards, cardsRemainingInDeck);
        instance.usedForInDeck = true;
        return instance;
    }

    public static PlayDeckContentsDataAdapter getOutOfDeckAdapter(Context context, ArrayList<Card> cards, int cardsRemainingInDeck){
        PlayDeckContentsDataAdapter instance = new PlayDeckContentsDataAdapter(context, cards, cardsRemainingInDeck);
        instance.usedForInDeck = false;
        return instance;
    }

    private PlayDeckContentsDataAdapter(Context context, ArrayList<Card> cards, int cardsRemainingInDeck){
        this.mContext = context;
        this.cards = cards;
        this.cardsRemainingInDeck = cardsRemainingInDeck;
    }

    @Override
    public SingleItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_deck_single_card, null);
        return new SingleItem(v);
    }

    @Override
    public void onBindViewHolder(SingleItem holder, int position) {
        Card singleItem = cards.get(position);
        holder.setCardImage(singleItem.getCardImage());
        holder.setCardNameTVText(singleItem.getName());
        if(usedForInDeck){
            holder.setCardQuantityTVText(singleItem.getInDeck());
            holder.setCardProbabilityTVText(singleItem.getInDeck() / cardsRemainingInDeck); //draw % = amount in deck / total cards in deck
        }
        else{
            holder.setCardQuantityTVText(singleItem.getNotInDeck());
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class SingleItem extends RecyclerView.ViewHolder{

        private ImageView cardImage;
        private TextView cardNameTV;
        private TextView cardQuantityTV;
        private TextView cardProbabilityTV;
        protected int index;

        public SingleItem(View view) {
            super(view);
            this.cardImage = (ImageView) view.findViewById(R.id.card_image);
            this.cardNameTV = (TextView) view.findViewById(R.id.card_name);
            this.cardQuantityTV = (TextView) view.findViewById(R.id.card_quantity);
            this.cardProbabilityTV = (TextView) view.findViewById(R.id.card_draw_probability);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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
        public void setCardProbabilityTVText(double probability){
            String formatedProbability = probability + "%";
            this.cardProbabilityTV.setText(formatedProbability);
        }
    }

}
