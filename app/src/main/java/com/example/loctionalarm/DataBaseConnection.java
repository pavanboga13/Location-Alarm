package com.example.loctionalarm;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.ResultSet;
import java.util.HashMap;

public class DataBaseConnection extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "LocationAlaram.db";
    public static final String CONTACTS_TABLE_NAME = "usertasks";
    public static final String CONTACTS_COLUMN_ID = "TaskID";
    public static final String CONTACTS_COLUMN_Place = "TaskPlace";
    public static final String CONTACTS_COLUMN_Details = "TaskDetails";
    private HashMap hp;

    public DataBaseConnection(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table usertasks " + "(TaskID INTEGER PRIMARY KEY AUTOINCREMENT, TaskPlace text,TaskDetails int)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usertasks");
        onCreate(db);
    }

    public void dbinsert(String tplace, String tdetails) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("TaskPlace", tplace);
        contentValues.put("TaskDetails", tdetails);
        db.insert("usertasks", null, contentValues);
    }

    public Cursor gettaskid() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from usertasks", null );
        return res;

    }


    public Cursor getData(String utaskid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertasks where TaskID = "+Integer.parseInt(utaskid)+" ", null );
        return res;
    }

    public void getdeletedata(String utaskid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("usertasks", "TaskID =" +Integer.parseInt(utaskid) , null );
        db.close();
    }




}
