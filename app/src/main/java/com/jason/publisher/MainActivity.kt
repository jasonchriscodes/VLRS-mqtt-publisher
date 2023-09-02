package com.jason.publisher

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jason.publisher.R
import com.google.android.gms.location.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MainActivity : AppCompatActivity() {

    private val brokerUrl = "tcp://43.226.218.94:1883"
    private val clientId = "jasonAndroidClientId"
    private val username = "cngz9qqls7dk5zgi3y4j"
    private val topic = "v1/devices/me/telemetry"

    private lateinit var mqttClient: MqttClient
    private lateinit var textView: TextView
    private var lat = 0.0
    private var lon = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            // Do something with the latest location here
            if (lastLocation != null) {
                lat = lastLocation.latitude
                lon = lastLocation.longitude
                // Use latitude and longitude data according to your needs
                textView.text = "$lat, $lon"
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val delayInMillis: Long = 1000

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Location permission denied, you can provide feedback to the user or handle it according to your needs
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        textView = findViewById(R.id.textView)

        // Set up MQTT client
        val persistence = MemoryPersistence()
        mqttClient = MqttClient(brokerUrl, clientId, persistence)
        val options = MqttConnectOptions()
        //options.isCleanSession = true
        options.userName = username;
        //options.
        //options.password = password

        connectMQTTClient(options)

        startSendingData()
    }

    private fun startSendingData() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Submit data here
                if (lat != 0.0 && lon != 0.0) {
                    publishMessage("{\"latitude\":$lat, \"longitude\":$lon}")
                    textView.text = "The most recent Coordinates sent\nLatitude: $lat\nLongitude: $lon"
                }

                // Next data delivery schedule
                handler.postDelayed(this, delayInMillis)
            }
        }, delayInMillis)
    }

    private fun connectMQTTClient(options: MqttConnectOptions) {
        try {
            mqttClient.connect(options)
            subscribeToTopic()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    // callback function
    private fun publishMessage(msg: String) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = 1
            message.isRetained = false
            mqttClient.publish(topic, message)
            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    TODO("Not yet implemented")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d("Message Arrived", "Message Arrived")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("Delivery Complete", "Message sent")
                }

            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        try {
            mqttClient.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}
