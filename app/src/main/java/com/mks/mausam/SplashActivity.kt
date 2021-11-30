package com.mks.mausam

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class SplashActivity : AppCompatActivity() {

    lateinit var mfusedLocation: FusedLocationProviderClient
    private var myRequestCode = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.statusBarColor = Color.parseColor("#ff71fa")
        mfusedLocation = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
    }

    private fun getLastLocation() {
        if (checkPermission()){
            if (locationEnable()){
                mfusedLocation.lastLocation.addOnCompleteListener {
                    task->
                    var location:Location? = task.result
                    if (location == null){
                        newLocation()
                    }else{
//                        Log.e("Location: ", location.longitude.toString());
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("lat", location.latitude.toString())
                            intent.putExtra("long", location.longitude.toString())
                            startActivity(intent)
                            finish()
                        },2000)
                    }
                }
            }else{
                Toast.makeText(this, "Turn on GPS", Toast.LENGTH_LONG).show()
            }
        }else{
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun newLocation() {
        var locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        mfusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mfusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }
        private val locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                var lastLocation:Location = p0.lastLocation
            }
        }

    private fun locationEnable(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission
            .ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), myRequestCode)
    }

    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == myRequestCode){
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
                getLastLocation()
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}