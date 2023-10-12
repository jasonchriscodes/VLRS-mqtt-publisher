package com.jason.publisher

object Helper {
    fun bearingToDirection(bearingDegrees: Float): String {
        val direction = when {
            (bearingDegrees >= 337.5 || bearingDegrees < 22.5) -> "North"
            (bearingDegrees >= 22.5 && bearingDegrees < 67.5) -> "Northeast"
            (bearingDegrees >= 67.5 && bearingDegrees < 112.5) -> "East"
            (bearingDegrees >= 112.5 && bearingDegrees < 157.5) -> "Southeast"
            (bearingDegrees >= 157.5 && bearingDegrees < 202.5) -> "South"
            (bearingDegrees >= 202.5 && bearingDegrees < 247.5) -> "Southwest"
            (bearingDegrees >= 247.5 && bearingDegrees < 292.5) -> "West"
            (bearingDegrees >= 292.5 && bearingDegrees < 337.5) -> "Northwest"
            else -> "Invalid Bearing"
        }

        return direction
    }
}