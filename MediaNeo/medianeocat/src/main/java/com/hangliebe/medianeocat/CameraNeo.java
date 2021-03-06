package com.hangliebe.medianeocat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.concurrent.Executor;

public class CameraNeo {
    private static String TAG = "CameraNeo";
    private Context mContext;
    private CameraManager mCameraManager;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;

    public enum RequestType {
        REPEATING_REQUEST,
        CAPTURE
    }

    public enum BurstRequestType {
        BURST_REPEATING_REQUEST
    }

    public CameraNeo(Context context) {
        mContext = context;
        if (mCameraManager == null) {
            mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        }

        mCameraThread = new HandlerThread("camera_thread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    public void openCamera(String id, NeoCallback.StateCallback stateCallback){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mCameraManager.openCamera(id, stateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void setCameraDevice(CameraDevice camera) {
        mCameraDevice = camera;
    }

    public void createSession(int sessionMode, List<OutputConfiguration> outputs,
                               NeoCallback.SessionStateCallback callback) {
        if (mCameraDevice == null) {
            return;
        }
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
        SessionConfiguration configuration = new SessionConfiguration(
                sessionMode,
                outputs,
                executor,
                callback
        );
        try {
            mCameraDevice.createCaptureSession(configuration);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void setCaptureSession(CameraCaptureSession session) {
        mCaptureSession = session;
    }

    public CaptureRequest.Builder getBuilder(int templateType) {
        if (mCameraDevice == null) {
            Log.d(TAG, "mCameraDevice should not be null!");
        }
        CaptureRequest.Builder builder = null;
        try {
            builder = mCameraDevice.createCaptureRequest(templateType);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public void sendRequest(CaptureRequest request,
                            CameraCaptureSession.CaptureCallback callback, RequestType type) {

        if (mCaptureSession == null) {
            return;
        }

        try {
            switch (type) {
                case REPEATING_REQUEST:
                    mCaptureSession.setRepeatingRequest(request, callback, mCameraHandler);
                    break;
                case CAPTURE:
                    mCaptureSession.capture(request, callback, mCameraHandler);
                    break;
                default:
                    break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void sendRequest(List<CaptureRequest> request,
                            CameraCaptureSession.CaptureCallback callback, BurstRequestType type)
            throws CameraAccessException {

        if (mCaptureSession == null) {
            return;
        }

        switch (type) {
            case BURST_REPEATING_REQUEST:
                mCaptureSession.setRepeatingBurst(request, callback, mCameraHandler);
                break;
            default:
                break;
        }
    }

    public void release() {
        if (mCaptureSession != null) {
            mCaptureSession.close();
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();

        }
    }
}
