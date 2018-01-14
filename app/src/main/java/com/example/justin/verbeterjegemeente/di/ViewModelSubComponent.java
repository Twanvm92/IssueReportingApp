package com.example.justin.verbeterjegemeente.di;

import com.example.justin.verbeterjegemeente.viewModel.ServiceListViewModel;

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
}
