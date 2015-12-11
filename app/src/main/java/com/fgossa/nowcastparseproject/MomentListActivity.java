package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseQueryAdapter;

public class MomentListActivity extends ListActivity {

    private ParseQueryAdapter<Moment> mainAdapter;
    private FavoriteMomentAdapter favoritesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //<felipe>

        setContentView(R.layout.activity_new_moment);

        //</felipe.

        getListView().setClickable(false);

        mainAdapter = new ParseQueryAdapter<Moment>(this, Moment.class);
        mainAdapter.setTextKey("title");
        mainAdapter.setImageKey("photo");

        // Subclass of ParseQueryAdapter
        favoritesAdapter = new FavoriteMomentAdapter(this);

        // Default view is all moments
        setListAdapter(mainAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_moment_list, menu);
        return true;
    }

    /*
     * Posting moments and refreshing the list will be controlled from the Action
     * Bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh: {
                updateMomentList();
                break;
            }

            case R.id.action_favorites: {
                showFavorites();
                break;
            }

            case R.id.action_new: {
                newMoment();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMomentList() {
        mainAdapter.loadObjects();
        setListAdapter(mainAdapter);
    }

    private void showFavorites() {
        favoritesAdapter.loadObjects();
        setListAdapter(favoritesAdapter);
    }

    private void newMoment() {
        Intent i = new Intent(this, MomentActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // If a new post has been added, update
            // the list of posts
            updateMomentList();
        }
    }

}