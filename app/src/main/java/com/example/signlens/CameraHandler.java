package com.example.signlens;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class CameraHandler implements SurfaceHolder.Callback {
    private Camera camera;
    private boolean isPreviewing = false; // Tracks if the camera preview is currently running.
    private Context context; // Used to access system services like WindowManager.
    private SurfaceHolder surfaceHolder;

    private static final String TAG = "CameraHandler";

    public CameraHandler(Context context, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.surfaceHolder.addCallback(this);
        this.camera = Camera.open();
    }

    public void openCamera() {
        releaseCamera(); // Ensure camera is not in use before opening it.
        try {
            camera = Camera.open(); // Open the back-facing camera.
            setCameraDisplayOrientation(); // Configure the display orientation to match device configuration.
            Log.d(TAG, "Camera opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to open camera", e);
        }
    }

    // Sets the camera display orientation based on device rotation.
    private void setCameraDisplayOrientation() {
        if (camera == null) return;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info); // Obtain camera information for the back-facing camera.

        // Get the current rotation of the display to adjust camera orientation accordingly.
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        // Calculate the correct display orientation
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // Compensate for the mirroring of the front camera.
        } else {  // back-facing camera
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result); // Apply the calculated orientation to the camera.
        Log.d(TAG, "Camera display orientation set to " + result + " degrees.");
    }

    // Starts the camera preview with the current settings.
    public void startPreview() {
        if (camera != null && surfaceHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                isPreviewing = true;
                Log.d(TAG, "Camera preview started");
            } catch (Exception e) {
                Log.e(TAG, "Failed to start camera preview", e);
            }
        }
    }

    // Stops the camera preview and releases the camera.
    public void releaseCamera() {
        if (camera != null) {
            if (isPreviewing) {
                camera.stopPreview();
                isPreviewing = false;
                Log.d(TAG, "Preview stopped");
            }
            camera.release();
            camera = null;
            Log.d(TAG, "Camera released");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(); // Start camera preview as soon as the surface is ready.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // React to surface change by restarting the preview.
        if (holder.getSurface() == null) return;
        try {
            camera.stopPreview();
            Log.d(TAG, "Preview stopped due to surface change");
            setCameraDisplayOrientation(); // Reconfigure the camera orientation based on the new surface.
            startPreview(); // Restart the preview with new settings.
        } catch (Exception e) {
            Log.e(TAG, "Error restarting preview", e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera(); // Release the camera when the surface is destroyed.
    }

    // Returns true if the camera preview is currently running.
    public boolean isPreviewing() {
        return isPreviewing;
    }
}
