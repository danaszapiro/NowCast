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

        setContentView(R.layout.activity_new_moment);

        getListView().setClickable(true);

        mainAdapter = new ParseQueryAdapter<Moment>(this, Moment.class);
        mainAdapter.setTextKey("title");
        mainAdapter.setImageKey("photo");

        favoritesAdapter = new FavoriteMomentAdapter(this);

        setListAdapter(mainAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_moment_list, menu);
        return true;
    }

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
            updateMomentList();
        }
    }

}