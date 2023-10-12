package com.jason.publisher

import android.location.Location

interface LocationListener {
    fun onLocationUpdate(location: Location)
}