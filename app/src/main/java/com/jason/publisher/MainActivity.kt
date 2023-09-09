package com.jason.publisher

// import statements
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_ORIENTATION
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jason.publisher.R
import com.google.android.gms.location.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {

    // mqtt configuration
    private val brokerUrl = "tcp://43.226.218.94:1883"
    private val clientId = "jasonAndroidClientId"
    private val username = "cngz9qqls7dk5zgi3y4j"
    private val topic = "v1/devices/me/telemetry"

    // mqtt client and other variables
    private lateinit var mqttClient: MqttClient
    private lateinit var textView: TextView
    private var lat = 0.0
    private var lon = 0.0
    private var CHANNEL_ID = "test"
    private lateinit var mapController: MapController
    private lateinit var mapView: MapView

//    private var location = GeoPoint(0.0,0.0)
//    private var location = GeoPoint(data.latitude, data.longitude)
    private lateinit var marker : Marker
    private lateinit var polyline: Polyline

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
                textView.text = "$lat, $lon, $bearing, $direction"
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
        textView = findViewById(R.id.textView)
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

        with(mapView) {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        }

        marker = Marker(mapView)
        marker.icon= resources.getDrawable(R.drawable.ic_car)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        polyline = Polyline(mapView)

        mapController = mapView.controller as MapController
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                if (index < data.size) {
                    var coordinate = data[index]

                    val center = GeoPoint(-36.854790, 174.764690)
                    mapController.animateTo(center)
                    mapController.setCenter(center)
                    mapController.setZoom(16)

                    marker.position = coordinate
                    marker.rotation = bearing
                    mapView.overlays.add(marker)

                    polyline.addPoint(coordinate)
                    mapView.overlays.add(polyline)

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
                bearing = Math.toDegrees((orientation[0] * -1).toDouble()).toFloat()
                bearing = (bearing + 360) % 360
                direction = bearingToDirection(bearing)
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
