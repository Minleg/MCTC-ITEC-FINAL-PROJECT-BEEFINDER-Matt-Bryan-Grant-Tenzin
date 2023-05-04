package com.bignerdranch.android.beetrackingapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BeeRecyclerViewAdapter(var bees: List<Bee>) : RecyclerView.Adapter<BeeRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(bee: Bee) {
            view.findViewById<TextView>(R.id.date_spotted).text = "${bee.dateSpotted}"
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
