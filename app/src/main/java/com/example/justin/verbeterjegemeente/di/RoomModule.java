package com.example.justin.verbeterjegemeente.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
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
                                                 AppExecutors executors) {
        return new ServicesRepository(serviceClient, serviceDao, executors);
    }

    @Singleton
    @Provides
    ServiceRequestsRepository providesServiceRequestsRepository(ServiceClient serviceClient,
                                                                AppExecutors appExecutors) {
        return new ServiceRequestsRepository(serviceClient, appExecutors);
    }
}
