package com.jason.publisher

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
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
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
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
import com.jason.publisher.model.AttributesData
import com.jason.publisher.model.Bus
import com.jason.publisher.model.BusBearing
import com.jason.publisher.model.BusBearingCustomer
import com.jason.publisher.model.BusData
import com.jason.publisher.model.BusItem
import com.jason.publisher.model.Coordinate
import com.jason.publisher.model.JsonMember1Item
import com.jason.publisher.model.Message
import com.jason.publisher.services.ApiService
import com.jason.publisher.services.ApiServiceBuilder
import com.jason.publisher.services.ClientAttributesResponse
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.nio.charset.StandardCharsets
import kotlin.random.Random

/**
 * OfflineActivity class responsible for managing the application in offline mode.
 */
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
    private var bearingCustomer = 0.0F
    private var speed = 0.0F
    private var direction = "North"
    private var busConfig = ""

    private var routeIndex = 0 // Initialize index at the start
    private var busRoute = ArrayList<GeoPoint>()
    private var busStop = ArrayList<GeoPoint>()
    private var busBearing = ArrayList<BusBearing>()
    private var busBearingCustomer = ArrayList<BusBearingCustomer>()

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
    private var departureTime = "00:00:00"
    private var isFirstTime = false

    private lateinit var timer: CountDownTimer
    private var apiService = ApiServiceBuilder.buildService(ApiService::class.java)
    private var clientKeys = "latitude,longitude,bearing,bearingCustomer,speed,direction"
    val handler = Handler(Looper.getMainLooper())
    private var sharedBus = ArrayList<String> ()
    private lateinit var arrBusData : List<BusItem>
    private var markerBus = HashMap<String, Marker>()
    private var routeDirection = "forward"

    /**
     * Initializes the activity, sets up sensor and service managers, loads configuration, subscribes to admin messages,
     * and initializes UI components.
     */
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getDefaultConfigValue()
        }

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
//            val intent = Intent(this, ChatActivity::class.java)
//            startActivity(intent)
            showChatDialog()
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

    /**
     * Shows a dialog for sending a message to the operator.
     */
    private fun showChatDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Send Message to Operator")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("Send") { dI, _ ->
            sendMessageToOperator(dI,input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }

    /**
     * Sends a message to the operator via API service.
     * @param dI The dialog interface.
     * @param message The message to send.
     */
    private fun sendMessageToOperator(dI: DialogInterface?, message: String) {
        val contentMessage = mapOf("operatorMessage" to message)
        val call = apiService.postAttributes(
            ApiService.BASE_URL+mqttManager.getUsername()+"/attributes",
            "application/json",
            contentMessage
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@OfflineActivity,
                        "Message has been sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    dI?.cancel()
                } else {
                    Toast.makeText(
                        this@OfflineActivity,
                        "There is something wrong, try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@OfflineActivity,
                    "There is something wrong, try again!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Shows a pop-up dialog for setting departure time.
     */
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
        minutesPicker.maxValue = 59

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                hoursDeparture = hoursPicker.value
                minutesDeparture = minutesPicker.value
                showDepartureTime = spinner.selectedItem.toString()
                Log.d("departureTimeDialog", showDepartureTime)
                mapViewSetup()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    startLocationUpdate()
                }
                publishShowDepartureTime() // Added to publish the show departure time
                publishDepartureTime()
                publishRouteDirection()
                sendRequestAttributes()
                // Start the countdown timer
                startCountdown()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle Cancel button click
            }
            .show()
    }

    /**
     * Requests admin messages periodically.
     */
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

    /**
     * Subscribes to admin messages and displays notifications for new messages.
     */
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

    /**
     * Displays a notification for a new message from the admin.
     * @param message The message content.
     */
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

    /**
     * Saves a new message into shared preferences and updates message count.
     * @param message The message content.
     */
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

    /**
     * Starts updating the bus location on the map.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startLocationUpdate() {
        var isForward =
            true // Flag to indicate the direction of movement, true for forward, false for backward

        val busData = intent.getParcelableExtra(Constant.busDataKey, BusData::class.java)
        latitude = busRoute[routeIndex].latitude
        longitude = busRoute[routeIndex].longitude
        PolarCoordinateToBearing(latitude,longitude,latitude,longitude)
        val stops = busData!!.stops!!.jsonMember1 as List<JsonMember1Item>
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
                    speed = Random.nextFloat() * 10f + 50f
//                    speed = Helper.calculateSpeed(
//                        latitude,
//                        longitude,
//                        busRoute[routeIndex - 1].latitude,
//                        busRoute[routeIndex - 1].longitude,
//                        PUBLISH_POSITION_TIME
//                    )
                }

                // Determine the direction of index movement
//                if (isForward) { // If moving forward
                    routeIndex++
                    if (routeIndex == busRoute.size - 1) {
                        isForward = false // Change direction to backward if reached upper limit
                    }
                    Log.d("routeDirectionIF", routeIndex.toString())
                // coordinates backward route starting from 123
                if (routeIndex > 122){
                    routeDirection = "backward"
                }
//                } else { // If moving backward
//                    routeIndex--
//                    if (routeIndex == 0) {
//                        isForward = true // Change direction to forward if reached lower limit
//                    }
//                    Log.d("routeDirectionIB", routeIndex.toString())
//                    routeDirection = "backward"
//                }
                handler.postDelayed(this, PUBLISH_POSITION_TIME)
            }}
        handler.post(updateRunnable)
    }

    /**
     * Retrieves bus route data from a JSON file and initializes route data.
     */
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

    /**
     * Generates markers for bus stops on the map.
     */
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

    /**
     * Generates polylines for bus route segments on the map.
     */
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

    /**
     * Sets up the map view by removing existing markers, invalidating the map, updating bus marker icon and position,
     * and resetting the route index.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mapViewSetup() {
        binding.map.overlays.remove(busMarker)
        binding.map.invalidate()
        busMarker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus2, null)
        busMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        updateMarkerPosition()
        routeIndex = 0
    }

    /**
     * Updates the position of the bus marker on the map and publishes telemetry data.
     * Uses a runnable to update the marker position periodically.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateMarkerPosition() {
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                val attributesData = AttributesData(latitude, longitude, bearing, bearingCustomer,speed, direction)
                Log.d("attributesData", attributesData.toString())
                postAttributes(apiService, mqttManager.getUsername(), attributesData)
                Log.d("Mqttmanager", mqttManager.getUsername())
                busMarker.position = GeoPoint(latitude, longitude)
                busMarker.rotation =  busBearing[routeIndex].bearing!!.toFloat()
                bearing = busBearing[routeIndex].bearing!!.toFloat()
                Log.d("Check Array", busBearingCustomer.isEmpty().toString())
                Log.d("Check Index", routeIndex.toString())
                Log.d("Check Index", busBearingCustomer.toString())
                bearingCustomer = busBearingCustomer[routeIndex].bearingCustomer!!.toFloat()
                Log.d("bearingUpdateMarker", bearing.toString())
                direction = Helper.bearingToDirection(bearing)
                binding.map.overlays.add(busMarker)
                binding.map.invalidate()
                publishTelemetryData()
                Log.d("updateMarker", "")
                if (routeIndex == busBearing.lastIndex) {
                    routeIndex = 0
                    // Remove the callback if it's the last index
//                    handler.removeCallbacksAndMessages(null)
                } else {
                    handler.postDelayed(this, PUBLISH_POSITION_TIME)
                }
            }
        }
        handler.post(updateRunnable)
    }


    /**
     * Starts a countdown timer for the departure time.
     */
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
            }
        }
        timer.start()
    }

    /**
     * Publishes telemetry data including latitude, longitude, bearing, speed, direction, and other relevant information.
     */
    @SuppressLint("LongLogTag")
    private fun publishTelemetryData() {
        val jsonObject = JSONObject()
        jsonObject.put("latitude", latitude)
        jsonObject.put("longitude", longitude)
        jsonObject.put("bearing", bearing)
        jsonObject.put("bearingCustomer", bearingCustomer)
        jsonObject.put("direction", direction)
        jsonObject.put("speed", speed)
        jsonObject.put("bus", busConfig)
        jsonObject.put("showDepartureTime", showDepartureTime)
        jsonObject.put("departureTime", departureTime)
        jsonObject.put("routeDirection", routeDirection)
        Log.d("bearingTelemetry", bearing.toString())
        Log.d("departureTimeTelemetry:", departureTime)
        Log.d("departureTimeShowTelemetry:", showDepartureTime)
        Log.d("routeDirectionTelemetry", routeDirection)
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

    /**
     * Convert polar coordinate to bearing.
     */
    private fun PolarCoordinateToBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Calculate the differences in longitudes and latitudes
        val deltaLon = lon2Rad - lon1Rad
        val deltaLat = lat2Rad - lat1Rad

        // Calculate the bearing using atan2 function
        val y = sin(deltaLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)
        var brng = atan2(y, x)

        // Convert the bearing from radians to degrees
        brng = Math.toDegrees(brng)

        // Normalize the bearing to be in the range [0, 360)
        brng = (brng + 360) % 360

        return brng
    }

    /**
     * Publishes the current status of routeDirection.
     */
    private fun publishRouteDirection(){
        val jsonObject = JSONObject()
        jsonObject.put("routeDirection", routeDirection)
        Log.d("routeDirection", routeDirection)
        val jsonString = jsonObject.toString()
        mqttManager.publish(MainActivity.PUB_POS_TOPIC, jsonString, 1)
    }

    /**
     * Publishes the current status of whether to show the departure time.
     */
    private fun publishShowDepartureTime(){
        val jsonObject = JSONObject()
        jsonObject.put("showDepartureTime", showDepartureTime)
        Log.d("ShowDepartureTime", showDepartureTime)
        val jsonString = jsonObject.toString()
        mqttManager.publish(MainActivity.PUB_POS_TOPIC, jsonString, 1)
    }

    /**
     * Publishes the current departure time.
     */
    private fun publishDepartureTime(){
        val jsonObject = JSONObject()
        jsonObject.put("departureTime", departureTime)
        Log.d("ShowDepartureTime", showDepartureTime)
        val jsonString = jsonObject.toString()
        mqttManager.publish(MainActivity.PUB_POS_TOPIC, jsonString, 1)
    }

    /**
     * Posts attributes data to the API service.
     */
    private fun postAttributes(apiService: ApiService, accessToken: String, attributesData: AttributesData) {
        val call = apiService.postAttributes(
            "${ApiService.BASE_URL}$accessToken/attributes",
            "application/json",
            attributesData
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Request successful","${response.body()}")
                } else {
                    Log.d("Request failed","${response.errorBody()}")
                    Toast.makeText(this@OfflineActivity, "Request failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    Log.d("Request failed code","${response.code()}")
                    Log.d("Request failed message","${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@OfflineActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Retrieves default configuration values for the activity, such as latitude, longitude, bearing, and more.
     */
    // because this is offline mode,
    // the default value required is only the new message comparator
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getDefaultConfigValue() {
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("lng", 0.0)
        bearing = intent.getFloatExtra("ber", 0.0F)
        bearingCustomer = intent.getFloatExtra("berCus", 0.0F)
        speed = intent.getFloatExtra("spe", 0.0F)
        direction = intent.getStringExtra("dir").toString()
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()

        val aid = intent.getStringExtra(Constant.aidKey)
        busConfig = intent.getStringExtra(Constant.deviceNameKey).toString()

        val busData = intent.getParcelableExtra(Constant.busDataKey, BusData::class.java)

        arrBusData = busData!!.sharedBus
        Log.d("arrBusDataOffline1", arrBusData.toString())
        Log.d("aidOffLine", aid.toString())
        arrBusData = arrBusData.filter { it.aid != aid }
        Log.d("arrBusDataOffline2", arrBusData.toString())
        for (bus in arrBusData) {
            markerBus[bus.accessToken] = Marker(binding.map)
            markerBus[bus.accessToken]!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus, null)
            markerBus[bus.accessToken]!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        busBearing = busData.bearing as ArrayList<BusBearing>
        busBearingCustomer = busData.bearingCust as ArrayList<BusBearingCustomer>
        Log.d("Bearing Length", busBearing.size.toString())
    }

    /**
     * Sends data attributes to the server.
     */
    private fun sendRequestAttributes(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                for (bus in arrBusData) {
                    getAttributes(apiService, bus.accessToken, clientKeys)
                }
                handler.postDelayed(this, 3000)
            }
        }, 0)
    }

    /**
     * Retrieves attributes data for each bus from the server.
     */
    private fun getAttributes(apiService: ApiService, token: String, clientKeys: String) {
        Log.d("getAttribute: ", "test1")
        Log.d("token: ", token)
        val call = apiService.getAttributes(
            "${ApiService.BASE_URL}$token/attributes",
            "application/json",
            clientKeys
        )
        call.enqueue(object : Callback<ClientAttributesResponse> {
            override fun onResponse(call: Call<ClientAttributesResponse>, response: Response<ClientAttributesResponse>) {
                Log.d("Attribute Data", response.body().toString())
                if (response.isSuccessful) {
                    if (response.body()?.client != null){
                        val lat = response.body()?.client?.latitude ?: 0.0
                        val lon = response.body()!!.client.longitude ?: 0.0
                        val ber = response.body()!!.client.bearing ?: 0.0F
                        val berCus = response.body()!!.client.bearingCustomer ?: 0.0F
                        Log.d( "Check Array", arrBusData.size.toString())
                        for (bus in arrBusData) {
                            if (token == bus.accessToken) {
                                markerBus[token]!!.position = GeoPoint(lat, lon)
                                markerBus[token]!!.rotation = ber
                                binding.map.overlays.add(markerBus[token])
                                binding.map.invalidate()
                            }
                        }
                    }

                } else {
                    Log.d("request data bus", response.message().toString())
                }
            }

            override fun onFailure(call: Call<ClientAttributesResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    /**
     * Retrieves the total message count from shared preferences.
     */
    private fun getMessageCount() {
        // sets the value of the textview
        // based on the length of the stored arraylist
        totalMessage = sharedPrefMananger.getMessageList(MSG_KEY).size
//        binding.notificationBadge.text = totalMessage.toString()
    }

    /**
     * Listener for sensor changes, specifically for accelerometer and compass sensors.
     * Updates the bearing and direction based on sensor readings.
     */
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
//                bearing = Math.toDegrees((orientation[0] * -1).toDouble()).toFloat()
//                bearing = (bearing + 360) % 360
//                direction = Helper.bearingToDirection(bearing)
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            // TODO: handle accuracy
        }

    }

    /**
     * Resumes sensor listeners when the activity is resumed.
     */
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

    /**
     * Pauses sensor listeners when the activity is paused.
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }

    /**
     * Stops sound, disconnects MQTT manager, and cleans up resources when the activity is destroyed.
     */
    override fun onDestroy() {
        soundManager.stopSound()
        mqttManager.disconnect()
        super.onDestroy()
    }

    /**
     * Companion object holding constant values used throughout the activity.
     * Includes server URI, client ID, MQTT topics, and other constants.
     */
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