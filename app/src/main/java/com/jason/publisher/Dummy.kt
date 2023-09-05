package com.jason.publisher

object Dummmy {
    var listData = listOf(
        Dummy(-36.857891, 174.761383),
        Dummy(-36.850916, 174.764501),
        Dummy(-36.853900, 174.768353),
        Dummy(-36.858416, 174.763631)
    )
}


data class Dummy (
    val latitude: Double,
    val longitude: Double
)