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

/**
 * Object containing helper functions used throughout the application.
 */
object Helper {

    /**
     * Converts a bearing angle to a compass direction.
     *
     * @param bearing The bearing angle in degrees.
     * @return The compass direction as a string.
     */
    fun bearingToDirection(bearing: Float): String {
        return when {
            bearing == 0f || bearing == 360f -> "East"
            bearing > 0f && bearing < 90f -> "North-East"
            bearing == 90f -> "North"
            bearing > 90f && bearing < 180f -> "North-West"
            bearing == 180f -> "West"
            bearing > 180f && bearing < 270f -> "South-West"
            bearing == 270f -> "South"
            bearing > 270f && bearing < 360f -> "South-East"
            else -> "Invalid Bearing"
        }
    }

    /**
     * Converts a timestamp to a readable format.
     *
     * @param timeInMillis The timestamp in milliseconds.
     * @return The formatted date and time as a string.
     */
    fun convertTimeToReadableFormat(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dayOfWeek = dayFormat.format(calendar.time)
        val timeOfDay = hourFormat.format(calendar.time)

        return "$dayOfWeek, $timeOfDay"
    }

    /**
     * Calculates the speed between two geographical points given the time delay.
     *
     * @param firstLat The latitude of the first point.
     * @param firstLong The longitude of the first point.
     * @param secondLat The latitude of the second point.
     * @param secondLong The longitude of the second point.
     * @param delayInMillis The time delay in milliseconds.
     * @return The calculated speed in meters per second.
     */
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

    /**
     * Calculates the distance between two geographical points using the Haversine formula.
     *
     * @param lat1 The latitude of the first point.
     * @param lon1 The longitude of the first point.
     * @param lat2 The latitude of the second point.
     * @param lon2 The longitude of the second point.
     * @return The distance between the points in kilometers.
     */
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return 6371 * c // radius of the Earth in kilometers
    }

    /**
     * Creates a custom drawable with the bus stop number.
     *
     * @param context The application context.
     * @param busStopNumber The bus stop number.
     * @param maxBusStopNumber The maximum bus stop number.
     * @return A drawable with the bus stop symbol and number.
     */
    fun createBusStopSymbol(context: Context, busStopNumber: Int, maxBusStopNumber: Int): Drawable {
        // Determine the adjusted bus stop number
        val adjustedNumber = when (busStopNumber) {
            1 -> "S"
            maxBusStopNumber -> "E"
            else -> (busStopNumber - 1).toString()
        }

        // create a custom drawable with the bus stop number
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_bus_stop) as BitmapDrawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        // add the adjusted bus stop number to the right of the symbol
        val textSize = 30f // adjust the text size as needed
        val paint = Paint().apply {
            color = Color.WHITE // set text color
            isFakeBoldText = true // enable bold text
            typeface = Typeface.DEFAULT_BOLD // set bold typeface
            setTextSize(textSize)
        }
        val text = adjustedNumber
        val x = (canvas.width - paint.measureText(text)) / 2 // adjust the horizontal position to center the text
        val y = canvas.height - 10f // adjust the vertical position to position the text below the symbol

        canvas.drawText(text, x, y, paint)

        return BitmapDrawable(context.resources, bitmap)
    }

}