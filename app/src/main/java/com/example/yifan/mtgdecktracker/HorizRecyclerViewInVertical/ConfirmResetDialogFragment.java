package com.example.yifan.mtgdecktracker.HorizRecyclerViewInVertical;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

/**
 * Created by Yifan on 7/2/2016.
 */
public class ConfirmResetDialogFragment extends DialogFragment{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        final ConfirmResetDialogCallbacks host = (ConfirmResetDialogCallbacks) getTargetFragment();
        builder.setMessage("Cancel Changes?")
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
