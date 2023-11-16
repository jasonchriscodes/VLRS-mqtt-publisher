package com.jason.publisher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceInfo(
    val latitude: Double,
    val longitude: Double,
    val bearing: Float,
    val speed: Float,
    val direction: String
) : Parcelable
