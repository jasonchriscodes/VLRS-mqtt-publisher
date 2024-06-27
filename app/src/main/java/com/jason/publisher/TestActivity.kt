package com.jason.publisher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jason.publisher.databinding.ActivityTestBinding
import com.jason.publisher.services.SharedPrefMananger
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject

/**
 * TestActivity is responsible for managing MQTT connections and publishing messages.
 */
class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var mqttManager: MqttClient

    /**
     * Overrides the onCreate method to initialize the activity and set up MQTT connections.
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize MQTT client
        mqttManager = MqttClient(MainActivity.SERVER_URI, MainActivity.CLIENT_ID,
            org.eclipse.paho.client.mqttv3.persist.MemoryPersistence()
        )

        // Set up MQTT connection options
        val connectOptions = MqttConnectOptions()
        connectOptions.userName = "Tl4ZHLQUdpBiqnHEW8hh"
        connectOptions.isCleanSession = true
        connectOptions.connectionTimeout = 10
        connectOptions.keepAliveInterval = 60

        // Connect button click listener
        binding.btnConnect.setOnClickListener {
            mqttManager.connect(connectOptions)
        }

        // Disconnect button click listener
        binding.btnDisconnect.setOnClickListener {
            mqttManager.disconnect()
        }

        // Publish button click listener
        binding.btnPublish.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("message", binding.etMessage.text.toString())
            val jsonString = jsonObject.toString()
            val mqttMessage = MqttMessage(jsonString.toByteArray())
            mqttMessage.qos = 1
            mqttMessage.isRetained = false

            mqttManager.publish(MainActivity.PUB_POS_TOPIC, mqttMessage)
        }
    }
}