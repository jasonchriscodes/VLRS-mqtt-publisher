package com.jason.publisher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceInfo(
    val latitude: Double,
    val longitud: Double,
    val bearing: Float,
    val speed: Float,
    val direction: String
) : Parcelable
