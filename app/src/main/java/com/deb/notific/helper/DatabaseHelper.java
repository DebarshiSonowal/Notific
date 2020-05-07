package com.deb.notific.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "missedcall_database";
    private static final Integer DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MISSEDCALLS_TABLE = "CREATE TABLE " +
                Contract.MissedCalls.TABLE_NAME + " (" +
                Contract.MissedCalls._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.MissedCalls.COLUMN_NAME + " TEXT NOT NULL, " +
                Contract.MissedCalls.COLUMN_NUMBER + " TEXT NOT NULL, " +
                Contract.MissedCalls.COLUMN_TIME + " TEXT NOT NULL" +
                ");";
        db.execSQL(SQL_CREATE_MISSEDCALLS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Contract.MissedCalls.TABLE_NAME);
        onCreate(db);
    }
}
