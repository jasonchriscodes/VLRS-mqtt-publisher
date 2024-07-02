package com.jason.publisher.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a bus.
 *
 * @property shared The shared data related to the bus.
 */

@Parcelize
data class Bus(
    @field:SerializedName("shared")
    val shared: Shared? = null
): Parcelable

/**
 * Data class representing an item with latitude and longitude coordinates.
 *
 * @property latitude The latitude coordinate.
 * @property longitude The longitude coordinate.
 */
@Parcelize
data class JsonMember1Item(

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Parcelable

/**
 * Data class representing shared data.
 *
 * @property busStop1 The list of bus stop data in the new format.
 * @property busRoute1 The list of bus route data in the new format.
 * @property message The message related to the bus.
 * @property config The bus configuration data.
 * @property bearing The list of bus bearing data.
 * @property bearingCustomer The list of customer-specific bus bearing data.
 */
@Parcelize
data class Shared(
    @field:SerializedName("busStop")
    val busStop1: List<BusStop>? = null,

    @field:SerializedName("busRoute")
    val busRoute1: List<BusRoute>? = null,

    @field:SerializedName("busStop2")
    val busStop: List<BusStop>? = null,

    @field:SerializedName("busRoute2")
    val busRoute: List<BusRoute>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("config")
    val config: BusConfig? = null,

    @field:SerializedName("bearing")
    val bearing: List<BusBearing>? = null,

    @field:SerializedName("bearingCustomer")
    val bearingCustomer: List<BusBearingCustomer>? = null
) : Parcelable

/**
 * Data class representing bus bearing data.
 *
 * @property bearing The bearing angle.
 */
@Parcelize
data class BusBearing(
    @field:SerializedName("bearing")
    val bearing: Double? = null
): Parcelable

/**
 * Data class representing customer-specific bus bearing data.
 *
 * @property bearingCustomer The customer-specific bearing angle.
 */
@Parcelize
data class BusBearingCustomer(
    @field:SerializedName("bearing")
    val bearingCustomer: Double? = null
): Parcelable

/**
 * Data class representing bus configuration data.
 *
 * @property busConfig The list of bus items.
 */
@Parcelize
data class BusConfig(
    @field:SerializedName("busConfig")
    val busConfig: List<BusItem>

) : Parcelable

/**
 * Data class representing a bus item.
 *
 * @property aid The aid of the bus.
 * @property bus The name of the bus.
 * @property accessToken The access token of the bus.
 */
@Parcelize
data class BusItem(
    @field:SerializedName("aid")
    val aid: String,

    @field:SerializedName("bus")
    val bus: String,

    @field:SerializedName("accessToken")
    val accessToken: String

) : Parcelable

/**
 * Data class representing bus stop data in the new format.
 *
 * @property latitude The latitude of the bus stop.
 * @property longitude The longitude of the bus stop.
 */
@Parcelize
data class BusStop(
    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Parcelable

/**
 * Data class representing bus route data in the new format.
 *
 * @property latitude The latitude of the bus route point.
 * @property longitude The longitude of the bus route point.
 */
@Parcelize
data class BusRoute(
    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Parcelable
