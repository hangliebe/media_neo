package com.hangliebe.medianeo.rtmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.hangliebe.medianeo.R;
import com.hangliebe.medianeo.tool.ImageTransformer;
import com.hangliebe.medianeocat.CameraNeo;
import com.hangliebe.medianeocat.NeoCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR;
import static com.hangliebe.medianeocat.CameraNeo.RequestType.REPEATING_REQUEST;

public class LiveBroadcastActivity extends AppCompatActivity {
    private static final String TAG = "LiveBroadcastActivity";

    private Button btnStartPre;
    private SurfaceView mSurfaceVuew;
    private ImageReader mImageReader;
    private HandlerThread thread;
    private Handler handler;
    private static int WIDTH = 480;
    private static int HEIGHT = 320;
    int encodeCount;
    long previewTime;
    long encodeTime;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    private CameraNeo mCameraNeo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broadcast);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    private void initialize() {
        btnStartPre = findViewById(R.id.btn_startPre);
        mSurfaceVuew = findViewById(R.id.previewSurface);
        btnStartPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neoCamera("0");
            }
        });
        mCameraNeo = new CameraNeo(this);

        thread = new HandlerThread("push_live_data");
        // start thread
        thread.start();
        handler = new Handler(thread.getLooper());
        String filesDir = getFilesDir().toString();
        FFmpegHandle.getInstance().initVideo(filesDir);
        Log.d(TAG, "path:" + filesDir);
        // FFmpegHandle.getInstance().initVideo("rtmp://81.68.165.138:1935/hangrtmp");

        mImageReader = ImageReader.newInstance(WIDTH, HEIGHT, ImageFormat.YUV_420_888, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.d(TAG, "GET IMANGE DATA");
                Image image = reader.acquireNextImage();
                byte[] data = ImageTransformer.getBytesFromImageAsType(image, ImageFormat.NV21);

                long endTime = System.currentTimeMillis();

                encodeTime = System.currentTimeMillis();
                FFmpegHandle.getInstance().onFrameCallback(data);
                Log.d(TAG,"编码第:" + (encodeCount++) + "帧，耗时:" + (System.currentTimeMillis() - encodeTime));

                Log.d(TAG, "采集第:" + (++encodeCount) + "帧，距上一帧间隔时间:"
                        + (endTime - previewTime) + "  " + Thread.currentThread().getName());
                previewTime = endTime;
                image.close();
             }
        },handler);

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
                list.add(new OutputConfiguration(mSurfaceVuew.getHolder().getSurface()));
                list.add(new OutputConfiguration(mImageReader.getSurface()));
                // 3 create session
                mCameraNeo.createSession(SESSION_REGULAR, list, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraNeo.setCaptureSession(session);

                        // 4 create request and send request
                        CaptureRequest.Builder builder = mCameraNeo.getBuilder(CameraDevice.TEMPLATE_RECORD);
                        builder.addTarget(mSurfaceVuew.getHolder().getSurface());
                        builder.addTarget(mImageReader.getSurface());

                        mCameraNeo.sendRequest(builder.build(), null, REPEATING_REQUEST);
                    }
                });
            }
        });
    }
}