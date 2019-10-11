package com.example.smartcalcator.ScanItems;

public interface ScannerListener {
    void onDetected(String detections);
    void onStateChanged(String state, int i);
}
