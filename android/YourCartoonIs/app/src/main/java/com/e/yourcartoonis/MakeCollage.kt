package com.e.yourcartoonis

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.e.yourcartoonis.Collage.CollageSuper
import com.e.yourcartoonis.Collage.Collage_3_001
import kotlinx.android.synthetic.main.collage.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MakeCollage :AppCompatActivity(){
    private var path: ArrayList<String>? = null
    private var recv_image : ArrayList<Bitmap> = ArrayList<Bitmap>()
    private var Collage : CollageSuper? = null
    private var CollageFragment : Fragment? =null
    private var id:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collage)
        path = intent.getStringArrayListExtra("path")

        decodeTmpBitmapList(path,recv_image)

        setContentView(R.layout.collage)
        Collage = Collage_3_001()
        supportFragmentManager.beginTransaction().replace(R.id.fragment1,
            Collage!!
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
            im.isClickable = false
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
        Layout_Button.setOnClickListener {
            Collage = supportFragmentManager.findFragmentById(R.id.fragment1) as CollageSuper?
            if(Collage == null){
                Log.e("###","collage is null")
            }
            next_Button.visibility = View.INVISIBLE
            Collage!!.goLayout()
            Layout_Button.visibility = View.INVISIBLE
            next_Button.visibility = View.VISIBLE
        }
        next_Button.setOnClickListener {
            Collage = supportFragmentManager.findFragmentById(R.id.fragment1) as CollageSuper?
            if(Collage == null){
                Log.e("###","collage is null")
            }
            next_Button.visibility = View.INVISIBLE
            Layout_Button.visibility = View.VISIBLE
            save.visibility = View.VISIBLE
            Collage!!.setScrollTouch()
        }
        sticker_done.setOnClickListener {
            Collage = supportFragmentManager.findFragmentById(R.id.fragment1) as CollageSuper?
            if(Collage == null){
                Log.e("###","collage is null")
            }
            sticker_img.visibility = View.INVISIBLE
            recv_img.visibility = View.VISIBLE
            sticker_done.visibility = View.INVISIBLE
            Collage!!.stickerDone(id)
        }
        save.setOnClickListener {
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            val saveformat = SimpleDateFormat("yyyyMMdd_HHmmss")
            val time = Date()
            val current: String = df.format(time)
            val saveFileName :String = saveformat.format(time)
            fragment1.buildDrawingCache()
            val bitmap = fragment1.getDrawingCache()
            val fileOutputStream : FileOutputStream
            val folder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/")
            if(!folder.exists())
                Log.e("###","folder not found")
            val dst_path = Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera/"
            try {
                fileOutputStream =
                    FileOutputStream(dst_path + saveFileName + ".jpg") // 경로 + 제목 + .jpg로 FileOutputStream Setting
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try { // TODO : 미디어 스캔
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val file = File(dst_path + saveFileName + ".jpg")
                intent.setData(Uri.fromFile(file))
                sendBroadcast(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                println("::::ERROR:::: $e")
            }
            Toast.makeText(this.applicationContext,"저장되었습니다.",Toast.LENGTH_SHORT).show()
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
    fun startSticker(id :Int) {
        save.visibility = View.INVISIBLE
        sticker_done.visibility = View.VISIBLE
        recv_img.visibility = View.INVISIBLE
        sticker_img.visibility = View.VISIBLE
        this.id = id
        ShowSticker(applicationContext,sticker_img).execute()
    }
}