package com.example.geo_fence_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import com.example.geo_fence_android.model.LocationInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonArray
import com.google.gson.JsonElement
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
        sentDataToBackend()
        //TODO: Location fetching
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