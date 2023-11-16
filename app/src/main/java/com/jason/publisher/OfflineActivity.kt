package com.jason.publisher

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.jason.publisher.databinding.ActivityOfflineBinding
import com.jason.publisher.model.Bus
import com.jason.publisher.model.Message
import com.jason.publisher.services.MqttManager
import com.jason.publisher.services.NotificationManager
import com.jason.publisher.services.SharedPrefMananger
import com.jason.publisher.services.SoundManager
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker

class OfflineActivity: AppCompatActivity() {

    private lateinit var binding: ActivityOfflineBinding
    private lateinit var mqttManager: MqttManager
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var notificationManager: NotificationManager
    private lateinit var soundManager: SoundManager
    private lateinit var mapController: MapController

    private var latitude = 0.0
    private var longitude = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = "North"

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

        Configuration.getInstance().load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE))

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mqttManager = MqttManager(serverUri = SERVER_URI, clientId = CLIENT_ID)
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
        var index = 0
        val busData = intent.getStringExtra(Constant.busDataKey)
        val gson = Gson()
        val data = gson.fromJson(busData, Bus::class.java)
        val routes = data.shared!!.busRoute!!.jsonMember1
        val stops = data.shared.busStop!!.jsonMember1
        routes!!.forEach {
            busRoute.add(GeoPoint(it!!.latitude!!, it.longitude!!))
        }
        stops!!.forEach {
            busStop.add(GeoPoint(it!!.latitude!!, it.longitude!!))
        }
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                latitude = busRoute[index].latitude
                longitude = busRoute[index].longitude
                if (index != 0) {
                    speed = Helper.calculateSpeed(
                        latitude,
                        longitude,
                        busRoute[index - 1].latitude,
                        busRoute[index - 1].longitude,
                        PUBLISH_POSITION_TIME
                    ).toFloat()
                }
                index++
                handler.postDelayed(this, PUBLISH_POSITION_TIME)
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
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()
    }

    private fun getMessageCount() {
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