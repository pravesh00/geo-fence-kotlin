package com.example.geo_fence_android

import android.content.Context
import android.util.Log
import com.roam.sdk.models.RoamError

import com.roam.sdk.models.RoamLocation

import com.roam.sdk.service.RoamReceiver


class LocationReceiver : RoamReceiver() {
    override fun onLocationUpdated(context: Context?, roamLocation: RoamLocation?) {
        super.onLocationUpdated(context, roamLocation)
        Log.e("location",roamLocation.toString())
    }

    override fun onError(context: Context?, roamError: RoamError?) {
        Log.e("location",roamError.toString())
    }
}