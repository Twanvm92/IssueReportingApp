package com.example.justin.verbeterjegemeente.service.model;

import android.util.Log;

import com.example.justin.verbeterjegemeente.app.utils.StringWithTag;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Service</code> is a class that gets created after a get request for services has been send
 * to an open311 interface. The JSON that gets caught in the response is converted to a <code>Service</code> class
 * by a gson converter in the <code>Retrofit</code> class
 *
 * @author Twan van Maastricht
 * @see ServiceClient#getServices(String)
 * @see retrofit2.Retrofit
 */

public class Service {

    private String service_code;
    private String service_name;
    private String description;
    private boolean metadeta;
    private String type;
    private String keywords;
    private String group;

    /**
     * Loops through every <code>Service</code> and finds unique group names
     * which are our main categories. These unique group names are then
     * put into a new ArrayList.
     *
     * @param serviceL     list filled with services that is looped through.
     * @return a list of main categories.
     *
     * @see Service
     */
    public static List<StringWithTag> genMainCategories(List<Service> serviceL) {

        List<StringWithTag> catagoryList = new ArrayList<>();
        int x = 1; // set iterable separately for categoryList
        for (int i = 0; i < serviceL.size(); i++) {
            Log.e("group + code: ", serviceL.get(i).getService_code());
            // first categoryList item is a default String
            if (catagoryList.size() > 1) { // do something if list already has 1 or more categories
                // do something if previous category is not the same as new category in servicelist
                if (!catagoryList.get(x).equals(serviceL.get(i).getGroup())) {
//                    catagoryList.add(serviceL.get(i).getGroup());
                    catagoryList.add(new StringWithTag(serviceL.get(i).getGroup(), serviceL.get(i).getService_code())); // add new category
                    x++; // only up this iterable if new category is added
                }
            } else {
                catagoryList.add(new StringWithTag(serviceL.get(i).getGroup(), serviceL.get(i).getService_code()));
                Log.e("service groups: ", serviceL.get(i).getGroup());
            }
        }

        return catagoryList;
    }

    /**
     * Will check if the current category filter chosen by the user, matches the attribute group
     * in a <code>Service</code> and if so, will append the service code of that service
     * to a <code>StringBuilder</code>.
     *
     * @param serviceL list of services from an open311 Interface.
     * @param currCat  current category filter chosen by the user.
     * @return
     */
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

    public String getService_code() {
        return service_code;
    }

    public String getService_name() {
        return service_name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMetadeta() {
        return metadeta;
    }

    public String getType() {
        return type;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getGroup() {
        return group;
    }
}
