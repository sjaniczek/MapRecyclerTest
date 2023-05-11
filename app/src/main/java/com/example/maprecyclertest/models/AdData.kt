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
            "adIdd" to adId,
            "petName" to petName,
            "lostDate" to lostDate,
            "ownerName" to ownerName,
            "phoneNumber" to phoneNumber,
            "decodedAddress" to decodedAddress
        )
    }
    data class LocationData(val latitude: Double, val longitude: Double)
}

