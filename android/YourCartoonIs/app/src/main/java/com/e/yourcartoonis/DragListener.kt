package com.e.yourcartoonis

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView

class DragListener(c:Context) : View.OnDragListener {
    private val c : Context
    init{
        this.c = c
    }
    override fun onDrag(p0: View?, p1: DragEvent?): Boolean {
        //Log.e("###","dragstart")
        when(p1!!.action){
            DragEvent.ACTION_DRAG_STARTED ->{
                //Log.e("###","drag start")
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.e("###", "enter ${p0}")
            }
            DragEvent.ACTION_DROP -> {
                if(p0 is ScrollView){
                    val im = ImageView(c)
                    val path = p1.clipData.getItemAt(0).text
                    im.layoutParams=LinearLayout.LayoutParams(1000,1000)
                    im.setImageBitmap(Bitmap.createScaledBitmap(decodeTmpBitmap(path.toString()), 1000, 1000, true))
                    p0.addView(im)
                }
                Log.e("###", "drop ${p0}")
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.e("###", "exit")
            }
            DragEvent.ACTION_DRAG_LOCATION -> {

            }
            else -> Log.e("###","drag error")
        }
        return true
    }
    fun decodeTmpBitmap(path : String) : Bitmap{
        return BitmapFactory.decodeFile(path)
    }
}