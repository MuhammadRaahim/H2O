package com.jdars.h20.models

import com.google.android.gms.maps.model.LatLng

data class Service(
    var id:String? = null,
    var userid:String? = null,
    var name:String? = null,
    val Phone:String? =null,
    val address:String?=null,
    val latLng: LatLng?=null,
    var date: String? = null,
    var description: String? = null,
    var quantity:String? = null
)