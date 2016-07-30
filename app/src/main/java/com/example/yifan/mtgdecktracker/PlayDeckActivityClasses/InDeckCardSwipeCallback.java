package com.example.yifan.mtgdecktracker.PlayDeckActivityClasses;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Yifan on 7/30/2016.
 */
public class InDeckCardSwipeCallback extends ItemTouchHelper.SimpleCallback{
    private PlayDeckActivityCommunicator activity;

    public InDeckCardSwipeCallback(PlayDeckActivityCommunicator activity){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.DOWN); //set to respond to down swipes
        this.activity = activity;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //not used
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        activity.moveFromInDeckToOutOfDeck(viewHolder.getAdapterPosition());
    }
}
