package com.example.maprecyclertest.models

import androidx.lifecycle.ViewModel

class AdViewModel : ViewModel() {
    var adData: AdData? = null

    fun saveFormData(adData: AdData) {
        this.adData = adData
    }
}