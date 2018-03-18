package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.util.Log;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.database.ServiceDao;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.ApiResponse;
import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.util.ApiUtil;
import com.example.justin.verbeterjegemeente.util.InstantAppExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ServicesRepositoryTest {

    private ServiceClient serviceClient;
    private ServiceDao serviceDao;
    private ServicesRepository servicesRepository;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        serviceDao = mock(ServiceDao.class);
        serviceClient = mock(ServiceClient.class);
        servicesRepository = new ServicesRepository(serviceClient, serviceDao, new InstantAppExecutors());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadServicesFromDb() {
        servicesRepository.loadServices();
        verify(serviceDao).getAllServices();
    }

    @Test
    public void loadServicesFromNetwork() {
        MutableLiveData<List<ServiceEntry>> dbData = new MutableLiveData<>();
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        ServiceEntry serviceEntry2 = new ServiceEntry(2, "ACCC",
                "anotherTestService", "This is also a test", false, "test",
                "none", "Roads");
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        serviceEntryList.add(serviceEntry2);

        when(serviceDao.getAllServices()).thenReturn(dbData);
        LiveData<ApiResponse<List<ServiceEntry>>> call = ApiUtil.successCall(serviceEntryList);
        when(serviceClient.getServices(Constants.LANG_EN)).thenReturn(call);
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);

        servicesRepository.loadServices().observeForever(observer);
        verify(serviceClient, never()).getServices(Constants.LANG_EN);
        MutableLiveData<List<ServiceEntry>> updatedDbData = new MutableLiveData<>();
        when(serviceDao.getAllServices()).thenReturn(updatedDbData);
        dbData.setValue(null);
        verify(serviceClient).getServices(Constants.LANG_EN);

    }

    @Test
    public void loadServicesButDontGoToNetworkSecondTime() {
        MutableLiveData<List<ServiceEntry>> dbData = new MutableLiveData<>();
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        ServiceEntry serviceEntry2 = new ServiceEntry(2, "ACCC",
                "anotherTestService", "This is also a test", false, "test",
                "none", "Roads");
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        serviceEntryList.add(serviceEntry2);

        when(serviceDao.getAllServices()).thenReturn(dbData);
        LiveData<ApiResponse<List<ServiceEntry>>> call = ApiUtil.successCall(serviceEntryList);
        when(serviceClient.getServices(Constants.LANG_EN)).thenReturn(call);
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);

        servicesRepository.loadServices().observeForever(observer);
        verify(serviceClient, never()).getServices(Constants.LANG_EN);
        MutableLiveData<List<ServiceEntry>> updatedDbData = new MutableLiveData<>();
        when(serviceDao.getAllServices()).thenReturn(updatedDbData);
        dbData.setValue(null);
        verify(serviceClient).getServices(Constants.LANG_EN);

        // setup another call for loadServices()
        MutableLiveData<List<ServiceEntry>> dbDataT = new MutableLiveData<>();

        when(serviceDao.getAllServices()).thenReturn(dbDataT);
        Observer<Resource<List<ServiceEntry>>> observerT = mock(Observer.class);

        servicesRepository.loadServices().observeForever(observerT);
        MutableLiveData<List<ServiceEntry>> updatedDbDataT = new MutableLiveData<>();
        when(serviceDao.getAllServices()).thenReturn(updatedDbDataT);
        dbDataT.setValue(null);
        // check if loadServices was only called once because of initialized boolean set to true
        // at first call
        verify(serviceClient, times(1)).getServices(Constants.LANG_EN);
    }

    @Test
    public void refreshServicesGoToNetwork() {
        MutableLiveData<List<ServiceEntry>> dbData = new MutableLiveData<>();
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        ServiceEntry serviceEntry2 = new ServiceEntry(2, "ACCC",
                "anotherTestService", "This is also a test", false, "test",
                "none", "Roads");
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        serviceEntryList.add(serviceEntry2);

        when(serviceDao.getAllServices()).thenReturn(dbData);
        LiveData<ApiResponse<List<ServiceEntry>>> call = ApiUtil.successCall(serviceEntryList);
        when(serviceClient.getServices(Constants.LANG_EN)).thenReturn(call);
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);

        MutableLiveData<Resource<List<ServiceEntry>>> serviceListObservable = new MediatorLiveData<>();
        servicesRepository.refreshServices
                ((MediatorLiveData<Resource<List<ServiceEntry>>>) serviceListObservable)
                .observeForever(observer);
        verify(serviceClient, never()).getServices(Constants.LANG_EN);
        MutableLiveData<List<ServiceEntry>> updatedDbData = new MutableLiveData<>();
        when(serviceDao.getAllServices()).thenReturn(updatedDbData);
        dbData.setValue(null);
        verify(serviceClient).getServices(Constants.LANG_EN);

    }

    @Test
    public void refreshServicesDontGoToNetwork() {
        MutableLiveData<List<ServiceEntry>> dbData = new MutableLiveData<>();
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        ServiceEntry serviceEntry2 = new ServiceEntry(2, "ACCC",
                "anotherTestService", "This is also a test", false, "test",
                "none", "Roads");
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        serviceEntryList.add(serviceEntry2);

        when(serviceDao.getAllServices()).thenReturn(dbData);
        LiveData<ApiResponse<List<ServiceEntry>>> call = ApiUtil.successCall(serviceEntryList);
        when(serviceClient.getServices(Constants.LANG_EN)).thenReturn(call);
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);

        MutableLiveData<Resource<List<ServiceEntry>>> serviceListObservable = new MediatorLiveData<>();
        servicesRepository.refreshServices
                ((MediatorLiveData<Resource<List<ServiceEntry>>>) serviceListObservable)
                .observeForever(observer);
        verify(serviceClient, never()).getServices(Constants.LANG_EN);
        MutableLiveData<List<ServiceEntry>> updatedDbData = new MutableLiveData<>();
        when(serviceDao.getAllServices()).thenReturn(updatedDbData);
        dbData.setValue(serviceEntryList);
        verify(serviceClient, never()).getServices(Constants.LANG_EN);

    }
}