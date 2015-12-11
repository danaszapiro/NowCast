package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseFile;

/*
 * NewMomentActivity contains two fragments that handle
 * data entry and capturing a photo of a given moment.
 * The Activity manages the overall moment data.
 */
public class MomentActivity extends Activity {

    private Moment moment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        moment = new Moment();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        // Begin with main data entry view,
        // NewMomentFragment
        setContentView(R.layout.activity_new_moment);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new NewMomentFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    public Moment getCurrentMoment() {
        return moment;
    }

}