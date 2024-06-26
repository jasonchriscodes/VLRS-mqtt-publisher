package com.jason.publisher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jason.publisher.Contacts.ChatActivity
import com.jason.publisher.databinding.ActivityLiveBinding
import com.jason.publisher.services.LocationManager
import com.jason.publisher.services.MqttManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker

/**
 * Activity for live tracking and displaying real-time data on a map.
 */
class LiveActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLiveBinding
    private lateinit var mapController: MapController
    private lateinit var mqttManager: MqttManager
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker : Marker

//  Mock Mode
//    private var latitude: Double? = -36.77995
//    private var longitude: Double? = 174.99204
//  Live Mode
    private var latitude: Double? = -36.8557154
    private var longitude: Double? = 174.7649233
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = ""

    var args: String? = null
    var deviceName: String? = null
    var username: String? = null
    var clientId: String? = null

    /**
     * Handles location permission request result.
     */
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

    /**
     * Initializes the activity, sets up the map, location updates, and MQTT manager.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("lng", 0.0)
        locationManager = LocationManager(this)
        startLocationUpdate()

        direction = Helper.bearingToDirection(bearing)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getConfigData()
        Log.d("Check username", username!!)
        mqttManager = MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = clientId!!, username = username!!)

        mapConfiguration()
        publishDeviceInfo()

        binding.chatButton.setOnClickListener {
            val name = intent.getStringExtra(Constant.deviceNameKey)
            val contactIntent = Intent(this, ChatActivity::class.java)
            contactIntent.putExtra(Constant.deviceNameKey, name)
            startActivity(contactIntent)
        }
    }

    /**
     * Starts location updates.
     */
    private fun startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            locationManager.startLocationUpdates(object : LocationListener {
                override fun onLocationUpdate(location: Location) {
                    latitude = location.latitude
                    longitude = location.longitude
                    bearing = location.bearing
                    speed = location.speed
                    direction = Helper.bearingToDirection(location.bearing)
                }
            })
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                123
            )
        }
    }

    /**
     * Publishes device info periodically.
     */
    private fun publishDeviceInfo() {
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                Log.d("Live Coordinate", "latitude:$latitude,longitude:$longitude")
                var message = ""
                if (latitude != null) {
                    marker.position = GeoPoint(latitude!!, longitude!!)
                    marker.rotation = bearing
                    binding.map.overlays.add(marker)
                    binding.map.invalidate()
                    direction = bearingToDirection(bearing)
                    message = "{\"latitude\":${latitude}, \"longitude\":${longitude}, \"bearing\":${bearing},  \"direction\":${direction},  \"speed\":${speed}}"
                } else {
                    message = "{\"latitude\":0.0, \"longitude\":0.0, \"bearing\":0.0,  \"direction\":North,  \"speed\":0.0}"
                }
                showNotification()
                mqttManager.publish(
                    topic = "v1/devices/me/telemetry",
                    message = message,
                    qos = 1
                )
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateRunnable)
    }

    /**
     * Shows a notification with the current location and direction.
     */
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
            .setContentText("Lat: $latitude, Long: $longitude, Direction: $direction")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setSubText("Data Send")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // handle permission for post notifications here if needed
            return
        }
        NotificationManagerCompat.from(this).notify(1, builder.build())
    }

    /**
     * Converts a bearing angle to a compass direction.
     *
     * @param bearingDegrees The bearing angle in degrees.
     * @return The compass direction as a string.
     */
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

    /**
     * Retrieves configuration data from the intent.
     */
    private fun getConfigData() {
         args = intent.getStringExtra(Constant.busDataKey)
         deviceName = intent.getStringExtra(Constant.deviceNameKey)
         username = intent.getStringExtra(Constant.tokenKey)
         clientId = intent.getStringExtra(Constant.aidKey)
    }

    /**
     * Configures the map settings and initializes the marker.
     */
    private fun mapConfiguration() {
        with(binding.map) {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
        }
        marker = Marker(binding.map)
        marker.icon= resources.getDrawable(R.drawable.ic_bus)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        mapController = binding.map.controller as MapController
        val center = GeoPoint(-36.85563836740891, 174.76524204357094)
        mapController.setCenter(center)
        mapController.setZoom(14)
    }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 123
        private val CHANNEL_ID = "livedata"
    }
}