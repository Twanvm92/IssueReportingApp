package com.example.justin.verbeterjegemeente.di;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by twanv on 17-12-2017.
 */

@Module
public class ExecutorsModule {

    @Provides
    @Singleton
    public Executor provideSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }

}
