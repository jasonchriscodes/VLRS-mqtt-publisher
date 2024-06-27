package com.jason.publisher.utils
import com.jason.publisher.OfflineData
import android.location.Location

class BusStopAssignment {

    companion object {
        fun getTheClosestBusStopToPubDevice(lat: Double, lng: Double): String {
            val busStopList = OfflineData.getBusStopOfflineWithName()
            var minDistance = Double.MAX_VALUE
            var closestBusStopName = "unknown";

            for (busStop in busStopList) {
                val distance: Double =
                    calculateDistance(lat, lng, busStop.latitude, busStop.longitude)
                if (distance < minDistance) {
                    minDistance = distance
                    closestBusStopName = busStop.busStopName;
                }
            }
            return closestBusStopName
        }

        private fun calculateDistance(
            lat1: Double,
            lng1: Double,
            lat2: Double,
            lng2: Double
        ): Double {
            val startPoint = Location("start")
            startPoint.latitude = lat1
            startPoint.longitude = lng1

            val endPoint = Location("end")
            endPoint.latitude = lat2
            endPoint.longitude = lng2

            return startPoint.distanceTo(endPoint).toDouble();
        }
    }
}
