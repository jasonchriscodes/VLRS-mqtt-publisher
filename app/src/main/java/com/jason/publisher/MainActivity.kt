package com.jason.publisher

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
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
import com.google.gson.Gson
import com.jason.publisher.Contacts.ChatActivity
import com.jason.publisher.databinding.ActivityMainBinding
import com.jason.publisher.model.AttributesData
import com.jason.publisher.model.Bus
import com.jason.publisher.model.BusConfig
import com.jason.publisher.model.BusData
import com.jason.publisher.model.BusItem
import com.jason.publisher.model.BusRoute
import com.jason.publisher.model.BusStop
import com.jason.publisher.model.Message
import com.jason.publisher.services.ApiService
import com.jason.publisher.services.ApiServiceBuilder
import com.jason.publisher.services.ClientAttributesResponse
import com.jason.publisher.services.LocationManager
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
    private var busConfig = ""

    private var lastMessage = ""
    private var totalMessage = 0

    private var busName = ""
    private var token = ""
    private var apiService = ApiServiceBuilder.buildService(ApiService::class.java)
    private var markerBus = HashMap<String, Marker>()
    private var arrBusData = OnlineData.getConfig()
    private var clientKeys = "latitude,longitude,bearing,speed,direction"

    private var hoursDeparture = 0
    private var minutesDeparture = 0
    private var showDepartureTime = "Yes"
    private var departureTime = "00:00:00"
    private var isFirstTime = false
    private lateinit var timer: CountDownTimer
    private lateinit var otherBusData : List<BusItem>
    private var firstTime = true

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE))
        getAccessToken()


        locationManager = LocationManager(this)
        sharedPrefMananger = SharedPrefMananger(this)
        notificationManager = NotificationManager(this)
        soundManager = SoundManager(this)

