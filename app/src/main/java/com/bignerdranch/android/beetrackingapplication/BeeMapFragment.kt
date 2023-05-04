package com.bignerdranch.android.beetrackingapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import java.util.Date

private const val TAG = "BEE_MAP_FRAGMENT"

class BeeMapFragment : Fragment() {

    private lateinit var addBeeButton: FloatingActionButton

    private var locationPermissionGranted = false

    private var movedMapToUserLocation = false

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var map: GoogleMap? = null

    private val beeMarkers = mutableListOf<Marker>()

    private var beeList = listOf<Bee>()

    private val beeViewModel: BeeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BeeViewModel::class.java)
    }

    private val mapReadyCallback = OnMapReadyCallback { googleMap ->
        Log.d(TAG, "Google map ready")
        map = googleMap

        googleMap.setOnInfoWindowClickListener { marker ->
            val beeForMarker = marker.tag as Bee
            requestDeleteBee(beeForMarker)
        }
        updateMap()
    }

    private fun requestDeleteBee(bee: Bee) {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete_bee))
            .setPositiveButton(android.R.string.ok) { dialog, id ->
                beeViewModel.deleteBee(bee)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, id ->
                // do nothing
            }
            .create()
            .show()
    }

    private fun updateMap() {

        drawBees()

        if (locationPermissionGranted) {
            if (!movedMapToUserLocation) {
                moveMapToUserLocation()
            }
        }
    }

    private fun setAddBeeButtonEnabled(isEnabled: Boolean) {
        addBeeButton.isClickable = isEnabled
        addBeeButton.isEnabled = isEnabled

        if (isEnabled) {
            addBeeButton.backgroundTintList = AppCompatResources.getColorStateList(requireActivity(),
                android.R.color.holo_blue_light)
        } else {
            addBeeButton.backgroundTintList = AppCompatResources.getColorStateList(requireActivity(),
                android.R.color.darker_gray)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationPermissionGranted = true
            Log.d(TAG, "permission already granted")
            updateMap()
            setAddBeeButtonEnabled(true)
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        } else {
            val requestLocationPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    Log.d(TAG, "User granted permission")
                    setAddBeeButtonEnabled(true)
                    locationPermissionGranted = true
                    fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
                } else {
                    Log.d(TAG, "User did not grant permission")
                    setAddBeeButtonEnabled(false)
                    locationPermissionGranted = false
                    showSnackbar(getString(R.string.give_permission))
                }

                updateMap()
            }

            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveMapToUserLocation() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
            map?.uiSettings?.isZoomControlsEnabled = true

            fusedLocationProvider?.lastLocation?.addOnCompleteListener {getLocationTask ->
                val location = getLocationTask.result
                if (location != null) {
                    Log.d(TAG, "User's location ${location}")
                    val center = LatLng(location.latitude, location.longitude)
                    val zoomLevel = 12f
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))
                    movedMapToUserLocation = true
                } else {
                    showSnackbar(getString(R.string.no_location))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainView = inflater.inflate(R.layout.fragment_bee_map, container, false)

        addBeeButton = mainView.findViewById(R.id.add_bee)
        addBeeButton.setOnClickListener {
            addBeeAtLocation()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(mapReadyCallback)

        setAddBeeButtonEnabled(false)

        requestLocationPermission()

        beeViewModel.latestBees.observe(requireActivity()) { latestBees ->
            beeList = latestBees
            drawBees()
        }

        return mainView
    }

    @SuppressLint("MissingPermission")
    private fun addBeeAtLocation() {
        if (map == null) { return }
        if (fusedLocationProvider == null) { return }
        if (!locationPermissionGranted) {
            showSnackbar(getString(R.string.grant_location_permission))
            return
        }

        fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { locationRequestTask ->
            val location = locationRequestTask.result
            if (location != null) {
                val bee = Bee(
                    dateSpotted = Date(),
                    location = GeoPoint(location.latitude, location.longitude)
                )
                beeViewModel.addBee(bee)
                moveMapToUserLocation()
                showSnackbar(getString(R.string.added_bee))
            } else {
                showSnackbar(getString(R.string.no_location))
            }
        }
    }

    private fun drawBees() {
        if (map == null) { return }

        for (marker in beeMarkers) {
            marker.remove()
        }

        for (bee in beeList) {
            bee.location?.let { geoPoint ->

                val iconId = R.drawable.bee_small

                val markerOptions = MarkerOptions()
                    .position(LatLng(geoPoint.latitude, geoPoint.longitude))
                    .title(bee.imagePath)
                    .snippet("Spotted on ${bee.dateSpotted}")
                    .icon(BitmapDescriptorFactory.fromResource(iconId))

                map?.addMarker(markerOptions)?.also { marker ->
                    beeMarkers.add(marker)
                    marker.tag = bee
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BeeMapFragment()
    }
}