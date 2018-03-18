package com.example.justin.verbeterjegemeente.di;

import com.example.justin.verbeterjegemeente.viewModel.ServiceListViewModel;
import com.example.justin.verbeterjegemeente.viewModel.ServiceRequestListViewModel;

import dagger.Subcomponent;

/**
 * Created by twanv on 26-11-2017.
 */

@Subcomponent
public interface ViewModelSubComponent {

    @Subcomponent.Builder
    interface Builder {
        ViewModelSubComponent build();
    }

    ServiceListViewModel serviceListViewModel();
    ServiceRequestListViewModel serviceRequestListViewModel();
}
