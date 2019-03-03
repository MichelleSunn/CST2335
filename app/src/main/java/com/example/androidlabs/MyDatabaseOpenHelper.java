package com.example.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDatabaseFile";
    public static final int VERSION_NUM = 1;
    public static final String TABLE_NAME = "Message";
    public static final String COL_ID = "_id";
    public static final String COL_MESSAGE = "MESSAGE";
    public static final String COL_TYPE = "TYPE";

    public MyDatabaseOpenHelper(Activity ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MESSAGE + " TEXT, " + COL_TYPE + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:" + newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}
