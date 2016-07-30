package com.example.yifan.mtgdecktracker.SavedDecksActivityClasses;

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
import com.example.yifan.mtgdecktracker.BasicLand;
import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.Edition;
import com.example.yifan.mtgdecktracker.NonBasicLand;
import com.example.yifan.mtgdecktracker.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BigCardImageFragment extends Fragment {

    private ImageButton mCloseButton;
    private Button mOkBtn;
    private ImageView mBigCardImageView;
    private Spinner mSetSpinner;
    private ArrayAdapter<String> mSetAdapter;
    private SavedDeckActivityCommunicator hostActivity;
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
            // TODO: 7/22/2016 allow screen rotation
        }
        else{
            Bundle args = getArguments();
            this.selectedCard = args.getParcelable(SELECTED_CARD);
            this.recyclerViewIndex = args.getInt(RECYCLER_VIEW_INDEX);
            this.startingEditionIndex = args.getInt(STARTING_EDITION_INDEX);
            this.mainboardCard = args.getBoolean(MAINBOARD_CARD);
        }

        hostActivity = (SavedDeckActivityCommunicator) getActivity();

        //set up setChoices
        setChoices = new ArrayList<>();
        imageURLs = new HashMap<>();
        ArrayList<Edition> editions = (selectedCard).getEditions();
        if(selectedCard instanceof NonBasicLand){

            Log.d(LOG_TAG, editions.toString());
            for(Edition editionItem: editions){
                setChoices.add(editionItem.getSet());
                imageURLs.put(editionItem.getSet(), editionItem.getImageURL());
            }
        }
        else if(selectedCard instanceof BasicLand){

            Log.d(LOG_TAG, editions.toString());
            //used to number basic lands from the same set. A set can have multiple new printings of a basic land. The algo below appends a number to the end of them.
            //ex) BFZ , BFZ, BFZ becomes BFZ 1, BFZ 2, BFZ 3...
            for(int i = 0; i < editions.size(); i++){
                //add in the first basic land of the set
                int matchingSetNameCount = 1;
                String currentSetName = editions.get(i).getSet();
                setChoices.add(currentSetName + " " + matchingSetNameCount);
                imageURLs.put(currentSetName + " " + matchingSetNameCount, editions.get(i).getImageURL());
                
                //check if not going out of bounds
                if(i + 1 < editions.size()){
                    String nextSetName = editions.get(i + 1).getSet();
                    //check if successive basic lands are reprinted in the same set and appends a number to them
                    while(currentSetName.equals(nextSetName)){
                        matchingSetNameCount++;
                        if(i + matchingSetNameCount < editions.size()){
                            nextSetName = nextSetName + " " + matchingSetNameCount;
                            setChoices.add(nextSetName);
                            imageURLs.put(nextSetName, editions.get(i + matchingSetNameCount).getImageURL());
                            nextSetName = editions.get(i + matchingSetNameCount).getSet();
                        }
                        else{
                            break;
                        }
                    }
                    //move up i to index into the next set, - 1 is because we initialize matchingSetNameCount initially to 1 to start numbering at 1.
                    i += matchingSetNameCount - 1;
                }
            }
            Log.d(LOG_TAG, imageURLs.toString());
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
                selectedCard.setCurrentEditionIndex(mSetSpinner.getSelectedItemPosition());
                hostActivity.initCardImageCallback(recyclerViewIndex, mainboardCard);
                getActivity().getSupportFragmentManager().beginTransaction().remove(BigCardImageFragment.this).commit();

            }
        });

    }

    private void spinnerSetUp(){
        //populate spinner choices
        mSetAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, setChoices);
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
                Log.i(LOG_TAG, imageURL);
                Log.d(LOG_TAG, imageURL);
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
