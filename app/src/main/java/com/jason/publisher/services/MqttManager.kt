package com.jason.publisher.services

import android.util.Log
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

/**
 * Class responsible for managing MQTT connections, publishing, and subscribing to topics.
 *
 * @param serverUri The URI of the MQTT server.
 * @param clientId The client ID to use for the connection.
 * @param username The username for the MQTT connection (default is "cngz9qqls7dk5zgi3y4j").
 */
class MqttManager(
    serverUri: String,
    clientId: String,
    private var username: String = "cngz9qqls7dk5zgi3y4j" // Bus A
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

    /**
     * Checks if the MQTT client is connected.
     *
     * @return True if connected, false otherwise.
     */
    fun isMqttConnect(): Boolean {
        return mqttClient.isConnected
    }

    /**
     * Publishes a message to a specified topic.
     *
     * @param topic The topic to publish to.
     * @param message The message to publish.
     * @param qos The Quality of Service level for the message (default is 0).
     */
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

    /**
     * Subscribes to a specified topic and sets a callback to handle incoming messages.
     *
     * @param topic The topic to subscribe to.
     * @param callback The callback to handle incoming messages.
     */
    fun subscribe(topic: String, callback: (String) -> Unit) {
        mqttClient.subscribe(topic) { _, msg ->
            val payload = String(msg.payload)
            callback(payload)
        }
    }

    /**
     * Disconnects from the MQTT broker.
     */
    fun disconnect() {
        mqttClient.disconnect()
    }

    /**
     * Gets the username used for the MQTT connection.
     *
     * @return The username.
     */
    fun getUsername(): String {
        return username
    }
}