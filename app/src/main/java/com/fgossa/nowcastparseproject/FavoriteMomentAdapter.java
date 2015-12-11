package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */

import java.util.Arrays;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class FavoriteMomentAdapter extends ParseQueryAdapter<Moment> {

    public FavoriteMomentAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Moment>() {
            public ParseQuery<Moment> create() {
                ParseQuery query = new ParseQuery("Moment");
                query.whereContainedIn("rating", Arrays.asList("Public"));
                query.orderByDescending("rating");
                return query;
            }
        });
    }

    @Override
    public View getItemView(Moment moment, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_list_favorites, null);
        }

        super.getItemView(moment, v, parent);

        ParseImageView momentImage = (ParseImageView) v.findViewById(R.id.icon);
        ParseFile photoFile = moment.getParseFile("photo");
        if (photoFile != null) {
            momentImage.setParseFile(photoFile);
            momentImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                }
            });
        }

        TextView titleTextView = (TextView) v.findViewById(R.id.text1);
        titleTextView.setText(moment.getTitle());
        TextView ratingTextView = (TextView) v.findViewById(R.id.favorite_moment_rating);
        ratingTextView.setText(moment.getRating());
        return v;
    }

}
