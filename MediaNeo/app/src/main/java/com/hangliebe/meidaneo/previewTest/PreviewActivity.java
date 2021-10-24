package com.hangliebe.medianeo.previewTest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.hangliebe.medianeo.R;
import com.hangliebe.medianeocat.CameraNeo;
import com.hangliebe.medianeocat.NeoCallback;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;
import static com.hangliebe.medianeocat.CameraNeo.RequestType.REPEATING_REQUEST;

public class PreviewActivity extends AppCompatActivity {
    private Button btnStartPre;
    private SurfaceView surfaceView;

    private CameraNeo mCameraNeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        btnStartPre = findViewById(R.id.btn_startPre);
        surfaceView = findViewById(R.id.previewSurface);
        btnStartPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neoCamera("0");
            }
        });
        mCameraNeo = new CameraNeo(this);
    }

    private void neoCamera(String id) {
        // 1 open camera
        mCameraNeo.openCamera(id, new NeoCallback.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                super.onOpened(camera);
                mCameraNeo.setCameraDevice(camera);
                // 2 config session
                List<OutputConfiguration> list = new ArrayList<>();
                list.add(new OutputConfiguration(surfaceView.getHolder().getSurface()));
                // 3 create session
                mCameraNeo.createSession(SESSION_REGULAR, list, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraNeo.setCaptureSession(session);

                        // 4 create request and send request
                        CaptureRequest.Builder builder = mCameraNeo.getBuilder(CameraDevice.TEMPLATE_RECORD);
                        builder.addTarget(surfaceView.getHolder().getSurface());
                        mCameraNeo.sendRequest(builder.build(), null, REPEATING_REQUEST);
                    }
                });
            }
        });
    }
}