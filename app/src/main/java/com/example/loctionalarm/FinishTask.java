package com.example.loctionalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class FinishTask extends AppCompatActivity {

    EditText text1, text2, text3;
    Button finibtn1, searchtask;
    DataBaseConnection mydb;
    Cursor taskfromDATABASE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_task);

        text3 = findViewById(R.id.finiedit1);
        text1 = findViewById(R.id.textView2);
        text2 = findViewById(R.id.textView3);
        searchtask = findViewById(R.id.search_task_id_onDATA);
        finibtn1 = findViewById(R.id.finibtn1);

        mydb = new DataBaseConnection(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        text1.setClickable(false);
        text1.setEnabled(false);
        text2.setClickable(false);
        text2.setEnabled(false);


        searchtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskdata = text3.getText().toString().trim();
                taskfromDATABASE = mydb.getData(taskdata);
                if (taskfromDATABASE.getCount() == 0)
                {
                    Toast.makeText(FinishTask.this, "Task Not Found...", Toast.LENGTH_SHORT).show();
                    text1.setText("");
                    text2.setText("");
                    text3.setText("");
                }
                else
                {
                    taskfromDATABASE.moveToFirst();
                    text1.setText(taskfromDATABASE.getString(1));
                    text2.setText(taskfromDATABASE.getString(2));

                }
                mydb.close();
            }
        });

        finibtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskdata = text3.getText().toString().trim();


                if (text3.length() == 0)
                {
                    Toast.makeText(FinishTask.this, "Enter Task Id...", Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(getApplicationContext(), WorkArea.class));
                     finish();
                }
                else {

                    if (text2.length() == 0) {

                        Toast.makeText(FinishTask.this, "Task not Found...", Toast.LENGTH_SHORT).show();
                        text3.setText("");
                    } else {
                        mydb.getdeletedata(taskdata);
                        Toast.makeText(FinishTask.this, "Task Finished...", Toast.LENGTH_SHORT).show();

                        text1.setText("");
                        text2.setText("");
                        text3.setText("");
                    }
                }
            }
        });
   }
}