package com.example.justin.verbeterjegemeente;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        ServiceClient client = ServiceGenerator.createService(ServiceClient.class);
        /*final Call<List<Service>> serviceCall = client.getServices("en");

        serviceCall.enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                List<Service> serviceList = response.body();

                if (serviceList != null) {
                    Log.i("Response: ", "" + serviceList.get(0).getService_name());
                } else {
                    Log.i("Response: ", "List was empty");
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {

            }
        });*/

        client.getServices("en")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<Service>>() {
                    @Override
                    public void onSuccess(List<Service> value) {
                        Log.i("Service: ", value.get(0).getService_name());
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });

        tabLayout.getTabAt(0).setIcon(R.drawable.mapicon);
        tabLayout.getTabAt(1).setIcon(R.drawable.listicon);
        tabLayout.getTabAt(2).setIcon(R.drawable.accounticon);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "");
        adapter.addFragment(new Tab2Fragment(), "");
        adapter.addFragment(new Tab3Fragment(), "");
        viewPager.setAdapter(adapter);
    }

}
