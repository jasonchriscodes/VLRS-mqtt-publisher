package com.jason.publisher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing bus data, including routes, stops, shared bus items, bearing, and customer-specific bearing.
 *
 * @property routes The bus route data.
 * @property stops The bus stop data.
 * @property sharedBus The list of shared bus items.
 * @property bearing The list of bus bearing data.
 * @property bearingCust The list of customer-specific bus bearing data.
 */
@Parcelize
data class BusData(
    var routes: BusRoute?,
    var stops: BusStop?,
    var sharedBus: List<BusItem>,
    var bearing: List<BusBearing>?,
    var bearingCust: List<BusBearingCustomer>?
) : Parcelable
