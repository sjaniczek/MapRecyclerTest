package com.example.maprecyclertest.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maprecyclertest.R
import com.example.maprecyclertest.models.AdData
import com.squareup.picasso.Picasso

class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val adPetName: TextView = itemView.findViewById(R.id.adPetName)
    private val adOwnerName: TextView = itemView.findViewById(R.id.adOwnerName)
    private val adPhoneNumber: TextView = itemView.findViewById(R.id.adPhoneNumber)
    private val adImgUrlView: ImageView = itemView.findViewById(R.id.adImgUrl)

    fun bindToAd(ad: AdData, starClickListener: View.OnClickListener){
        adPetName.text = ad.petName
        adOwnerName.text = ad.ownerName
        adPhoneNumber.text = ad.phoneNumber

        val imageUrl = if (ad.imageUrl.isNullOrEmpty()) {
            // If empty or null, use the default image URL
            DEFAULT_IMAGE_URL
        } else {
            // Otherwise, use the imageUrl from the AdData object
            ad.imageUrl
        }

        Picasso.get()
            .load(imageUrl)
            .into(adImgUrlView)
    }
}
private const val DEFAULT_IMAGE_URL = "https://i.stack.imgur.com/l60Hf.png"
