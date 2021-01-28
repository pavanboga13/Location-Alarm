package com.example.loctionalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class AddTask extends AppCompatActivity {

    TextView mtaskid;
    EditText  mlocationname, mtaskdetails;
    Button senddatatodatabase;
    DataBaseConnection mydb;
    Cursor taskidauto;
    FileWriter f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mtaskid = findViewById(R.id.editview1);
        mlocationname = findViewById(R.id.editview2);
        mtaskdetails = findViewById(R.id.editinaddtask);
        senddatatodatabase = findViewById(R.id.btnLoc);

        mydb = new DataBaseConnection(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Cursor r=mydb.gettaskid();
        if(r.getCount()==0)
            mtaskid.setText(new String("1"));
        else
        {
            r.moveToLast();
            int x=Integer.parseInt(r.getString(0));x+=1;
            mtaskid.setText(new String(""+x));
        }
        mtaskid.setClickable(false);
        mtaskid.setEnabled(false);

        senddatatodatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tlocation = mlocationname.getText().toString().trim();
                String tdetalis = mtaskdetails.getText().toString().trim();

                if (tlocation.length()==0 || tdetalis.length()==0) {
                    Toast.makeText(AddTask.this, "All fields Required...", Toast.LENGTH_LONG).show();
                } else {
                    mydb.dbinsert(tlocation, tdetalis);
                    Toast.makeText(AddTask.this, "Task in queue.", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(getApplicationContext(), ViewLocationTask.class);
                    intent.putExtra("location",tlocation);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}