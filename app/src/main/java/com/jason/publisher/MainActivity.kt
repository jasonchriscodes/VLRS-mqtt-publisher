package com.jason.publisher

// import statements
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.Paint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    // mqtt configuration
    private val brokerUrl = "tcp://43.226.218.94:1883"
    private val clientId = "jasonAndroidClientId"
    private val username = "cngz9qqls7dk5zgi3y4j"
    private val topic = "v1/devices/me/telemetry"

    // mqtt client and other variables
    private lateinit var mqttClient: MqttClient
    // private lateinit var textView: TextView
    private var lat = 0.0
    private var lon = 0.0
    private var CHANNEL_ID = "test"
    private lateinit var mapController: MapController
    private lateinit var mapView: MapView

//    private var location = GeoPoint(0.0,0.0)
//    private var location = GeoPoint(data.latitude, data.longitude)
    private lateinit var marker : Marker
    private lateinit var polyline: Polyline
    private lateinit var routeData: Map<String, List<Coordinate>>

    private lateinit var sensorManager: SensorManager
    private var bearing : Float = 0.0F
    private var direction : String? = null
    private var mAccelerometer = FloatArray(3)
    private var mGeomagneic = FloatArray(3)
    private var compassSensor: Sensor? = null
    private var acceleroSensor: Sensor? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            // Do something with the latest location here
            if (lastLocation != null) {
                lat = lastLocation.latitude
                lon = lastLocation.longitude
                bearing = lastLocation.bearing
                // Use latitude and longitude data according to your needs
                // textView.text = "$lat, $lon, $bearing, $direction"
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val delayInMillis: Long = 5000

    override fun onStart() {
        super.onStart()
        // check for location permission and request if not granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // handle location permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // permission granted, you can proceed
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Location permission denied, you can provide feedback to the user or handle it according to your needs
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // intialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // initialize ui elements
        // textView = findViewById(R.id.textView)
        mapView = findViewById(R.id.map)

        // load map configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        // initialize sensor manager and sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Set up MQTT client
        val persistence = MemoryPersistence()
        mqttClient = MqttClient(brokerUrl, clientId, persistence)
        val options = MqttConnectOptions()
        options.userName = username;
        connectMQTTClient(options)

        var index = 0
        var data = Dummmy.listData
        var busStopList = mutableListOf(
            GeoPoint(-36.7803790629091, 174.99233253149978),
            GeoPoint(-36.781390583365734,  175.0069988766529),
            GeoPoint(-36.7833809573664,175.01085953338028),
            GeoPoint(-36.798831305442754, 175.0344712359602),
            GeoPoint(-36.79589493383375, 175.04736534425854),
            GeoPoint(-36.80140942774004, 175.06578856597753),
            GeoPoint(-36.801060986698346, 175.06972167622655),
            GeoPoint(-36.7988708171856, 175.07526927783536),
            GeoPoint(-36.78841923355155, 175.08308720758166),
            GeoPoint(-36.8011013407773, 175.06983728488143),
            GeoPoint(-36.80149048573887, 175.0661880120918),
            GeoPoint(-36.81456058451561, 175.08249437425002),
            GeoPoint(-36.80915689275718, 175.06174092840925),
            GeoPoint(-36.79600806801311, 175.04828948305965),
            GeoPoint(-36.79689036301606, 175.03242493729644),
            GeoPoint(-36.783650668750916, 175.01138915873818),
            GeoPoint(-36.79158652202191, 174.9993847004959),
            GeoPoint(-36.78724309953361, 175.00125045277974),
            GeoPoint(-36.7803790629091, 174.99233253149978),
        )
        var overlayItems = ArrayList<OverlayItem>()

        busStopList.forEachIndexed { index, geoPoint ->
            val busStopNumber = index + 1
            val busStopSymbol = createBusStopSymbol(busStopNumber)
            val marker = OverlayItem(
                "Bus Stop $busStopNumber",
                "Description",
                geoPoint
            )
            marker.setMarker(busStopSymbol)
            overlayItems.add(marker)
        }

        val overlayItem = ItemizedIconOverlay<OverlayItem>(overlayItems, object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                return false
            }

        }, applicationContext)
        mapView.overlays.add(overlayItem)

        generatePolyline()

        with(mapView) {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        }
        marker = Marker(mapView)
        marker.icon= resources.getDrawable(R.drawable.ic_bus)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // polyline = Polyline(mapView)
        val routePolylineFrom = Polyline(mapView)
        val routePolylineTo = Polyline(mapView)

        var fromList = ArrayList<Int>()
        var toList = ArrayList<Int>()
        for (i in 1..1) {
            fromList.add(i)
        }
        for (i in 2..2) {
            toList.add(i)
        }

        // Set the color of the polyline (e.g., blue)
        routePolylineFrom.color = android.graphics.Color.BLUE

        // Set the color of the polyline (e.g., blue)
        routePolylineTo.color = android.graphics.Color.RED

        for (index in fromList) {
            routeData["$index"]?.forEach { position ->
                routePolylineFrom.addPoint(GeoPoint(position.latitude,position.longitude))
            }
        }
        for (index in toList) {
            routeData["$index"]?.forEach { position ->
                routePolylineTo.addPoint(GeoPoint(position.latitude,position.longitude))
            }
        }

        mapView.overlays.add(routePolylineFrom)
        mapView.overlays.add(routePolylineTo)

        mapController = mapView.controller as MapController
        val center = GeoPoint(-36.797158, 175.041309)
        mapController.setCenter(center)
        mapController.setZoom(14)
        // mapView.invalidate()
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                if (index < data.size) {
                    var coordinate = data[index]

                    marker.position = coordinate
                    marker.rotation = bearing
                    mapView.overlays.add(marker)

                    // polyline.addPoint(coordinate)
                    // mapView.overlays.add(polyline)

                    mapView.invalidate()
                    Log.d("Coordinate", "${coordinate.latitude},${coordinate.longitude}")
                    index = (index + 1) % data.size
                    publishMessage("{\"latitude\":${coordinate.latitude}, \"longitude\":${coordinate.longitude}, \"bearing\":${bearing},  \"direction\":${direction}}")
                    handler.postDelayed(this, delayInMillis)
                }
            }
        }
        handler.post(updateRunnable)

    }

    private fun createBusStopSymbol(busStopNumber: Int): Drawable {
        // Create a custom drawable with the bus stop number
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_bus_stop) as BitmapDrawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        // Add the bus stop number to the right of the symbol
        val textSize = 60f // Adjust the text size as needed
        val paint = Paint().apply {
            color = Color.RED // Set text color
        }
        val text = busStopNumber.toString()
        val x = (canvas.width - paint.measureText(text)) / 2 // Adjust the horizontal position to center the text
        val y = canvas.height - 20f // Adjust the vertical position to position the text below the symbol

        canvas.drawText(text, x, y, paint)

        return BitmapDrawable(resources, bitmap)
    }




    private fun generatePolyline() {
        try {
            val stream = assets.open("busRoute.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val gson = Gson()
                val typeToken = object : TypeToken<Map<String, List<Coordinate>>>() {}.type
                routeData = gson.fromJson(strContent, typeToken)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } catch (ignored: IOException) {
            Toast.makeText(
                this@MainActivity,
                "Oops, there is something wrong. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // register sensor listeners for compass and accelerometer
        sensorManager.registerListener(
            sensorListener,
            compassSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            acceleroSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        // unregister sensor listeners when the activity is paused
        sensorManager.unregisterListener(sensorListener)
    }

    private fun bearingToDirection(bearingDegrees: Float): String {
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

    private fun connectMQTTClient(options: MqttConnectOptions) {
        try {
            mqttClient.connect(options)
            subscribeToTopic()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // callback function to pusblish mqtt messages
    private fun publishMessage(msg: String) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = 1
            message.isRetained = false
            mqttClient.publish(topic, message)
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    TODO("Not yet implemented")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d("Message Arrived", "Message Arrived")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("Delivery Complete", "Message sent")
                    showNotification()
                }

            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            val alpha = 0.97f
            if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                mAccelerometer[0] = alpha * mAccelerometer[0] + (1 - alpha) * event.values[0]
                mAccelerometer[1] = alpha * mAccelerometer[1] + (1 - alpha) * event.values[1]
                mAccelerometer[2] = alpha * mAccelerometer[2] + (1 - alpha) * event.values[2]
            }
            if (event!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagneic[0] = alpha * mGeomagneic[0] + (1 - alpha) * event.values[0]
                mGeomagneic[1] = alpha * mGeomagneic[1] + (1 - alpha) * event.values[1]
                mGeomagneic[2] = alpha * mGeomagneic[2] + (1 - alpha) * event.values[2]
            }
            val R = FloatArray(9)
            val I = FloatArray(9)
            val isSuccess = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagneic)
            if (isSuccess) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
//                bearing = Math.toDegrees((orientation[0] * -1).toDouble()).toFloat()
//                bearing = (bearing + 360) % 360
//                direction = bearingToDirection(bearing)
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

    }

    private fun showNotification() {
        val notificationManageer = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManageer.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Connected")
            .setSmallIcon(R.drawable.ic_signal)
            .setContentText("Lat: $lat, Long: $lon, Direction: $direction")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setSubText("Data Send")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // handle permission for post notifications here if needed
            return
        }
        NotificationManagerCompat.from(this).notify(1, builder.build())
    }

    private fun subscribeToTopic() {
        try {
            mqttClient.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}
