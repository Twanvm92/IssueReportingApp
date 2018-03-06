package com.example.justin.verbeterjegemeente.viewModel;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.justin.verbeterjegemeente.app.ServiceRequestApplication;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.service.repositories.ServicesRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ServiceListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private ServicesRepository mockServicesRepository;
    private Application application;
    private ServiceListViewModel serviceListViewModel;
    private AndroidViewModel mockAndroidViewModel;
    private Resources resources;
    private MutableLiveData<Resource<List<ServiceEntry>>> serviceListObservable;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);

        mockServicesRepository = mock(ServicesRepository.class);
        application = mock(ServiceRequestApplication.class);
        resources = mock(Resources.class);
        mockAndroidViewModel = mock(AndroidViewModel.class);
        when(mockAndroidViewModel.getApplication()).thenReturn(application);
        when(application.getResources()).thenReturn(resources);
        when(resources.getString(anyInt())).thenReturn("Test");

        serviceListObservable = new MutableLiveData<>();
        when(mockServicesRepository.loadServices()).thenReturn(serviceListObservable);

        serviceListViewModel = new ServiceListViewModel(application, mockServicesRepository);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNotNull() {
        assertThat(serviceListViewModel.getServiceListObservable(), notNullValue());
    }

    @Test
    public void testLoadServicesReturnsLivedata() {
        verify(mockServicesRepository).loadServices();
        verifyNoMoreInteractions(mockServicesRepository);

    }

    @Test
    public void setMainCatagoriesWithoutDataAvailable() {
        assertEquals(1 , serviceListViewModel.mainCatagories.size());
    }

    @Test
    public void setMainCatagoriesWithDataAvailableOnSuccess() {
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        serviceListObservable.observeForever(observer);
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        Resource<List<ServiceEntry>> successResource = Resource.success(serviceEntryList);
        serviceListObservable.setValue(successResource);
        verify(observer).onChanged(successResource);
        serviceListViewModel.setMainCatagories(serviceListObservable.getValue().data);
        assertEquals(2 , serviceListViewModel.mainCatagories.size());
    }

    @Test
    public void setMainCatagoriesWithoutDataAvailableOnError() {
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        serviceListObservable.observeForever(observer);
        Resource<List<ServiceEntry>> failedResource = Resource.error("Test", null);
        serviceListObservable.setValue(failedResource);
        verify(observer).onChanged(failedResource);
        assertEquals(1 , serviceListViewModel.mainCatagories.size());
    }

    @Test
    public void setMainCatagoriesWithDataAvailableOnError() {
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        serviceListObservable.observeForever(observer);
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        Resource<List<ServiceEntry>> failedResource = Resource.error("Test", serviceEntryList);
        serviceListObservable.setValue(failedResource);
        verify(observer).onChanged(failedResource);
        serviceListViewModel.setMainCatagories(serviceListObservable.getValue().data);
        assertEquals(2 , serviceListViewModel.mainCatagories.size());
    }

    @Test
    public void fillSubCategorySpinnerOnMainCatagorySelected() {
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        serviceListObservable.observeForever(observer);
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        Resource<List<ServiceEntry>> successResource = Resource.success(serviceEntryList);
        serviceListObservable.setValue(successResource);
        serviceListViewModel.setMainCatagories(serviceListObservable.getValue().data);
        AdapterView adapterView = mock(AdapterView.class);
        when(adapterView.getSelectedItem()).thenReturn(new Object());
        when(adapterView.getSelectedItem().toString()).thenReturn("Lanterns");
        serviceListViewModel.onItemSelected(adapterView, mock(View.class), 2, 0);
        assertEquals(2 , serviceListViewModel.subCatagories.size());
        assertEquals(serviceEntry.getService_name(), serviceListViewModel.subCatagories.get(1).string);
    }

    @Test
    public void SubCategorySpinnerOnNoMainCatagorySelected() {
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        serviceListObservable.observeForever(observer);
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        Resource<List<ServiceEntry>> successResource = Resource.success(serviceEntryList);
        serviceListObservable.setValue(successResource);
        serviceListViewModel.setMainCatagories(serviceListObservable.getValue().data);
        AdapterView adapterView = mock(AdapterView.class);
        when(adapterView.getSelectedItem()).thenReturn(new Object());
        when(adapterView.getSelectedItem().toString()).thenReturn("Select a main catagory");
        serviceListViewModel.onItemSelected(adapterView, mock(View.class), 2, 0);
        assertEquals(1 , serviceListViewModel.subCatagories.size());
    }

    @Test
    public void updateServicesReturnsResourceWithDataSuccess() {
        MediatorLiveData<Resource<List<ServiceEntry>>> liveData = new MediatorLiveData<>();
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        liveData.observeForever(observer);
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        Resource<List<ServiceEntry>> successResource = Resource.success(serviceEntryList);

        doAnswer(invocation -> {
//            serviceListObservable.setValue(successResource);
            MediatorLiveData<Resource<List<ServiceEntry>>> liveDataArgument= invocation.getArgument(0);
            liveDataArgument.setValue(successResource);
            return null;
        }).when(mockServicesRepository).refreshServices(any(MediatorLiveData.class));
        mockServicesRepository.refreshServices(liveData);
        verify(observer).onChanged(successResource);
    }

    @Test
    public void updateServicesReturnsResourceWithoutDataError() {
        MediatorLiveData<Resource<List<ServiceEntry>>> liveData = new MediatorLiveData<>();
        Observer<Resource<List<ServiceEntry>>> observer = mock(Observer.class);
        ServiceEntry serviceEntry = new ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns");
        liveData.observeForever(observer);
        List<ServiceEntry> serviceEntryList = new ArrayList<>();
        serviceEntryList.add(serviceEntry);
        Resource<List<ServiceEntry>> successResource = Resource.success(serviceEntryList);

        doAnswer(invocation -> {
//            serviceListObservable.setValue(successResource);
            MediatorLiveData<Resource<List<ServiceEntry>>> liveDataArgument= invocation.getArgument(0);
            liveDataArgument.setValue(successResource);
            return null;
        }).when(mockServicesRepository).refreshServices(any(MediatorLiveData.class));
        mockServicesRepository.refreshServices(liveData);
        verify(observer).onChanged(successResource);
    }
}