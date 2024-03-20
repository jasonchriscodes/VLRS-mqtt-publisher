package com.jason.publisher

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.jason.publisher.databinding.ActivitySplashScreenBinding
import com.jason.publisher.model.Bus
import com.jason.publisher.model.BusItem
import com.jason.publisher.services.LocationManager
import com.jason.publisher.services.ModeSelectionDialog
import com.jason.publisher.services.MqttManager
import com.jason.publisher.services.SharedPrefMananger
import org.json.JSONObject

/**
 * SplashScreen activity responsible for initializing the application and handling initial setup.
 * It retrieves necessary data, such as device ID and configuration, then routes to the appropriate screen.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var mqttManager: MqttManager
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var modeSelectionDialog: ModeSelectionDialog
    private lateinit var binding: ActivitySplashScreenBinding
    private var data: String? = null
    var name = ""
    private var accessToken = ""
    private var aaid = ""
    private val db = Firebase.firestore
    private lateinit var busItem: BusItem

    private var latitude = 0.0
    private var longitude = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = ""

    /**
     * Overrides the onCreate method to initialize the activity and perform necessary setup.
     */
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modeSelectionDialog = ModeSelectionDialog(this)

        aaid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//        Toast.makeText(this, aaid, Toast.LENGTH_LONG).show()
        Log.d("aaid bus a:", aaid)
        sharedPrefMananger = SharedPrefMananger(this)

        val colRef = db.collection("config").get()
        colRef.addOnSuccessListener { query ->
            for (doc in query.documents) {
                if (doc.data!!["aid"] == aaid) {
                    name = doc.data!!["name"].toString()
                    accessToken = doc.data!!["accessToken"].toString()
                    sharedPrefMananger.saveString(Constant.deviceNameKey, name)
                }
            }
        }

        // initialize the MQTT manager with server URI and client ID
        mqttManager =
            MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = "jasonAndroidClientId")
        locationManager = LocationManager(this)
        startLocationUpdate()

        routeToNextScreen()

        // subscribe to a MQTT topic for attribute responses and update UI accordingly
        mqttManager.subscribe(topic = "v1/devices/me/attributes/response/+") { message ->
            data = message
        }
        requestData()
    }

    /**
     * Routes to the next screen after a specified delay.
     */
    private fun routeToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            showOptionDialog()
        }, 5000)
    }

    /**
     * Shows the mode selection dialog and handles the selected mode.
     */
    private fun showOptionDialog() {
        modeSelectionDialog.showModeSelectionDialog(object : ModeSelectionDialog.ModeSelectionListener {
            override fun onModeSelected(mode: String) {
                var intent = Intent(this@SplashScreen, MainActivity::class.java)
                val busData: HashMap<String, Any>
                if (mode == "Offline") {
                    intent = Intent(this@SplashScreen, OfflineActivity::class.java)
                    busData = getRoutesAndStops(true)
                } else {
                    busData = getRoutesAndStops(false)
                }
                Log.d("busdata: ", busData.toString())
                intent.putExtra(Constant.busDataKey, busData)
                intent.putExtra(Constant.deviceNameKey, name)
                intent.putExtra(Constant.tokenKey, accessToken)
                intent.putExtra(Constant.aidKey, aaid)

                intent.putExtra("lat", latitude)
                intent.putExtra("lng", longitude)
                intent.putExtra("ber", bearing)
                intent.putExtra("spe", speed)
                intent.putExtra("dir", direction)

                mqttManager.disconnect()
                startActivity(intent)
                finish()
            }
        })
    }

    /**
     * Retrieves routes and stops based on the selected mode.
     */
    private fun getRoutesAndStops(isOffline: Boolean): HashMap<String, Any> {
        val routesAndStops = HashMap<String, Any>()
        val gson = Gson()
        val busData = gson.fromJson(data, Bus::class.java)
        val busses = busData.shared!!.config!!.busConfig
        for (bus in busses) {
            if (bus.aid == aaid) {
                accessToken = bus.accessToken
                name = bus.bus
            }
        }
                Log.d("arraybus: ", busses.toString())
        if (isOffline) {
            routesAndStops["routes"] = busData.shared!!.busRoute1!!.jsonMember1!! + busData.shared.busRoute1!!.jsonMember1!!
            routesAndStops["stops"] = busData.shared.busStop1!!.jsonMember1!!
            routesAndStops["sharedBus"] = busses
            routesAndStops["bearing"] = busData.shared.bearing!!
        } else {
            routesAndStops["routes"] = busData.shared!!.busRoute!!
            routesAndStops["stops"] = busData.shared.busStop!!
            routesAndStops["sharedBus"] = busses
        }
        return routesAndStops
    }

    /**
     * Starts location updates if the necessary permissions are granted.
     */
    private fun startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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
     * Publishes a request for necessary attributes.
     */
    private fun requestData() {
        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys", "busRoute,busStop,busRoute2,busStop2,config,bearing")
        val jsonString = jsonObject.toString()
        mqttManager.publish("v1/devices/me/attributes/request/5", jsonString)
    }

}