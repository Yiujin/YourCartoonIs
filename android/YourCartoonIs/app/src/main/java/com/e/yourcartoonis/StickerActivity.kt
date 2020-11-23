package com.e.yourcartoonis

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.collage.*
import kotlinx.android.synthetic.main.stickerlayout.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class StickerActivity  : AppCompatActivity() {
    private var path: ArrayList<String>? = null
    private var stickerView : LinearLayout? = null
    private var recv_image = ArrayList<Bitmap>()
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
        done.setOnClickListener {
            sticker_image_frame.buildDrawingCache()
            val bitmap = sticker_image_frame.getDrawingCache()
            val path = savetmpfile(bitmap)
            intent.putExtra("path",path)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }
    private class ShowSticker(c: Context,sticker:LinearLayout): AsyncTask<Void, Void, String>() {
        private val context:Context
        var assetManager:AssetManager? = null
        var inputStream: ArrayList<InputStream> = ArrayList()
        var len:Int = 0
        var stickerView : LinearLayout? = null
        var list : Array<String>? = null
        init {
            this.context = c
            assetManager = context.assets
            this.stickerView = sticker
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun onProgressUpdate(vararg values: Void?) {
            for(i in 0..len-1){
                val im = ImageView(context)
                im.adjustViewBounds = true
                im.layoutParams= LinearLayout.LayoutParams(200,200)
                im.setImageBitmap(BitmapFactory.decodeStream(inputStream[i]))
                im.tag = list!![i]
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
                im.setOnDragListener(DragListenerSticker(context))
                stickerView!!.addView(im)
            }
            super.onProgressUpdate(*values)
        }
        override fun doInBackground(vararg params: Void?): String? {
            list = context.assets.list("TomAndJerry")
            len = list!!.size
            try {
                for(i in 0..len-1){
                    inputStream.add(assetManager!!.open("TomAndJerry/${list!![i]}"))
                }
                publishProgress()
            }
            catch(e: IOException) {
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)
        }
    }
    fun decodeTmpBitmapList(path : ArrayList<String>?,dest : ArrayList<Bitmap>){
        val len = path!!.size-1
        for(i in 0..len){
            dest.add(BitmapFactory.decodeFile(path[i]))
        }
    }
    fun savetmpfile(recv : Bitmap) : String{
        val tmpFile = File.createTempFile("transfer_image_${99}","jpg")
        tmpFile.deleteOnExit()
        val output = FileOutputStream(tmpFile)
        recv.compress(Bitmap.CompressFormat.JPEG,90,output)
        val path = tmpFile.absolutePath
        output.close()
        return path
    }
}