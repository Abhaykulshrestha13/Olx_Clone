package com.example.olxapp.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.eample.olxapp.ui.login.LoginActivity
import com.example.olxapp.MainActivity
import com.example.olxapp.R
import com.example.olxapp.baseActivity
import com.example.olxapp.utilites.Constants
import com.example.olxapp.utilites.SharedPref
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.*

class SplashScreen: baseActivity(){

    private val MY_PERMISSIONS_REQUEST_LOCATION = 100
    private var locationRequest:LocationRequest? = null
    private val REQUEST_GPS = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        askForPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationCallback()
    }



    override fun onResume() {
        super.onResume()
        askForPermission()
    }

    private fun askForPermission() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this,permissions,MY_PERMISSIONS_REQUEST_LOCATION)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==MY_PERMISSIONS_REQUEST_LOCATION){
            var granted = false
            for(grantResult in grantResults){
                if(grantResult==PackageManager.PERMISSION_GRANTED){
                    granted = true
                }
            }
            if(granted){
                enableGps()
            }
        }
    }

    private fun enableGps() {
        locationRequest = LocationRequest.create()
        locationRequest?.interval = 3000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        var task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        task.addOnCompleteListener {
            try{
                val reponse = task.getResult(ApiException::class.java)
                startLocationUpdates()
            }
            catch (exception:ApiException){
                when(exception.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this,REQUEST_GPS)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_GPS){
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }
    private fun getLocationCallback() {
        locationCallback = object:LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                val location = p0.lastLocation
                SharedPref(this@SplashScreen).setString(Constants.CITY_NAME,getCityName(location))
//                getCityName(location)
//                Toast.makeText(this@SplashScreen,getCityName(location),Toast.LENGTH_LONG).show()
                if(SharedPref(this@SplashScreen).getString(Constants.USER_ID)?.isEmpty()!!) {
                    startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                    finish()
                }
                else{
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun getCityName(location: Location?): String {
        var cityName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        try{
            val address = geoCoder.getFromLocation(location?.latitude!!,location.longitude,1)
            cityName = address[0].locality
        }catch (e:IOException){
            Log.d("Location exception","failed")
        }
        return cityName
    }
}