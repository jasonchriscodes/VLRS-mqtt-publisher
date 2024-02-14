package com.jason.publisher.services

import android.util.Log
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttManager(
    serverUri: String,
    clientId: String,
    username: String = "cngz9qqls7dk5zgi3y4j" // Bus A
) {
    private val persistence = MemoryPersistence()
    private val mqttClient = MqttClient(serverUri, clientId, persistence)
    private val connectOptions = MqttConnectOptions()

    init {
        connectOptions.userName = username
        connectOptions.isCleanSession = true
        connectOptions.connectionTimeout = 10
        connectOptions.keepAliveInterval = 60
        try {
            // connect to the MQTT broker when an instance of MqttManager is created
            mqttClient.connect(connectOptions)
        } catch (e: MqttException) {
            Log.d("MqttManager", "Failed to connect to MQTT broker: ${e.message}")
            // handle the exception according to your app's requirements
        }
    }

    fun publish(topic: String, message: String, qos: Int = 0) {
        try {
            Log.d("mqtt manager", "publish method call")
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = qos
            mqttMessage.isRetained = false
            mqttClient.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            Log.d("MqttManager", "Failed to publish message: ${e.message}")
            // handle the exception according to your app's requirements
        }
    }

    fun subscribe(topic: String, callback: (String) -> Unit) {
        mqttClient.subscribe(topic) { _, msg ->
            val payload = String(msg.payload)
            callback(payload)
        }
    }

    // disconnect from the MQTT broker
    fun disconnect() {
        mqttClient.disconnect()
    }
}