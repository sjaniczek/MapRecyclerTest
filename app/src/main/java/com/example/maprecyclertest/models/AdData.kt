package com.example.maprecyclertest.models

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties



@IgnoreExtraProperties
data class AdData(
    var adId: String? = "",
    var petName: String? = "",
    var lostDate: String? = "",
    var ownerName: String? = "",
    var phoneNumber: String? = "",
    var decodedAddress: String? = "",
    var petBehavior: String? = "",
    var imageUrl: String? = "",
    var imageUri: Uri? = null,
    var locationData: LocationData? = null
){

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "adId" to adId,
            "petName" to petName,
            "lostDate" to lostDate,
            "ownerName" to ownerName,
            "phoneNumber" to phoneNumber,
            "decodedAddress" to decodedAddress,
            "petBehavior" to petBehavior,
            "imageUrl" to imageUrl,
            "locationData" to locationData
        )
    }

//    fun getImageUriObject(): Uri? {
//        return imageUri?.let {
//            Uri.parse(it)
//        }
//    }
    data class LocationData(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    ) {
        constructor() : this(0.0, 0.0)
    }
}

