package com.example.maprecyclertest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.maprecyclertest.databinding.FragmentAdDetailsBinding
import com.example.maprecyclertest.models.AdData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException

class AdDetailsFragment : Fragment() {

    private lateinit var postKey: String
    private lateinit var postReference: DatabaseReference
    private lateinit var commentsReference: DatabaseReference

    private var postListener: ValueEventListener? = null
    //private var adapter: AdAdapter? = null
    private var _binding: FragmentAdDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get post key from arguments
        postKey = requireArguments().getString(EXTRA_POST_KEY)
            ?: throw IllegalArgumentException("Must pass EXTRA_POST_KEY")

        // Initialize Database
        postReference = Firebase.database.reference
            .child("test").child(postKey)
        //commentsReference = Firebase.database.reference
            //.child("post-comments").child(postKey)

        // Initialize Views
        //with(binding) {
        //    buttonPostComment.setOnClickListener { postComment() }
        //    recyclerPostComments.layoutManager = LinearLayoutManager(context)
        }
    override fun onStart() {
        super.onStart()

        // Add value event listener to the post
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val ad = dataSnapshot.getValue<AdData>()
                ad?.let {
                    binding.textViewPetName.text = it.petName
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                Toast.makeText(context, "Failed to load post.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        postReference.addValueEventListener(postListener)

        // Keep copy of post listener so we can remove it when app stops
        this.postListener = postListener

        // Listen for comments
        //adapter = CommentAdapter(requireContext(), commentsReference)
        //binding.recyclerPostComments.adapter = adapter
    }

    override fun onStop() {
        super.onStop()

        // Remove post value event listener
        postListener?.let {
            postReference.removeEventListener(it)
        }

        // Clean up comments listener
        //adapter?.cleanupListener()
    }
    companion object {
        private const val TAG = "PostDetailFragment"
        const val EXTRA_POST_KEY = "post_key"
    }

}