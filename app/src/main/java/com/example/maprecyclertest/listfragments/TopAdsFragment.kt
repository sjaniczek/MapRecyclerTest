package com.example.maprecyclertest.listfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.maprecyclertest.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class TopAdsFragment : AdsListFragment() {

    override fun getQuery(databaseReference: DatabaseReference): Query {
        // All my posts
        return databaseReference.child("test-users")
            .limitToFirst(100)
    }
}