package com.example.yifan.mtgdecktracker.saved_deck_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;

/**
 * Created by Yifan on 6/12/2016.
 * adapter used for the EditDeckFragment's listviews
 */
public class DeckCreationAdapter extends ArrayAdapter<Card> {
    private static String LOG_TAG = DeckCreationAdapter.class.getSimpleName();

    public DeckCreationAdapter(Context context, int resource) {
        super(context, resource);
    }

    public DeckCreationAdapter(Context context, ArrayList<Card> resource) {
        super(context, R.layout.card_info_in_listview, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardInfoHolder cardInfoHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_info_in_listview, parent, false);
            cardInfoHolder = new CardInfoHolder(convertView);
            convertView.setTag(cardInfoHolder);
        }
        else{
            cardInfoHolder = (CardInfoHolder) convertView.getTag();
        }

        Card card = getItem(position);
        cardInfoHolder.cardNameTV.setText(card.getName());
        String cardQuantityString = "x" + String.valueOf(card.getTotal());
        cardInfoHolder.cardQuantityTV.setText(cardQuantityString);

        return convertView;
    }

    static class CardInfoHolder {
        TextView cardNameTV;
        TextView cardQuantityTV;

        public CardInfoHolder(View view){
            cardNameTV = (TextView) view.findViewById(R.id.card_name_inboard);
            cardQuantityTV = (TextView) view.findViewById(R.id.card_quantity_inboard);
        }
    }
}
