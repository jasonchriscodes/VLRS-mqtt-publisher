package com.jason.publisher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.jason.publisher.model.Bus
import com.jason.publisher.services.MqttManager
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject

class TestActivity : AppCompatActivity() {
    private val persistence = MemoryPersistence()
    private val mqttClient = MqttClient("tcp://43.226.218.94:1883", "jasonAndroidClientId", persistence)
    private val connectOptions = MqttConnectOptions()
    private lateinit var textView: TextView
    private var text = "Test"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        textView = findViewById<TextView>(R.id.textView2)
        textView.text = text
        connectOptions.userName = "cngz9qqls7dk5zgi3y4j"
        connectOptions.isCleanSession = true
        connectOptions.connectionTimeout = 10
        connectOptions.keepAliveInterval = 60
        mqttClient.connect(connectOptions)

        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys","message")
        val jsonString = jsonObject.toString()
        val gson = Gson()
        mqttClient.subscribe("v1/devices/me/attributes/response/+") { _, msg ->
            runOnUiThread {
                if (text != msg.toString()) {
                    text = msg.toString()
                    val data = gson.fromJson(text, Bus::class.java)
                    var msg = data.shared!!.message
                    msg = "Text from admin: $msg"
                    showNotification(msg, "Channel12", System.currentTimeMillis().toInt())
                }
            }
            Log.d("MSGGG", msg.toString())
        }

        val mqttMessage = MqttMessage(jsonString.toByteArray())
        mqttMessage.qos = 1
        mqttMessage.isRetained = false
        mqttClient.publish("v1/devices/me/attributes/request/5", mqttMessage)

        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                mqttClient.publish("v1/devices/me/attributes/request/5", mqttMessage)
                textView.text = text
                handler.postDelayed(this, 100)
            }
        }
        handler.post(updateRunnable)
        textView.setOnClickListener {
            mqttClient.publish("v1/devices/me/attributes/request/5", mqttMessage)
            textView.text = text
        }
    }

    fun showNotification(content: String, channelId: String, id: Int) {
        val notificationManageer = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManageer.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Connected")
            .setSmallIcon(R.drawable.ic_signal)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setSubText("Data Send")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] per8missions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // handle permission for post notifications here if needed
            return
        }
        NotificationManagerCompat.from(this).notify(id, builder.build())
    }

}