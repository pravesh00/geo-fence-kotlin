package com.example.geo_fence_android


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import java.nio.BufferUnderflowException

lateinit var radius:String
lateinit var username:String
lateinit var latitude:String
lateinit var longitude:String

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps);

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val fab: View =findViewById(R.id.fab);

        fab.setOnClickListener {
            openSettingsActivity()
        }
        //TODO: mock data to be removed
        radius="500"
        latitude="-33.852"
        longitude="151.211"
        username="default"
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
        val sydney = LatLng(-33.852, 151.211)
        googleMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        val circle: Circle = googleMap.addCircle(
            CircleOptions()
                .center(LatLng(-33.852, 151.211))
                .radius(10000.0)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}