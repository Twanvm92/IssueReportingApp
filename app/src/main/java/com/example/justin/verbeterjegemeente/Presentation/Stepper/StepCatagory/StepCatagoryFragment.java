package com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory;

import android.arch.lifecycle.Observer;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.justin.verbeterjegemeente.R;
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
import butterknife.ButterKnife;


public class StepCatagoryFragment extends Fragment implements Step, Injectable {
    @BindView(R.id.StepCatagoryFragment_sp_MainCatagory)
    Spinner mainCatagorySpinner;

    @BindView(R.id.StepCatagoryFragment_sp_SubCatagory)
    Spinner subCatagorySpinner;

    private FragmentStepCatagoryBinding mBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ArrayAdapter<StringWithTag> catagoryAdapter;
    ArrayAdapter<String> subCategoryAdapter;
    private List<String> catagoryList;
    private ArrayList<String> subCategoryList;
    private List<Service> serviceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        ButterKnife.bind(this, v);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_catagory, container, false);


        //initialize your UI
//        setupCategorySpinner();
//        setupSubCategorySpinner();

        mBinding.StepCatagoryFragmentSpMainCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                fillSubCategorySpinner(parent);
                // reset the selected sub catagory to the default one everytime
                // a new catagory is selected
//                if (position != 0) {
//                    subCatagorySpinner.setSelection(0);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // something is always selected...also by default
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ServiceListViewModel viewModel = ViewModelProviders.of(this,
                viewModelFactory).get(ServiceListViewModel.class);

        observeViewModel(viewModel);

        mBinding.setViewModel(viewModel);



//        ServiceRequestApplication.getAppComponent(getActivity()).inject(this);
    }

    private void observeViewModel(ServiceListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getServiceListObservable().observe(this, services -> {
            if (services != null) {
                serviceList = services;

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

    /**
     * Setup a spinner that will hold the main categories taken from the services provided
     * by an open311 Interface.
     */
    public void setupCategorySpinner() {
//        // create an arraylist that will contain different categories fetched from an open311 interface
//        catagoryList = new ArrayList<String>();
//        // add a default item for the spinner
//        catagoryList.add(getResources().getString(R.string.kiesProblemen));
//        catagoryAdapter = new ArrayAdapter<StringWithTag>(getActivity(),
//                android.R.layout.simple_spinner_item) {
//            @Override
//            //pakt de positions van elements in catagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
//            public boolean isEnabled(int position) {
//                if (position == 0) {
//                    return false;
//                } else {
//                    return true;
//                }
//            }
//        };
//        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mainCatagorySpinner.setAdapter(catagoryAdapter);
    }

    /**
     * Setup a spinner that will hold the sub categories taken from the services provided
     * by an open311 Interface.
     */
    public void setupSubCategorySpinner() {
        // create an arraylist that will contain different sub categories fetched from an open311 interface
        subCategoryList = new ArrayList<String>();
        // add a default item for the spinner
        subCategoryList.add(getResources().getString(R.string.kiesSubProblemen));
        subCategoryAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, subCategoryList) {
            @Override
            //pakt de positions van elements in subCatagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCatagorySpinner.setAdapter(subCategoryAdapter);
    }

    /**
     * Fill the previously setup sub category spinner with sub categories that match the
     * main category's group name.
     * @param parent
     */
    public void fillSubCategorySpinner(AdapterView<?> parent) {
        if (subCategoryList.size() > 1) { // check if list has more than just the default string
            subCategoryList.clear(); // clear ist before filling it again
            subCategoryList.add(getResources().getString(R.string.kiesSubProblemen));
        }

        for (Service s : serviceList) {
            // check if selected main category is same as main category of service object
            if (parent.getSelectedItem().toString().equals(s.getGroup())) {
                subCategoryList.add(s.getService_name()); // add sub category to list
            }
        }
        subCategoryAdapter.notifyDataSetChanged();

    }


}
