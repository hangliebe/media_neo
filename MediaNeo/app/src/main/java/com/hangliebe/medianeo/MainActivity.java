package com.hangliebe.medianeo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hangliebe.medianeo.rtmp.LiveBroadcastActivity;
import com.hangliebe.medianeo.rtmp.RtmpActivity;
import com.hangliebe.medianeo.previewTest.PreviewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnRtmp;
    private Button btnPreview;
    private Button btnLiveBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialControl();
    }

    private void initialControl() {
        btnRtmp = findViewById(R.id.btn_rtmp);
        btnRtmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RtmpActivity.class);
                startActivity(intent);
            }
        });

        btnPreview = findViewById(R.id.btn_preview);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                startActivity(intent);
            }
        });

        btnPreview = findViewById(R.id.btn_live_broadcast);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LiveBroadcastActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkPublishPermission() {
        List<String> permissions = new ArrayList<>();
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)) {
            permissions.add(android.Manifest.permission.CAMERA);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO)) {
            permissions.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissions.size() != 0) {
      //      ActivityCompat.requestPermissions(MainActivity.this,(String[]) permissions.toArray(new String[0]),WRITE_PERMISSION_REQ_CODE);
            return false;
        }
        return false;
    }
}
