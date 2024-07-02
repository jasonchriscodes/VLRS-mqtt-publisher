package com.jason.publisher

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.jason.publisher.databinding.ActivitySplashScreenBinding
import com.jason.publisher.model.Bus
import com.jason.publisher.model.BusData
import com.jason.publisher.model.BusItem
import com.jason.publisher.services.LocationManager
import com.jason.publisher.services.ModeSelectionDialog
import com.jason.publisher.services.MqttManager
import com.jason.publisher.services.SharedPrefMananger
import org.json.JSONObject

/**
 * SplashScreen activity responsible for initializing the application and handling initial setup.
 * It retrieves necessary data, such as device ID and configuration, then routes to the appropriate screen.
 */
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPrefMananger: SharedPrefMananger
    private lateinit var modeSelectionDialog: ModeSelectionDialog
    private lateinit var binding: ActivitySplashScreenBinding
    private var data: String? = null
    var name = ""
    private var accessToken = ""
    private var aaid = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private var bearing = 0.0F
    private var speed = 0.0F
    private var direction = ""

    /**
     * Overrides the onCreate method to initialize the activity and perform necessary setup.
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modeSelectionDialog = ModeSelectionDialog(this)
        aaid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        sharedPrefMananger = SharedPrefMananger(this)
        locationManager = LocationManager(this)
        startLocationUpdate()
        routeToNextScreen()

        // Start animation
        val logoExplorer = findViewById<ImageView>(R.id.logoExplorer)
        val logoFullers = findViewById<ImageView>(R.id.logoFullers)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoExplorer.startAnimation(animation)
        logoFullers.startAnimation(animation)
    }

    /**
     * Routes to the next screen after a specified delay.
     */
    private fun routeToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            showOptionDialog()
        }, 5000)
    }

    /**
     * Shows the mode selection dialog and handles the selected mode.
     */
    private fun showOptionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.mode_selection_dialog)

        val onlineModeButton = dialog.findViewById<Button>(R.id.onlineModeButton)
        val offlineModeButton = dialog.findViewById<Button>(R.id.offlineModeButton)

        onlineModeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        offlineModeButton.setOnClickListener {
            val intent = Intent(this, OfflineActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Starts location updates if the necessary permissions are granted.
     */
    private fun startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.startLocationUpdates(object : LocationListener {
                override fun onLocationUpdate(location: Location) {
                    latitude = location.latitude
                    longitude = location.longitude
                    bearing = location.bearing
                    speed = location.speed
                    direction = Helper.bearingToDirection(location.bearing)
                }
            })
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                123
            )
        }
    }
}
