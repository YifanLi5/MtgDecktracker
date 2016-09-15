package com.yifanli.mtgdecktracker.async_tasks;

import android.os.AsyncTask;

import com.yifanli.mtgdecktracker.deck_data_classes.Card;

/**
 * Created by Yifan on 9/14/2016.
 */
public class CompressImageTask extends AsyncTask<Card, Void, Void> {
    @Override
    protected Void doInBackground(Card... params) {
        Card targetCard = params[0];
        targetCard.compressImageToByteArray();
        return null;
    }
}
