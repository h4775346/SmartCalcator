package com.example.smartcalcator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartcalcator.ScanItems.Scanner;
import com.example.smartcalcator.ScanItems.ScannerListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import com.mapzen.speakerbox.Speakerbox;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {

    EditText mResultEt;
    ImageView mPreviewIv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int WRITE_REQUEST_CODE = 101;


    String cameraPermission[];
    String storagePermission[];
    ImageView image_talk, image_save;

    Uri image_uri;
    Speakerbox speakerbox;
    SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakerbox = new Speakerbox(getApplication());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 10000);


        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);
        image_talk = findViewById(R.id.image_talk);
        image_save = findViewById(R.id.image_save);
        surfaceView = findViewById(R.id.surface);
        Scanner scanner = new Scanner(this, surfaceView);
        scanner.setScanning(true);
        scanner.scan();
        scanner.setListener(new ScannerListener() {
            @Override
            public void onDetected(String detections) {
                mResultEt.setText(detections);
            }

            @Override
            public void onStateChanged(String state, int i) {

            }
        });


        image_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (speakerbox.getTextToSpeech().isSpeaking() == true) {
                    speakerbox.getTextToSpeech().stop();
                    image_talk.setImageResource(R.drawable.ic_action_talk);
                } else {

                    speakerbox.play(mResultEt.getText().toString());
                    image_talk.setImageResource(R.drawable.ic_action_stop);
                    speakerbox.getTextToSpeech().setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                        @Override
                        public void onUtteranceCompleted(String utteranceId) {

                            speakerbox.getTextToSpeech().stop();
                            image_talk.setImageResource(R.drawable.ic_action_talk);
                        }
                    });


                }


            }


        });


        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile();

            }
        });


        // camera permission

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // storage permission


        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }

    private Runnable finished() {

        speakerbox.getTextToSpeech().stop();
        image_talk.setImageResource(R.drawable.ic_action_talk);
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menue
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        speakerbox.getTextToSpeech().stop();
        image_talk.setImageResource(R.drawable.ic_action_talk);
    }


    private void createFile() {
        // when you create document, you need to add Intent.ACTION_CREATE_DOCUMENT
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // filter to only show openable items.
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested Mime type
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "daaaataaaa.txt");

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.addImage) {

            showImageImportDialog();

        }


        if (id == R.id.settings) {

            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();


        }


        if (id == R.id.share) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mResultEt.getText().toString());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Send Text To");
            startActivity(Intent.createChooser(shareIntent, "Share..."));


        }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {


        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //Camera Option Clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        //camera Allowed
                        pickCamera();
                    }
                }
                if (which == 1) {
                    // gallery Option Clicked


                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        //Storage Allowed
                        pickGallery();
                    }
                }
            }
        });

        dialog.create().show();
    }

    private void pickGallery() {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY);


    }

    private void pickCamera() {


        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);


        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);


    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);


        return result && result1;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {


            case CAMERA_REQUEST_CODE:

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    }

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;
            case STORAGE_REQUEST_CODE:


                boolean writeStorageAccepted = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;

                if (writeStorageAccepted) {
                    pickGallery();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }


                break;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY) {


                CropImage.activity(data.getData()).
                        setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);


            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri).
                        setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);


            }
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                mPreviewIv.setImageURI(resultUri);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();

                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {

                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(this, "Got Data", Toast.LENGTH_SHORT).show();

                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }

                    mResultEt.setText(sb.toString());

                }


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

                Toast.makeText(this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

        if (requestCode == WRITE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null
                            && data.getData() != null) {
                        writeInFile(data.getData(), "bison is bision");
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }

        }


    }
    private void writeInFile (@NonNull Uri uri, @NonNull String text){
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(mResultEt.getText().toString());
            bw.flush();
            bw.close();
            Toast.makeText(this, "Success Operation", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}