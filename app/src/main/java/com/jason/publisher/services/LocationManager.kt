package com.jason.publisher.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.jason.publisher.LocationListener

/**
 * Class responsible for managing location updates.
 *
 * @param context The application context.
 */
class LocationManager(private val context: Context) {

    private val fusLocation: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var locationCallback: LocationCallback? = null

    /**
     * Starts location updates and notifies the listener with the updated location.
     *
     * @param listener The listener to be notified with location updates.
     */
    fun startLocationUpdates(listener: LocationListener) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { loc ->
                    listener.onLocationUpdate(loc)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, so return without requesting updates
            return
        }
        fusLocation.requestLocationUpdates(
            locationRequest,
            locationCallback as LocationCallback,
            null
        )
    }
}