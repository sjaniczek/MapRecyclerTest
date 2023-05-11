package com.example.maprecyclertest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Point
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.maprecyclertest.databinding.FragmentMapsBinding
import com.example.maprecyclertest.models.AdData
import com.example.maprecyclertest.models.AdViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.net.URI.create
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: AdViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pinView: ImageView

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            // Permission denied, show a message or do something else
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)


//    private val callback = OnMapReadyCallback { googleMap ->
//
////        val centerLatLng = googleMap.cameraPosition.target
////        val centerScreenPoint = googleMap.projection.toScreenLocation(centerLatLng)
////        pinView.translationX = (centerScreenPoint.x - (pinView.width / 2)).toFloat()
////        pinView.translationY = (centerScreenPoint.y - (pinView.height / 2)).toFloat()
//        googleMap.setOnCameraMoveListener {
//            val centerPoint = Point(mapView.width / 2, mapView.height / 2)
//            val centerLatLng = googleMap.projection.fromScreenLocation(centerPoint)
//            marker?.position = centerLatLng
//        }
//        googleMap.uiSettings.isZoomControlsEnabled = true
//        googleMap.uiSettings.setAllGesturesEnabled(true)
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0F))
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
//            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
//            override fun isCancellationRequested() = false
//        }).addOnSuccessListener { location : Location? ->
//                Log.i("userloc",location.toString())
//                if (location == null)
//                    Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
//                else {
//                    val lat = location.latitude
//                    val lon = location.longitude
//                    val latLng = LatLng(lat,lon)
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(6F))
//                }
//            }
//        binding.btn.setOnClickListener { val projection = googleMap.projection
//            val centerLatLng = googleMap.cameraPosition.target
//            val middleLocation = projection.toScreenLocation(centerLatLng)
//            val middleLocation2 = projection.fromScreenLocation(middleLocation)
//            Log.d("mapTest",centerLatLng.toString())
//            Log.d("mapTest",middleLocation2.toString()) }
//
////        fusedLocationClient.lastLocation.addOnFailureListener{
////            Log.e("userloc","Didnt find last location")
////        }
////        googleMap.setOnCameraIdleListener {
////            val target = googleMap.cameraPosition.target
////            val addresses: List<Address>
////            val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
////
////            addresses = geocoder.getFromLocation(
////                target.latitude,
////                target.longitude,
////                1
////            )
////            Log.i("mapt", target.toString())
////
////           // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
////
////            binding.btn.setOnClickListener{
////                if (!addresses.isNullOrEmpty()) {
////                    val address = addresses[0].getAddressLine(0)
////                    Log.i("mapt", address)
////                    //findNavController().navigate(MapsFragmentDirections.action_mapsFragment_to_addAdFragment)
////                }
////                else {
////                    Toast.makeText(context, "Wybierz inną lokację!", Toast.LENGTH_SHORT).show()
////                }
////            }
////
////        }
//
//    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        //pinView = binding.imageView
        //val adData = viewModel.adData


        val adData = viewModel.adData
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_layout) as SupportMapFragment
        mapFragment.getMapAsync(this)

//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            mapFragment?.getMapAsync(callback)
//        }


    }

//    private fun saveLocation(location: LatLng) {
//        val currentAdData = viewModel.adData ?: AdData(
//            "", "", "", "",
//            "", "", "", "", null
//        )
//        val updatedAdData = currentAdData.copy(
//            locationData = AdData.LocationData(
//                location.latitude,
//                location.longitude
//            )
//        )
//        viewModel.saveFormData(updatedAdData)
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(true)
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0F))

        getCurrentLocation()

        binding.btn.setOnClickListener{
                    val currentLocation = googleMap.cameraPosition.target
                    decodeLocation(currentLocation)
               }
    }

private  fun decodeLocation(location: LatLng){
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val decodedAddress = addresses[0].getAddressLine(0)
            viewModel.adData?.decodedAddress = decodedAddress
            viewModel.adData?.locationData = AdData.LocationData(location.latitude, location.longitude)
            findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToAddAdFragment())
        } else {
            Toast.makeText(context, "Wybierz inną lokację!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        Toast.makeText(context, "Błąd w pobieraniu adresu. Spróbuj ponownie później lub wpisz adres ręcznie.", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }

// update the ad data with the decoded address

}
    private fun getCurrentLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Permission already granted, do something here

        }
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }).addOnSuccessListener { location: Location? ->
            Log.i("userloc", location.toString())
            if (location == null)
                Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
            else {
                val lat = location.latitude
                val lon = location.longitude
                val latLng = LatLng(lat, lon)
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(6F))
            }
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

