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
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.app.utils.StringWithTag;
import com.example.justin.verbeterjegemeente.data.DataManager;
import com.example.justin.verbeterjegemeente.data.network.Status;
import com.example.justin.verbeterjegemeente.databinding.FragmentStepCatagoryBinding;
import com.example.justin.verbeterjegemeente.di.Injectable;
import com.example.justin.verbeterjegemeente.viewModel.ServiceListViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;


public class StepCatagoryFragment extends Fragment implements BlockingStep, Injectable {
    private FragmentStepCatagoryBinding mBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ServiceListViewModel viewModel;

    private DataManager dataManager;
    private final String TAG = "StepCatagoryFragment: ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_catagory, container, false);

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
            if (services.data != null) {
                viewModel.setMainCatagories(services.data);
            }
            if (services.status == Status.ERROR) {
//                Snackbar.make(getView(), getString(R.string.noConnection), Snackbar.LENGTH_SHORT);
                if (viewModel.mainCatagories.size() == 1) {
                    Toast.makeText(getContext(), "Retry, no services in db either", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.FoutOphalenProblemen), Toast.LENGTH_SHORT).show();
                }
            } else if (services.status == Status.SUCCESS) {
                Toast.makeText(getContext(), getString(R.string.servicesLoaded), Toast.LENGTH_SHORT).show();
            } else if (services.status == Status.LOADING) {
                Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
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
        if (context instanceof DataManager) {
            dataManager = (DataManager) context;
        } else {
            throw new IllegalStateException("Activity must implement DataManager interface!");
        }
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
        // update ui when selected
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        StringWithTag s = (StringWithTag) mBinding.StepCatagoryFragmentSpSubCatagory.getSelectedItem();
        String serviceCode = (String) s.tag;

        dataManager.saveData(serviceCode);
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

}
