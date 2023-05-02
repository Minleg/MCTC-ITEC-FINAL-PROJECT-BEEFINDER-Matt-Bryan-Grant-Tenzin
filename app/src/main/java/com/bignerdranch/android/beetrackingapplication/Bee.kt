package com.bignerdranch.android.beetrackingapplication

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Bee(
    val imagePath: String? = null,
    val location: GeoPoint? = null,
    val dateSpotted: Date? = null,
    @get:Exclude @set:Exclude
    var documentReference: DocumentReference? = null,
)