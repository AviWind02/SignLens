package com.example.signlens;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity {
    private CameraHandler cameraHandler;
    private Button startCameraButton;
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


        // Test OpenCV functionality
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        if (mat.empty()) {
            Log.e(TAG, "Matrix creation failed.");
        } else {
            Log.d(TAG, "Matrix creation successful. Matrix data: " + mat.dump());
        }
    }



}
