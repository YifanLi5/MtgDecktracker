package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.NonLand;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;
import java.util.HashMap;

//todo: find good way to set which edition was selected
public class BigCardImageFragment extends Fragment {

    private ImageButton mCloseButton;
    private Button mOkBtn;
    private ImageView mBigCardImageView;
    private Spinner mSetSpinner;
    private ArrayAdapter<String> mSetAdapter;
    private FragmentActivityAdapterCommunicator hostActivity;
    private View rootView;
    private Card selectedCard;
    private int recyclerViewIndex;
    private boolean mainboardCard;
    private int startingEditionIndex;
    private String currentSet;
    private ArrayList<String> setChoices; //used to populate the spinner
    private HashMap<String, String> imageURLs; //key = setChoices items, value = the imageURL associated with the set. Easy to retrieve image url from setChoice

    private static final String LOG_TAG = BigCardImageFragment.class.getSimpleName();
    private static final String SELECTED_CARD = "SelectedCard";
    private static final String RECYCLER_VIEW_INDEX = "RecyclerViewIndex";
    private static final String STARTING_EDITION_INDEX = "StartingEditionIndex";
    private static final String MAINBOARD_CARD = "MainboardCard";

    public BigCardImageFragment() {
        // Required empty public constructor
    }

    public static BigCardImageFragment getInstance(Card selectedCard, int recyclerViewIndex, boolean mainboardCard, int startingEditionIndex){
        BigCardImageFragment newInstance = new BigCardImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(SELECTED_CARD, selectedCard);
        args.putInt(RECYCLER_VIEW_INDEX, recyclerViewIndex);
        args.putInt(STARTING_EDITION_INDEX, startingEditionIndex);
        args.putBoolean(MAINBOARD_CARD, mainboardCard);
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_big_card_image, container, false);
        mBigCardImageView = (ImageView) rootView.findViewById(R.id.big_card_imageview);
        if(savedInstanceState != null){
            /*this.selectedCard = (Card) savedInstanceState.get(SELECTED_CARD);
            this.recyclerViewIndex = savedInstanceState.getInt(RECYCLER_VIEW_INDEX);
            this.mainboardCard = savedInstanceState.getBoolean(MAINBOARD_CARD);
            this.startingEditionIndex = savedInstanceState.getInt(STARTING_EDITION_INDEX);
            */
        }
        else{
            Bundle args = getArguments();
            this.selectedCard = args.getParcelable(SELECTED_CARD);
            this.recyclerViewIndex = args.getInt(RECYCLER_VIEW_INDEX);
            this.startingEditionIndex = args.getInt(STARTING_EDITION_INDEX);
            this.mainboardCard = args.getBoolean(MAINBOARD_CARD);
        }

        hostActivity = (FragmentActivityAdapterCommunicator) getActivity();

        //set up setChoices
        setChoices = new ArrayList<>();
        imageURLs = new HashMap<>();
        if(selectedCard instanceof NonLand){
            ArrayList<NonLand.Edition> editions = ((NonLand) selectedCard).getEditions();
            Log.d(LOG_TAG, editions.toString());
            for(NonLand.Edition editionItem: editions){
                setChoices.add(editionItem.getSet());
                imageURLs.put(editionItem.getSet(), editionItem.getImageURL());
            }

        }


        buttonSetUp();
        spinnerSetUp();
        return rootView;
    }

    private void buttonSetUp(){
        mCloseButton = (ImageButton) rootView.findViewById(R.id.close_btn);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(BigCardImageFragment.this).commit();
            }
        });

        mOkBtn = (Button) rootView.findViewById(R.id.ok_btn);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBigCardImageView.buildDrawingCache();
                selectedCard.setCardImage(Bitmap.createBitmap(mBigCardImageView.getDrawingCache()));
                ((NonLand) selectedCard).setCurrentEditionIndex(mSetSpinner.getSelectedItemPosition());
                hostActivity.initCardImageCallback(recyclerViewIndex, mainboardCard);

                getActivity().getSupportFragmentManager().beginTransaction().remove(BigCardImageFragment.this).commit();

            }
        });

    }

    private void spinnerSetUp(){
        //populate spinner choices
        mSetAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, setChoices);
        mSetSpinner = (Spinner) rootView.findViewById(R.id.set_spinner);
        mSetSpinner.setAdapter(mSetAdapter);
        //set initial choice to what the initial image correlates to
        mSetSpinner.setSelection(startingEditionIndex);
        currentSet = setChoices.get(startingEditionIndex);
        //set up click listener for spinner choices to change image to the correlating set

        mSetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSet = (String) parent.getItemAtPosition(position);
                String imageURL = imageURLs.get(currentSet);
                Glide.with(BigCardImageFragment.this)
                        .load(imageURL)
                        .into(mBigCardImageView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


}
