package com.example.justin.verbeterjegemeente.Database;

/**
 * Created by Mika Krooswijk on 10-5-2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHanlder extends SQLiteOpenHelper {


    // Declaring the name, file name and version number of the database in final variable.
    private static final String TAG = "meldingDBHandler";
    private static final String DATABASE_NAME = "testdb.db";
    private static final int DATABASE_VERSION = 1;

    private static final String MELDING_TABLE_NAME = "melding";
        private static final String MELDING_COLUMN_ID = "_meldingId";
        private static final String MELDING_COLUMN_IDAPI = "meldingIdApi";

    private static final String USER_TABLE_NAME = "user";
        private static final String USER_COLUMN_EMAIL  = "email";
        private static final String USER_COLUMN_FISTNAME = "firstname";
        private static final String USER_COLUMN_LASTNAME = "lastname";
        private static final String USER_COLUMN_PHONENUMBER = "phonenumber";
        private static final String USER_COLUMN_USERID = "userid";


    // Constructor for the databse handler that will create the database if not already done.
    public DatabaseHanlder(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // The method that will actually create the database, called by the constructor if the database is not already created.
    public void onCreate(SQLiteDatabase database){
        String CREATE_MELDING_TABLE = "CREATE TABLE " + MELDING_TABLE_NAME + "(" +
                MELDING_COLUMN_ID + " INTEGER PRIMARY KEY," +
                MELDING_COLUMN_IDAPI + " INTEGER";

        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_COLUMN_EMAIL +

        database.execSQL(CREATE_MELDING_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addMelding(){

    }


}


