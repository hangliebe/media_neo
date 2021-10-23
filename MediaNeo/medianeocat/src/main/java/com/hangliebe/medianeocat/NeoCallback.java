package com.hangliebe.medianeocat;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.util.Log;

import androidx.annotation.NonNull;

public class NeoCallback {
    public static String TAG = "NeoCallback";

    public static abstract class StateCallback extends CameraDevice.StateCallback {
        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
        }

        @Override
        public void onOpened(@NonNull CameraDevice camera) {

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "camera device " + camera.getId() + " onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, "camera device " + camera.getId() + " onError, errorCode: " + error);
        }
    }

    public static abstract class SessionStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "camera session onConfigureFailed");
        }
    }
}
