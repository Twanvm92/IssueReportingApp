package com.example.justin.verbeterjegemeente.data.database;
import android.arch.persistence.room.RoomDatabase;


/**
 * Created by twanv on 14-11-2017.
 */

@android.arch.persistence.room.Database(entities = {ServiceEntry.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract ServiceDao serviceDao();
}
