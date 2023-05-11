package com.example.maprecyclertest.listfragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class MyAdsFragment : AdsListFragment() {

    override fun getQuery(databaseReference: DatabaseReference): Query {
        // All my posts
        return databaseReference.child("test")
            .limitToFirst(100)
    }
}