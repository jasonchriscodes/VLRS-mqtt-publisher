package com.jason.publisher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    fun convertTimeToReadableFormat(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dayOfWeek = dayFormat.format(calendar.time)
        val timeOfDay = hourFormat.format(calendar.time)

        return "$dayOfWeek, $timeOfDay"
    }

    fun calculateSpeed(
        firstLat: Double,
        firstLong: Double,
        secondLat: Double,
        secondLong: Double,
        delayInMillis: Long
    ): Float {
        val distance = haversine(firstLat, firstLong, secondLat, secondLong)
        return (distance * 1000 / (delayInMillis / 1000)).toFloat()
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371 * c // radius of the Earth in kilometers
    }

    fun createBusStopSymbol(context: Context, busStopNumber: Int): Drawable {
        // create a custom drawable with the bus stop number
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_bus_stop) as BitmapDrawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        drawable.setBounds(0, 0, canvas.width, canvas.height)

        // add the bus stop number to the right of the symbol
        val paint = Paint().apply {
            color = Color.GREEN // set text color
            isFakeBoldText = true // enable bold text
            typeface = Typeface.DEFAULT_BOLD // set bold typeface
        }
        val text = busStopNumber.toString()
        val x =
            (canvas.width - paint.measureText(text)) / 2 // adjust the horizontal position to center the text
        val y =
            canvas.height - 20f // adjust the vertical position to position the text below the symbol

        canvas.drawText(text, x, y, paint)

        return BitmapDrawable(context.resources, bitmap)
    }
}