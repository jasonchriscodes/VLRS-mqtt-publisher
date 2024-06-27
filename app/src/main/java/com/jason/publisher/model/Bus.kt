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
 * @property busStop1 The first bus stop data.
 * @property busRoute1 The first bus route data.
 * @property busStop The second bus stop data.
 * @property busRoute The second bus route data.
 * @property message The message related to the bus.
 * @property config The bus configuration data.
 * @property bearing The list of bus bearing data.
 * @property bearingCustomer The list of customer-specific bus bearing data.
 */
@Parcelize
data class Shared(

    @field:SerializedName("busStop")
    val busStop1: BusStop? = null,

    @field:SerializedName("busRoute")
    val busRoute1: BusRoute? = null,

    @field:SerializedName("busStop2")
    val busStop: BusStop? = null,

    @field:SerializedName("busRoute2")
    val busRoute: BusRoute? = null,

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
 * Data class representing bus stop data.
 *
 * @property jsonMember1 The first list of bus stop items.
 * @property jsonMember2 The second list of bus stop items.
 */
@Parcelize
data class BusStop(

    @field:SerializedName("1")
    val jsonMember1: List<JsonMember1Item?>? = null,

    @field:SerializedName("2")
    val jsonMember2: List<JsonMember1Item?>? = null
) : Parcelable

/**
 * Data class representing bus route data.
 *
 * @property jsonMember1 The first list of bus route items.
 * @property jsonMember2 The second list of bus route items.
 */
@Parcelize
data class BusRoute(

    @field:SerializedName("1")
    val jsonMember1: List<JsonMember1Item?>? = null,

    @field:SerializedName("2")
    val jsonMember2: List<JsonMember1Item?>? = null
) : Parcelable
