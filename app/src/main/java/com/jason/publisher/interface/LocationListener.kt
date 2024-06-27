package com.jason.publisher

import android.location.Location

/**
 * Interface for handling location updates.
 */
interface LocationListener {

    /**
     * Called when the location is updated.
     *
     * @param location The updated location.
     */
    fun onLocationUpdate(location: Location)
}
