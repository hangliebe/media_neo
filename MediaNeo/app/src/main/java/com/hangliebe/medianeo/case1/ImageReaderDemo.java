package com.hangliebe.medianeo.case1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import com.hangliebe.medianeocat.CameraNeo;
import com.hangliebe.medianeocat.CameraNeoException;
import com.hangliebe.medianeocat.NeoCallback;
import com.hangliebe.medianeo.R;

import net.butterflytv.rtmp_client.RTMPMuxer;

import java.util.ArrayList;
import java.util.List;

public class ImageReaderDemo extends AppCompatActivity {
    private static String TAG = "ImageReaderDemo";
    private static int WIDTH = 1090;
    private static int HEIGHT = 1080;

    private ImageReader imageReader;
    CameraNeo cameraNeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_reader_demo);
        imageReader = ImageReader.newInstance(WIDTH, HEIGHT, ImageFormat.YUV_420_888, 1);
//        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                Log.d(TAG,"getImage");
//            }
//        });
        cameraNeo = new CameraNeo(this);
    }

    private void catchCamera(String id) throws CameraAccessException {
        cameraNeo.openCamera(id, new NeoCallback.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                super.onOpened(camera);
                cameraNeo.setCameraDevice(camera);
                List<OutputConfiguration> outputs = new ArrayList<>();
                outputs.add(new OutputConfiguration(imageReader.getSurface()));
                cameraNeo.createSession(SessionConfiguration.SESSION_REGULAR, outputs, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        cameraNeo.setCaptureSession(session);
                        CaptureRequest.Builder builder = null;

                        builder = cameraNeo.getBuilder(CameraDevice.TEMPLATE_STILL_CAPTURE);
                        builder.addTarget(imageReader.getSurface());

                        cameraNeo.sendRequest(builder.build(), null, CameraNeo.RequestType.REPEATING_REQUEST);
                    }
                });
            }
        });
    }

    public void push() {
        RTMPMuxer rtmpMuxer = new RTMPMuxer();

    }
}
