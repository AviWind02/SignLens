package com.example.signlens;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


public class MainActivity extends AppCompatActivity {
    private CameraHandler cameraHandler;
    private Button startCameraButton;
    private SurfaceView surfaceView;
    private static final String TAG = "MainActivity";

    // Static block to load the OpenCV library
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV initialization failed.");
        } else {
            Log.d(TAG, "OpenCV initialization successful.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startCameraButton = findViewById(R.id.button_start_camera);
        surfaceView = findViewById(R.id.camera_preview);
        SurfaceHolder holder = surfaceView.getHolder();

        // Initialize CameraHandler
        cameraHandler = new CameraHandler(this, holder);

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 1);
        }

        // Toggle camera preview on button click
        startCameraButton.setOnClickListener(view -> {
            if (cameraHandler.isPreviewing()) {
                cameraHandler.releaseCamera();
            } else {
                cameraHandler.openCamera();
                cameraHandler.startPreview();
            }
        });

        // Test OpenCV functionality
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        if (mat.empty()) {
            Log.e(TAG, "Matrix creation failed.");
        } else {
            Log.d(TAG, "Matrix creation successful. Matrix data: " + mat.dump());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraHandler != null && surfaceView.getHolder().getSurface().isValid()) {
            cameraHandler.openCamera();
            cameraHandler.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraHandler != null) {
            cameraHandler.releaseCamera();
        }
    }



}
