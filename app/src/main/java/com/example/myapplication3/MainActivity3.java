package com.example.myapplication3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication3.databinding.ActivityMain3Binding;

import java.util.concurrent.Semaphore;

public class MainActivity3 extends AppCompatActivity {
    private ActivityMain3Binding binding;
    private boolean mBlnIsListenPermissionGranted = false;
    private RecordPermissionListener mPermHandler = null;

    private ActivityResultLauncher<String> mRequestPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (this.mPermHandler != null)
                    this.mPermHandler.HandleRecordPermissionResult(isGranted);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    @Override
    public void onDestroy() {
        this.binding = null;
        this.mRequestPermission = null;
        super.onDestroy();
    }

    public void RequestListenPermission()
    {
        this.mRequestPermission.launch(Manifest.permission.RECORD_AUDIO);
      }

    public void SavePermissionHandler(RecordPermissionListener permHandler){
        this.mPermHandler = permHandler;
    }
}