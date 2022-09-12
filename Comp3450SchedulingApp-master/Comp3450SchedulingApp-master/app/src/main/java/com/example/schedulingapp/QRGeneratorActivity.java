//Main Author: Logan Royer
//Code References in comments
//Activity created to generate a bardcode from a string

package com.example.schedulingapp;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class QRGeneratorActivity extends AppCompatActivity {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        //When the option to generate a QR code is selected from within a schedule, we will
        //extract the data from the intent and generate the code.
        String scheduleInfo = getIntent().getStringExtra("SCHEDULE_INFO");
        generateQRCodeImage(scheduleInfo, WIDTH, HEIGHT);

        Button b = findViewById(R.id.CancelGeneratorButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                finish();
            }
        });
    }

    //Method to generate turn the text into a QR code and display it on the screen
    private void generateQRCodeImage(String text, int width, int height) {
        try {
            //Text is converted to a QR code bitmatrix, code from:
            //https://www.callicoder.com/generate-qr-code-in-java-using-zxing/
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            //BitMatrix converted to BitMap using code from:
            //https://stackoverflow.com/questions/19337448/generate-qr-code-directly-into-imageview
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){
                    bmp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }

            //Display the resulting bitmap on screen
            ImageView QR = findViewById(R.id.QrDisplay);
            QR.setImageBitmap(bmp);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to generate QR code!", Toast.LENGTH_SHORT).show();
            Log.e("Zxing Library", e.toString());
        }
    }
}