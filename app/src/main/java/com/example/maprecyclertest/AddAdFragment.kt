package com.example.maprecyclertest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.maprecyclertest.databinding.FragmentAddAdBinding
import com.example.maprecyclertest.models.AdData
import com.example.maprecyclertest.models.AdViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class AddAdFragment : Fragment() {

    private lateinit var storage: FirebaseStorage
    private lateinit var dbRef: DatabaseReference
    private val viewModel: AdViewModel by activityViewModels()
    private var _binding: FragmentAddAdBinding? = null
    private val binding get() = _binding!!

    private var imageUrl: String? = null
    private var imageUri: Uri? = null
    private var savedState: Bundle? = null



    companion object {
        private const val REQUEST_PERMISSIONS = 1
        private const val TAG = "dbTest"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchImagePicker()
            }
        }

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                imageUrl = imageUri.toString()
                binding.imagePlaceholder.setImageURI(imageUri)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAdBinding.inflate(inflater, container, false)
        //binding.editTextTextAddress.setText(args.decodedLocation)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLoadImage2.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } else {
                launchImagePicker()
            }
        }

        binding.buttonGoToMap.setOnClickListener {
            saveFormData()
            findNavController().navigate(AddAdFragmentDirections.actionAddAdFragmentToMapsFragment())
        }
        dbRef = FirebaseDatabase.getInstance().getReference("test")

        binding.buttonAcceptAd.setOnClickListener {
            uploadImageAndForm()
            }
        val adData = viewModel.adData
        if (adData != null) {
            binding.editTextPetName.setText(adData.petName)
            binding.editTextOwnerName.setText(adData.ownerName)
            binding.editTextTextAddress.setText(adData.decodedAddress)
            binding.radioGroupBehaviour.findViewWithTag<RadioButton>(adData.petBehavior)?.isChecked = true//TODO DOESNT WORK
            binding.imagePlaceholder.setImageURI(adData.imageUri)
        }

    }
//    private fun decodeLocation(latLng: AdData.LocationData?): String? {
//
//        val addresses: List<Address>
//        val geocoder = Geocoder(context, Locale.getDefault())
//
//            addresses = geocoder.getFromLocation(
//                latLng?.latitude ?: 1.0,
//                latLng?.longitude ?: 1.0,
//                1
//            )
//           // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//        return if (!addresses.isNullOrEmpty()) {
//            addresses[0].getAddressLine(0)
//        } else {
//            Toast.makeText(context, "Wybierz inną lokację!", Toast.LENGTH_SHORT).show()
//            "Wrong location"
//        }
//
//    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageLauncher.launch(intent)
    }
    private fun uploadImageAndForm() {
        val storageRef = FirebaseStorage.getInstance().reference
        val databaseRef = FirebaseDatabase.getInstance().reference.child("test-users")
        val fileName = UUID.randomUUID().toString()
        val petId = databaseRef.push().key!!
        val fileRef = storageRef.child("images/$fileName")
        val imageUri = imageUri
//            Log.d("TAG", "Image URI is null")
//            return
//        }
        if (binding.editTextPetName.text.isNullOrEmpty() ||
            binding.editTextTextLostDate.text.isNullOrEmpty() ||
            binding.editTextOwnerName.text.isNullOrEmpty() ||
            binding.editTextTextPhoneNumber.text.isNullOrEmpty() ||
            binding.editTextTextAddress.text.isNullOrEmpty() ||
            imageUri == null
        ) {
            Toast.makeText(context, "Please fill all fields and add an image", Toast.LENGTH_SHORT).show()
            return
        }

        val petName = binding.editTextPetName.text.toString()
        val lostDate = binding.editTextTextLostDate.text.toString()
        val ownerName = binding.editTextOwnerName.text.toString()
        val phoneNumber = binding.editTextTextPhoneNumber.text.toString()
        val decodedAddress = binding.editTextTextAddress.text.toString()
        //val selectedRadioButtonText = binding.radioGroupBehaviour.findViewById<RadioButton>(binding.radioGroupBehaviour.checkedRadioButtonId).text.toString()
        //val locationData = viewModel.adData?.locationData
        val uploadTask = fileRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                imageUrl = uri.toString()

//                }
                val petAd = AdData(petId,petName,lostDate,ownerName,phoneNumber,decodedAddress,"",imageUrl, uri)//,selectedRadioButtonText,imageUrl,imageUri,locationData)

                databaseRef.child(petId).setValue(petAd.toMap())
                    .addOnSuccessListener {
                        // Form uploaded successfully
                        Toast.makeText(context, "Form submitted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Form upload failed
                        Log.e(TAG, "Error uploading form: ${e.message}", e)
                        Toast.makeText(context, "Error submitting form", Toast.LENGTH_SHORT).show()
                    }
            }
        }
            .addOnFailureListener { e ->
                // Image upload failed
                Log.e(TAG, "Error uploading image: ${e.message}", e)
                Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveFormData(){
       val adData = AdData(
           petName = binding.editTextPetName.text.toString(),
           ownerName = binding.editTextOwnerName.text.toString(),
           lostDate = binding.editTextTextLostDate.text.toString(),
           petBehavior = binding.radioGroupBehaviour.findViewById<RadioButton>(binding.radioGroupBehaviour.checkedRadioButtonId)?.text.toString(),
           imageUri = if (imageUri != null) imageUri else viewModel.adData?.imageUri
       )

       viewModel.saveFormData(adData)

//        val adData = getAdDataFromUI()
//        viewModel.saveFormData(adData)
    }
//    private fun getAdDataFromUI(): AdData {
//        val petName = binding.editTextPetName.text.toString()
//        return AdData(petName)
//    }



}