package com.example.smartcalcator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartcalcator.ScanItems.Scanner;
import com.example.smartcalcator.ScanItems.ScannerListener;

public class CameraActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView txt_result,start_scan;
    Scanner scanner;
    ViewDialog dialog;
    boolean stoped_scanning=true;
    ScrollView mScril;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        dialog = new ViewDialog(this);

        surfaceView = findViewById(R.id.surface);
        txt_result = findViewById(R.id.txt_recognise);
        start_scan=findViewById(R.id.start_scan);
        mScril=findViewById(R.id.mScril);
        scanner = new Scanner(CameraActivity.this, surfaceView);
        scanner.setScanning(true);
        scanner.setListener(new ScannerListener() {
            @Override
            public void onDetected(String detections) {

                txt_result.setText(detections);
                Log.d("detect1 " , detections);

                if (start_scan.getVisibility()==View.INVISIBLE && stoped_scanning){
                    stoped_scanning=false;
                    startHandler();


                }


            }

            @Override
            public void onStateChanged(String state, int i) {


            }
        });

        start_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_scan.setVisibility(View.INVISIBLE);
                mScril.setVisibility(View.VISIBLE);


                scanner.scan();
                stoped_scanning=true;



            }
        });



    }

    private void startHandler() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start_scan.setVisibility(View.VISIBLE);
                mScril.setVisibility(View.INVISIBLE);
                scanner.setScanning(false);
                dialog.showDialog(txt_result.getText().toString());


            }
        },5000);




    }
}
