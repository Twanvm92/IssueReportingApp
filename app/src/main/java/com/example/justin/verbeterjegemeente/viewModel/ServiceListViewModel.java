package com.example.justin.verbeterjegemeente.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;

import com.example.justin.verbeterjegemeente.app.utils.StringWithTag;
import com.example.justin.verbeterjegemeente.service.model.Service;
import com.example.justin.verbeterjegemeente.service.repositories.ServicesRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by twanv on 26-11-2017.
 */

public class ServiceListViewModel extends AndroidViewModel {
    private final LiveData<List<Service>> serviceListObservable;
    public final ObservableArrayList<StringWithTag> mainCatagories = new ObservableArrayList<>();
    public final ObservableArrayList<StringWithTag> subCatagories = new ObservableArrayList<>();

    @Inject
    public ServiceListViewModel(@NonNull Application application, @NonNull ServicesRepository servicesRepository) {
        super(application);

        // If any transformation is needed, this can be simply done by Transformations class ...
        serviceListObservable = servicesRepository.getServiceList();
//        List<Service> servicesss = serviceListObservable.getValue();
//        mainCatagories.addAll(Service.genMainCategories(servicesss));

    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public LiveData<List<Service>> getServiceListObservable() {
        return serviceListObservable;
    }

    public void setMainCatagories(List<Service> services) {
        mainCatagories.addAll(Service.genMainCategories(services));
    }
}
