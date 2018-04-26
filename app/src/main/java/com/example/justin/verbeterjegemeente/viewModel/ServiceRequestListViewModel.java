package com.example.justin.verbeterjegemeente.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.service.model.Coordinates;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;
import com.example.justin.verbeterjegemeente.service.repositories.ServiceRequestsRepository;
import com.example.justin.verbeterjegemeente.ui.BredaMapInterface;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by twanv on 14-1-2018.
 */

public class ServiceRequestListViewModel extends AndroidViewModel {
    private final LiveData<Resource<List<ServiceRequest>>> serviceRequestListObservable;
    private ServiceRequestsRepository serviceRequestsRepository;
    private MutableLiveData<BredaMapInterface> bredaMapInterface;
    private MutableLiveData<WebViewClient> webViewClient;
    private MutableLiveData<Boolean> visible;
    private LiveData<String> serviceCode;
    private MutableLiveData<Boolean> mapLoaded;

    @Inject
    public ServiceRequestListViewModel(@NonNull Application application,
                                       @NonNull ServiceRequestsRepository serviceRequestsRepository) {
        super(application);
        this.serviceRequestsRepository = serviceRequestsRepository;
        bredaMapInterface = new MutableLiveData<>();
        webViewClient = new MutableLiveData<>();
        visible = new MutableLiveData<>();
        serviceRequestListObservable = new MediatorLiveData<>();
        mapLoaded = new MutableLiveData<>();
        mapLoaded.setValue(false);
//        serviceRequestListObservable = updateServiceRequests("open", "RB");
        setWebViewClient();
    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public LiveData<Resource<List<ServiceRequest>>> getServiceRequestListObservable() {
        return serviceRequestListObservable;
    }

    public LiveData<Boolean> getMapLoaded() {
        return mapLoaded;
    }

    public void setBredaMapInterface() {
        bredaMapInterface.setValue(new BredaMapInterface((BredaMapInterface.OnCameraChangedListener) CameraCoordinates -> {
//            url.postValue("javascript:Geomerk.Map.removeFeatures();");
            String lat = Double.toString(CameraCoordinates.getLat());
            String lng = Double.toString(CameraCoordinates.getLon());
//            updateServiceRequests(lat, lng, "open",
//                    "200", serviceCode.getValue());

            Timber.d("coordinates: " + CameraCoordinates.getLat());
//            Timber.d("Service Requests have been removed from map");
        }, () -> {
            mapLoaded.postValue(true);
        }));
    }

    public MutableLiveData<BredaMapInterface> getBredaMapInterface() {
        return bredaMapInterface;
    }

    public void setWebViewClient() {
        webViewClient.setValue(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });
    }

    public void setPageVisibility(Boolean status) {
        visible.postValue(status);
    }

    public LiveData<WebViewClient> getWebViewClient() {
        return webViewClient;
    }

    public LiveData<Boolean> getVisible() {
        return visible;
    }

    public void updateServiceRequests(String lat, String lng, String status, String meters,
                                      String serviceCode) {
        serviceRequestsRepository.refreshServiceRequests(
                (MediatorLiveData<Resource<List<ServiceRequest>>>) serviceRequestListObservable, lat,
                lng, status, meters, serviceCode);
    }

    public LiveData<Resource<List<ServiceRequest>>> updateServiceRequests(String status,
                                                                          String serviceCode) {
        return serviceRequestsRepository.refreshServiceRequests(
                (MediatorLiveData<Resource<List<ServiceRequest>>>) serviceRequestListObservable,
                status, serviceCode);
    }
}
