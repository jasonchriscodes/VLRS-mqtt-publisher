package com.jason.publisher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.databinding.ActivitySplashScreenBinding
import org.json.JSONObject

class SplashScreen : AppCompatActivity() {
    private lateinit var mqttManager: MqttManager
    private lateinit var binding: ActivitySplashScreenBinding
    private var data: String? = null
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("Android ID", mId)
        var name = ""
        val colRef = db.collection("config").get()
        colRef.addOnSuccessListener {query ->
            for (doc in query.documents) {
                if (doc.data!!["aid"] == mId) {
                    name = doc.data!!["name"].toString()
                }
            }
        }

        // initialize the MQTT manager with server URI and client ID
        mqttManager = MqttManager(serverUri = "tcp://43.226.218.94:1883", clientId = "jasonAndroidClientId")

        // subscribe to a MQTT topic for attribute responses and update UI accordingly
        mqttManager.subscribe(topic = "v1/devices/me/attributes/response/+") { message ->
            data = message
        }

        requestData()

        Handler(Looper.getMainLooper()).postDelayed({
            mqttManager.disconnect()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("busData", data)
            intent.putExtra("nameDevice", name)
            startActivity(intent)
            finish()
        }, 3000)

    }

    private fun requestData() {
        val jsonObject = JSONObject()
        jsonObject.put("sharedKeys","busRoute,busStop")
        val jsonString = jsonObject.toString()
        mqttManager.publish("v1/devices/me/attributes/request/5", jsonString)
    }

}