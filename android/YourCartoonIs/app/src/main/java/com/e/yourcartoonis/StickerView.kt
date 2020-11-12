package com.e.yourcartoonis

import android.app.ActionBar
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.Image
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.e.yourcartoonis.MotionClass.MoveGestureDetector
import com.e.yourcartoonis.MotionClass.RotateGestureDetector

class StickerView : AppCompatImageView {
    private var ScaleDetector : ScaleGestureDetector? = null
    private var ScaleFactor = 1.0f
    private var MoveDetector : MoveGestureDetector? = null
    private var MoveXdelta = 0.0f
    private var MoveYdelta = 0.0f
    private var RotateDetector : RotateGestureDetector? = null
    private var RotationDelta = 0.0f

    constructor(context:Context) : super(context)
    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet)

    init{
        this.ScaleDetector = ScaleGestureDetector(context,ScaleListener())
        this.RotateDetector = RotateGestureDetector(context,RotateListener())
        this.MoveDetector = MoveGestureDetector(context,MoveListener())
        scaleType = ScaleType.MATRIX
        setOnTouchListener(onTouchListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            Log.e("###","scale")
            ScaleFactor = ScaleFactor * detector!!.scaleFactor
            ScaleFactor = Math.max(0.5f,Math.min(ScaleFactor,10.0f))
            val scaledWidth = width*ScaleFactor
            val scaledHeight = height*ScaleFactor
            if(scaledHeight >= 100 && scaledWidth >= 100) {
                layoutParams.width = scaledWidth.toInt()
                layoutParams.height = scaledHeight.toInt()
                Log.e("###", "width height ${scaledWidth} , ${scaledHeight}")
                val mat = Matrix()
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
        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            ScaleDetector!!.onTouchEvent(motionEvent)
            RotateDetector!!.onTouchEvent(motionEvent)
            MoveDetector!!.onTouchEvent(motionEvent)
            return true
        }
    }
    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener(){
        override fun onMove(detector: MoveGestureDetector?): Boolean {
            Log.e("###","onMove")
            val delta = detector!!.focusDelta
            MoveXdelta = delta.x
            MoveYdelta = delta.y
            x = x + MoveXdelta
            y = y + MoveYdelta
            invalidate()
            return true
        }
    }
    private inner class RotateListener : RotateGestureDetector.SimpleOnRotateGestureListener(){
        override fun onRotate(detector: RotateGestureDetector?): Boolean {
            Log.e("###","onRotate")
            RotationDelta = detector!!.rotationDegreesDelta
            rotation = RotationDelta
            invalidate()
            return true
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.save()
        Log.e("###","onDraw , scale : ${ScaleFactor}, delta : ${MoveXdelta},${MoveYdelta} , Rotate : ${RotationDelta}")
        canvas.scale(ScaleFactor,ScaleFactor)
        canvas.restore()
    }
}