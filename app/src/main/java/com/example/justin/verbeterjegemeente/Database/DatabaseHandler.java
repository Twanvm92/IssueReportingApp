package com.example.justin.verbeterjegemeente.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import com.example.justin.verbeterjegemeente.domain.User;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Declaring the name, file name and version number of the database in final variable.
    private static final String TAG = "meldingDBHandler";
    private static final String DATABASE_NAME = "testdb.db";
    private static final int DATABASE_VERSION = 2;
    private static final String MELDING_TABLE_NAME = "melding";
    private static final String MELDING_COLUMN_ID = "_meldingId";
    private static final String MELDING_COLUMN_IDAPI = "meldingIdApi";
    private static final String MELDING_COLUMN_UPDATETIME = "updatetime";
    private static final String USER_TABLE_NAME = "user";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_FISTNAME = "firstname";
    private static final String USER_COLUMN_LASTNAME = "lastname";
    private static final String USER_COLUMN_PHONENUMBER = "phonenumber";
    private static final String USER_COLUMN_USERID = "_userid";
    private static final String UPVOTE_TABLE_NAME = "upvotes";
    private static final String UPVOTE_COLUMN_SERVICE_REQUEST_ID = "meldingId";
    private static final String CREATE_UPVOTE_TABLE = "CREATE TABLE " + UPVOTE_TABLE_NAME + "(" +
            UPVOTE_COLUMN_SERVICE_REQUEST_ID + " INTEGER PRIMARY KEY )";

    // Constructor for the database handler that will create the database if not already done.
    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // The method that will actually create the database, called by the constructor if the database is not already created.
    public void onCreate(SQLiteDatabase database) {
        String CREATE_MELDING_TABLE = " CREATE TABLE " + MELDING_TABLE_NAME + " ( " +
                MELDING_COLUMN_ID + " INTEGER PRIMARY KEY," +
                MELDING_COLUMN_UPDATETIME + " TEXT , " +
                MELDING_COLUMN_IDAPI + " TEXT )";

        String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_COLUMN_EMAIL + " TEXT, " +
                USER_COLUMN_FISTNAME + " TEXT, " +
                USER_COLUMN_LASTNAME + " TEXT, " +
                USER_COLUMN_PHONENUMBER + " INTEGER, " +
                USER_COLUMN_USERID + " INTEGER PRIMARY KEY )";

        String c = "CREATE TABLE " + UPVOTE_TABLE_NAME + "(" +
                UPVOTE_COLUMN_SERVICE_REQUEST_ID + " INTEGER PRIMARY KEY )";

        database.execSQL(CREATE_MELDING_TABLE);
        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_UPVOTE_TABLE);
        database.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_UPVOTE_TABLE);
        db.close();
    }

    public void addMelding() {

    }

    // Adds a user to the database
    public void addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_FISTNAME, user.getFirstName());
        values.put(USER_COLUMN_LASTNAME, user.getLastName());
        values.put(USER_COLUMN_PHONENUMBER, user.getPhoneNumber());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(USER_TABLE_NAME, null, values);
        db.close();
    }

    // Adds a report to the database
    public void addReport(String srID){
        Log.i("SR ID", srID);

        ContentValues values = new ContentValues();
        values.put(MELDING_COLUMN_IDAPI, srID);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(MELDING_TABLE_NAME, null, values);
        db.close();
    }

    // Adds a report to the database
    public void addUpvote(String srID){
        Log.i("SR ID", srID);

        ContentValues values = new ContentValues();
        values.put(UPVOTE_COLUMN_SERVICE_REQUEST_ID, srID);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(UPVOTE_TABLE_NAME, null, values);
        db.close();
    }

    // Adds a report to the database
    public void updateReportUpdatetime(String srID, String srUpdateTime){
        Log.i("DatabaseHandler: ", "SR ID: " +  srID);
        Log.i("DatabaseHandler: ", "SR Updatetime: " + srUpdateTime);

        ContentValues values = new ContentValues();
        values.put(MELDING_COLUMN_UPDATETIME, srUpdateTime);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(MELDING_TABLE_NAME, values, MELDING_COLUMN_IDAPI + " = ?", new String[]{srID});
        db.close();
    }

    // Gets the user stored in the database
    public User getUser() {

        User user = new User();

        String query = "SELECT * FROM " + USER_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {


            user.setEmail(cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL)));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(USER_COLUMN_FISTNAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(USER_COLUMN_LASTNAME)));
            user.setPhoneNumber(cursor.getInt(cursor.getColumnIndex(USER_COLUMN_PHONENUMBER)));
        }

        cursor.close();

        db.close();
        return user;
    }

    // Returns a list of all stored reports
    public ArrayList<ServiceRequest> getReports(){

        ArrayList<ServiceRequest> list = new ArrayList<>();
        String query = "SELECT * FROM " + MELDING_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()){
            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setUpdatedDatetime(cursor.getString(cursor.getColumnIndex(MELDING_COLUMN_UPDATETIME)));
            serviceRequest.setServiceRequestId(cursor.getString(cursor.getColumnIndex(MELDING_COLUMN_IDAPI)));
            list.add(serviceRequest);

        }
        cursor.close();

        db.close();
        return list;
    }

    // Deletes a user from the database
    public void deleteUser() {
        String query = "DELETE FROM " + USER_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
        Log.i("DELETE", "all records deleted");
    }

    // Deletes a report from the database
    public void deleteReport(String id) {
        String query = "DELETE FROM " + MELDING_TABLE_NAME + " WHERE " + MELDING_COLUMN_IDAPI + " = '" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public boolean ReportExists(String id) {
        String query = "SELECT * FROM " + MELDING_TABLE_NAME + " WHERE " + MELDING_COLUMN_IDAPI + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean reportExists = cursor.moveToNext();
        cursor.close();
        db.close();

        return reportExists;

    }

    public boolean upvoteExists(String id) {
        String query = "SELECT * FROM " + UPVOTE_TABLE_NAME + " WHERE " + UPVOTE_COLUMN_SERVICE_REQUEST_ID + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean upvoteExists = cursor.moveToNext();
        cursor.close();
        db.close();

        return upvoteExists;
    }

}


