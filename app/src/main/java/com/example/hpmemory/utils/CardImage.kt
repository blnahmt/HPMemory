package com.example.hpmemory

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class AspectRatioImageView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    // Set the desired aspect ratio (width/height)
    private val aspectRatio = 0.8

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, (width / aspectRatio).toInt())
    }
}