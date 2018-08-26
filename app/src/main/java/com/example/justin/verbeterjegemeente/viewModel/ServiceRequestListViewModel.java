package com.example.justin.verbeterjegemeente.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.justin.verbeterjegemeente.R;
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
    private MutableLiveData<Boolean> mapLoaded;
    private MutableLiveData<String> showSnackbar;

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
        showSnackbar = new MutableLiveData<>();
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
        bredaMapInterface.setValue(new BredaMapInterface(CameraCoordinates -> {
            String lat = Double.toString(CameraCoordinates.getLat());
            String lng = Double.toString(CameraCoordinates.getLon());

            Timber.d("coordinates: " + CameraCoordinates.getLat());
        }, () -> mapLoaded.postValue(true), this::showSnackbar));
    }

    public MutableLiveData<BredaMapInterface> getBredaMapInterface() {
        return bredaMapInterface;
    }

    private void setWebViewClient() {
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

    private void showSnackbar(String message) {
        showSnackbar.postValue(message);
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

    public LiveData<String> getShowSnackbar() {
        return showSnackbar;
    }

    public LiveData<Resource<List<ServiceRequest>>> updateServiceRequests(String status,
                                                                          String serviceCode) {
        return serviceRequestsRepository.refreshServiceRequests(
                (MediatorLiveData<Resource<List<ServiceRequest>>>) serviceRequestListObservable,
                status, serviceCode);
    }
}
