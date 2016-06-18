package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVerticalTest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yifan.mtgdecktracker.Card;
import com.example.yifan.mtgdecktracker.R;
import com.example.yifan.mtgdecktracker.StaticUtilityMethods;


public class ModifyCardEntryFragment extends Fragment {

    private static final String LOG_TAG = ModifyCardEntryFragment.class.getSimpleName();
    private View rootView;
    private TextView mCardName;
    private EditText mCardQuantity;
    private Button mRemoveCard;
    private Button mSave;
    private EditDeckCommunicator mFragmentCommunicator; //AddCardsToDeckFragment communicates with this Fragment thru the host activity, that host activity implements EditDeckCommunicator
    private int positionClicked; //This fragment should hold the position of the item clicked in the listview so if the item is deleted we know which one to delete (which index to remove from the arraylist). Lessens errors with passing it internally.
    private static ModifyCardEntryFragment singletonInstance;


    public ModifyCardEntryFragment() {
        // Required empty public constructor
        // this needs to be public b/c android backgrounds needs it to be, do not call default constructor in code use
    }

    //singleton design pattern to prevent multiple instances from being created
    public static ModifyCardEntryFragment newInstance(Card card, int positionClicked){
        singletonInstance = new ModifyCardEntryFragment();
        Bundle args = new Bundle();
        args.putString("CardName", card.getName());
        args.putString("CardQuantity", String.valueOf(card.getTotal()));
        args.putInt("PositionClicked", positionClicked);
        singletonInstance.setArguments(args);
        return singletonInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "creating fragment " + LOG_TAG);
        mFragmentCommunicator = (EditDeckCommunicator) getActivity();
        rootView = inflater.inflate(R.layout.fragment_modify_card_entry, container, false);
        textViewSetUp(getArguments().getString("CardName"), getArguments().getString("CardQuantity"));
        this.positionClicked = getArguments().getInt("PositionClicked");
        buttonsSetUp();
        return rootView;
    }

    private void buttonsSetUp(){
        mRemoveCard = (Button) rootView.findViewById(R.id.remove_card);
        mSave = (Button) rootView.findViewById(R.id.save);

        mRemoveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCommunicator.setCardCount(0, positionClicked);
                StaticUtilityMethods.hideKeyboardFrom(getContext(), rootView);
                closeThisFragment();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCardQuantity = mCardQuantity.getText().toString();
                try{
                    mFragmentCommunicator.setCardCount(Integer.parseInt(newCardQuantity), positionClicked);
                    StaticUtilityMethods.hideKeyboardFrom(getContext(), rootView);
                    closeThisFragment();
                }
                catch(NumberFormatException ex){
                    Toast.makeText(getContext(), "invalid number for quantity", Toast.LENGTH_SHORT);
                }


            }
        });
    }

    private void closeThisFragment(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this).addToBackStack(null).commit();
    }

    private void textViewSetUp(String cardName, String startingCardQuantity){
        mCardName = (TextView) rootView.findViewById(R.id.card_name);
        mCardQuantity = (EditText) rootView.findViewById(R.id.card_quantity);
        mCardName.setText(cardName);
        mCardQuantity.setText(startingCardQuantity);
    }

    public int getPositionClicked() {
        return positionClicked;
    }

}
