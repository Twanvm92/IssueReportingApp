package com.example.justin.verbeterjegemeente.service.repositories

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.provider.MediaStore
import com.example.justin.verbeterjegemeente.data.network.ApiResponse
import com.example.justin.verbeterjegemeente.data.network.Resource
import com.example.justin.verbeterjegemeente.data.network.ServiceClient
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest
import com.example.justin.verbeterjegemeente.util.ApiUtil
import com.example.justin.verbeterjegemeente.util.InstantAppExecutors
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import java.util.*

class ServiceRequestsRepositoryTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private var serviceClient: ServiceClient = mock()
    private var serviceRequestsRepository: ServiceRequestsRepository =
            ServiceRequestsRepository(serviceClient, InstantAppExecutors())

    @Test
    fun refreshServiceRequestsFromNetwork() {
        val livedata = MediatorLiveData<Resource<List<ServiceRequest>>>()
        val status = "open"
        val serviceCode = "Lanterns"
        val serviceRequest = ServiceRequest()
        serviceRequest.serviceRequestId = "43Re"
        serviceRequest.serviceCode = serviceCode
        val serviceRequest2 = ServiceRequest()
        serviceRequest.serviceRequestId = "43pa"
        serviceRequest.serviceCode = serviceCode
        val serviceRequestList = ArrayList<ServiceRequest>()
        serviceRequestList.add(serviceRequest)
        serviceRequestList.add(serviceRequest2)

        val call = ApiUtil
                .successCall<List<ServiceRequest>>(serviceRequestList)
        whenever(serviceClient.getNearbyServiceRequests(any(), any())).thenReturn(call)
        val observer:Observer<Resource<List<ServiceRequest>>> = mock()

        serviceRequestsRepository.refreshServiceRequests(livedata, status, serviceCode).observeForever(observer)
        verify(serviceClient).getNearbyServiceRequests(any(), any())
    }

}