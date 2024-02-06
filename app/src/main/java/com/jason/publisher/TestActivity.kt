package com.jason.publisher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jason.publisher.databinding.ActivityTestBinding
import com.jason.publisher.services.SharedPrefMananger
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var mqttManager: MqttClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mqttManager = MqttClient("tcp://demo.thingsboard.io", MainActivity.CLIENT_ID,
            org.eclipse.paho.client.mqttv3.persist.MemoryPersistence()
        )

        val connectOptions = MqttConnectOptions()
        connectOptions.userName = "qI45RIAiIVpJ4Ubz5qQJ"
        connectOptions.isCleanSession = true
        connectOptions.connectionTimeout = 10
        connectOptions.keepAliveInterval = 60

        binding.btnConnect.setOnClickListener {
            mqttManager.connect(connectOptions)
        }

        binding.btnDisconnect.setOnClickListener {
            mqttManager.disconnect()
        }

        binding.btnPublish.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("message", binding.etMessage.text.toString())
            val jsonString = jsonObject.toString()
            val mqttMessage = MqttMessage(jsonString.toByteArray())
            mqttMessage.qos = 1
            mqttMessage.isRetained = false

            mqttManager.publish(MainActivity.PUB_POS_TOPIC, mqttMessage)
        }

//        sharedPrefMananger = SharedPrefMananger(this)
//
//        val messageList = sharedPrefMananger.getMessageList("messageKey")
//        val adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_2,
//            android.R.id.text1,
//            messageList.map {
//                "${it.message}\n${Helper.convertTimeToReadableFormat(it.date)}"
//            })
//        binding.messageList.adapter = adapter
//        adapter.notifyDataSetChanged()
    }
}