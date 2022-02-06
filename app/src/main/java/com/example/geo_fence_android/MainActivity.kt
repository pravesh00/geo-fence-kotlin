package com.example.geo_fence_android


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
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
private var zoomLevel:Int?=null
private var marker:MarkerOptions?=null
lateinit var oldLat:String
lateinit var oldLng:String
lateinit var banner:TextView

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps);

        initUI()

        var sharedPreferences=getSharedPreferences("Geo-fence", MODE_PRIVATE)
        var editor= sharedPreferences.edit()

        fab?.setOnClickListener {
            openSettingsActivity()
        }

        radius= sharedPreferences.getString("radius","500").toString()
        latitude=sharedPreferences.getString("latitude","0").toString()
        longitude=sharedPreferences.getString("longitude","0").toString()
        username="default"

        Roam.initialize(this, "f47884af842c8548f9c3b654e758158bcef04a20c0c755444b31adb58249f23d")

        if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this)
        } else if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this)
        }
        if(sharedPreferences.getString("userID","").toString()==""){
            Roam.createUser("dfd",null,object : RoamCallback {
                override fun onSuccess(roamUser: RoamUser) {
                    editor.putString("userID",roamUser.userId)
                    editor.apply()
                }

                override fun onFailure(roamError: RoamError) {
                    Log.e("user",roamError.message)
                }
            })
        }
        else{
            var userId=sharedPreferences.getString("userId","default");
            Roam.getUser(userId, object : RoamCallback {
                override fun onSuccess(roamUser: RoamUser) {
                }

                override fun onFailure(roamError: RoamError) {
                }
            })
        }
        if(sharedPreferences.getString("latitude",null)==null) {
            fetchLocationAndUpdate()
        }else{
            oldLat=sharedPreferences.getString("latitude",null).toString()
            oldLng=sharedPreferences.getString("longitude",null).toString()
            updateMap()
        }

    }


    private fun fetchLocationAndUpdate() {
        if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this)
        } else if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this)
        }
        var sharedPreferences=getSharedPreferences("Geo-fence", MODE_PRIVATE)
        var editor= sharedPreferences.edit()

            Roam.getCurrentLocation(
                RoamTrackingMode.DesiredAccuracy.HIGH,
                10,
                object : RoamLocationCallback {

                    override fun location(p0: android.location.Location?) {
                        Log.d("loc", p0?.latitude.toString())
                        latitude = p0?.latitude.toString()
                        longitude = p0?.longitude.toString()
                        oldLat= latitude
                        oldLng= longitude
                        editor.putString("latitude", latitude)
                        editor.putString("longitude", longitude)
                        editor.apply()
                        updateMap()
                    }

                    override fun onFailure(roamError: RoamError) {
                        Log.d("loc", roamError.message)
                    }
                })


    }

    private fun initUI() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        banner=findViewById(R.id.txtBanner)
        fab =findViewById(R.id.fab);

    }
    fun getZoomLevel(circle: Circle?){
        if (circle != null) {
            val radius = circle.radius
            val scale = radius / 500
            zoomLevel = (15 - Math.log(scale) / Math.log(2.0)).toInt()
        }
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun updateMap(){
        mapFragment?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                val loc = LatLng(latitude.toDouble(), longitude.toDouble())
                marker=MarkerOptions().position(loc).title("Your Currrent Location")

                var markerMap= googleMap.addMarker(
                    marker!!
                )

                val circle: Circle = googleMap.addCircle(
                    CircleOptions()
                        .center(LatLng(oldLat.toDouble(), oldLng.toDouble()))
                        .radius(radius.toDouble())
                        .fillColor(Color.parseColor("#7f0A95FF"))
                        .strokeWidth(0f)
                )
                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    override fun run() {

                        Roam.getCurrentLocation(
                            RoamTrackingMode.DesiredAccuracy.HIGH,
                            10,
                            object : RoamLocationCallback {

                                override fun location(p0: android.location.Location?) {
                                    Log.d("loc", p0?.latitude.toString())
                                    marker=MarkerOptions().position(LatLng(p0?.latitude!!,p0.longitude)).title("Your current Location")
                                    markerMap?.remove()
                                    var newMarker=googleMap.addMarker(marker!!)
                                    markerMap=newMarker
                                    if(distance(oldLat.toDouble(),p0.latitude, oldLng.toDouble(),p0.longitude)> radius.toDouble()){
                                        banner.setText("Out of Range")
                                    }else{
                                        banner.setText("You are in Range")
                                    }

                                }

                                override fun onFailure(roamError: RoamError) {
                                    Log.d("loc", roamError.message)
                                }
                            })

                        handler.postDelayed(this, 1000)//1 sec delay
                    }
                }, 0)

                getZoomLevel(circle)
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        loc,
                        zoomLevel?.toFloat()!!
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