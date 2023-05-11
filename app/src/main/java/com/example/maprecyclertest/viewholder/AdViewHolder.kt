package com.example.maprecyclertest.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maprecyclertest.R
import com.example.maprecyclertest.models.AdData

class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val adPetName: TextView = itemView.findViewById(R.id.adPetName)
    private val adOwnerName: TextView = itemView.findViewById(R.id.adOwnerName)

    fun bindToAd(ad: AdData, starClickListener: View.OnClickListener){
        adPetName.text = ad.petName
        adOwnerName.text = ad.ownerName

    }
}