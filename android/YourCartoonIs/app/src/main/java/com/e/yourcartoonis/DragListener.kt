package com.e.yourcartoonis

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.github.chrisbanes.photoview.PhotoView

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
                    val im = PhotoView(c)
                    val path = p1.clipData.getItemAt(0).text
                    im.layoutParams=LinearLayout.LayoutParams(1000,1000)
                    im.setImageBitmap(Bitmap.createScaledBitmap(decodeTmpBitmap(path.toString()), 1000, 1000, true))
                    p0.addView(im)
                }
                /*
                if(p0 is FrameLayout){
                    val assestManager = c.assets
                    val path = p1.clipData.getItemAt(0).text
                    Log.e("###","path : ${path.toString()}")
                    val inputstream = assestManager.open("TomAndJerry/${path.toString()}")
                    val im = ImageView(c)
                    im.x = p1.x
                    im.y = p1.y
                    im.layoutParams=LinearLayout.LayoutParams(200,200)
                    im.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputstream), 200,200, true))
                    var outcheck = false
                    im.setOnTouchListener { view, motionEvent ->
                        when(motionEvent.action){
                            MotionEvent.ACTION_DOWN -> {
                                outcheck = false
                            }
                            MotionEvent.ACTION_MOVE -> {
                                view.x += motionEvent.x - view.width/2
                                view.y += motionEvent.y - view.height/2
                                if(!(view.x+view.width > p0.x && view.x < p0.x+p0.width && view.y+view.height > p0.y && view.y < p0.y + p0.height))
                                    outcheck = true
                            }
                            MotionEvent.ACTION_UP -> {
                                if(outcheck) {
                                    p0.removeView(view)
                                    Log.e("###","view removed")
                                }
                            }
                        }
                        true
                    }
                    p0.addView(im)
                    Log.e("###","drop frame success")
                    }
                    */
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