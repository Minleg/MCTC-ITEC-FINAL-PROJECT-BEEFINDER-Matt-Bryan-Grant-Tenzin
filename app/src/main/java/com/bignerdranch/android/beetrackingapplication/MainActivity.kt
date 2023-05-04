package com.bignerdranch.android.beetrackingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {

    val CURRENT_FRAGMENT_BUNDLE_KEY = "current fragment bundle key"
    var currentFragmentTag = "MAP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentFragmentTag = savedInstanceState?.getString(CURRENT_FRAGMENT_BUNDLE_KEY) ?: "MAP"
        showFragment(currentFragmentTag)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.show_plus -> { showFragment("CAMERA"); true}
                R.id.show_map -> { showFragment("MAP"); true}
                R.id.show_list -> { showFragment("LIST"); true}
                else -> false
            }
        }
    }

    private fun showFragment(tag: String) {

        currentFragmentTag = tag

        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val transaction = supportFragmentManager.beginTransaction()
            when (tag) {
                "CAMERA" -> transaction.replace(R.id.fragmentContainerView, CameraFragment.newInstance(), "CAMERA")
                "MAP" -> transaction.replace(R.id.fragmentContainerView, BeeMapFragment.newInstance(), "MAP")
                "LIST" -> transaction.replace(R.id.fragmentContainerView, BeeListFragment.newInstance(), "LIST")
            }
            transaction.commit()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_BUNDLE_KEY, currentFragmentTag)
    }
}