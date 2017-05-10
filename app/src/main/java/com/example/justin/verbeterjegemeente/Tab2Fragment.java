package com.example.justin.verbeterjegemeente;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.domain.PostServiceRequestResponse;
import com.example.justin.verbeterjegemeente.domain.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private Button btnTEST;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab2_fragment,container,false);
        btnTEST = (Button) view.findViewById(R.id.button2);
        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "Dit");
                RequestBody sc = RequestBody.create(MediaType.parse("text/plain"), "172");
                RequestBody apiK = RequestBody.create(MediaType.parse("text/plain"), ServiceGenerator.TEST_API_KEY);


                ServiceClient client = ServiceGenerator.createService(ServiceClient.class);
                Call<ArrayList<PostServiceRequestResponse>> serviceRequestResponseCall = client.postServiceRequest(apiK, description, sc);

                serviceRequestResponseCall.enqueue(new Callback<ArrayList<PostServiceRequestResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<PostServiceRequestResponse>> call, Response<ArrayList<PostServiceRequestResponse>> response) {
                        if(response.isSuccessful()) {

                            ArrayList<PostServiceRequestResponse> pRespList = response.body();

                            for (PostServiceRequestResponse psrr : pRespList) {
                                Log.e("Service response: ", psrr.getId());
                            }

                        } else {

                            try {
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(getActivity(), jObjError.getString("description"),Toast.LENGTH_SHORT).show();
                                Log.e("Error message: ", jObjError.getString("description"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<PostServiceRequestResponse>> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

                Call<List<Service>> serviceCall = client.getServices("en");

                serviceCall.enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        List<Service> serviceList = response.body();

                        if (serviceList != null) {
                            for (Service s : serviceList) {
                                Log.e("Response: ", "" + s.getService_name());
                            }

                        } else {
                            Log.e("Response: ", "List was empty");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Service>> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return view;
    }
}
