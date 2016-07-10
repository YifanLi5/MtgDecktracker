package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;

/**
 * Created by Yifan on 6/12/2016.
 */
public class DeckCreationAdapter extends ArrayAdapter<Card> {
    private static String LOG_TAG = DeckCreationAdapter.class.getSimpleName();
    NoFilter noFilter;

    public DeckCreationAdapter(Context context, int resource){
        super(context, resource);
    }

    public DeckCreationAdapter(Context context, ArrayList<Card> resource) {
        super(context, R.layout.card_info_in_listview, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCardView = inflater.inflate(R.layout.card_info_in_listview, parent, false);

        TextView cardNameText = (TextView) singleCardView.findViewById(R.id.card_name_inboard);
        TextView cardQuantityText = (TextView) singleCardView.findViewById(R.id.card_quantity_inboard);

        Card card = getItem(position);
        Log.d(LOG_TAG, "name: " + card.getName());
        Log.d(LOG_TAG, "total: " + card.getTotal());
        cardNameText.setText(card.getName());
        cardQuantityText.setText("x"+String.valueOf(card.getTotal()));//explicitly cast int (card.getTotal()) to String b/c setText looks for string resource based on id
        return singleCardView;
    }

    @Override
    public Filter getFilter() {
        if (noFilter == null) {
            noFilter = new NoFilter();
        }
        return noFilter;
    }

    private class NoFilter extends Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            return new FilterResults();
        }

        protected void publishResults(CharSequence constraint,
                                      Filter.FilterResults results) {
            // Do nothing
        }
    }
}
