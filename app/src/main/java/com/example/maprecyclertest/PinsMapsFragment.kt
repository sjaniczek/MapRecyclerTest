package com.example.maprecyclertest

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.maprecyclertest.databinding.FragmentMapsBinding
import com.example.maprecyclertest.databinding.FragmentPinsMapsBinding
import com.example.maprecyclertest.listfragments.AdsListFragment
import com.example.maprecyclertest.models.AdData

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class PinsMapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter  {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var dbRef: DatabaseReference
    private var _binding: FragmentPinsMapsBinding? = null
    private val binding get() = _binding!!

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(true)

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0F))

        val dbRef = FirebaseDatabase.getInstance().reference.child("test-users")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val adData = dataSnapshot.getValue(AdData::class.java)
                    val locationData = adData?.locationData
                    if (locationData != null) {
                        val latitude = locationData.latitude
                        val longitude = locationData.longitude
                        val markerOptions = MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .title(adData.petName)
                            .snippet(adData.lostDate)


                        val marker = googleMap.addMarker(markerOptions)
                        marker?.tag = adData
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        googleMap.setInfoWindowAdapter(this)
        googleMap.setOnInfoWindowClickListener { marker ->
            // Retrieve adData from marker's tag
            val adData = marker.tag as AdData?
            // Perform any necessary actions or navigation here
            // Example: Navigating to AdDetailsFragment with adData
            Log.d("datatest","onMapReadyclick")

            Log.d("datatest","onMapReadyclickBUTTON")

            adData?.let {
                val args = bundleOf(AdDetailsFragment.EXTRA_POST_KEY to it.adId)
                findNavController().navigate(R.id.action_mainFragment_to_adDetailsFragment, args)
            }
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPinsMapsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fetchLocationDataFromDatabase()
    }

    private fun fetchLocationDataFromDatabase() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("test-users")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val locationDataList = mutableListOf<AdData.LocationData>()

                for (snapshot in dataSnapshot.children) {
                    val adData = snapshot.getValue(AdData::class.java)
                    adData?.locationData?.let { locationData ->
                        locationDataList.add(locationData)

                    }
                }
                Log.d("loctest", locationDataList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Error fetching location data: ${error.message}")
            }
        })
    }


    override fun getInfoContents(marker: Marker): View? {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.custom_info_window, null)
        Log.d("datatest","getinfocontents")

        // Retrieve adData from marker's tag
        val adData = marker.tag as AdData?
        val petNameTextView = view.findViewById<TextView>(R.id.petNameTextView)
        val lostDateTextView = view.findViewById<TextView>(R.id.lostDateTextView)
        petNameTextView.text = adData?.petName
        lostDateTextView.text = adData?.lostDate

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
       return null
    }

}

