package com.example.justin.verbeterjegemeente.service.repositories

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import com.example.justin.verbeterjegemeente.data.network.ApiResponse
import com.example.justin.verbeterjegemeente.data.network.Resource
import com.example.justin.verbeterjegemeente.util.ApiUtil.createCall
import com.example.justin.verbeterjegemeente.util.Foo
import com.example.justin.verbeterjegemeente.util.InstantAppExecutors
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class NetworkBoundResourceNoRoomTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private var createCallFunction: (() -> LiveData<ApiResponse<Foo>>)? = null
    val appExecutors = InstantAppExecutors()
    val toBeUpdatedFoo = MediatorLiveData<Resource<Foo>>()
    // used my own version of NetworkBoundResource class - T. van Maastricht
    private var networkBoundResource: NetworkBoundResourceNoRoom<Foo, Foo>? = null

    @Before
    fun setUp() {

    }

    // removed fetchedOnce property



    @Test
    fun basicFromNetwork() {

        val networkResult = Foo(1)
        createCallFunction = { createCall(Response.success(networkResult)) }
        networkBoundResource =
                object : NetworkBoundResourceNoRoom<Foo, Foo>(appExecutors, toBeUpdatedFoo) {

                    override fun createCall(): LiveData<ApiResponse<Foo>> {
                        return createCallFunction!!.invoke()
                    }
                }

        val observer:Observer<Resource<Foo>> = mock()
        (networkBoundResource as NetworkBoundResourceNoRoom<Foo, Foo>).asLiveData().observeForever(observer)

        verify(observer).onChanged(Resource.success(networkResult))
    }

    @Test
    fun failureFromNetwork() {

        val body = ResponseBody.create(MediaType.parse("text/html"), "error")
        createCallFunction = { createCall(Response.error<Foo>(500, body)) }

        networkBoundResource =
                object : NetworkBoundResourceNoRoom<Foo, Foo>(appExecutors, toBeUpdatedFoo) {

                    override fun createCall(): LiveData<ApiResponse<Foo>> {
                        return createCallFunction!!.invoke()
                    }
                }

        val observer:Observer<Resource<Foo>> = mock()
        (networkBoundResource as NetworkBoundResourceNoRoom<Foo, Foo>).asLiveData().observeForever(observer)

        // removed drain() method  - T. van Maastricht
        verify(observer).onChanged(Resource.error<Foo>("error", null))
        verifyNoMoreInteractions(observer)
    }
}