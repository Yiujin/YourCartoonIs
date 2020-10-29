package com.e.yourcartoonis

import android.view.DragEvent
import android.view.View
import android.widget.ImageView

class DragOnTarget(im: ImageView) : View.OnDragListener {
    private val im : ImageView
    init{
        this.im = im
    }
    override fun onDrag(p0: View?, p1: DragEvent?): Boolean {
        when(p1!!.action){
            DragEvent.ACTION_DRAG_LOCATION -> {
                im.x = p1!!.x
                im.y = p1!!.y
            }
        }
        return true
    }
}