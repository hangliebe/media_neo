package com.hangliebe.medianeo.rtmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hangliebe.medianeocat.CameraNeo;
import com.hangliebe.medianeocat.NeoCallback;
import com.hangliebe.medianeo.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;
import static android.media.MediaCodec.MetricsConstants.MIME_TYPE;
import static com.hangliebe.medianeocat.CameraNeo.RequestType.REPEATING_REQUEST;

public class RtmpActivity extends AppCompatActivity {
    private final static String TAG = "RtmpActivity";
    private final static int HEIGHT = 1920;
    private final static int WIDTH = 1080;
    private static int KEY_BIT_RATE = 1000000;

    private Button btnCatchCamera;
    private TextView textView;

    private MediaCodec mMediaCodec;
    private Surface mCodecInputSurface;
    private CameraNeo mCameraNeo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp);

        initialControl();
        initialCodec();
        mCameraNeo = new CameraNeo(this);
    }

    @SuppressLint("WrongViewCast")
    private void initialControl() {
        btnCatchCamera = findViewById(R.id.catch_camera);
        textView = findViewById(R.id.textView);

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

        try {
            mMediaCodec = MediaCodec.createDecoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
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
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mCodecInputSurface = mMediaCodec.createInputSurface();
    }

    private void neoCamera(String id) {
        mCameraNeo.openCamera(id, new NeoCallback.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                super.onOpened(camera);
                List<OutputConfiguration> list = new ArrayList<>();
                list.add(new OutputConfiguration(mCodecInputSurface));
                mCameraNeo.createSession(SESSION_REGULAR, list, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        //create request and send request
                        CaptureRequest.Builder builder = mCameraNeo.getBuilder(CameraDevice.TEMPLATE_RECORD);
                        builder.addTarget(mCodecInputSurface);
                        mCameraNeo.sendRequest(builder.build(),null, REPEATING_REQUEST);
                    }
                });
            }
        });
    }

}