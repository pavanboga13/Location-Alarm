package com.example.loctionalarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OpenCamera extends AppCompatActivity {

    ImageView imageView;
    Button btnOpen, btsave;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);

        imageView = findViewById(R.id.image_view);
        btnOpen = findViewById(R.id.open_camera);
        btsave = findViewById(R.id.btn_save);

        ActivityCompat.requestPermissions(OpenCamera.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(OpenCamera.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(filename, ".jpg", storageDirectory);
                    currentPhotoPath = image.getAbsolutePath();

                    Uri imageuri = FileProvider.getUriForFile(OpenCamera.this, "com.example.loctionalarm.fileprovider", image);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                    startActivityForResult(intent, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesavetomyphonegallery();
            }
        });
    }

    private void imagesavetomyphonegallery() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();

        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Please capture image...", Toast.LENGTH_SHORT).show();
        }
        else {
            Bitmap bitmap = bitmapDrawable.getBitmap();

            FileOutputStream outputStream = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath() + "/LocationAlarm");
            dir.mkdir();

            String fileName = String.format("%d.jpeg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            try {
                outputStream = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Toast.makeText(this, "Image saved....", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap);
        }
    }
}