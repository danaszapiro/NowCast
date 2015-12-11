package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/*
 * This fragment manages the data entry for a
 * new Moment object. It lets the user input a
 * moment name, give it a rating, and take a
 * photo. If there is already a photo associated
 * with this moment, it will be displayed in the
 * preview at the bottom, which is a standalone
 * ParseImageView.
 */
public class NewMomentFragment extends Fragment {

    private ImageButton photoButton;
    private Button saveButton;
    private Button cancelButton;
    private TextView momentName;
    private Spinner momentRating;
    private ParseImageView momentPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_moment, parent, false);

        momentName = ((EditText) v.findViewById(R.id.moment_name));

        // The momentRating spinner lets people assign favorite moments.
        // Moments with 4 or 5 ratings will appear in the Favorites view.
        momentRating = ((Spinner) v.findViewById(R.id.rating_spinner));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ratings_array, android.R.layout.simple_spinner_dropdown_item);
        momentRating.setAdapter(spinnerAdapter);

        photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(momentName.getWindowToken(), 0);
                startCamera();
            }
        });

        saveButton = ((Button) v.findViewById(R.id.save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Moment moment = ((MomentActivity) getActivity()).getCurrentMoment();

                // When the user clicks "Save," upload the moment to Parse
                // Add data to the moment object:
                moment.setTitle(momentName.getText().toString());

                // Associate the moment with the current user
                moment.setAuthor(ParseUser.getCurrentUser());

                // Add the rating
                moment.setRating(momentRating.getSelectedItem().toString());

                // If the user added a photo, that data will be added in the CameraFragment

                // Save the moment and return
                moment.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });

        cancelButton = ((Button) v.findViewById(R.id.cancel_button));
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        // Until the user has taken a photo, hide the preview
        momentPreview = (ParseImageView) v.findViewById(R.id.moment_preview_image);
        momentPreview.setVisibility(View.INVISIBLE);

        return v;
    }

    /*
     * All data entry about a Moment object is managed from the NewMomentActivity.
     * When the user wants to add a photo, we'll start up a custom
     * CameraFragment that will let them take the photo and save it to the Moment
     * object owned by the NewMomentActivity. Create a new CameraFragment, swap
     * the contents of the fragmentContainer (see activity_new_moment.xml), then
     * add the NewMomentFragment to the back stack so we can return to it when the
     * camera is finished.
     */
    public void startCamera() {
        Fragment cameraFragment = new CameraFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, cameraFragment);
        transaction.addToBackStack("NewMomentFragment");
        transaction.commit();
    }

    /*
     * On resume, check and see if a moment photo has been set from the
     * CameraFragment. If it has, load the image in this fragment and make the
     * preview image visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = ((MomentActivity) getActivity())
                .getCurrentMoment().getPhotoFile();
        if (photoFile != null) {
            momentPreview.setParseFile(photoFile);
            momentPreview.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    momentPreview.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}