package com.e.yourcartoonis

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.florent37.shapeofview.shapes.DiagonalView

class MyDiagonal : DiagonalView {
    constructor(c:Context) : super(c)
    constructor(c:Context,attr:AttributeSet) : super(c,attr)
    constructor(c:Context,attr:AttributeSet,def:Int):super(c,attr,def)
    private var Intercept : Boolean = false
    fun setIntercept(off:Boolean){
        Intercept = off
    }
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if(Intercept)
            return true
        else
            return false
    }
}