package com.jason.publisher

import android.util.Log
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttManager(serverUri: String, clientId: String) {
    private val persistence = MemoryPersistence()
    private val mqttClient = MqttClient(serverUri, clientId, persistence)
    private val connectOptions = MqttConnectOptions()

    init {
        connectOptions.userName = "cngz9qqls7dk5zgi3y4j"
        connectOptions.isCleanSession = true
        connectOptions.connectionTimeout = 10
        connectOptions.keepAliveInterval = 60
        try {
            mqttClient.connect(connectOptions)
        } catch (e: MqttException) {
            Log.d("MqttManager", "Failed to connect to MQTT broker: ${e.message}")
            // Handle the exception according to your app's requirements
        }
    }

    fun publish(topic: String, message: String, qos: Int = 0) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = qos
            mqttClient.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            Log.d("MqttManager", "Failed to publish message: ${e.message}")
            // Handle the exception according to your app's requirements
        }
    }

    fun subscribe(topic: String, callback: (String) -> Unit) {
        mqttClient.subscribe(topic) { _, msg ->
            val payload = String(msg.payload)
            callback(payload)
        }
    }

    fun disconnect() {
        mqttClient.disconnect()
    }
}