package com.example.geo_fence_android

import com.example.geo_fence_android.model.LocationInfo
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface APIInterface {
    @GET("/")
    fun getDomain(): Call<String>
    @Headers("Content-type:application/json")
    @POST("/user")
    fun postLocationDetails(@Body reqBody: LocationInfo): Call<ResponseBody>

    companion object {

        var BASE_URL = "https://geo-fence-pravesh.herokuapp.com/user/"

        fun create() : APIInterface {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIInterface::class.java)

        }
    }

}