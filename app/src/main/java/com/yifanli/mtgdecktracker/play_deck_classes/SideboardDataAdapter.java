package com.yifanli.mtgdecktracker.play_deck_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifanli.mtgdecktracker.R;
import com.yifanli.mtgdecktracker.deck_data_classes.Card;

import java.util.ArrayList;

/**
 * Created by Yifan on 8/25/2016.
 * adapter used for mainboard and sideboard recyclerviews
 */
public class SideboardDataAdapter extends RecyclerView.Adapter<SideboardDataAdapter.SingleItem>{

    private static final String LOG_TAG = SideboardDataAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Card> cards;
    private int cardsInBoard;

    public SideboardDataAdapter(Context context, ArrayList<Card> cards, int cardsInBoard){
        this.mContext = context;
        this.cards = cards;
        this.cardsInBoard = cardsInBoard;
    }

    @Override
    public SingleItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sideboard_single_card, parent, false);
        return new SingleItem(v);
    }

    @Override
    public void onBindViewHolder(SingleItem holder, int position) {
        Card singleItem = cards.get(position);
        holder.setCardImage(singleItem.getCardImage());
        holder.setCardNameTVText(singleItem.getName());
        holder.setCardQuantityTVText(singleItem.getInDeck());
    }


    @Override
    public int getItemCount()  {
        return (cards != null ? cards.size() : 0);
    }

    public class SingleItem extends RecyclerView.ViewHolder{

        private ImageView cardImage;
        private TextView cardNameTV;
        private TextView cardQuantityTV;

        public SingleItem(View itemView) {
            super(itemView);
            this.cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            this.cardNameTV = (TextView) itemView.findViewById(R.id.card_name);
            this.cardQuantityTV = (TextView) itemView.findViewById(R.id.card_quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, cardNameTV.getText() + " clicked");
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
            String formattedQuantity = "x" + quantity;
            this.cardQuantityTV.setText(formattedQuantity);
        }
    }
}
