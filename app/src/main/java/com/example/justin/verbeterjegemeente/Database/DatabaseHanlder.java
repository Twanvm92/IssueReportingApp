package com.example.justin.verbeterjegemeente.Database;

/**
 * Created by Mika Krooswijk on 10-5-2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import com.example.justin.verbeterjegemeente.domain.Melding;
import com.example.justin.verbeterjegemeente.domain.User;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

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
        private static final String USER_COLUMN_USERID = "_userid";


    // Constructor for the databse handler that will create the database if not already done.
    public DatabaseHanlder (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }



    // The method that will actually create the database, called by the constructor if the database is not already created.
    public void onCreate(SQLiteDatabase database){
        String CREATE_MELDING_TABLE = " CREATE TABLE " + MELDING_TABLE_NAME + " ( " +
                MELDING_COLUMN_ID + " INTEGER PRIMARY KEY," +
                MELDING_COLUMN_IDAPI + " TEXT )";

        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_COLUMN_EMAIL + " TEXT, " +
                USER_COLUMN_FISTNAME + " TEXT, " +
                USER_COLUMN_LASTNAME + " TEXT, " +
                USER_COLUMN_PHONENUMBER + " INTEGER, " +
                USER_COLUMN_USERID + " INTEGER PRIMARY KEY )";

        database.execSQL(CREATE_MELDING_TABLE);
        database.execSQL(CREATE_USER_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addMelding(){

    }

    // Adds a user to the database
    public void addUser(User user){
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_FISTNAME, user.getFirstName());
        values.put(USER_COLUMN_LASTNAME, user.getLastName());
        values.put(USER_COLUMN_PHONENUMBER, user.getPhoneNumber());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(USER_TABLE_NAME, null,  values);
        db.close();
    }

    // Adds a report to the database
    public void addReport(String id){
        ContentValues values = new ContentValues();
        values.put(MELDING_COLUMN_IDAPI, id);


        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(MELDING_TABLE_NAME, null,  values);
        db.close();
    }

    // Gets the user stored in the database
    public User getUser(){

        User user = new User();

        String query = "SELECT * FROM " + USER_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()){


            user.setEmail(cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL)));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(USER_COLUMN_FISTNAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(USER_COLUMN_LASTNAME)));
            user.setPhoneNumber(cursor.getInt(cursor.getColumnIndex(USER_COLUMN_PHONENUMBER)));
        }

        db.close();
        return user;
    }


    // Returns a list of all stored reports
    public ArrayList<String> getReports(){

       ArrayList<String> list = new ArrayList<>();

        String query = "SELECT * FROM " + MELDING_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex(MELDING_COLUMN_IDAPI)));
        }

        db.close();
        return list;
    }

    // Deletes a user from the database
    public void deleteUser(){
        String query = "DELETE FROM " + USER_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        Log.i("DELETE", "all records deleted");
    }

    // Deletes a report from the database
    public void deleteReport(String id){
        String query = "DELETE FROM " + MELDING_TABLE_NAME + " WHERE " + MELDING_COLUMN_IDAPI + " = '" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

    }

    public boolean ReportExists(String id){
        String query = "SELECT * FROM " + MELDING_TABLE_NAME + " WHERE " + MELDING_COLUMN_IDAPI + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToNext()){
            return true;
        }else{
            return false;
        }



    }


}


