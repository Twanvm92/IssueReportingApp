package com.example.justin.verbeterjegemeente.Business;

import android.util.Log;

import com.example.justin.verbeterjegemeente.domain.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by twanv on 10-6-2017.
 */

public class ServiceManager {

    public static ArrayList<String> genMainCategories(List<Service> serviceL, ArrayList<String> catagoryList) {

        int x = 1; // set iterable separately for categoryList
        for (int i = 0; i < serviceL.size(); i++) {
            Log.e("group + code: ", serviceL.get(i).getService_code());
            // first categoryList item is a default String
            if(catagoryList.size() > 1) { // do something if list already has 1 or more categories
                // do something if previous category is not the same as new category in servicelist
                if(!catagoryList.get(x).equals(serviceL.get(i).getGroup())) {
                    catagoryList.add(serviceL.get(i).getGroup()); // add new category
                    x++; // only up this iterable if new category is added
                }
            } else {
                catagoryList.add(serviceL.get(i).getGroup());
                Log.e("service groups: ", serviceL.get(i).getGroup());
            }
        }

        return catagoryList;
    }

    public static String genServiceCodeQ(List<Service> serviceL, String currCat) {
        StringBuilder sb = new StringBuilder();
        String delim = "";

        if (serviceL != null) {
            for (Service s : serviceL) {
                if (s.getGroup().contains(currCat)) {
                    sb.append(delim).append(s.getService_code());
                    delim = ",";
                }
            }
        }

        Log.e("serviceCodeQuery: ", sb.toString());
        return sb.toString();
    }
}
