package com.hangliebe.medianeo.rtmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hangliebe.medianeocat.CameraNeo;
import com.hangliebe.medianeocat.NeoCallback;
import com.hangliebe.medianeo.R;
import com.hangliebe.medianeo.tool.AlertTool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;
import static com.hangliebe.medianeocat.CameraNeo.RequestType.REPEATING_REQUEST;

public class RtmpActivity extends AppCompatActivity {
    private final static String TAG = "RtmpActivity";
    private final static int HEIGHT = 1920;
    private final static int WIDTH = 1080;
    private static int KEY_BIT_RATE = 1600000;

    private Button btnConfigCodec;
    private Button btnCatchCamera;
    private TextView textView;

    private MediaCodec mMediaCodec;
    private Surface mCodecInputSurface;
    private CameraNeo mCameraNeo;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp);

        initialControl();

        mCameraNeo = new CameraNeo(this);
    }

    @SuppressLint("WrongViewCast")
    private void initialControl() {
        btnConfigCodec = findViewById(R.id.config_codec);
        btnCatchCamera = findViewById(R.id.catch_camera);
        textView = findViewById(R.id.textView);
        btnConfigCodec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialCodec();
            }
        });

        btnCatchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neoCamera("0");
            }
        });
    }

    private void initialCodec() {
        // create MediaFormat
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", WIDTH, HEIGHT);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30); // 30 for frame rate
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //关键帧
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        try {
            mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            AlertTool.longToast(this, e.getMessage());
        }
        mMediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                textView.setText(index);
                ByteBuffer outputBuffer = codec.getOutputBuffer(index);
                // use the buffer
                codec.releaseOutputBuffer(index, 1000);
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

            }
        });
        try {
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (Exception e) {
            AlertTool.longToast(this, e.getMessage());
        }
        mCodecInputSurface = mMediaCodec.createInputSurface();
        mMediaCodec.start();
        textView.setText("start codec");
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
                list.add(new OutputConfiguration(mCodecInputSurface));
                // 3 create session
                mCameraNeo.createSession(SESSION_REGULAR, list, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraNeo.setCaptureSession(session);
                        // 4 create request and send request
                        CaptureRequest.Builder builder = mCameraNeo.getBuilder(CameraDevice.TEMPLATE_PREVIEW);
                        builder.addTarget(mCodecInputSurface);
                        mCameraNeo.sendRequest(builder.build(),null, REPEATING_REQUEST);
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
        }
        if (mCameraNeo != null) {
            mCameraNeo.release();
        }
    }

    public native String stringFromJNI();
}