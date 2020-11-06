package com.e.yourcartoonis

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.Image
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView

class StickerView : AppCompatImageView {
    private var ScaleDetector : ScaleGestureDetector? = null
    private var ScaleFactor = 1.0f

    constructor(context:Context) : super(context)
    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet)

    init{
        this.ScaleDetector = ScaleGestureDetector(context,ScaleListener())
        scaleType = ScaleType.MATRIX
        setOnTouchListener(onTouchListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            Log.e("###","scale")
            ScaleFactor = ScaleFactor * detector!!.scaleFactor
            ScaleFactor = Math.max(0.1f,Math.min(ScaleFactor,10.0f))
            val scaledWidth = width*ScaleFactor
            val scaledHeight = height*ScaleFactor
            if(!(scaledHeight<1.5 || scaledWidth <1.5)) {
                val params = layoutParams
                params.width = scaledWidth.toInt()
                params.height = scaledHeight.toInt()
                Log.e("###", "width height ${scaledWidth} , ${scaledHeight}")
                layoutParams = params
                var mat = Matrix()
                mat.postScale(ScaleFactor,ScaleFactor)
                imageMatrix = mat
            }
            else
                ScaleFactor = 1.0f
            invalidate()
            return true
        }
    }
    private inner class onTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, e: MotionEvent?): Boolean {
            ScaleDetector!!.onTouchEvent(e)
            return true
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.save()
        Log.e("###","onDraw , scale : ${ScaleFactor}")
        canvas.scale(ScaleFactor,ScaleFactor)
        canvas.restore()
    }
}