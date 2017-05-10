package com.example.justin.verbeterjegemeente.Database;

/**
 * Created by Mika Krooswijk on 10-5-2017.
 */

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHanlder extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "MeldingDatabase.sqlite";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHanlder(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
