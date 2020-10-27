package com.e.yourcartoonis

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView

class DragListener : View.OnDragListener {

    override fun onDrag(p0: View?, p1: DragEvent?): Boolean {
        Log.e("###","dragstart")
        when(p1!!.action){
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.e("###", "enter")
            }
            DragEvent.ACTION_DROP -> Log.e("###","drop")
            else -> Log.e("###","drag error")
        }
        return true
    }
}