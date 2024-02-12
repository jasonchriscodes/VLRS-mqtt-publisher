package com.jason.publisher.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Bus(

    @field:SerializedName("shared")
    val shared: Shared? = null
)

@Parcelize
data class JsonMember1Item(

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Parcelable

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
    val config: BusConfig? = null
)

@Parcelize
data class BusConfig(
    @field:SerializedName("busConfig")
    val busConfig: List<BusItem>

) : Parcelable

@Parcelize
data class BusItem(
    @field:SerializedName("aid")
    val aid: String,

    @field:SerializedName("bus")
    val bus: String,

    @field:SerializedName("accessToken")
    val accessToken: String

) : Parcelable

@Parcelize
data class BusStop(

    @field:SerializedName("1")
    val jsonMember1: List<JsonMember1Item?>? = null,

    @field:SerializedName("2")
    val jsonMember2: List<JsonMember1Item?>? = null
) : Parcelable

@Parcelize
data class BusRoute(

    @field:SerializedName("1")
    val jsonMember1: List<JsonMember1Item?>? = null,

    @field:SerializedName("2")
    val jsonMember2: List<JsonMember1Item?>? = null
) : Parcelable
