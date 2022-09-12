//Main Author: Logan Royer
//Activity created to scan a barcode and display results
//Code References:
//https://www.truiton.com/2016/09/android-example-programmatically-scan-qr-code-and-bar-code/
//Makes use of Google's Mobile Vision API

package com.example.schedulingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileNotFoundException;

public class QRReaderActivity extends AppCompatActivity {
    private static final String CAMERALOG_TAG = "Camera API";
    private static final String BCODELOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;
    private BarcodeDetector codeReader;
    private Uri imageUri;
    private String photoPath, resString = "No_Result";
    private File JSON_File;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);

        //File to store the imported schedule
        JSON_File = new File(getIntent().getStringExtra("FILE_LOCATION"));

        //Holds results of the scan
        scanResults = findViewById(R.id.QrScanResults);

        //Button to take the picture
        Button a = findViewById(R.id.QrScanButton);
        a.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                takePicture();
            }
        });

        //Button to press when finished
        Button b = findViewById(R.id.doneButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(resString.equals("No_Result")) {
                    Toast.makeText(getApplicationContext(),
                            "No schedule has been found to import! Please try taking the"
                                    + "picture again or cancel.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Schedule tempSch = JsonAdapter.createScheduleFromString(resString);
                    JsonAdapter.addJSONToFile(JSON_File.getAbsoluteFile(), JsonAdapter.toJson(tempSch));
                    Log.d("TESTING", JsonAdapter.loadJSONFromAsset(JSON_File));
                    startActivity(new Intent(QRReaderActivity.this, MainActivity.class));
                }
            }
        });

        //Cancel button
        Button c = findViewById(R.id.cancelButton);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        //Sets up the barcode reader
        codeReader = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!codeReader.isOperational()) {
            scanResults.setText("Could not set up the detector!");
            Toast.makeText(this, "Detector Setup Error!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //Takes a picture using the phones camera
    private void takePicture() {
        try {
            //Uses the built in camera to take a picture to get the QR code for import
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //Creates a temp file to store the picture in the apps temp directory
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photo = File.createTempFile("qrCode", ".jpg", dir);
            photoPath = photo.getAbsolutePath();
            imageUri = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider", photo);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            //Launches the camera to get the picture
            startActivityForResult(pictureIntent, PHOTO_REQUEST);
        }
        catch(Exception e){
            Toast.makeText(this, "Failed to take picture.", Toast.LENGTH_SHORT)
                    .show();
            Log.e(CAMERALOG_TAG, e.toString());
            e.printStackTrace();
        }
    }

    //Called when a picture has been taken and needs to be scanned for a QR
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            //Saves the picture
            launchMediaScanIntent();
            try {
                //Gets a bitmap to be checked for a QR
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (codeReader.isOperational() && bitmap != null) {
                    //Gets the QR codes from the image and stores them into an array
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = codeReader.detect(frame);

                    //Get the value from the fist barcode, ignoring others
                    Barcode code = barcodes.valueAt(0);
                    resString = code.displayValue;

                    //If the API could not read any QR Codes from the image, ask the user to try again
                    if (barcodes.size() == 0) {
                        scanResults.setText("Sorry, we could not read the QR Code. Please try again." +
                                "You may need to zoom in or out on the QR.");
                        Toast.makeText(this, "Sorry, could not read the QR",
                                Toast.LENGTH_SHORT).show();
                    }
                    //If the API was successful save the results
                    else{
                        //Save the result to the screen and append it to the JSON file
                        scanResults.setText("Importing successful!");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
                //Displays the picture that was taken.
                ImageView scanImageResult = findViewById(R.id.scan_picture);
                scanImageResult.setImageBitmap(bitmap);

                Button b = findViewById(R.id.QrScanButton);
                if(b.getText() == "Take Picture"){
                    b.setText("Try Again");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(BCODELOG_TAG, e.toString());
            }
        }
    }

    //Decodes the picture to get a bitmap
    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //Sales the picture
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        //Returns a bitmap
        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    //Saves the picture to a temp file
    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
