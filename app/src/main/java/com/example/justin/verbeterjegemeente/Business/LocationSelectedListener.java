package com.example.justin.verbeterjegemeente.Business;

import com.google.android.gms.maps.model.LatLng;

/**
 * This interface listens when a Location selection is updated
 * and passes the new location to the class that is listening.
 */

public interface LocationSelectedListener {
    /**
     * Passes a lat and long of the changed location to the
     * class that is listening.
     *
     * @param curLatLong the lat and long value of location
     */
    public void locationSelected(LatLng curLatLong);
}
