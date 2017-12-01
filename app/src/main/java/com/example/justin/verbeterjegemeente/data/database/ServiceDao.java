package com.example.justin.verbeterjegemeente.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Created by twanv on 10-11-2017.
 */

@Dao
public interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(ServiceEntry... serviceEntries);

    @Query("SELECT * FROM service")
    ServiceEntry getAllServices();

    @Query("DELETE FROM service")
    void deleteAllServices();

}
