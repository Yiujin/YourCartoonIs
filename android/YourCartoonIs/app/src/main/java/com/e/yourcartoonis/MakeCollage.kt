package com.e.yourcartoonis

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.collage.*
import java.io.Serializable

class MakeCollage (c: Context, imList : ArrayList<Bitmap>?,sc:ArrayList<ScrollView>){
    private var imList: ArrayList<Bitmap>? = null
    private var context:Context
    private var sc : ArrayList<ScrollView>
    init{
        this.imList = imList
        this.context = c
        this.sc = sc
    }
    public fun make() {
        val size = imList!!.size
        for (i in 0..2) {
            var imView = ImageView(this.context)
            imView.layoutParams = LinearLayout.LayoutParams(1000, 1000)
            imView.setImageBitmap(Bitmap.createScaledBitmap(imList!![i], 1000, 1000, true))
            sc[i].addView(imView)
        }
        /*
        for ( i in 0..0) {
            imViewList[i].layoutParams = LinearLayout.LayoutParams(500, 500)
            imViewList[i].setPadding(20, 0, 0, 0)
            imViewList[i].setImageBitmap(BitmapArray[i])
        }*/
    }
}