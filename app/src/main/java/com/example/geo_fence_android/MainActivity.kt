package com.example.geo_fence_android


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.geo_fence_android.model.LocationInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.roam.sdk.Roam
import com.roam.sdk.RoamTrackingMode
import com.roam.sdk.callback.RoamCallback
import com.roam.sdk.callback.RoamLocationCallback
import com.roam.sdk.models.RoamError
import com.roam.sdk.models.RoamUser

lateinit var radius:String
lateinit var username:String
lateinit var latitude:String
lateinit var longitude:String
private var mapFragment: SupportMapFragment? =null
private var fab:View?=null

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps);

        initUI()

        var sharedPreferences=getSharedPreferences("Geo-fence", MODE_PRIVATE)

        fab?.setOnClickListener {
            openSettingsActivity()
        }

        radius= sharedPreferences.getString("radius","500").toString()
        latitude=sharedPreferences.getString("latitude","0").toString()
        longitude=sharedPreferences.getString("longitude","0").toString()
        username="default"

        setupRoam()

    }

    private fun setupRoam() {
        var sharedPreferences=getSharedPreferences("Geo-fence", MODE_PRIVATE)
        var editor= sharedPreferences.edit()

        Roam.initialize(this, "f47884af842c8548f9c3b654e758158bcef04a20c0c755444b31adb58249f23d")

        if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this)
        } else if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this)
        }
        if(sharedPreferences.getString("userID","").toString()===""){
            Roam.createUser("dfd",null,object : RoamCallback {
                override fun onSuccess(roamUser: RoamUser) {
                    editor.putString("userID",roamUser.userId)
                    editor.apply()
                }

                override fun onFailure(roamError: RoamError) {
                    Log.e("user",roamError.message)
                }
            })}

        if(sharedPreferences.getString("latitude",null)==null) {
            Roam.getCurrentLocation(
                RoamTrackingMode.DesiredAccuracy.HIGH,
                10,
                object : RoamLocationCallback {

                    override fun location(p0: android.location.Location?) {
                        Log.d("loc", p0?.latitude.toString())
                        latitude = p0?.latitude.toString()
                        longitude = p0?.longitude.toString()
                        editor.putString("latitude", latitude)
                        editor.putString("longitude", longitude)
                        editor.apply()
                        updateMap()
                    }

                    override fun onFailure(roamError: RoamError) {
                        Log.d("loc", roamError.message)
                    }
                })
        }else{
            updateMap()
        }
    }

    private fun initUI() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fab =findViewById(R.id.fab);

    }

    private fun updateMap(){
        mapFragment?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                val loc = LatLng(latitude.toDouble(), longitude.toDouble())
                googleMap.addMarker(
                    MarkerOptions()
                        .position(loc)
                        .title("Your Location")
                )
                val circle: Circle = googleMap.addCircle(
                    CircleOptions()
                        .center(LatLng(latitude.toDouble(), longitude.toDouble()))
                        .radius(radius.toDouble())
                        .fillColor(Color.parseColor("#7f0A95FF"))
                        .strokeWidth(0f)
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        loc,
                        15f
                    )
                )
            }
        })
    }

    private fun openSettingsActivity() {
        val userProfile= LocationInfo(username, latitude, longitude)
        userProfile.setRadius(radius)

        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        val b=Bundle()

        b.putSerializable("data",userProfile)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
    }


}