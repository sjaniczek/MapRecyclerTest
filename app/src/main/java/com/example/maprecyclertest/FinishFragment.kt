package com.example.maprecyclertest

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.maprecyclertest.databinding.FragmentFinishBinding
import java.util.*


class FinishFragment : Fragment() {

    private var _binding: FragmentFinishBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        val view = binding.root
        val bundle = arguments
        if (bundle == null) {
            Log.e("Confirmation", "ConfirmationFragment did not receive traveler information")
            return view
        }
//        val args: FinishFragmentArgs by navArgs()
//
//
//        binding.addressLatLang.text = ("Latitude "+args.addLatLng.latitude.toString()+"\nLongitude "+args.addLatLng.longitude.toString())
//        binding.addressStandard.text = args.realAddress



        return view
    }


}