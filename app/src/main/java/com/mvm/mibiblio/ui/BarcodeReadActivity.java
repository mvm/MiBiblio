package com.mvm.mibiblio.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.CheckBox;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.mvm.mibiblio.CollectionDbHelper;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.search.GoogleBooksNetSearch;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class BarcodeReadActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION = 1;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CheckBox seriesCheck;
    private PreviewView cameraView;
    private List<String> barcodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_read);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.seriesCheck = findViewById(R.id.checkSeries);
        this.cameraView = findViewById(R.id.cameraView);
        this.barcodes = new ArrayList<>();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startCamera();
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCamera(cameraProvider);
            } catch(Exception e) {
                Log.e("mibiblio", e.getLocalizedMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void bindCamera(ProcessCameraProvider cameraProvider) {
        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();

        cameraProvider.unbindAll();

        Preview preview = new Preview.Builder().build();
        PreviewView previewView = findViewById(R.id.cameraView);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this),
                new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        Image image = imageProxy.getImage();
                        if(image != null) {
                            InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());
                            barcodeScanner.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<List<com.google.mlkit.vision.barcode.Barcode>>() {
                                        @Override
                                        public void onSuccess(List<Barcode> barcodes) {
                                            for(Barcode b : barcodes) {
                                                read(b);
                                            }
                                        }
                                    })
                                    .addOnCompleteListener(BarcodeReadActivity.this, new OnCompleteListener<List<Barcode>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Barcode>> task) {
                                    imageProxy.close();
                                }
                            });
                        }

                    }
                });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);

    }

    private void read(Barcode barcode) {
        if(barcode == null || barcodes.contains(barcode.getRawValue())) return;

        if(!seriesCheck.isChecked()) {
            Intent intent = new Intent();
            intent.putExtra("barcode", barcode.getDisplayValue());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            //TODO: serial import code here
            Log.d("mibiblio", "serial import: " + barcode.getRawValue());
            ContentValues searchParams = new ContentValues();
            searchParams.put("isbn", barcode.getRawValue());
            GoogleBooksNetSearch search = new GoogleBooksNetSearch(new GoogleBooksNetSearch.OnResultListener() {
                @Override
                public void onResult(List<BookModel> adapter) {
                    onNetResult(adapter);
                }
            });
            search.execute(searchParams);
        }
        barcodes.add(barcode.getRawValue());
    }

    private void onNetResult(List<BookModel> books) {
        if(books == null) return;

        CollectionDbHelper dbHelper = ((MiBiblioApplication)this.getApplication())
                .getCurrentCollection().getDbHelper(this.getApplicationContext());
        BookModel firstResult = books.get(0);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        firstResult.insert(db);
        db.close();
        Snackbar.make(this.cameraView, String.format(
                getString(R.string.barcode_bookread), firstResult.getTitle()
        ), BaseTransientBottomBar.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
