package com.jason.publisher

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jason.publisher.Contacts.ChatActivity
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
    private lateinit var busMarker: Marker

    private var latitude = 0.0
    private var longitude = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = "North"
    private var busConfig = ""

    private var routeIndex = 0 // Initialize index at the start
    private var busRoute = ArrayList<GeoPoint>()
            private var busStop = ArrayList<GeoPoint>()

    private var lastMessage = ""
    private var totalMessage = 0

    private lateinit var sensorManager: SensorManager
    private var mAccelerometer = FloatArray(3)
    private var mGeomagneic = FloatArray(3)
    private var compassSensor: Sensor? = null
    private var acceleroSensor: Sensor? = null

    private var hoursDeparture = 0
    private var minutesDeparture = 0
    private var showDepartureTime = "Yes"
    private var departureTime = "00:00"
    private var isFirstTime = false

    private lateinit var timer: CountDownTimer

    @RequiresApi(Build.VERSION_CODES.Q)
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
        subscribeAdminMessage()
        requestAdminMessage()

        busMarker = Marker(binding.map)
        mapController = binding.map.controller as MapController
        getBusRouteData()
        val center = GeoPoint(busRoute[0].latitude, busRoute[0].longitude)
        mapController.setCenter(center)
        mapController.setZoom(18.0)

        binding.map.apply {
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            mapCenter
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            setMultiTouchControls(true)
            getLocalVisibleRect(Rect())
        }

        binding.chatButton.setOnClickListener {
            // route to message list
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        // Set up spinner
        val items = arrayOf("Yes", "No")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set click listener for pop-up button
        binding.popUpButton.setOnClickListener {
            binding.popUpButton.setImageDrawable(getDrawable(R.drawable.ic_refresh))
            if(!isFirstTime){
                showPopUpDialog()
            } else
            {
                this.recreate()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopUpDialog() {
        isFirstTime = true
        val dialogView = layoutInflater.inflate(R.layout.popup_dialog, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerShowTime)
        val hoursPicker = dialogView.findViewById<NumberPicker>(R.id.hoursPicker)
        val minutesPicker = dialogView.findViewById<NumberPicker>(R.id.minutesPicker)

        // Options for the Spinner
        val items = arrayOf("Yes", "No")

        // ArrayAdapter for the Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set ArrayAdapter to the Spinner
        spinner.adapter = adapter

        // Initialize the NumberPickers
        hoursPicker.minValue = 0
        hoursPicker.maxValue = 1

        minutesPicker.minValue = 0
        minutesPicker.maxValue = 30

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                hoursDeparture = hoursPicker.value
                minutesDeparture = minutesPicker.value
                showDepartureTime = spinner.selectedItem.toString()
                Log.d("departureTimeDialog", showDepartureTime)
                mapViewSetup()
                startLocationUpdate()
                publishShowDepartureTime() // Added to publish the show departure time
                publishDepartureTime()
                // Start the countdown timer
                startCountdown()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle Cancel button click
            }
            .show()
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

        val busData = intent.getSerializableExtra(Constant.busDataKey) as HashMap<*, *>
        latitude = busRoute[routeIndex].latitude
        longitude = busRoute[routeIndex].longitude
        val stops = busData["stops"] as List<JsonMember1Item>
        stops.forEach {
            busStop.add(GeoPoint(it.latitude!!, it.longitude!!))
        }

        generatePolyline()
        generateBusStop()

        val handler = Handler(Looper.getMainLooper())
        var updateRunnable = object : Runnable {
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
            }}
        handler.post(updateRunnable)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mapViewSetup() {
        binding.map.overlays.remove(busMarker)
        binding.map.invalidate()
        busMarker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus, null)
        busMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        updateMarkerPosition()
        routeIndex = 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateMarkerPosition() {
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                busMarker.position = GeoPoint(latitude, longitude)
                busMarker.rotation = bearing
                binding.map.overlays.add(busMarker)
                binding.map.invalidate()
                publishTelemetryData()
                Log.d("updateMarker", "")
                handler.postDelayed(this, PUBLISH_POSITION_TIME)
            }
        }
        handler.post(updateRunnable)
    }

    private fun startCountdown() {
        val totalMinutes = hoursDeparture * 60 + minutesDeparture
        val totalMillis = totalMinutes * 60 * 1000 // Convert total minutes to milliseconds

        timer = object : CountDownTimer(totalMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // This method will be called every second
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                departureTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                Log.d("departureTime", departureTime)
            }

            override fun onFinish() {
                // This method will be called when the timer finishes
                departureTime = "Timer finished"
                Log.d("departureTime", "Timer finished")
            }
        }
        timer.start()
    }


    @SuppressLint("LongLogTag")
    private fun publishTelemetryData() {
        val jsonObject = JSONObject()
        jsonObject.put("latitude", latitude)
        jsonObject.put("longitude", longitude)
        jsonObject.put("bearing", bearing)
        jsonObject.put("direction", direction)
        jsonObject.put("speed", speed)
        jsonObject.put("bus", busConfig)
        jsonObject.put("showDepartureTime", showDepartureTime)
        jsonObject.put("departureTime", departureTime)
        Log.d("departureTimeTelemetry:", departureTime)
        Log.d("departureTimeShowTelemetry:", showDepartureTime)
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

    private fun publishShowDepartureTime(){
        val jsonObject = JSONObject()
        jsonObject.put("showDepartureTime", showDepartureTime)
        Log.d("ShowDepartureTime", showDepartureTime)
        val jsonString = jsonObject.toString()
        mqttManager.publish(MainActivity.PUB_POS_TOPIC, jsonString, 1)
    }

    private fun publishDepartureTime(){
        val jsonObject = JSONObject()
        jsonObject.put("departureTime", departureTime)
        Log.d("ShowDepartureTime", showDepartureTime)
        val jsonString = jsonObject.toString()
        mqttManager.publish(MainActivity.PUB_POS_TOPIC, jsonString, 1)
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