package com.example.yifan.mtgdecktracker.SavedDecksActivityClasses;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by Yifan on 7/2/2016.
 */
public class ConfirmResetDialogFragment extends DialogFragment{

    private static final String MESSAGE = "Message";
    private static final String FOR_FRAGMENT = "ForFragment";

    public static ConfirmResetDialogFragment getInstanceForActivity(String message){
        ConfirmResetDialogFragment instance = new ConfirmResetDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putBoolean(FOR_FRAGMENT, false);
        instance.setArguments(args);
        return instance;
    }

    public static ConfirmResetDialogFragment getInstanceForFragment(String message, Fragment targetFragment){
        ConfirmResetDialogFragment instance = new ConfirmResetDialogFragment();
        instance.setTargetFragment(targetFragment, 1);
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putBoolean(FOR_FRAGMENT, true);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        final ConfirmResetDialogCallbacks host;
        Bundle args = getArguments();
        String msg = args.getString(MESSAGE, "confirm whatever? (minor bug)");
        if(args.getBoolean(FOR_FRAGMENT)){
            host = (ConfirmResetDialogCallbacks) getTargetFragment();
        }
        else{
            host = (ConfirmResetDialogCallbacks) getActivity();
        }
        builder.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        host.onPositiveClick(ConfirmResetDialogFragment.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        host.onNegativeClick(ConfirmResetDialogFragment.this);
                    }
                });


        return builder.create();
    }

    public interface ConfirmResetDialogCallbacks {
        public void onPositiveClick(DialogFragment dialog);

        public void onNegativeClick(DialogFragment dialog);
    }
}
