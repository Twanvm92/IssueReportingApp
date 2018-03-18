package com.example.justin.verbeterjegemeente.service.repositories;
/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
import com.example.justin.verbeterjegemeente.data.network.ApiResponse;
import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.util.ApiUtil;
import com.example.justin.verbeterjegemeente.util.Foo;
import com.example.justin.verbeterjegemeente.util.InstantAppExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class NetworkBoundResourceRefreshTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    // removed param() and constructor - T. van Maastricht



    private Function<Foo, Void> saveCallResult;

    private Function<Foo, Boolean> shouldFetch;

    private Function<Void, LiveData<ApiResponse<Foo>>> createCall;

    private MutableLiveData<Foo> dbData = new MutableLiveData<>();

    // used my own version of NetworkBoundResource class - T. van Maastricht
    private NetworkBoundResourceRefresh<Foo, Foo> networkBoundResource;

    // removed fetchedOnce property

    @Before
    public void setUp() throws Exception {

        AppExecutors appExecutors = new InstantAppExecutors();
       MediatorLiveData<Resource<Foo>> toBeUpdatedFoo = new MediatorLiveData<>();

        networkBoundResource = new NetworkBoundResourceRefresh<Foo, Foo>(appExecutors, toBeUpdatedFoo) {
            @Override
            protected void saveCallResult(@NonNull Foo item) {
                saveCallResult.apply(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Foo data) {
                // since test methods don't handle repetitive fetching, call it only once
                return shouldFetch.apply(data);
            }

            @NonNull
            @Override
            protected LiveData<Foo> loadFromDb() {
                return dbData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Foo>> createCall() {
                return createCall.apply(null);
            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void basicFromNetwork() {
        AtomicReference<Foo> saved = new AtomicReference<>();
        // changed shouldFetch Function    - T. van Maastricht
        shouldFetch = (foo) -> Objects.isNull(foo) || Foo.isEmpty(foo);
        Foo fetchedDbValue = new Foo(1);
        saveCallResult = foo -> {
            saved.set(foo);
            dbData.setValue(fetchedDbValue);
            return null;
        };
        final Foo networkResult = new Foo(1);
        createCall = (aVoid) -> ApiUtil.createCall(Response.success(networkResult));

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        dbData.setValue(null);
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(fetchedDbValue));
    }

    @Test
    public void failureFromNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        // changed shouldFetch Function    - T. van Maastricht
        shouldFetch = (foo) -> Objects.isNull(foo) || Foo.isEmpty(foo);
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };
        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        createCall = (aVoid) -> ApiUtil.createCall(Response.error(500, body));

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        dbData.setValue(null);
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(Resource.error("error", null));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithoutNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        // changed shouldFetch Function    - T. van Maastricht
        shouldFetch = (foo) -> Objects.isNull(foo) || Foo.isEmpty(foo);
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        Foo dbFoo = new Foo(1);
        dbData.setValue(dbFoo);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.success(dbFoo));
        assertThat(saved.get(), is(false));
        Foo dbFoo2 = new Foo(2);
        dbData.setValue(dbFoo2);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.success(dbFoo2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithFetchFailure() {
        Foo dbValue = new Foo(1);
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = (foo) -> foo == dbValue;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };
        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        MutableLiveData<ApiResponse<Foo>> apiResponseLiveData = new MutableLiveData();
        createCall = (aVoid) -> apiResponseLiveData;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbValue);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(dbValue));

        apiResponseLiveData.setValue(new ApiResponse<>(Response.error(400, body)));
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(Resource.error("error", dbValue));

        Foo dbValue2 = new Foo(2);
        dbData.setValue(dbValue2);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.error("error", dbValue2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithReFetchSuccess() {
        Foo dbValue = new Foo(1);
        Foo dbValue2 = new Foo(2);
        AtomicReference<Foo> saved = new AtomicReference<>();
        shouldFetch = (foo) -> foo == dbValue;
        saveCallResult = foo -> {
            saved.set(foo);
            dbData.setValue(dbValue2);
            return null;
        };
        MutableLiveData<ApiResponse<Foo>> apiResponseLiveData = new MutableLiveData();
        createCall = (aVoid) -> apiResponseLiveData;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbValue);
        // removed drain() method  - T. van Maastricht
        final Foo networkResult = new Foo(1);
        verify(observer).onChanged(Resource.loading(dbValue));
        apiResponseLiveData.setValue(new ApiResponse<>(Response.success(networkResult)));
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(dbValue2));
        verifyNoMoreInteractions(observer);
    }

}