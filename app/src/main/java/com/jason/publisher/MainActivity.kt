package com.jason.publisher

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.jason.publisher.databinding.ActivityMainBinding
import com.jason.publisher.model.Bus
import com.jason.publisher.model.BusRoute
import com.jason.publisher.model.BusStop
import com.jason.publisher.model.Message
import com.jason.publisher.services.LocationManager
import com.jason.publisher.services.MqttManager
import com.jason.publisher.services.NotificationManager
import com.jason.publisher.services.SharedPrefMananger
import com.jason.publisher.services.SoundManager
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mqttManager: MqttManager
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var notificationManager: NotificationManager
    private lateinit var soundManager: SoundManager
    private lateinit var mapController: MapController

    private var latitude = 0.0
    private var longitude = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = "North"

    private var lastMessage = ""
    private var totalMessage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE))

        mqttManager = MqttManager(serverUri = SERVER_URI, clientId = CLIENT_ID)
        locationManager = LocationManager(this)
        sharedPrefMananger = SharedPrefMananger(this)
        notificationManager = NotificationManager(this)
        soundManager = SoundManager(this)

        getDefaultConfigValue()
        getMessageCount()
        startLocationUpdate()
        mapViewSetup()
        subscribeAdminMessage()
        requestAdminMessage()

        binding.chatButton.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestAdminMessage() {
        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys","message")
        val jsonString = jsonObject.toString()
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                mqttManager.publish(PUB_MSG_TOPIC, jsonString)
                handler.postDelayed(this, REQUEST_PERIODIC_TIME)
            }
        })
    }

    private fun subscribeAdminMessage() {
        mqttManager.subscribe(SUB_MSG_TOPIC) { message ->
            runOnUiThread {
                val gson = Gson()
                val data = gson.fromJson(message, Bus::class.java)
                val msg = data.shared!!.message!!
                if (lastMessage != msg) {
                    saveNewMessage(msg)
                    showNotification(msg)
                }
            }
        }
    }

    private fun showNotification(message: String) {
        notificationManager.showNotification(
            channelId = "channel2",
            notificationId = System.currentTimeMillis().toInt(),
            title = "Message from Admin",
            message = message,
            true
        )
        soundManager.playSound(SOUND_FILE_NAME)
    }

    private fun saveNewMessage(message: String) {
        sharedPrefMananger.saveString(LAST_MSG_KEY, message)
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()

        val messageList = ArrayList<Message>()
        val newMessage = Message(message, false, System.currentTimeMillis())
        val currentMessage = sharedPrefMananger.getMessageList(MSG_KEY)
        if (currentMessage.isNotEmpty()) {
            currentMessage.forEach { msg ->
                messageList.add(msg)
            }
        }
        messageList.add(newMessage)
        sharedPrefMananger.saveMessageList(MSG_KEY, messageList)

        getMessageCount()
    }

    private fun startLocationUpdate() {
        locationManager.startLocationUpdates(object: LocationListener {
            override fun onLocationUpdate(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
                bearing = location.bearing
                speed = location.speed
                direction = Helper.bearingToDirection(location.bearing)
            }
        })
    }

    private fun mapViewSetup() {
        val center = GeoPoint(latitude, longitude)

        val marker = Marker(binding.map)
        marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus, null)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        mapController = binding.map.controller as MapController
        mapController.setCenter(center)
        mapController.setZoom(18.0)

        binding.map.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            mapCenter
            setMultiTouchControls(true)
            getLocalVisibleRect(Rect())
        }

        updateMarkerPosition(marker)
        generatePolyline()
    }

    private fun generatePolyline() {
        val busRoute = ArrayList<GeoPoint>()
        val busStop = ArrayList<GeoPoint>()
        val busData = intent.getSerializableExtra(Constant.busDataKey) as HashMap<*, *>
        val routes = busData["routes"] as BusRoute
        routes.jsonMember1!!.forEach {
            busRoute.add(GeoPoint(it!!.latitude!!, it.longitude!!))
        }
        val stops = busData["stops"] as BusStop
        stops.jsonMember1!!.forEach {
            busStop.add(GeoPoint(it!!.latitude!!, it.longitude!!))
        }

        val overlayItems = ArrayList<OverlayItem>()
        busStop.forEachIndexed { index, geoPoint ->
            val busStopNumber = index + 1
            val busStopSymbol = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus_stop, null)
            val marker = OverlayItem(
                "Bus Stop $busStopNumber",
                "Description",
                geoPoint
            )
            marker.setMarker(busStopSymbol)
            overlayItems.add(marker)
        }
        val overlayItem = ItemizedIconOverlay(
            overlayItems,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                    return false
                }
            },
            applicationContext
        )
        binding.map.overlays.add(overlayItem)

        val polyline = Polyline()
        polyline.setPoints(busRoute)
        polyline.outlinePaint.color = Color.BLUE
        polyline.outlinePaint.strokeWidth = 5f

        binding.map.overlays.add(polyline)
        binding.map.invalidate()
    }

    private fun updateMarkerPosition(marker: Marker) {
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                marker.position = GeoPoint(latitude, longitude)
                marker.rotation = bearing
                binding.map.overlays.add(marker)
                binding.map.invalidate()
                publishPosition()
                handler.postDelayed(this, PUBLISH_POSITION_TIME)
            }
        }
        handler.post(updateRunnable)
    }

    private fun publishPosition() {
        val jsonObject = JSONObject()
        jsonObject.put("latitude", latitude)
        jsonObject.put("longitude", longitude)
        jsonObject.put("bearing", bearing)
        jsonObject.put("direction", direction)
        jsonObject.put("speed", speed)
        val jsonString = jsonObject.toString()
        mqttManager.publish(PUB_POS_TOPIC, jsonString, 1)
        notificationManager.showNotification(
            channelId = "channel1",
            notificationId = 1,
            title = "Connected",
            message = "Lat: $latitude, Long: $longitude, Direction: $direction",
            false
        )
    }

    private fun getDefaultConfigValue() {
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("lng", 0.0)
        bearing = intent.getFloatExtra("ber", 0.0F)
        speed = intent.getFloatExtra("spe", 0.0F)
        direction = intent.getStringExtra("dir").toString()
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()
    }

    private fun getMessageCount() {
        totalMessage = sharedPrefMananger.getMessageList(MSG_KEY).size
        binding.notificationBadge.text = totalMessage.toString()
    }

    override fun onDestroy() {
        soundManager.stopSound()
        mqttManager.disconnect()
        super.onDestroy()
    }

    companion object {
        private const val SERVER_URI = "tcp://43.226.218.94:1883"
        private const val CLIENT_ID = "jasonAndroidClientId"
        private const val PUB_POS_TOPIC = "v1/devices/me/telemetry"
        private const val SUB_MSG_TOPIC = "v1/devices/me/attributes/response/+"
        private const val PUB_MSG_TOPIC = "v1/devices/me/attributes/request/1"
        private const val REQUEST_PERIODIC_TIME = 1000L
        private const val PUBLISH_POSITION_TIME = 1000L
        private const val LAST_MSG_KEY = "lastMessageKey"
        private const val MSG_KEY = "messageKey"
        private const val SOUND_FILE_NAME = "notif.wav"
    }
}
