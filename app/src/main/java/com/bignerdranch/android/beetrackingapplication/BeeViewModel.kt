package com.bignerdranch.android.beetrackingapplication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "BEE_VIEW_MODEL"

class BeeViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val beeCollectionReference = db.collection("bees")

    val latestBees = MutableLiveData<List<Bee>>()

    // gets all bees sightings
    private val latestBeeListener = beeCollectionReference
        .orderBy("dateSpotted", Query.Direction.DESCENDING)
        .limit(10)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error fetching latest bees", error)
            } else if (snapshot != null) {
                val bees = mutableListOf<Bee>()
                for (beeDocument in snapshot) {
                    val bee = beeDocument.toObject(Bee::class.java)
                    bee.documentReference = beeDocument.reference
                    bees.add(bee)
                }
                Log.d(TAG, "Bees from firebase: $bees")
                latestBees.postValue(bees) // updates this mutable live data as anything changes on firebase db
            }
        }

    fun addBee(bee: Bee) {
        beeCollectionReference.add(bee)
            .addOnSuccessListener { beeDocumentReference ->
                Log.d(TAG, "New bee added at ${beeDocumentReference.path}")
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error adding bee $bee", error)
            }
    }

    fun deleteBee(bee: Bee) {
        bee.documentReference?.delete()
    }
}
