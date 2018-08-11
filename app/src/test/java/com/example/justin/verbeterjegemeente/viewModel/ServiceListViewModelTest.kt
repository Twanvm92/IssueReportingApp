package com.example.justin.verbeterjegemeente.viewModel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry
import com.example.justin.verbeterjegemeente.data.network.Resource
import com.example.justin.verbeterjegemeente.service.repositories.ServicesRepository
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class)
class ServiceListViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private var mockServicesRepository: ServicesRepository = mock()
    private var application: Application = mock()
    private val mockAndroidViewmodel: AndroidViewModel = mock()
    private lateinit var serviceListViewModel: ServiceListViewModel
    private var resources: Resources = mock()
    private var serviceListObservable: MutableLiveData<Resource<List<ServiceEntry>>> = MutableLiveData()

    @Before
    fun setUp() {
        PowerMockito.mockStatic(Log::class.java)

        whenever(mockServicesRepository.loadServices()).thenReturn(serviceListObservable)
        whenever(mockAndroidViewmodel.getApplication<Application>()).thenReturn(application)
        whenever(application.resources).thenReturn(resources)
        whenever(resources.getString(anyInt())).thenReturn("Test")
        serviceListViewModel = ServiceListViewModel(application, mockServicesRepository)

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    fun testNotNull() {
        assertThat(serviceListViewModel.serviceListObservable, notNullValue())
    }

    @Test
    fun testLoadServicesReturnsLivedata() {
        verify<ServicesRepository>(mockServicesRepository).loadServices()
        verifyNoMoreInteractions(mockServicesRepository)
    }

    @Test
    fun setMainCatagoriesWithoutDataAvailable() {
        assertEquals(1, serviceListViewModel.mainCatagories.size.toLong())
    }

    @Test
    fun setMainCatagoriesWithDataAvailableOnSuccess() {
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        serviceListObservable.observeForever(observer)
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val successResource = Resource.success<List<ServiceEntry>>(serviceEntryList)
        serviceListObservable.value = successResource
        verify<Observer<Resource<List<ServiceEntry>>>>(observer).onChanged(successResource)
        serviceListViewModel.setMainCatagories(serviceListObservable.value!!.data)
        assertEquals(2, serviceListViewModel.mainCatagories.size.toLong())
    }

    @Test
    fun setMainCatagoriesWithoutDataAvailableOnError() {
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        serviceListObservable.observeForever(observer)
        val failedResource = Resource.error<List<ServiceEntry>>("Test", null)
        serviceListObservable.value = failedResource
        verify<Observer<Resource<List<ServiceEntry>>>>(observer).onChanged(failedResource)
        assertEquals(1, serviceListViewModel.mainCatagories.size.toLong())
    }

    @Test
    fun setMainCatagoriesWithDataAvailableOnError() {
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        serviceListObservable.observeForever(observer)
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val failedResource = Resource.error<List<ServiceEntry>>("Test", serviceEntryList)
        serviceListObservable.value = failedResource
        verify<Observer<Resource<List<ServiceEntry>>>>(observer).onChanged(failedResource)
        serviceListViewModel.setMainCatagories(serviceListObservable.value!!.data)
        assertEquals(2, serviceListViewModel.mainCatagories.size.toLong())
    }

    @Test
    fun fillSubCategorySpinnerOnMainCatagorySelected() {
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        serviceListObservable.observeForever(observer)
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val successResource = Resource.success<List<ServiceEntry>>(serviceEntryList)
        serviceListObservable.value = successResource
        serviceListViewModel.setMainCatagories(serviceListObservable.value!!.data)
        val adapterView = mock<AdapterView<*>>()
        `when`(adapterView.selectedItem).thenReturn(Any())
        `when`(adapterView.selectedItem.toString()).thenReturn("Lanterns")
        serviceListViewModel.onItemSelected(adapterView, mock(), 2)
        assertEquals(2, serviceListViewModel.subCatagories.size.toLong())
        assertEquals(serviceEntry.service_name, serviceListViewModel.subCatagories[1].string)
    }

    @Test
    fun SubCategorySpinnerOnNoMainCatagorySelected() {
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        serviceListObservable.observeForever(observer)
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val successResource = Resource.success<List<ServiceEntry>>(serviceEntryList)
        serviceListObservable.value = successResource
        serviceListViewModel.setMainCatagories(serviceListObservable.value!!.data)

        assertEquals(1, serviceListViewModel.subCatagories.size.toLong())
        assertEquals(false, serviceListViewModel.mainError.get())

    }

    @Test
    fun ShowErrorOnNoMainCatagorySelectedByUserAfterSpinnerInitialised() {
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        serviceListObservable.observeForever(observer)
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val successResource = Resource.success<List<ServiceEntry>>(serviceEntryList)
        serviceListObservable.value = successResource
        val adapterView = mock<AdapterView<*>>()
        `when`(adapterView.selectedItem).thenReturn(Any())
        val defaultS = "Kies een hoofdcategorie"
        `when`(resources.getString(ArgumentMatchers.anyInt())).thenReturn(defaultS)
        `when`(adapterView.selectedItem.toString()).thenReturn(defaultS)
        serviceListViewModel.onItemSelected(adapterView, mock(View::class.java), 2)
        assertEquals(false, serviceListViewModel.mainError.get())

        serviceListViewModel.onItemSelected(adapterView, mock(View::class.java), 2)
        assertEquals(true, serviceListViewModel.mainError.get())
    }

    @Test
    fun ShowNoErrorOnSubCatagorySelectedByUserAfterSpinnerInitialised() {
        val adapterView = mock<AdapterView<*>>()
        `when`(adapterView.selectedItem).thenReturn(Any())
        val defaultS = "Kies een subcategorie"
        `when`(resources.getString(ArgumentMatchers.anyInt())).thenReturn(defaultS)
        `when`(adapterView.selectedItem.toString()).thenReturn(defaultS)
        serviceListViewModel.onItemSelected(adapterView, mock(View::class.java), 2, 0)
        assertEquals(false, serviceListViewModel.subError.get())

        val testSubCategory = "TestCategory"
        `when`(adapterView.selectedItem.toString()).thenReturn(testSubCategory)
        serviceListViewModel.onItemSelected(adapterView, mock(View::class.java), 2, 0)
        assertEquals(false, serviceListViewModel.subError.get())
    }

    @Test
    fun ShowErrorOnNoSubCatagorySelectedByUserAfterSpinnerInitialised() {
        val adapterView = mock<AdapterView<*>>()
        `when`(adapterView.selectedItem).thenReturn(Any())
        val defaultS = "Kies een subcategorie"
        `when`(resources.getString(ArgumentMatchers.anyInt())).thenReturn(defaultS)
        `when`(adapterView.selectedItem.toString()).thenReturn(defaultS)
        serviceListViewModel.onItemSelected(adapterView, mock(View::class.java), 2, 0)
        assertEquals(false, serviceListViewModel.subError.get())

        serviceListViewModel.onItemSelected(adapterView, mock(View::class.java), 2, 0)
        assertEquals(true, serviceListViewModel.subError.get())
    }

    @Test
    fun updateServicesReturnsResourceWithDataSuccess() {
        val liveData = MediatorLiveData<Resource<List<ServiceEntry>>>()
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        liveData.observeForever(observer)
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val successResource = Resource.success<List<ServiceEntry>>(serviceEntryList)

        doAnswer { invocation ->
            val liveDataArgument = invocation.getArgument<MediatorLiveData<Resource<List<ServiceEntry>>>>(0)
            liveDataArgument.value = successResource
            null
        }.`when`<ServicesRepository>(mockServicesRepository).refreshServices(any())
        mockServicesRepository.refreshServices(liveData)
        verify<Observer<Resource<List<ServiceEntry>>>>(observer).onChanged(successResource)
    }

    @Test
    fun updateServicesReturnsResourceWithoutDataError() {
        val liveData = MediatorLiveData<Resource<List<ServiceEntry>>>()
        val observer = mock<Observer<Resource<List<ServiceEntry>>>>()
        val serviceEntry = ServiceEntry(1, "ABCD",
                "testservice", "This is a test", false, "test",
                "none", "Lanterns")
        liveData.observeForever(observer)
        val serviceEntryList = ArrayList<ServiceEntry>()
        serviceEntryList.add(serviceEntry)
        val errorResource = Resource.error<List<ServiceEntry>>("Error", null)

        doAnswer { invocation ->
            //            serviceListObservable.setValue(successResource);
            val liveDataArgument = invocation.getArgument<MediatorLiveData<Resource<List<ServiceEntry>>>>(0)
            liveDataArgument.value = errorResource
            null
        }.`when`<ServicesRepository>(mockServicesRepository).refreshServices(any())
        mockServicesRepository.refreshServices(liveData)
        verify<Observer<Resource<List<ServiceEntry>>>>(observer).onChanged(errorResource)
    }
}