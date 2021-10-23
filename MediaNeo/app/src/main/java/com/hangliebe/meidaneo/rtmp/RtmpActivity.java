package com.hangliebe.meidaneo.rtmp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;

import com.hangliebe.meidaneo.R;

import java.io.IOException;

import static android.media.MediaCodec.MetricsConstants.MIME_TYPE;

public class RtmpActivity extends AppCompatActivity {
    private final static String TAG = "RtmpActivity";
    private final static int HEIGHT = 1920;
    private final static int WIDTH = 1080;
    private static int KEY_BIT_RATE = 100000;

    private MediaCodec mMediaCodec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp);

        initialCodec();
    }

    private void initialCodec() {
        // create MediaFormat
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", WIDTH, HEIGHT);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30); // 30 for frame rate

        try {
            mMediaCodec = MediaCodec.createDecoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

}