package com.jason.publisher.model

import com.google.gson.annotations.SerializedName

data class Bus(

	@field:SerializedName("shared")
	val shared: Shared? = null
)

data class JsonMember1Item(

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
)

data class Shared(

	@field:SerializedName("busStop")
	val busStop: BusStop? = null,

	@field:SerializedName("busRoute")
	val busRoute: BusRoute? = null
)

data class BusStop(

	@field:SerializedName("1")
	val jsonMember1: List<JsonMember1Item?>? = null
)

data class BusRoute(

	@field:SerializedName("1")
	val jsonMember1: List<JsonMember1Item?>? = null
)
