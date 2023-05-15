package com.example.maprecyclertest.listfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maprecyclertest.AdDetailsFragment
import com.example.maprecyclertest.R
import com.example.maprecyclertest.models.AdData
import com.example.maprecyclertest.viewholder.AdViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

abstract class AdsListFragment : Fragment() {

    private lateinit var database: DatabaseReference
    // [END define_database_reference]

    private lateinit var recycler: RecyclerView
    private lateinit var manager: LinearLayoutManager
    private var adapter: FirebaseRecyclerAdapter<AdData, AdViewHolder>? = null

    val uid: String
        get() = Firebase.auth.currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_all_ads, container, false)

        // [START create_database_reference]
        database = Firebase.database.reference
        // [END create_database_reference]

        recycler = rootView.findViewById(R.id.recyclerViewAd)
        recycler.setHasFixedSize(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manager = LinearLayoutManager(activity)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        recycler.layoutManager = manager


        val postsQuery = getQuery(database)

        val options = FirebaseRecyclerOptions.Builder<AdData>()
            .setQuery(postsQuery, AdData::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<AdData, AdViewHolder>(options) {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AdViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return AdViewHolder(inflater.inflate(R.layout.ad_list_item, viewGroup, false))
            }

            override fun onBindViewHolder(viewHolder: AdViewHolder, position: Int, model: AdData) {
                val postRef = getRef(position)

                // Set click listener for the whole post view
                val postKey = postRef.key
                viewHolder.itemView.setOnClickListener {
                    // Launch PostDetailFragment
                    val navController = requireActivity().findNavController(R.id.nav_host_fragment)
                    val args = bundleOf(AdDetailsFragment.EXTRA_POST_KEY to postKey)
                    Toast.makeText(context, postKey, Toast.LENGTH_SHORT).show()
                    //Toast.makeText(context,args.toString(),Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_mainFragment_to_adDetailsFragment, args)
                }

                // Determine if the current user has liked this post and set UI accordingly
                // viewHolder.setLikedState(model.stars.containsKey(uid))

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToAd(model) {
                    // Need to write to both places the post is stored
                    //val globalPostRef = database.child("posts").child(postRef.key!!)
                    //val userPostRef = database.child("user-posts").child(model.uid!!).child(postRef.key!!)

                    // Run two transactions
                    // onStarClicked(globalPostRef)
                    //onStarClicked(userPostRef)
                }
            }
            }

        recycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
    abstract fun getQuery(databaseReference: DatabaseReference): Query

}
