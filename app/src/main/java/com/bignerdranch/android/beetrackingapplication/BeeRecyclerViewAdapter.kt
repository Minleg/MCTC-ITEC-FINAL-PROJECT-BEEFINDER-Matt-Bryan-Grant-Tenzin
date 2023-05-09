package com.bignerdranch.android.beetrackingapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BeeRecyclerViewAdapter(var bees: List<Bee>) :
    RecyclerView.Adapter<BeeRecyclerViewAdapter.ViewHolder>() {

    val baseImagePath: String = "https://firebasestorage.googleapis.com/v0/b/beespotter-2c9ea.appspot.com/o/"
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.beeImageView)
        fun bind(bee: Bee) {
            view.findViewById<TextView>(R.id.date_spotted).text = "${bee.dateSpotted}"
            loadImage(bee.imagePath!!, imageView)
//            Picasso.get()
//                .load(baseImagePath + bee.imagePath)
//                .placeholder(R.drawable.placeholder_image) // Add placeholder image resource
//                .error(R.drawable.error_images) // Add error image resource
//                .into(
//                    imageView,
//                    object : Callback {
//                        override fun onSuccess() {
//                            // Image loaded successfully
//                        }
//
//                        override fun onError(e: Exception?) {
//                            // Log the error
//                            Log.e("Picasso", "Error loading image: ${e?.message}")
//                        }
//                    },
//                )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_bee_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeeRecyclerViewAdapter.ViewHolder, position: Int) {
        val bee = bees[position]
        holder.bind(bee)

//        // Load the image using Picasso
//        val imagePath = bee.imagePath
//        if (imagePath != null) {
//            val imageUrl = baseImagePath + imagePath
//            val imageView = holder.itemView.findViewById<ImageView>(R.id.beeImageView)
//
//            Picasso.get()
//                .load(imageUrl)
//                .into(imageView)
//        }
    }

    override fun getItemCount(): Int {
        return bees.size
    }

    fun loadImage(imagePath: String, imageView: ImageView) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child(imagePath)

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            // Handle the error
            Log.e("Adapter", "Error loading image: ${exception.message}")
        }
    }
}
