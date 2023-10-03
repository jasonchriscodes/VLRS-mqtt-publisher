package com.jason.publisher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jason.publisher.databinding.ActivitySplashScreenBinding
import org.json.JSONObject

class SplashScreen : AppCompatActivity() {
    private lateinit var mqttManager: MqttManager
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize the MQTT manager with server URI and client ID
        mqttManager = MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = "jasonAndroidClientId")

        // subscribe to a MQTT topic for attribute responses and update UI accordingly
        mqttManager.subscribe(topic = "v1/devices/me/attributes/response/+") { message ->
            runOnUiThread {
                binding.textview.text = message
            }
        }

        // set a click listener for a button to publish a JSON message
        binding.button.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("sharedKeys","busRoute,busStop")
            val jsonString = jsonObject.toString()
            mqttManager.publish("v1/devices/me/attributes/request/5", jsonString)
        }

        // set a click listener for a TextView to navigate to the MainActivity
        binding.textview.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // disconnect MQTT manager when the activity is destroyed
        mqttManager.disconnect()
    }
}