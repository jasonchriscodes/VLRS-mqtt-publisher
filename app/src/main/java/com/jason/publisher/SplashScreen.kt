package com.jason.publisher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.databinding.ActivitySplashScreenBinding
import com.jason.publisher.services.LocationManager
import com.jason.publisher.services.MqttManager
import com.jason.publisher.services.SharedPrefMananger
import org.json.JSONObject

class SplashScreen : AppCompatActivity() {
    private lateinit var mqttManager: MqttManager
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var binding: ActivitySplashScreenBinding
    private var data: String? = null
    private val db = Firebase.firestore
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        sharedPrefMananger = SharedPrefMananger(this)
        var name = ""
        var accessToken = ""
        val colRef = db.collection("config").get()
        colRef.addOnSuccessListener {query ->
            for (doc in query.documents) {
                if (doc.data!!["aid"] == mId) {
                    name = doc.data!!["name"].toString()
                    accessToken = doc.data!!["accessToken"].toString()
                    sharedPrefMananger.saveString(Constant.deviceNameKey, name)
                }
            }
        }

        // initialize the MQTT manager with server URI and client ID
        mqttManager = MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = "jasonAndroidClientId")
        locationManager = LocationManager(this)
        startLocationUpdate()

        // subscribe to a MQTT topic for attribute responses and update UI accordingly
        mqttManager.subscribe(topic = "v1/devices/me/attributes/response/+") { message ->
            data = message
        }

        requestData()

        Handler(Looper.getMainLooper()).postDelayed({
            mqttManager.disconnect()
            //val intent = Intent(this, MainActivity::class.java)
            val intent = Intent(this, LiveActivity::class.java)
            intent.putExtra(Constant.busDataKey, data)
            intent.putExtra(Constant.deviceNameKey, name)
            intent.putExtra(Constant.tokenKey, accessToken)
            intent.putExtra(Constant.aidKey, mId)
            intent.putExtra("lat", latitude)
            intent.putExtra("lng", longitude)
            startActivity(intent)
            finish()
        }, 3000)

    }

    private fun startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            locationManager.startLocationUpdates(object : LocationListener {
                override fun onLocationUpdate(location: Location) {
                    latitude = location.latitude
                    longitude = location.longitude
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

    private fun requestData() {
        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys","busRoute,busStop")
        val jsonString = jsonObject.toString()
        mqttManager.publish("v1/devices/me/attributes/request/5", jsonString)
    }

}