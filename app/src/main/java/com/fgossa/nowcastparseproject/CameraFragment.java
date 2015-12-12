package com.fgossa.nowcastparseproject;

/**
 * Created by FGO on 12/10/15.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

public class CameraFragment extends Fragment {

    public static final String TAG = "CameraFragment";

    private Camera camera;
    private int current_camera_id;  // ADDED THIS
    private SurfaceView surfaceView;
    private ParseFile photoFile;
    private ImageButton photoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);

        photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);

        if (camera == null) {
            try {
                current_camera_id = Camera.CameraInfo.CAMERA_FACING_BACK; // ADDED THIS
                camera = Camera.open(current_camera_id); //ADDED ARGUMENT
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.e(TAG, "No camera with exception: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }


        // ADDING CODE HERE
        photoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (true) {
                    camera.stopPreview();
                }
                //NB: if you don't release the current camera before switching, you app will crash
                camera.release();

                //swap the id of the camera to be used
                if(current_camera_id == Camera.CameraInfo.CAMERA_FACING_BACK){
                    current_camera_id = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    current_camera_id = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                camera = Camera.open(current_camera_id);
                //Code snippet for this method from somewhere on android developers, i forget where
                camera.setDisplayOrientation(90);
                try {
                    //this step is critical or preview on new camera will no know where to render to
                    camera.setPreviewDisplay(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
                return false;
            }
        });
        // END

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;
                else {
                    camera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    }, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            saveScaledPhoto(data);
                        }
                    });
                }
            }
        });

        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            public void surfaceDestroyed(SurfaceHolder holder) {
            }

        });

        return v;
    }

    private void saveScaledPhoto(byte[] data) {

        Bitmap momentImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        if (current_camera_id == Camera.CameraInfo.CAMERA_FACING_BACK)
            matrix.postRotate(90);
        else
            matrix.postRotate(270);
        Bitmap rotatedScaledMomentImage = Bitmap.createBitmap(momentImage, 0, 0, momentImage.getWidth(), momentImage.getHeight(), matrix, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMomentImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] scaledData = bos.toByteArray();
        photoFile = new ParseFile("moment_photo.jpg", scaledData);
        photoFile.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    addPhotoToMomentAndReturn(photoFile);
                }
            }
        });
    }

    private void addPhotoToMomentAndReturn(ParseFile photoFile) {((MomentActivity) getActivity()).getCurrentMoment().setPhotoFile(photoFile);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("NewMomentFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            try {
                camera = Camera.open(current_camera_id);
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.i(TAG, "No camera: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }

}