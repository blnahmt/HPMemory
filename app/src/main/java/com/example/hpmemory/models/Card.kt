package com.example.hpmemory.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


data class Card(
    val name:String?,
                 val home:String?,
                 val point: Double?,
                 val image: Bitmap?)