package com.e.yourcartoonis

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.stickerlayout.*
import java.io.IOException
import java.io.InputStream

class StickerInLayout  : AppCompatActivity() {
    private var path: ArrayList<String>? = null
    private var stickerView : LinearLayout? = null
    private var recv_image = ArrayList<Bitmap>()
    var frameLayout : ArrayList<FrameLayout>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stickerlayout)
        path = intent.getStringArrayListExtra("path")
        val asset = assets
        stickerView = sticker_linear
        decodeTmpBitmapList(path,recv_image)
        target_image.setImageBitmap(Bitmap.createScaledBitmap(recv_image[0], 1000, 1000, true))
        sticker_image_frame.setOnDragListener(DragListenerSticker(applicationContext))
        ShowSticker(applicationContext,stickerView!!).execute()
    }
    fun decodeTmpBitmapList(path : ArrayList<String>?,dest : ArrayList<Bitmap>){
        val len = path!!.size-1
        for(i in 0..len){
            dest.add(BitmapFactory.decodeFile(path[i]))
        }
    }
}