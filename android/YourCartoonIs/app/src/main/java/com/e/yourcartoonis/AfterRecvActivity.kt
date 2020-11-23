package com.e.yourcartoonis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_select.*
import kotlinx.android.synthetic.main.stickerlayout.*

class AfterRecvActivity : AppCompatActivity() {
    private var path: ArrayList<String>? = null
    private var recv_image = ArrayList<Bitmap>()
    var adapter: SelectAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_select)
        path = intent.getStringArrayListExtra("path")
        decodeTmpBitmapList(path, recv_image)
        adapter = SelectAdapter(applicationContext,recv_image,0)
        gridView.adapter = adapter
    }

    fun decodeTmpBitmapList(path: ArrayList<String>?, dest: ArrayList<Bitmap>) {
        val len = path!!.size - 1
        for (i in 0..len) {
            dest.add(BitmapFactory.decodeFile(path[i]))
        }
    }
}