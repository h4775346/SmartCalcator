package com.example.smartcalcator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.smartcalcator.ScanItems.Scanner;
import com.example.smartcalcator.ScanItems.ScannerListener;

public class CameraActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView txt_result;
    Scanner scanner;
    boolean stoped_scanning=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surface);
        txt_result = findViewById(R.id.txt_recognise);
         scanner = new Scanner(this, surfaceView);
        scanner.setScanning(true);
        scanner.scan();
        scanner.setListener(new ScannerListener() {
            @Override
            public void onDetected(String detections) {

                txt_result.setText(detections);
                Log.d("detect1 " , detections);

                if (stoped_scanning){
                    startHandler();
                    stoped_scanning=false;
                }


            }

            @Override
            public void onStateChanged(String state, int i) {


            }
        });




    }

    private void startHandler() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                scanner.setScanning(false);
                ViewDialog dialog = new ViewDialog(CameraActivity.this);
                dialog.showDialog(txt_result.getText().toString());


            }
        },5000);




    }
}
