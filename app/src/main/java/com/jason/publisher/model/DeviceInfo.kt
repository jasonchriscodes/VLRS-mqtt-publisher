package com.jason.publisher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing device information, including location, bearing, speed, and direction.
 *
 * @property latitude The latitude coordinate of the device.
 * @property longitude The longitude coordinate of the device.
 * @property bearing The bearing angle of the device.
 * @property speed The speed of the device.
 * @property direction The direction in which the device is moving.
 */
@Parcelize
data class DeviceInfo(
    val latitude: Double,
    val longitude: Double,
    val bearing: Float,
    val speed: Float,
    val direction: String
) : Parcelable
