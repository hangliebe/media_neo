package com.hangliebe.medianeo.case_opengles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.hangliebe.medianeo.R;
import com.hangliebe.medianeocat.CameraNeo;
import com.hangliebe.medianeocat.NeoCallback;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;
import static com.hangliebe.medianeocat.CameraNeo.RequestType.REPEATING_REQUEST;

public class OpenGLActivity extends AppCompatActivity {
    private static String TAG = "OpenGLActivity";
    private static final String BACK_ID = "0";
    private static int WIDTH = 1920;
    private static int HEIGHT = 1080;
    private Button btnOpenCamera;
    private GLSurfaceView mGLSurfaceView;
    private CameraRender mCameraRender;
    private SurfaceTexture mSurfaceTexture;
    CameraNeo mCameraNeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_glactivity);

        mCameraNeo = new CameraNeo(this);
        initialize();
    }

    private void initialize() {
        Log.d(TAG, "initialize");
        btnOpenCamera = findViewById(R.id.btn_open_camera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neoCamera(BACK_ID);
            }
        });
        mGLSurfaceView = findViewById(R.id.previewSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mCameraRender = new CameraRender();
        mGLSurfaceView.setRenderer(mCameraRender);
    }

    private void initSurfaceTexture() {
        Log.d(TAG, "initSurfaceTexture");
        mSurfaceTexture = new SurfaceTexture(mCameraRender.getOESTextureId());
        mSurfaceTexture.setDefaultBufferSize(WIDTH, HEIGHT);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGLSurfaceView.requestRender();
            }
        });

        mCameraRender.setSurfaceTexture(mSurfaceTexture);
    }

    private void neoCamera(String id) {
        initSurfaceTexture();
        Log.d(TAG, "start open camera");
        // 1 open camera
        mCameraNeo.openCamera(id, new NeoCallback.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                super.onOpened(camera);
                mCameraNeo.setCameraDevice(camera);
                // 2 config session
                List<OutputConfiguration> list = new ArrayList<>();
                final Surface surface = new Surface(mSurfaceTexture);
                list.add(new OutputConfiguration(surface));
                // 3 create session
                mCameraNeo.createSession(SESSION_REGULAR, list, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraNeo.setCaptureSession(session);

                        // 4 create request and send request
                        CaptureRequest.Builder builder = mCameraNeo.getBuilder(CameraDevice.TEMPLATE_RECORD);
                        builder.addTarget(surface);
                        mCameraNeo.sendRequest(builder.build(), null, REPEATING_REQUEST);
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraNeo.release();
    }
}