package com.jason.publisher.model

data class BusStopInfo(
    val busStopName : String,
    val latitude : Double,
    val longitude : Double,
    val distanceToNextStop : Double
)