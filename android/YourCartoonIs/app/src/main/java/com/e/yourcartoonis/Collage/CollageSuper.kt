package com.e.yourcartoonis.Collage

import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.e.yourcartoonis.MakeCollage

open class CollageSuper : Fragment() {
    open fun setScrollTouch(){

    }
    open fun goLayout(){}
    open fun stickerDone(id : Int){}
    protected fun DptoFloat(dp: Float) : Float{
        val metrics = resources.displayMetrics
        val px : Float = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        return px
    }
    protected class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean {
            return true
        }
        override fun onDoubleTapEvent(event: MotionEvent): Boolean {
            Log.d("###", "onDoubleTapEvent: $event")
            return true
        }
        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return true
        }
    }
}