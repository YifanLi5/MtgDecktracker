package com.yifanli.mtgdecktracker.play_deck_classes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.yifanli.mtgdecktracker.R;
import com.yifanli.mtgdecktracker.deck_data_classes.*;

import java.util.ArrayList;

/**
fragment used for sideboarding
 */
public class SideboardFragment extends Fragment {
    private static final String DECK = "Deck";
    private Deck deck;
    private ArrayList<Card> mainboardCards;
    private ArrayList<Card> sideboardCards;
    private RecyclerView mainboardRV;
    private RecyclerView sideboardRV;
    private Button confirmBtn;
    private View rootView;

    public SideboardFragment() {
        // Required empty public constructor
    }

    public static SideboardFragment newInstance(Deck deck) {
        SideboardFragment fragment = new SideboardFragment();
        Bundle args = new Bundle();
        args.putParcelable(DECK, deck);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deck = getArguments().getParcelable(DECK);
        }
        this.mainboardCards = deck.getMainBoard();
        this.sideboardCards = deck.getSideBoard();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sideboard, container, false);
        confirmBtn = (Button) rootView.findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(SideboardFragment.this).commit();
            }
        });
        recyclerViewSetup();

        return rootView;
    }

    private void recyclerViewSetup(){
        mainboardRV = (RecyclerView) rootView.findViewById(R.id.mainboard_recycler_view);
        mainboardRV.setHasFixedSize(true);
        mainboardRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        sideboardRV = (RecyclerView) rootView.findViewById(R.id.sideboard_recycler_view);
        sideboardRV.setHasFixedSize(true);
        sideboardRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

    }


}
