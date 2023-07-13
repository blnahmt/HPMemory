package com.example.hpmemory.extensions

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*

object Extensions {
    fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun String.decodePicString(): Bitmap {

        val imageBytes = Base64.getDecoder().decode(this)

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}