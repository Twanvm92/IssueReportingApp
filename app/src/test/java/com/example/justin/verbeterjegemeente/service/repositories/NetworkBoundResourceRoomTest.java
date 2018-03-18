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
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

// not using parameterized class - T. van Maastricht
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class NetworkBoundResourceRoomTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    // removed param() and constructor - T. van Maastricht

    // used my own NetworkBoundResourceClass - T. van Maastricht

    private Function<List<Foo>, Void> saveCallResult;

    private Function<List<Foo>, Boolean> shouldFetch;

    private Function<Void, LiveData<ApiResponse<List<Foo>>>> createCall;

    private MutableLiveData<List<Foo>> dbData = new MutableLiveData<>();
    // added dbdata with list.
    private MutableLiveData<List<Foo>> dbDataList = new MutableLiveData<>();

    private NetworkBoundResourceRoom<List<Foo>, List<Foo>> networkBoundResource;

    private AtomicBoolean fetchedOnce = new AtomicBoolean(false);
    @Before
    public void setUp() throws Exception {
        AppExecutors appExecutors = new InstantAppExecutors();

        networkBoundResource = new NetworkBoundResourceRoom<List<Foo>, List<Foo>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Foo> item) {
                saveCallResult.apply(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Foo> data) {
                // since test methods don't handle repetitive fetching, call it only once
                return shouldFetch.apply(data) && fetchedOnce.compareAndSet(false, true);
            }

            @NonNull
            @Override
            protected LiveData<List<Foo>> loadFromDb() {
                return dbData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Foo>>> createCall() {
                return createCall.apply(null);
            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void basicFromNetwork() {
        AtomicReference<List<Foo>> saved = new AtomicReference<>();
        shouldFetch = Objects::isNull;
        List<Foo> fooList = new ArrayList<>();
        Foo fetchedDbValue = new Foo(1);
        fooList.add(fetchedDbValue);
        saveCallResult = foos -> {
            saved.set(foos);
            dbData.setValue(fooList);
            return null;
        };
        final Foo networkResult = new Foo(1);
        List<Foo> fooListResult = new ArrayList<>();
        fooListResult.add(networkResult);
        createCall = (aVoid) -> ApiUtil.createCall(Response.success(fooListResult));

        Observer<Resource<List<Foo>>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        dbData.setValue(null);
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(fooListResult));
        verify(observer).onChanged(Resource.success(fooList));
    }

    @Test
    public void failureFromNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = Objects::isNull;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };
        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        createCall = (aVoid) -> ApiUtil.createCall(Response.error(500, body));

        Observer<Resource<List<Foo>>> observer = Mockito.mock(Observer.class);
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
        shouldFetch = Objects::isNull;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };

        Observer<Resource<List<Foo>>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        Foo dbFoo = new Foo(1);
        List<Foo> dbFooList = new ArrayList<>();
        dbFooList.add(dbFoo);
        dbData.setValue(dbFooList);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.success(dbFooList));
        assertThat(saved.get(), is(false));
        Foo dbFoo2 = new Foo(2);
        List<Foo> dbFooList2 = new ArrayList<>();
        dbFooList2.add(dbFoo2);
        dbData.setValue(dbFooList2);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.success(dbFooList2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithEmptyListWithoutNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = Objects::isNull;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };

        Observer<Resource<List<Foo>>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        Foo dbFoo1 = new Foo(1);
        List<Foo> dbFooList1 = new ArrayList<>();
        dbFooList1.add(dbFoo1);
        dbData.setValue(dbFooList1);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.success(dbFooList1));
        assertThat(saved.get(), is(false));
        Foo dbFoo2 = new Foo(2);
        List<Foo> dbFooList2 = new ArrayList<>();
        dbFooList1.add(dbFoo2);
        dbData.setValue(dbFooList2);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.success(dbFooList2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithFetchFailure() {
        Foo dbValue = new Foo(1);
        List<Foo> dbValueList1 = new ArrayList<>();
        dbValueList1.add(dbValue);
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = (foos) -> foos == dbValueList1;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };
        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        MutableLiveData<ApiResponse<List<Foo>>> apiResponseLiveData = new MutableLiveData();
        createCall = (aVoid) -> apiResponseLiveData;

        Observer<Resource<List<Foo>>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbValueList1);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(dbValueList1));

        apiResponseLiveData.setValue(new ApiResponse<>(Response.error(400, body)));
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(Resource.error("error", dbValueList1));

        Foo dbValue2 = new Foo(2);
        List<Foo> dbValueList2 = new ArrayList<>();
        dbValueList2.add(dbValue2);
        dbData.setValue(dbValueList2);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.error("error", dbValueList2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithReFetchSuccess() {
        Foo dbValue = new Foo(1);
        List<Foo> dbValueList = new ArrayList<>();
        dbValueList.add(dbValue);
        Foo dbValue2 = new Foo(2);
        List<Foo> dbValueList2 = new ArrayList<>();
        dbValueList2.add(dbValue2);
        AtomicReference<List<Foo>> saved = new AtomicReference<>();
        shouldFetch = (foos) -> foos == dbValueList;
        saveCallResult = foos -> {
            saved.set(foos);
            dbData.setValue(dbValueList2);
            return null;
        };
        MutableLiveData<ApiResponse<List<Foo>>> apiResponseLiveData = new MutableLiveData();
        createCall = (aVoid) -> apiResponseLiveData;

        Observer<Resource<List<Foo>>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbValueList);
        // removed drain() method  - T. van Maastricht
        final Foo networkResult = new Foo(1);
        List<Foo> networkResultList = new ArrayList<>();
        networkResultList.add(networkResult);
        verify(observer).onChanged(Resource.loading(dbValueList));
        apiResponseLiveData.setValue(new ApiResponse<>(Response.success(networkResultList)));
        // removed drain() method  - T. van Maastricht
        assertThat(saved.get(), is(networkResultList));
        verify(observer).onChanged(Resource.success(dbValueList2));
        verifyNoMoreInteractions(observer);
    }



}