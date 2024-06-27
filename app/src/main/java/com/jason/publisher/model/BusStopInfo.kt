package com.jason.publisher.model

/**
 * Data class representing information about a bus stop.
 *
 * @property busStopName The name of the bus stop.
 * @property latitude The latitude coordinate of the bus stop.
 * @property longitude The longitude coordinate of the bus stop.
 * @property distanceToNextStop The distance to the next bus stop.
 */
data class BusStopInfo(
    val busStopName : String,
    val latitude : Double,
    val longitude : Double,
    val distanceToNextStop : Double
)