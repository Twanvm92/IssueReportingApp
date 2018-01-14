package com.example.justin.verbeterjegemeente.ui.Stepper.StepCatagory;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.ui.callbacks.OnMainCatagorySelectedCallback;
import com.example.justin.verbeterjegemeente.app.utils.StringWithTag;
import com.example.justin.verbeterjegemeente.di.Injectable;
import com.example.justin.verbeterjegemeente.databinding.FragmentStepCatagoryBinding;
import com.example.justin.verbeterjegemeente.viewModel.ServiceListViewModel;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;


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

    }

    private void observeViewModel(ServiceListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getServiceListObservable().observe(this, services -> {
            if (services != null) {

                viewModel.setMainCatagories(services);

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

        StringWithTag s = (StringWithTag) mBinding.StepCatagoryFragmentSpSubCatagory.getSelectedItem();

        if (s.tag != null) {
            return null;
        } else {
            return new VerificationError("Please select a sub catagory!");
        }
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