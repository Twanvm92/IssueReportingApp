package com.example.justin.verbeterjegemeente.viewModel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import com.example.justin.verbeterjegemeente.data.network.Resource
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest
import com.example.justin.verbeterjegemeente.service.repositories.ServiceRequestsRepository
import com.example.justin.verbeterjegemeente.ui.BredaMapInterface
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.IsNull
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class ServiceRequestListViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private val mockServiceRequestsRepository: ServiceRequestsRepository = mock()
    private val application: Application = mock()
    private lateinit var serviceRequestListViewModel: ServiceRequestListViewModel

    @Before
    fun setUp() {
        serviceRequestListViewModel = ServiceRequestListViewModel(application,
                mockServiceRequestsRepository)
    }

    @Test
    fun testNotNull() = Assert.assertThat(serviceRequestListViewModel.serviceRequestListObservable, IsNull.notNullValue())

    @Test
    fun testMapNotLoaded() {
        Assert.assertEquals(false, serviceRequestListViewModel.mapLoaded.value)
    }

    @Test
    fun testBredaMapInterfaceSet() {
        serviceRequestListViewModel.setBredaMapInterface()

        Assert.assertThat(serviceRequestListViewModel.bredaMapInterface.value,
                instanceOf(BredaMapInterface::class.java))
    }

    @Test
    fun refreshServiceRequestsReturnsResourceWithDataSuccess() {
        val liveData = MediatorLiveData<Resource<List<ServiceRequest>>>()
        val observer = mock<Observer<Resource<List<ServiceRequest>>>>()
        val serviceRequest = ServiceRequest()
        serviceRequest.serviceRequestId = "43Re"
        serviceRequest.serviceCode = "Lanterns"
        liveData.observeForever(observer)
        val serviceRequestList = ArrayList<ServiceRequest>()
        serviceRequestList.add(serviceRequest)
        val successResource =
                Resource.success<List<ServiceRequest>>(serviceRequestList)

        liveData.value = successResource
        whenever(mockServiceRequestsRepository
                .refreshServiceRequests(any(), any(), any()))
                .thenReturn(liveData)

        val updatedRequests =
                mockServiceRequestsRepository.refreshServiceRequests(any(), any(), any())
        verify(observer).onChanged(successResource)
        Assert.assertEquals(liveData, updatedRequests)
    }

    @Test
    fun refreshServiceRequestsReturnsResourceWithoutDataError() {
        val liveData = MediatorLiveData<Resource<List<ServiceRequest>>>()
        val observer = mock<Observer<Resource<List<ServiceRequest>>>>()
        liveData.observeForever(observer)
        val errorResource = Resource.error<List<ServiceRequest>>("error", null)

        liveData.value = errorResource
        whenever(mockServiceRequestsRepository
                .refreshServiceRequests(any(), any(), any()))
                .thenReturn(liveData)

        val emptyServiceRequests =
                mockServiceRequestsRepository.refreshServiceRequests(any(), any(), any())
        verify(observer).onChanged(errorResource)
        Assert.assertEquals(liveData, emptyServiceRequests)
    }

}