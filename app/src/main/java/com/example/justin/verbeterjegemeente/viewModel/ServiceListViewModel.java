package com.example.justin.verbeterjegemeente.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.AdapterView;

import com.example.justin.verbeterjegemeente.R;
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
    public final ObservableArrayList<String> mainCatagories = new ObservableArrayList<>();
    public final ObservableArrayList<StringWithTag> subCatagories = new ObservableArrayList<>();
    private ObservableField<String> text;

    @Inject
    public ServiceListViewModel(@NonNull Application application, @NonNull ServicesRepository servicesRepository) {
        super(application);

        text = new ObservableField<>();
        // If any transformation is needed, this can be simply done by Transformations class ...
        serviceListObservable = servicesRepository.getServiceList();
        mainCatagories.add(getApplication().getResources().getString(R.string.kiesSubProblemen));
        subCatagories.add(new StringWithTag(getApplication().getResources().getString(R.string.kiesSubProblemen), null));

    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public LiveData<List<Service>> getServiceListObservable() {
        return serviceListObservable;
    }

    public void setMainCatagories(List<Service> services) {
        if (mainCatagories.size() > 1) { // check if list has more than just the default string
            mainCatagories.clear(); // clear ist before filling it again
            mainCatagories.add(getApplication().getResources().getString(R.string.kiesSubProblemen));
        }

        mainCatagories.addAll(Service.genMainCategories(services));
    }

    public ObservableField<String> getText() {
        return text;
    }

    /**
     * Fill the previously setup sub category spinner with sub categories that match the
     * main category's group name.
     * @param parent
     */
    public void fillSubCategorySpinner(AdapterView<?> parent) {
        if (serviceListObservable.getValue() != null) {
            if (subCatagories.size() > 1) { // check if list has more than just the default string
                subCatagories.clear(); // clear ist before filling it again
                subCatagories.add(new StringWithTag(getApplication().getResources().getString(R.string.kiesSubProblemen), null));
            }

            for (Service s : serviceListObservable.getValue()) {
                // check if selected main category is same as main category of service object
                if (parent.getSelectedItem().toString().equals(s.getGroup())) {
                    subCatagories.add(new StringWithTag(s.getService_name(), s.getService_code())); // add sub category to list
                }
            }
        }


    }
}
