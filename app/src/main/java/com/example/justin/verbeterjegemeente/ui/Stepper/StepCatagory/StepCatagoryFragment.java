package com.example.justin.verbeterjegemeente.ui.Stepper.StepCatagory;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.ui.callbacks.OnMainCatagorySelectedCallback;
import com.example.justin.verbeterjegemeente.app.utils.StringWithTag;
import com.example.justin.verbeterjegemeente.dagger2.Injectable;
import com.example.justin.verbeterjegemeente.databinding.FragmentStepCatagoryBinding;
import com.example.justin.verbeterjegemeente.service.model.Service;
import com.example.justin.verbeterjegemeente.viewModel.ServiceListViewModel;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class StepCatagoryFragment extends Fragment implements Step, Injectable {
    private FragmentStepCatagoryBinding mBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ServiceListViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        ButterKnife.bind(this, v);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_catagory, container, false);

        mBinding.setOnMainCatagorySelectedCallback(mainCatagorySelectedCallback);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this,
                viewModelFactory).get(ServiceListViewModel.class);

        observeViewModel(viewModel);

        mBinding.setViewModel(viewModel);



//        ServiceRequestApplication.getAppComponent(getActivity()).inject(this);
    }

    private void observeViewModel(ServiceListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getServiceListObservable().observe(this, services -> {
            if (services != null) {

                viewModel.setMainCatagories(services);

//                catagoryList = Service.genMainCategories(services);
//
//                // let the adapter know that data has changed
//                catagoryAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }



    private final OnMainCatagorySelectedCallback mainCatagorySelectedCallback = new OnMainCatagorySelectedCallback() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            viewModel.fillSubCategorySpinner(parent);

        }
    };


}
