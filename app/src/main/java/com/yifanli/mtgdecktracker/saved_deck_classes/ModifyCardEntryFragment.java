package com.yifanli.mtgdecktracker.saved_deck_classes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yifanli.mtgdecktracker.R;
import com.yifanli.mtgdecktracker.deck_data_classes.Card;
import com.yifanli.mtgdecktracker.statics_and_constants.StaticUtilityMethodsAndConstants;


public class ModifyCardEntryFragment extends Fragment {

    private static final String LOG_TAG = ModifyCardEntryFragment.class.getSimpleName();
    private View rootView;
    private EditText mCardQuantity;
    private SavedDeckActivityCommunicator hostActivity; //EditDeckFragment communicates with this Fragment thru the host activity, that host activity implements SavedDeckActivityCommunicator
    private int positionClicked; //This fragment should hold the position of the item clicked in the listview so if the item is deleted we know which one to delete (which index to remove from the arraylist). Lessens errors with passing it internally.

    private static final String CARD_NAME = "CardName";
    private static final String CARD_QUANTITY = "CardQuantity";
    private static final String POSITION_CLICKED = "PositionClicked";
    private static final String MAINBOARD_CHANGE = "MainboardChange";

    public ModifyCardEntryFragment() {
        // Required empty public constructor even though using singleton design
        // this needs to be public b/c android backgrounds needs it to be
    }

    //singleton design pattern to prevent multiple instances from being created
    public static ModifyCardEntryFragment newInstance(Card card, int positionClicked, boolean mainboardChange){
        ModifyCardEntryFragment singletonInstance = new ModifyCardEntryFragment();
        Bundle args = new Bundle();
        args.putString(CARD_NAME, card.getName());
        args.putString(CARD_QUANTITY, String.valueOf(card.getTotal()));
        args.putInt(POSITION_CLICKED, positionClicked);
        args.putBoolean(MAINBOARD_CHANGE, mainboardChange);
        singletonInstance.setArguments(args);
        return singletonInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hostActivity = (SavedDeckActivityCommunicator) getActivity();
        hostActivity.lockOrUnlockdrawer(DrawerLayout.LOCK_MODE_LOCKED_OPEN, Gravity.END);
        rootView = inflater.inflate(R.layout.fragment_modify_card_entry, container, false);
        textViewSetUp(getArguments().getString(CARD_NAME), getArguments().getString(CARD_QUANTITY));
        this.positionClicked = getArguments().getInt(POSITION_CLICKED);
        buttonsSetUp();
        return rootView;
    }

    private void buttonsSetUp(){
        Button mRemoveBtn = (Button) rootView.findViewById(R.id.remove_card);
        Button mConfirmBtn = (Button) rootView.findViewById(R.id.confirm_btn);
        ImageButton mCloseButton = (ImageButton) rootView.findViewById(R.id.close_btn);

        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostActivity.setCardCountCallback(0, positionClicked, getArguments().getBoolean(MAINBOARD_CHANGE));
                StaticUtilityMethodsAndConstants.hideKeyboardFrom(getContext(), rootView);
                hostActivity.closeDrawer(Gravity.END);
                //StaticUtilityMethodsAndConstants.closeThisFragment(getActivity(), ModifyCardEntryFragment.this);
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCardQuantity = mCardQuantity.getText().toString();
                try{
                    hostActivity.setCardCountCallback(Integer.parseInt(newCardQuantity), positionClicked, getArguments().getBoolean(MAINBOARD_CHANGE));
                    StaticUtilityMethodsAndConstants.hideKeyboardFrom(getContext(), rootView);
                    hostActivity.closeDrawer(Gravity.END);
                    //StaticUtilityMethodsAndConstants.closeThisFragment(getActivity(), ModifyCardEntryFragment.this);
                }
                catch(NumberFormatException ex){
                    Toast.makeText(getContext(), "invalid number for quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticUtilityMethodsAndConstants.hideKeyboardFrom(getContext(), rootView);
                hostActivity.closeDrawer(Gravity.END);
                //StaticUtilityMethodsAndConstants.closeThisFragment(getActivity(), ModifyCardEntryFragment.this);
            }
        });
    }

    private void textViewSetUp(String cardName, String startingCardQuantity){
        TextView mCardName = (TextView) rootView.findViewById(R.id.card_name);
        mCardQuantity = (EditText) rootView.findViewById(R.id.card_quantity);
        mCardName.setText(cardName);
        mCardQuantity.setText(startingCardQuantity);
    }

    public int getPositionClicked() {
        return positionClicked;
    }

}
