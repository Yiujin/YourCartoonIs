package com.e.yourcartoonis.Collage

import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.e.yourcartoonis.DragListener
import com.e.yourcartoonis.DragListenerSticker
import com.e.yourcartoonis.MakeCollage
import com.e.yourcartoonis.MyDiagonal

open class CollageSuper : Fragment() {
    protected var frameList : ArrayList<MyDiagonal>? =null
    protected var startXY = Array<Float>(2){1f;2f}
    protected var len : Int = 0
    fun goSticker(){
        val view = frameList!![0].parent
        Log.e("###","x,y : ${frameList!![0].x} , ${frameList!![0].y}")
        len = frameList!!.size -1
        for (i in 0..len) {
            frameList!![i].setIntercept(true)
            frameList!![i].setOnTouchListener { view, motionEvent ->
                Log.e("###","click")
                for(j in 0..2){
                    if(i==j)
                        continue
                    frameList!![j].visibility = View.INVISIBLE
                }
                frameList!![i].setOnDragListener(null)
                frameList!![i].setOnDragListener(DragListenerSticker(context!!))
                (activity as MakeCollage).startSticker(i)
                startXY[0] = frameList!![i].x
                startXY[1] = frameList!![i].y
                frameList!![i].animate().scaleX(1.5f).scaleY(1.5f).translationX(view.width/2-frameList!![i].x).translationY(view.height/2-frameList!![i].y).setDuration(100).start()
                frameList!![i].setIntercept(false)
                true
            }
        }
    }
    fun goLayout(){
        for(i in 0..len){
            frameList!![i].setOnTouchListener(null)
            frameList!![i].setIntercept(false)
        }
    }
    fun stickerDone(id : Int){
        frameList!![id].setOnDragListener(DragListener(context!!))
        for(i in 0..2){
            if(i==id) {
                frameList!![i].setIntercept(true)
                frameList!![id].animate().scaleX(1f).scaleY(1f).x(startXY[0]).y(startXY[1]).setDuration(100).start()
            }
            frameList!![i].visibility = View.VISIBLE
        }
    }
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