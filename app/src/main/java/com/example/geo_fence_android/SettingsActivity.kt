package com.example.geo_fence_android

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import com.example.geo_fence_android.model.LocationInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.roam.sdk.Roam
import com.roam.sdk.RoamTrackingMode
import com.roam.sdk.callback.RoamCallback
import com.roam.sdk.callback.RoamLocationCallback
import com.roam.sdk.models.RoamError
import com.roam.sdk.models.RoamUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

lateinit var txtRadius:TextInputEditText
lateinit var btnCancel: Button
lateinit var btnSubmit:Button
lateinit var data:LocationInfo
lateinit var intitalData:LocationInfo
lateinit var btnFetch:Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //Intitializing UI
        initUI()
        //Bundle data
        data= intent.getSerializableExtra("data") as LocationInfo
        intitalData=data
        txtRadius.setText(data.getRadius())
        btnCancel.setOnClickListener { view->cancel() }
        btnSubmit.setOnClickListener { view->submit() }
        txtRadius.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                data.setRadius(s.toString())
            }
        })
        btnFetch.setOnClickListener { view->fetch() }

    }

    private fun fetch() {

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

        Roam.getCurrentLocation(RoamTrackingMode.DesiredAccuracy.HIGH,10,object :
            RoamLocationCallback {

            override fun location(p0: android.location.Location?) {
                Log.d("loc", p0?.latitude.toString())
                latitude= p0?.latitude.toString()
                longitude=p0?.longitude.toString()
                editor.putString("latitude", latitude)
                editor.putString("longitude", longitude)
                editor.apply()
                sentDataToBackend()
                Snackbar.make(findViewById(R.id.settings),"Location fetched!!!",Snackbar.LENGTH_LONG).show()

            }

            override fun onFailure(roamError: RoamError) {
                Log.d("loc",roamError.message)
            }
        })

    }

    private fun sentDataToBackend() {
        val apiInterface=APIInterface.create().postLocationDetails(data);
        apiInterface.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.v("retrofit", "call failed")
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if(response?.body()!=null){
                    Log.d("datasent",response.message())
                }
            }

        })
    }

    private fun submit() {
        var sharedPreferences=getSharedPreferences("Geo-fence", MODE_PRIVATE)
        var editor= sharedPreferences.edit()
        editor.putString("radius", data.getRadius());
        editor.apply()
        var i= Intent(this,MainActivity::class.java)
        var b=Bundle()
        b.putSerializable("data", data)
        startActivity(i)
    }

    private fun cancel() {
        var i= Intent(this,MainActivity::class.java)
        var b=Bundle()
        b.putSerializable("data", intitalData)
        startActivity(i)
    }

    private fun initUI() {
        txtRadius=findViewById(R.id.txtRadius)
        btnCancel=findViewById(R.id.btnCancel)
        btnSubmit=findViewById(R.id.btnSubmit)
        btnFetch=findViewById(R.id.btnFetch)

    }
}