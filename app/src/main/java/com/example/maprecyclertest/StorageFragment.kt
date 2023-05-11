package com.example.maprecyclertest

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.maprecyclertest.databinding.FragmentAdDetailsBinding
import com.example.maprecyclertest.databinding.FragmentStorageBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*


class StorageFragment : Fragment() {

    private lateinit var storage: FirebaseStorage
    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!
    private var imageUrl: String? = null


    companion object {
        private const val REQUEST_PERMISSIONS = 1
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchImagePicker()
            }
        }

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri: Uri? = result.data?.data
                binding.imageView6.setImageURI(selectedImageUri)
                binding.buttonSubmitImage2.setOnClickListener {
                    uploadImage(selectedImageUri!!)
                }
            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager().not())) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.MANAGE_EXTERNAL_STORAGE
//            ), REQUEST_CODE_PERMISSIONS)
//            Log.i("storagetest", "permission check")
//            loadImagesFromDCIM()
//        } else {
//            Log.i("storagetest", "permission passed")
//            loadImagesFromDCIM()
//        }
        //loadImagesFromDCIM()
        binding.buttonLoadImage.setOnClickListener{
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
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i("storagetest", "onresquest")
//                loadImagesFromDCIM()
//            }
//        }
//    }
    private fun loadImagesFromDCIM(){
        Log.i("storagetest", "access granted")
        storage = Firebase.storage
        val storageRef = storage.reference

        val pathReference = storageRef.child("/photos/")
        //val imageFile = File("/storage/emulated/0/Download/downloaad.jpeg")
        val stream = FileInputStream(File("/storage/emulated/0/Download/download.jpeg"))

        val uploadTask = pathReference.putStream(stream)
        Log.i("storagetest", pathReference.name)
        uploadTask.addOnSuccessListener {
            Log.i("storagetest", "success")
        }.addOnFailureListener{
            Log.i("storagetest", "failure")
        }
    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageLauncher.launch(intent)
    }
    private fun uploadImage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = UUID.randomUUID().toString()
        val fileRef = storageRef.child("imagess/$fileName")

        val uploadTask = fileRef.putFile(imageUri)

//        uploadTask.addOnSuccessListener {
//            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
//        }
//        val urlTask = uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            fileRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val downloadUri = task.result
//                binding.textViewImageLink.text = downloadUri.toString()
//            } else {
//                // Handle failures
//                // ...
//            }
//
//        }
        uploadTask.addOnSuccessListener { taskSnapshot ->
            val databaseRef = FirebaseDatabase.getInstance().reference.child("images")
            val imageId = databaseRef.push().key ?: ""

            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                imageUrl = uri.toString()

                val imageMap = hashMapOf<String, Any>(
                    "id" to imageId,
                    "url" to imageUrl!!
                )

                databaseRef.child(imageId).setValue(imageMap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Image uploaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Error uploading image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
        }
    }
}