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

        mqttManager = MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = "jasonAndroidClientId")

        mqttManager.subscribe(topic = "v1/devices/me/attributes/response/+") { message ->
            runOnUiThread {
                binding.textview.text = message
            }
        }

        binding.button.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("sharedKeys","busRoute")
            val jsonString = jsonObject.toString()
            mqttManager.publish("v1/devices/me/attributes/request/5", jsonString)
        }

        binding.textview.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttManager.disconnect()
    }
}