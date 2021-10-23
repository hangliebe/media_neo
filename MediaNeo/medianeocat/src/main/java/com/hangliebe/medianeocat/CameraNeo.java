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

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.concurrent.Executor;

public class CameraNeo {
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
        mCameraHandler = new Handler((mCameraThread.getLooper()));
    }

    public void openCamera(String id, NeoCallback.StateCallback stateCallback) throws CameraAccessException {
        if (mCameraThread != null) {
            mCameraThread.start();
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mCameraManager.openCamera(id, stateCallback, mCameraHandler);
    }

    public void setCameraDevice(CameraDevice camera) {
        mCameraDevice = camera;
    }

    public void createSession(int sessionMode, List<OutputConfiguration> outputs,
                               NeoCallback.SessionStateCallback callback) throws CameraAccessException {
        if (mCameraDevice == null) {
            return;
        }

        SessionConfiguration configuration = new SessionConfiguration(
                sessionMode,
                outputs,
                null,
                callback
        );
        mCameraDevice.createCaptureSession(configuration);
    }

    public void setCaptureSession(CameraCaptureSession session) {
        mCaptureSession = session;
    }

    public CaptureRequest.Builder getBuilder(int templateType)
            throws CameraAccessException, CameraNeoException {
        if (mCameraDevice == null) {
            throw new CameraNeoException("getBuilder");
        }
        CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(templateType);
        return builder;
    }

    public void sendRequest(CaptureRequest request,
                            CameraCaptureSession.CaptureCallback callback, RequestType type)
            throws CameraAccessException {

        if (mCaptureSession == null) {
            return;
        }

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
}
