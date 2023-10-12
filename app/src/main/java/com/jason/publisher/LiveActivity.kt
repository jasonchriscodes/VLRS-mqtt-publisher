package com.jason.publisher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.jason.publisher.databinding.ActivityLiveBinding
import com.jason.publisher.model.DeviceInfo
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker

class LiveActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLiveBinding
    private lateinit var mapController: MapController
    private lateinit var mqttManager: MqttManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker : Marker

//    private var latitude: Double? = -36.8557154
//    private var longitude: Double? = 174.7649233
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = ""

    var args: String? = null
    var deviceName: String? = null
    var username: String? = null
    var clientId: String? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {
                latitude = lastLocation.latitude
                longitude = lastLocation.longitude
                bearing = lastLocation.bearing
                speed = lastLocation.speed
            }
        }
    }

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

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
        binding = ActivityLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("lng", 0.0)

        direction = Helper.bearingToDirection(bearing)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getConfigData()
        Log.d("Check username", username!!)
        mqttManager = MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = clientId!!, username = username!!)

        mapConfiguration()
        publishDeviceInfo()
    }

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

    private fun getConfigData() {
         args = intent.getStringExtra(Constant.busDataKey)
         deviceName = intent.getStringExtra(Constant.deviceNameKey)
         username = intent.getStringExtra(Constant.tokenKey)
         clientId = intent.getStringExtra(Constant.aidKey)
    }

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