//        val busData = intent.getSerializableExtra(Constant.busDataKey) as HashMap<*, *>

        getDefaultConfigValue()
        mqttManager = MqttManager(serverUri = SERVER_URI, clientId = CLIENT_ID, username = token)
        getMessageCount()
        startLocationUpdate()
        mapViewSetup()
        requestAdminMessage()
        subscribeSharedData()
        sendRequestAttributes()

        binding.chatButton.setOnClickListener {
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
//                showPopUpDialog()
            Toast.makeText(this, "is mqtt connect? " + mqttManager.isMqttConnect(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("HardwareIds")
    private fun getAccessToken() {
        val listConfig = OnlineData.getConfig()
        val aid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        for (config in listConfig) {
            if (config.aid == aid) {
                token = config.accessToken
                break
            }
        }
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

    /**
     * Requests admin messages periodically.
     */
    private fun requestAdminMessage() {
        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys","message,busRoute,busStop,config")
        val jsonString = jsonObject.toString()
        val handler = Handler(Looper.getMainLooper())
        mqttManager.publish(PUB_MSG_TOPIC, jsonString)
        handler.post(object : Runnable {
            override fun run() {
                mqttManager.publish(PUB_MSG_TOPIC, jsonString)
                handler.postDelayed(this, REQUEST_PERIODIC_TIME)
            }
        })
    }

    private fun subscribeSharedData() {
        mqttManager.subscribe(SUB_MSG_TOPIC) { message ->
            Log.d("Message from admin",message)
            runOnUiThread {
                val gson = Gson()
                val data = gson.fromJson(message, Bus::class.java)
                val msg = data.shared?.message
                val route = data.shared?.busRoute1
                val stops = data.shared?.busStop1
                Log.d(" Check message", message.toString())
                Log.d(" Check route", route?.jsonMember1.toString())
                if (firstTime) {
                    if (route != null) {
                        if (stops != null) {
                            generatePolyline(route,stops)
                            firstTime = false
                        }
                    }
                }
                if (lastMessage != msg) {
                    if (msg != null) {
                        saveNewMessage(msg)
                        showNotification(msg)
                    }
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
                        this@MainActivity,
                        "Message has been sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    dI?.cancel()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "There is something wrong, try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "There is something wrong, try again!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
        marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus2, null)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        mapController = binding.map.controller as MapController
        mapController.setCenter(center)
        mapController.setZoom(18.0)

        binding.map.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            mapCenter
            setMultiTouchControls(true)
            getLocalVisibleRect(Rect())
        }

        updateMarkerPosition(marker)
//        generatePolyline()
    }

    private fun generatePolyline(busRoute: BusRoute,busStop: BusStop) {
//        val busRoute = OfflineData.getRoutesOffline()
//        val busStop = OfflineData.getBusStopOffline()
//        val busData = intent.getSerializableExtra(Constant.busDataKey) as HashMap<*, *>
//        val routes = busData["routes"] as BusRoute
//        routes.jsonMember1!!.forEach {
//            busRoute.add(GeoPoint(it!!.latitude!!, it.longitude!!))
//        }
//        val stops = busData["stops"] as BusStop
//        stops.jsonMember1!!.forEach {
//            busStop.add(GeoPoint(it!!.latitude!!, it.longitude!!))
//        }
        val routes = mutableListOf<GeoPoint>()
        for (route in busRoute.jsonMember1!!) {
            routes.add(GeoPoint(route!!.latitude!!, route.longitude!!))
        }
        Log.d("Check test","test")
        Log.d("Check Length Route",routes.size.toString())

        val overlayItems = ArrayList<OverlayItem>()
        busStop.jsonMember1?.forEachIndexed { index, geoPoint ->
            val busStopNumber = index + 1
    //            val busStopSymbol = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus_stop, null)
            val busStopSymbol = Helper.createBusStopSymbol(applicationContext, busStopNumber)
            val marker = OverlayItem(
                "Bus Stop $busStopNumber",
                "Description",
                GeoPoint(geoPoint!!.latitude!!, geoPoint.longitude!!)
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
        polyline.setPoints(routes)
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
                publishTelemetryData()
                updateClientAttributes()
                handler.postDelayed(this, PUBLISH_POSITION_TIME)
            }
        }
        handler.post(updateRunnable)
    }

    private fun updateClientAttributes() {
        val url = ApiService.BASE_URL + "$token/attributes"
        val attributesData = AttributesData(latitude, longitude, bearing, null,speed, direction)
        val call = apiService.postAttributes(
            url = url,
            "application/json",
            requestBody = attributesData
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Client Attributes", response.message().toString())
                Log.d("Client Attributes", response.code().toString())
                Log.d("Client Attributes", response.errorBody().toString())
                if (response.isSuccessful) {
                    Log.d("Client Attributes", "Berhasil")
                } else {
                    Log.d("Client Attributes", "Gagal")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Client Attributes", t.message.toString())
            }
        })
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

    private fun publishTelemetryData() {
        val aid = intent.getStringExtra(Constant.aidKey)
        val jsonObject = JSONObject()
        val busname = findBusNameByAid(aid)
        if (busname != null) {
            Log.d("busname", busname)
        }
        jsonObject.put("latitude", latitude)
        jsonObject.put("longitude", longitude)
        jsonObject.put("bearing", bearing)
        jsonObject.put("direction", direction)
        jsonObject.put("speed", speed)
        jsonObject.put("bus", busConfig)
        jsonObject.put("showDepartureTime", showDepartureTime)
        jsonObject.put("departureTime", departureTime)
        jsonObject.put("bus", busname)
        Log.d("BusConfig", busConfig)
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

    /**
     * Finds the bus name by its associated ID.
     *
     * @param aid the ID of the bus.
     * @return the name of the bus or null if not found.
     */
    fun findBusNameByAid(aid: String?): String? {
        if (aid == null) {
            Log.e("findBusNameByAid", "AID is null")
            return null
        }

        val configList = OfflineData.getConfig()
        val busItem = configList.find { it.aid == aid }

        return busItem?.bus ?: run {
            Log.e("findBusNameByAid", "No bus found with AID: $aid")
            null
        }
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

    private fun getDefaultConfigValue() {
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("lng", 0.0)
        bearing = intent.getFloatExtra("ber", 0.0F)
        speed = intent.getFloatExtra("spe", 0.0F)
        direction = intent.getStringExtra("dir").toString()
        lastMessage = sharedPrefMananger.getString(LAST_MSG_KEY, "").toString()

        val aid = intent.getStringExtra(Constant.aidKey)
        busConfig = intent.getStringExtra(Constant.deviceNameKey).toString()

//        token = intent.getStringExtra(Constant.tokenKey).toString()

        Log.d("arrBusDataOnline1", arrBusData.toString())
        Log.d("aidOnline", aid.toString())
        arrBusData = arrBusData.filter { it.aid != aid }
        Log.d("arrBusDataOnline2", arrBusData.toString())
        for (bus in arrBusData) {
            markerBus[bus.accessToken] = Marker(binding.map)
            markerBus[bus.accessToken]!!.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_bus, null)
            markerBus[bus.accessToken]!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
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
                        val lon = response.body()!!.client.longitude
                        val ber = response.body()!!.client.bearing
                        val berCus = response.body()!!.client.bearingCustomer
                        Log.d( "Check Data", "$lat $lon")
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
                Log.d("","")
            }
        })
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
