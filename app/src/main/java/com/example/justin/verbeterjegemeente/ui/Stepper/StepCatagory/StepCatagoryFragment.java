package com.example.justin.verbeterjegemeente.ui.Stepper.StepCatagory;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.app.utils.StringWithTag;
import com.example.justin.verbeterjegemeente.data.DataManager;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.data.network.Status;
import com.example.justin.verbeterjegemeente.databinding.FragmentStepCatagoryBinding;
import com.example.justin.verbeterjegemeente.di.Injectable;
import com.example.justin.verbeterjegemeente.ui.callbacks.OnNavigationBarListener;
import com.example.justin.verbeterjegemeente.viewModel.ServiceListViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.List;

import javax.inject.Inject;


public class StepCatagoryFragment extends Fragment implements BlockingStep, Injectable {
    private FragmentStepCatagoryBinding mBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ServiceListViewModel viewModel;

    private DataManager dataManager;

    private OnNavigationBarListener onNavigationBarListener;

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
            if (services.data != null && !services.data.isEmpty()) {
                viewModel.visible.set(true);
                viewModel.setMainCatagories(services.data);
                updateNavigationBar(false);
            }
            showMessage(services);
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

        if (context instanceof OnNavigationBarListener) {
            onNavigationBarListener = (OnNavigationBarListener) context;
        }
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise

        StringWithTag s = (StringWithTag) mBinding.StepCatagoryFragmentSpSubCatagory.getSelectedItem();

        if (s.tag != null) {
            return null;
        } else {
            return new VerificationError(getString(R.string.kiesSubCategory));
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
        viewModel.subError.set(true);
    }

    public void showMessage(Resource<List<ServiceEntry>> services) {
        if (services.status == Status.ERROR) {
            if (viewModel.mainCatagories.size() == 1) {
                View view = mBinding.getRoot().findViewById(R.id.StepCatagoryFragment_l_catagoryLayout);

                updateNavigationBar(true);

                Toast.makeText(getContext(), getString(R.string.FoutOphalenProblemen), Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(view, getString(R.string.noConnection), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.Retry, view1 -> viewModel.updateServices());
                snackbar.setActionTextColor(getResources().getColor(R.color.green500));
                snackbar.show();

            } else {
                Toast.makeText(getContext(), getString(R.string.FoutOphalenProblemen), Toast.LENGTH_SHORT).show();
            }
        } else if (services.status == Status.SUCCESS) {
            Toast.makeText(getContext(), getString(R.string.servicesLoaded), Toast.LENGTH_SHORT).show();
        } else if (services.status == Status.LOADING) {
            Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
        }
    }

    // TODO check why this is not working
    private void updateNavigationBar(boolean enabled) {
        if (onNavigationBarListener != null) {
            onNavigationBarListener.onChangeEndButtonsEnabled(enabled);
        }
    }

}
