package com.example.yifan.mtgdecktracker.savedDecksActivityClasses;

import android.content.pm.ActivityInfo;
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

    private static final String LOG_TAG = BigCardImageFragment.class.getSimpleName();
    private ImageView mBigCardImageView;
    private Spinner mSetSpinner;
    private SavedDeckActivityCommunicator hostActivity;
    private View rootView;
    private Card selectedCard;
    private int recyclerViewIndex;
    private boolean isMainboardCard;
    private int currentEditionIndex;

    private ArrayList<String> mtgSetChoices; //used to populate the spinner
    private HashMap<String, String> imageURLMap; //key = mtgSetChoices items, value = the imageURL associated with the set. Easy to retrieve image url from setChoice

    //keys for initialization
    private static final String SELECTED_CARD = "SelectedCard";
    private static final String RECYCLER_VIEW_INDEX = "RecyclerViewIndex";
    private static final String STARTING_EDITION_INDEX = "StartingEditionIndex";
    private static final String IS_MAINBOARD_CARD = "IsMainboardCard";

    //keys for onSaveInstanceState
    private static final String MTG_SET_CHOICES = "MtgSetChoices";
    private static final String IMAGE_URL_MAP = "ImageUrlMap";
    private static final String CURRENT_EDITION_INDEX = "CurrentEditionIndex";
    private static final String CARD_IMAGE = "CardImage";

    public BigCardImageFragment() {
        // Required empty public constructor
    }

    public static BigCardImageFragment getInstance(Card selectedCard, int recyclerViewIndex, boolean mainboardCard, int startingEditionIndex){
        BigCardImageFragment newInstance = new BigCardImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(SELECTED_CARD, selectedCard);
        args.putInt(RECYCLER_VIEW_INDEX, recyclerViewIndex);
        args.putInt(STARTING_EDITION_INDEX, startingEditionIndex);
        args.putBoolean(IS_MAINBOARD_CARD, mainboardCard);
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_big_card_image, container, false);
        mBigCardImageView = (ImageView) rootView.findViewById(R.id.big_card_imageview);
        hostActivity = (SavedDeckActivityCommunicator) getActivity();
        if(savedInstanceState != null){
            this.mtgSetChoices = savedInstanceState.getStringArrayList(MTG_SET_CHOICES);
            this.imageURLMap = (HashMap<String, String>) savedInstanceState.getSerializable(IMAGE_URL_MAP);
            this.currentEditionIndex = savedInstanceState.getInt(CURRENT_EDITION_INDEX);
            mBigCardImageView.setImageBitmap((Bitmap) savedInstanceState.getParcelable(CARD_IMAGE));
            this.selectedCard = savedInstanceState.getParcelable(SELECTED_CARD);
            this.isMainboardCard = savedInstanceState.getBoolean(IS_MAINBOARD_CARD);
        }
        else{
            Bundle args = getArguments();
            this.selectedCard = args.getParcelable(SELECTED_CARD);
            this.recyclerViewIndex = args.getInt(RECYCLER_VIEW_INDEX);
            this.currentEditionIndex = args.getInt(STARTING_EDITION_INDEX);
            this.isMainboardCard = args.getBoolean(IS_MAINBOARD_CARD);

            //set up mtgSetChoices
            mtgSetChoices = new ArrayList<>();
            imageURLMap = new HashMap<>();
            ArrayList<Edition> editions = (selectedCard).getEditions();
            if(selectedCard instanceof NonBasicLand){
                Log.d(LOG_TAG, editions.toString());
                for(Edition editionItem: editions){
                    mtgSetChoices.add(editionItem.getSet());
                    imageURLMap.put(editionItem.getSet(), editionItem.getImageURL());
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
                    String modifiedSetName = currentSetName + " " + matchingSetNameCount;
                    mtgSetChoices.add(modifiedSetName);
                    imageURLMap.put(modifiedSetName, editions.get(i).getImageURL());
                    //check if not going out of bounds
                    if(i + 1 < editions.size()){
                        String nextSetName = editions.get(i + 1).getSet();
                        //check if successive basic lands are reprinted in the same set and appends a number to them
                        while(currentSetName.equals(nextSetName)){
                            matchingSetNameCount++;
                            if(i + matchingSetNameCount < editions.size()){
                                nextSetName = nextSetName + " " + matchingSetNameCount;
                                mtgSetChoices.add(nextSetName);
                                imageURLMap.put(nextSetName, editions.get(i + matchingSetNameCount).getImageURL());
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
                Log.d(LOG_TAG, imageURLMap.toString());
            }
        }

        buttonSetUp();
        spinnerSetUp();
        return rootView;
    }

    //disable rotation for this fragment.
    @Override
    public void onResume() {
        super.onResume();
        if(hostActivity != null){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(hostActivity != null){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(MTG_SET_CHOICES, mtgSetChoices);
        outState.putSerializable(IMAGE_URL_MAP, imageURLMap);
        outState.putInt(CURRENT_EDITION_INDEX, currentEditionIndex);
        outState.putParcelable(CARD_IMAGE, mBigCardImageView.getDrawingCache());
        outState.putParcelable(SELECTED_CARD, selectedCard);
        outState.putBoolean(IS_MAINBOARD_CARD, isMainboardCard);
    }

    private void buttonSetUp(){
        ImageButton mCloseButton = (ImageButton) rootView.findViewById(R.id.close_btn);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(BigCardImageFragment.this).commit();
            }
        });

        Button mOkBtn = (Button) rootView.findViewById(R.id.ok_btn);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBigCardImageView.buildDrawingCache();
                selectedCard.setCardImage(Bitmap.createBitmap(mBigCardImageView.getDrawingCache()));
                selectedCard.setCurrentEditionIndex(mSetSpinner.getSelectedItemPosition());
                hostActivity.initCardImageCallback(recyclerViewIndex, isMainboardCard);
                getActivity().getSupportFragmentManager().beginTransaction().remove(BigCardImageFragment.this).commit();

            }
        });

    }

    private void spinnerSetUp(){
        //populate spinner choices
        ArrayAdapter<String> mSetAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mtgSetChoices);
        mSetSpinner = (Spinner) rootView.findViewById(R.id.set_spinner);
        mSetSpinner.setAdapter(mSetAdapter);
        //set initial choice to what the initial image correlates to
        mSetSpinner.setSelection(currentEditionIndex);
        //set up click listener for spinner choices to change image to the correlating set
        mSetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { //listener will only be called if the item selected is different, therefore do not need to manually check if selecting different item
                currentEditionIndex = position;
                String currentSet = (String) parent.getItemAtPosition(position);
                Log.i(LOG_TAG, "loading image from set: " + currentSet);
                String imageURL = imageURLMap.get(currentSet);
                Glide.with(BigCardImageFragment.this)
                        .load(imageURL)
                        .into(mBigCardImageView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not used
            }
        });
    }


}
