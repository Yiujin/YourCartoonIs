package com.e.yourcartoonis

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.e.yourcartoonis.Collage.Collage_001
import kotlinx.android.synthetic.main.collage.*
import java.io.Serializable

class MakeCollage :AppCompatActivity(){
    private var path: ArrayList<String>? = null
    private var recv_image : ArrayList<Bitmap> = ArrayList<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collage)
        path = intent.getStringArrayListExtra("path")

        decodeTmpBitmapList(path,recv_image)

        setContentView(R.layout.collage)
        supportFragmentManager.beginTransaction().replace(R.id.fragment1,
            Collage_001()
        ).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fragment2,
            SelectCollage()
        ).commit()
        for( i in 0..recv_image.size-1){
            val im = ImageView(this.applicationContext)
            im.adjustViewBounds = true
            im.layoutParams=LinearLayout.LayoutParams(300,LinearLayout.LayoutParams.WRAP_CONTENT)
            im.setPadding(5,5,5,5)
            im.setBackgroundColor(Color.parseColor("#00000000"))
            im.setImageBitmap(recv_image[i])
            recv_img.addView(im)
            im.tag = path!![i]
            im.setOnLongClickListener { v: View ->
                val item = ClipData.Item(v.tag as CharSequence)
                val dragData = ClipData(
                    v.tag as CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val ShadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(dragData,ShadowBuilder,null,0)
            }
            im.setOnDragListener(DragListener(applicationContext))
        }
    }
    fun decodeTmpBitmapList(path : ArrayList<String>?,dest : ArrayList<Bitmap>){
        val len = path!!.size-1
        for(i in 0..len){
            dest.add(BitmapFactory.decodeFile(path[i]))
        }
    }
    fun getBitmap() : ArrayList<Bitmap>?{
        return recv_image
    }
    fun changeFragment(collage : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment1,collage).commit()
    }
}