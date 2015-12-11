package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_moment, parent, false);

        momentName = ((EditText) v.findViewById(R.id.moment_name));
        momentRating = ((Spinner) v.findViewById(R.id.rating_spinner));
        photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
        saveButton = ((Button) v.findViewById(R.id.save_button));
        cancelButton = ((Button) v.findViewById(R.id.cancel_button));
        momentPreview = (ParseImageView) v.findViewById(R.id.moment_preview_image);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ratings_array, android.R.layout.simple_spinner_dropdown_item);
        momentRating.setAdapter(spinnerAdapter);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(momentName.getWindowToken(), 0);
                startCamera();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Moment moment = ((MomentActivity) getActivity()).getCurrentMoment();
                moment.setTitle(momentName.getText().toString());
                moment.setAuthor(ParseUser.getCurrentUser());
                moment.setRating(momentRating.getSelectedItem().toString());
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
                Toast.makeText(getActivity().getApplicationContext(), "Your Moment is now being Casted!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });
        momentPreview.setVisibility(View.INVISIBLE);
        return v;
    }

    public void startCamera() {
        Fragment cameraFragment = new CameraFragment();
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, cameraFragment);
        transaction.addToBackStack("NewMomentFragment");
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = ((MomentActivity) getActivity()).getCurrentMoment().getPhotoFile();
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