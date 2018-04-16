package com.example.justin.verbeterjegemeente.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.service.model.Coordinates;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;
import com.example.justin.verbeterjegemeente.service.repositories.ServiceRequestsRepository;
import com.example.justin.verbeterjegemeente.ui.BredaMapInterface;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by twanv on 14-1-2018.
 */

public class ServiceRequestListViewModel extends AndroidViewModel {
    private final LiveData<Resource<List<ServiceRequest>>> serviceRequestListObservable;
    private ServiceRequestsRepository serviceRequestsRepository;
    private MutableLiveData<BredaMapInterface> bredaMapInterface;
    private MutableLiveData<WebViewClient> webViewClient;
    private MutableLiveData<String> url;
    private MutableLiveData<Boolean> visible;
    private LiveData<String> serviceCode;
    private final String TAG = "ServiceRequestListVM: ";

    @Inject
    public ServiceRequestListViewModel(@NonNull Application application, @NonNull ServiceRequestsRepository serviceRequestsRepository) {
        super(application);
        this.serviceRequestsRepository = serviceRequestsRepository;
        url = new MutableLiveData<>();
        bredaMapInterface = new MutableLiveData<>();
        webViewClient = new MutableLiveData<>();
        visible = new MutableLiveData<>();
        serviceRequestListObservable = new MediatorLiveData<>();
        serviceCode = new MutableLiveData<>();
        setWebViewClient();
    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public LiveData<Resource<List<ServiceRequest>>> getServiceRequestListObservable() {
        return serviceRequestListObservable;
    }

    public void setBredaMapInterface() {
        bredaMapInterface.setValue(new BredaMapInterface(CameraCoordinates -> {
            url.postValue("javascript:Geomerk.Map.removeFeatures();");
            String lat = Double.toString(CameraCoordinates.getLat());
            String lng = Double.toString(CameraCoordinates.getLon());
            updateServiceRequests(lat, lng, "open",
                    "200", serviceCode.getValue());

            Log.i(TAG, "coordinates: " + CameraCoordinates.getLat());
            Log.i(TAG, "Service Requests have been removed from map");
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
//                wbMap.setVisibility(View.INVISIBLE);
                visible.postValue(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                visible.postValue(true);
                Log.i(TAG, "kaart is geladen");
            }
        });
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
}
