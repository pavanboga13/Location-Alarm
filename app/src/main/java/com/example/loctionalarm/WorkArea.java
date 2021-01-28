package com.example.loctionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class WorkArea extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InternetConnection ic= new InternetConnection();
        setContentView(R.layout.activity_work_area);

        Button btnID1 = (Button) findViewById(R.id.btnID1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnID1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkArea.this, AddTask.class);
                startActivity(i);
            }
        });

        Button btnID9 =(Button) findViewById(R.id.btnID9);
        btnID9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkArea.this, OpenCamera.class);
                startActivity(i);
            }
        });

        Button btnID2 =(Button) findViewById(R.id.btnID2);
        btnID2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkArea.this, StartTask.class);
                startActivity(i);
            }
        });
        Button btnID3 = (Button) findViewById(R.id.btnID3);
        btnID3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkArea.this, FinishTask.class);
                startActivity(i);
            }
        });
        Button btnID5 = (Button) findViewById(R.id.btnID5);
        btnID5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkArea.this, CurrentLocation.class);
                startActivity(i);
            }
        });



    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), User_Login.class));
        finish();
    }
}