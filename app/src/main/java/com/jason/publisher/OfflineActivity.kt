package com.jason.publisher

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jason.publisher.Helper.createBusStopSymbol
import com.jason.publisher.databinding.ActivityOfflineBinding
import com.jason.publisher.model.Bus
import com.jason.publisher.model.Coordinate
import com.jason.publisher.model.JsonMember1Item
import com.jason.publisher.model.Message
import com.jason.publisher.services.MqttManager
import com.jason.publisher.services.NotificationManager
import com.jason.publisher.services.SharedPrefMananger
import com.jason.publisher.services.SoundManager
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import java.nio.charset.StandardCharsets

class OfflineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfflineBinding
    private lateinit var mqttManager: MqttManager
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var notificationManager: NotificationManager
    private lateinit var soundManager: SoundManager
    private lateinit var mapController: MapController
    private lateinit var routeData: Map<String, List<Coordinate>>

    private var latitude = 0.0
    private var longitude = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = "North"
    private var busConfig = ""

    private var busRoute = ArrayList<GeoPoint>()
    private var busStop = ArrayList<GeoPoint>()

    private var lastMessage = ""
    private var totalMessage = 0

    private lateinit var sensorManager: SensorManager
    private var mAccelerometer = FloatArray(3)
    private var mGeomagneic = FloatArray(3)
    private var compassSensor: Sensor? = null
    private var acceleroSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfflineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance()
            .load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE))

        // initialize each sensor used
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // initialize each service used
        val token = intent.getStringExtra(Constant.tokenKey)
        mqttManager = MqttManager(serverUri = SERVER_URI, clientId = CLIENT_ID, username = token!!)
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
            // route to message list
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestAdminMessage() {
        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys", "message")
        val jsonString = jsonObject.toString()
        val handler = Handler(Looper.getMainLooper())

        // looping periodically to check for new notifications
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

                // check whether the notification received
                // is the same as the previous notification
                // before to store it or show it as notification
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
            // use a timestamp as notificationId
            // so that when there is a new notification
            // it creates a new bubble, not overlapping each other
            notificationId = System.currentTimeMillis().toInt(),
            title = "Message from Admin",
            message = message,
            true
        )

        // plays a sound when a new notification comes in
        // to change the sound, please add a new sound to the assets folder
        // and adjust the name in the SOUND_FILE_NAME variable
        soundManager.playSound(SOUND_FILE_NAME)
    }

    // save new message into sharedprefrence
    private fun saveNewMessage(message: String) {
        sharedPrefMananger.saveString(LAST_MSG_KEY, message)

        // assign the last message to lastMessage variable
        // as a comparison when there is a new notification
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()

        // check whether there is a message list,
        // if there is, the message list is taken and
        // then added with a new message,
        // because sharedpreference can only store list data type as a single list,
        // it does not have a method of adding one by one
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

        getMessageCount() // calculate total message
    }

    private fun startLocationUpdate() {
        var isForward =
            true // Flag to indicate the direction of movement, true for forward, false for backward
        var routeIndex = 0 // Initialize index at the start

        val busData = intent.getSerializableExtra(Constant.busDataKey) as HashMap<*, *>
        getBusRouteData()
        latitude = busRoute[routeIndex].latitude
        longitude = busRoute[routeIndex].longitude
        val stops = busData["stops"] as List<JsonMember1Item>
        stops.forEach {
            busStop.add(GeoPoint(it.latitude!!, it.longitude!!))
        }

        generatePolyline()
        generateBusStop()

        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                latitude = busRoute[routeIndex].latitude
                longitude = busRoute[routeIndex].longitude

                // Calculate speed if index is not at the beginning
                if (routeIndex != 0) {
                    speed = Helper.calculateSpeed(
                        latitude,
                        longitude,
                        busRoute[routeIndex - 1].latitude,
                        busRoute[routeIndex - 1].longitude,
                        PUBLISH_POSITION_TIME
                    )
                }

                // Determine the direction of index movement
                if (isForward) { // If moving forward
                    routeIndex++
                    if (routeIndex == busRoute.size - 1) {
                        isForward = false // Change direction to backward if reached upper limit
                    }
                } else { // If moving backward
                    routeIndex--
                    if (routeIndex == 0) {
                        isForward = true // Change direction to forward if reached lower limit
                    }
                }

                handler.postDelayed(this, PUBLISH_POSITION_TIME)
            }
        })

    }

    private fun getBusRouteData() {
        try {
            val stream = assets.open("busRoute.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()

            val strContent = String(buffer, StandardCharsets.UTF_8)
            val gson = Gson()
            val typeToken = object : TypeToken<Map<String, List<Coordinate>>>() {}.type
            routeData = gson.fromJson(strContent, typeToken)

            val fromList = ArrayList<Int>()
            val toList = ArrayList<Int>()
            for (i in 1..1) {
                fromList.add(i)
            }
            for (i in 2..2) {
                toList.add(i)
            }

            for (index in fromList) {
                routeData["$index"]?.forEach { position ->
                    busRoute.add(GeoPoint(position.latitude, position.longitude))
                }
            }
            for (index in toList) {
                routeData["$index"]?.forEach { position ->
                    busRoute.add(GeoPoint(position.latitude, position.longitude))
                }
            }
        } catch (e: IOException) {
            Log.e("Get Data JSON Asset", e.toString())
        }
    }

    private fun generateBusStop() {
        val overlayItems = ArrayList<OverlayItem>()
        busStop.forEachIndexed { index, geoPoint ->
            Log.d("BUS STOP", geoPoint.toString())
            val busStopNumber = index + 1
//            val busStopSymbol = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus_stop, null)
            val busStopSymbol = createBusStopSymbol(applicationContext, busStopNumber)
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
    }

    private fun generatePolyline() {
        try {
            val routePolylineFrom = Polyline(binding.map)
            val routePolylineTo = Polyline(binding.map)

            val fromList = ArrayList<Int>()
            val toList = ArrayList<Int>()
            for (i in 1..1) {
                fromList.add(i)
            }
            for (i in 2..2) {
                toList.add(i)
            }

            routePolylineFrom.color = Color.BLUE
            routePolylineTo.color = Color.RED

            for (index in fromList) {
                routeData["$index"]?.forEach { position ->
                    routePolylineFrom.addPoint(GeoPoint(position.latitude, position.longitude))
                }
            }
            for (index in toList) {
                routeData["$index"]?.forEach { position ->
                    routePolylineTo.addPoint(GeoPoint(position.latitude, position.longitude))
                }
            }

            binding.map.overlays.add(routePolylineFrom)
            binding.map.overlays.add(routePolylineTo)
        } catch (ignored: IOException) {
            Toast.makeText(
                this,
                "Oops, there is something wrong. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
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
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            mapCenter
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            setMultiTouchControls(true)
            getLocalVisibleRect(Rect())
        }

        updateMarkerPosition(marker)
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
        jsonObject.put("bus", busConfig)
        Log.d("BusConfig", busConfig)
        val jsonString = jsonObject.toString()
        mqttManager.publish(MainActivity.PUB_POS_TOPIC, jsonString, 1)
        notificationManager.showNotification(
            channelId = "channel1",
            notificationId = 1,
            title = "Connected",
            message = "Lat: $latitude, Long: $longitude, Direction: $direction",
            false
        )
    }

    // because this is offline mode,
    // the default value required is only the new message comparator
    private fun getDefaultConfigValue() {
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("lng", 0.0)
        bearing = intent.getFloatExtra("ber", 0.0F)
        speed = intent.getFloatExtra("spe", 0.0F)
        direction = intent.getStringExtra("dir").toString()
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()

        val aid = intent.getStringExtra(Constant.aidKey)
        busConfig = intent.getStringExtra(Constant.deviceNameKey).toString()
    }

    private fun getMessageCount() {
        // sets the value of the textview
        // based on the length of the stored arraylist
        totalMessage = sharedPrefMananger.getMessageList(MSG_KEY).size
        binding.notificationBadge.text = totalMessage.toString()
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            val alpha = 0.97f
            if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                mAccelerometer[0] = alpha * mAccelerometer[0] + (1 - alpha) * event.values[0]
                mAccelerometer[1] = alpha * mAccelerometer[1] + (1 - alpha) * event.values[1]
                mAccelerometer[2] = alpha * mAccelerometer[2] + (1 - alpha) * event.values[2]
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagneic[0] = alpha * mGeomagneic[0] + (1 - alpha) * event.values[0]
                mGeomagneic[1] = alpha * mGeomagneic[1] + (1 - alpha) * event.values[1]
                mGeomagneic[2] = alpha * mGeomagneic[2] + (1 - alpha) * event.values[2]
            }
            val r = FloatArray(9)
            val i = FloatArray(9)

            val success = SensorManager.getRotationMatrix(r, i, mAccelerometer, mGeomagneic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                bearing = Math.toDegrees((orientation[0] * -1).toDouble()).toFloat()
                bearing = (bearing + 360) % 360
                direction = Helper.bearingToDirection(bearing)
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            // TODO: handle accuracy
        }

    }


    override fun onResume() {
        super.onResume()
        // unregister to avoid the sensor continuing to run
        // even though the application is not running
        // and to anticipate power usage
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
        sensorManager.unregisterListener(sensorListener)
    }

    override fun onDestroy() {
        soundManager.stopSound()
        mqttManager.disconnect()
        super.onDestroy()
    }

    companion object {
        const val SERVER_URI = "tcp://43.226.218.94:1883"
        const val CLIENT_ID = "jasonAndroidClientId"
        const val PUB_POS_TOPIC = "v1/devices/me/telemetry"
        private const val SUB_MSG_TOPIC = "v1/devices/me/attributes/response/+"
        private const val PUB_MSG_TOPIC = "v1/devices/me/attributes/request/1"
        private const val REQUEST_PERIODIC_TIME = 1000L
        private const val PUBLISH_POSITION_TIME = 1000L
        private const val LAST_MSG_KEY = "lastMessageKey"
        private const val MSG_KEY = "messageKey"
        private const val SOUND_FILE_NAME = "notif.wav"
    }
}