package com.bignerdranch.android.beetrackingapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent

class BeeRecyclerViewAdapter(var bees: List<Bee>) :
    RecyclerView.Adapter<BeeRecyclerViewAdapter.ViewHolder>() {

    val baseImagePath: String = "gs://beespotter-2c9ea.appspot.com"
    private lateinit var context: Context
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val storageReference = Firebase.storage.reference
        var imageView: ImageView = view.findViewById(R.id.beeImageView)

        fun bind(bee: Bee) {
            view.findViewById<TextView>(R.id.date_spotted).text = "${bee.dateSpotted}"
            Log.d("ImagePath", "$baseImagePath + ${bee.imagePath}")
            Picasso.get()
                .load(baseImagePath + bee.imagePath)
                .placeholder(R.drawable.placeholder_image) // Add placeholder image resource
                .error(R.drawable.error_images) // Add error image resource
                .into(
                    imageView,
                    object : Callback {
                        override fun onSuccess() {
                            // Image loaded successfully
                        }

                        override fun onError(e: Exception?) {
                            // Log the error
                            Log.e("Picasso", "Error loading image: ${e?.message}")
                        }
                    },
                )
//            Glide.with()
//                .load(storageReference)
//                .into(imageView)
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
    }

    override fun getItemCount(): Int {
        return bees.size
    }
}
