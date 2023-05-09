package com.bignerdranch.android.beetrackingapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [BeeListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BeeListFragment : Fragment() {

    private val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val recyclerView = inflater.inflate(R.layout.fragment_bee_list, container, false)
        //val recyclerViewItem = recyclerView.findViewById<RecyclerView>(R.id.bee_list)
        //val beeImageView = recyclerViewItem.findViewById<ImageView>(R.id.beeImageView)
        if (recyclerView !is RecyclerView) {
            throw java.lang.RuntimeException("BeeListFragment view should be a recycler view")
        }

        val bees = listOf<Bee>()
        val adapter = BeeRecyclerViewAdapter(bees)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        beeViewModel.latestBees.observe(requireActivity()) { beeList ->
            adapter.bees = beeList
            adapter.notifyDataSetChanged()

//            // Assuming you have an image URL stored in each Bee object
//            beeList.forEach { bee ->
//                Picasso.get().load(bee.imagePath).into(beeImageView)
//            }
        }
        return recyclerView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BeeListFragment()
    }
}
