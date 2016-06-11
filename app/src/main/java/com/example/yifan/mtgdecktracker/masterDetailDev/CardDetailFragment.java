package com.example.yifan.mtgdecktracker.masterDetailDev;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yifan.mtgdecktracker.JsonFetcher;
import com.example.yifan.mtgdecktracker.R;
import com.example.yifan.mtgdecktracker.masterDetailDev.dummy.DummyContent;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A fragment representing a single Card detail screen.
 * This fragment is either contained in a {@link CardListActivity}
 * in two-pane mode (on tablets) or a {@link CardDetailActivity}
 * on handsets.
 */
public class CardDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.CardItem mItem;

    private ImageView mCardView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public CardDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.cardName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_detail, container, false);
        mCardView = (ImageView) rootView.findViewById(R.id.card_image);

        new myTask().execute(mItem.imageURL);

        return rootView;
    }

    private class myTask extends AsyncTask<String, Void, Bitmap>{
        Bitmap cardImage;
        InputStream imageStream;
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                imageStream = new URL(params[0]).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cardImage = BitmapFactory.decodeStream(imageStream);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mCardView.setImageBitmap(bitmap);

        }
    }
}
