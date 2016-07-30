package com.example.yifan.mtgdecktracker.PlayDeckActivityClasses;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Yifan on 7/30/2016.
 */
public class NotInDeckCardSwipeCallback extends ItemTouchHelper.SimpleCallback{
    private PlayDeckActivityCommunicator activity;

    public NotInDeckCardSwipeCallback(PlayDeckActivityCommunicator activity){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.UP); //set to respond to up swipes
        this.activity = activity;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //not used
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        activity.moveFromOutOfDeckToInDeck(viewHolder.getAdapterPosition());
    }
}
