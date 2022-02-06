package com.example.geo_fence_android.model

import java.io.Serializable

class LocationInfo: Serializable {
    private lateinit var user: String
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var radius:String

    constructor(user: String, latitude: String, longitude: String) {
        this.user = user
        this.latitude = latitude
        this.longitude = longitude
    }

    constructor()

    fun getUsername():String{
        return user
    }

    fun getLatitude():String{
        return latitude
    }

    fun getLongitude():String{
        return longitude
    }
    fun setUsername(name:String?){
        this.user=name!!
    }

    fun setLatitude(lat:String?){
        this.latitude=lat!!
    }

    fun setLongitude(lng:String?){
        this.longitude=lng!!
    }

    fun getRadius():String{
        return radius
    }

    fun setRadius(rad:String?){
        this.radius=rad!!
    }



}