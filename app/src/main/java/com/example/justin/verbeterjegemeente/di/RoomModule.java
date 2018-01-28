package com.example.justin.verbeterjegemeente.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.justin.verbeterjegemeente.data.database.Database;
import com.example.justin.verbeterjegemeente.data.database.ServiceDao;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.service.repositories.ServiceRequestsRepository;
import com.example.justin.verbeterjegemeente.service.repositories.ServicesRepository;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by twanv on 17-12-2017.
 */

@Module(
        includes = {NetworkModule.class, ExecutorsModule.class}
)
public class RoomModule {

    private Database database;

    public RoomModule(Application mApplication) {
        database = Room.databaseBuilder(mApplication, Database.class, "db").build();
    }

    @Singleton
    @Provides
    Database providesRoomDatabase() {
        return database;
    }

    @Singleton
    @Provides
    ServiceDao providesServiceDao(Database database) {
        return database.serviceDao();
    }

    @Singleton
    @Provides
    ServicesRepository providesServiceRepository(ServiceClient serviceClient, ServiceDao serviceDao,
                                                 Executor diskIO) {
        return new ServicesRepository(serviceClient, serviceDao, diskIO);
    }

    @Singleton
    @Provides
    ServiceRequestsRepository providesServiceRequestsRepository(ServiceClient serviceClient,
                                                                Executor diskIO) {
        return new ServiceRequestsRepository(serviceClient, diskIO);
    }
}
