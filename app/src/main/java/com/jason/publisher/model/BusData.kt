package com.jason.publisher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusData(
    var routes: BusRoute?,
    var stops: BusStop?,
    var sharedBus: List<BusItem>,
    var bearing: List<BusBearing>?,
    var bearingCust: List<BusBearingCustomer>?
) : Parcelable
