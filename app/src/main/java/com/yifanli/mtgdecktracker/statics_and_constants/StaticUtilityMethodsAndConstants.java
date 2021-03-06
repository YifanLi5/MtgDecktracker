package com.yifanli.mtgdecktracker.statics_and_constants;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Yifan on 6/17/2016.
 * Contains constants and static methods used in the project
 */
public class StaticUtilityMethodsAndConstants {

    public static final String INTERNAL_STORAGE_FILENAME = "MTGDecktracker Saved Decks";

    private StaticUtilityMethodsAndConstants(){
        throw new UnsupportedOperationException("No instances.");
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    //method to hide keyboard, used after pressing add button.
    //called by getContext() and the view of the activity/fragment. ex) View rootView = inflater.inflate(R.layout.fragment_modify_card_entry, container, false);
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //used to close fragment
    //usually called by closeThisFragment(getActivity(), Fragment.this)
    //sometimes on rotation causes grayed out screen as if fragment was still there. Not using atm.
    public static void closeThisFragment(FragmentActivity activity, Fragment fragment){
        activity.getSupportFragmentManager().beginTransaction()
                .remove(fragment).addToBackStack(null).commit();
    }
}
