package com.example.yifan.mtgdecktracker;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

/**
 * Created by Yifan on 5/27/2016.
 * extention of ArrayAdapter to get rid of its filter. The filter gets rid of results added by ArrayAdapter.add() method sometimes so they are not shown
 */
public class ArrayAdapterNoFilter extends ArrayAdapter<String> {

    NoFilter noFilter;
    public ArrayAdapterNoFilter(Context context, int resource){
        super(context, resource);
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
