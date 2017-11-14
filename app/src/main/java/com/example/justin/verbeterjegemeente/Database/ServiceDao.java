package com.example.justin.verbeterjegemeente.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;

/**
 * Created by twanv on 10-11-2017.
 */

@Dao
public interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(ServiceEntry... weather);

    @Query("SELECT * FROM service")
    ServiceEntry getAllServices();

}
