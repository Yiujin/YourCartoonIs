package com.e.yourcartoonis


import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setPadding
import java.io.IOException
import java.io.InputStream

class ShowCollage (c: Context, collage: LinearLayout): AsyncTask<Void, Void, String>() {
    private val context: Context
    var assetManager: AssetManager? = null
    var inputStream: ArrayList<InputStream> = ArrayList()
    var len:Int = 0
    var collageView : LinearLayout? = null
    var list : Array<String>? = null
    init {
        this.context = c
        assetManager = context.assets
        this.collageView = collage
    }
    override fun onPreExecute() {
        super.onPreExecute()
    }
    override fun onProgressUpdate(vararg values: Void?) {
        for(i in 0..len-1){
            val im = ImageView(context)
            im.adjustViewBounds = true
            im.layoutParams= LinearLayout.LayoutParams(200,200)
            im.scaleType = ImageView.ScaleType.FIT_XY
            im.setPadding(5)
            im.setImageBitmap(BitmapFactory.decodeStream(inputStream[i]))
            im.tag = list!![i]
            im.setOnClickListener { v: View ->

            }
            collageView!!.addView(im)
        }
        super.onProgressUpdate(*values)
    }
    override fun doInBackground(vararg params: Void?): String? {
        list = context.assets.list("Collage")
        len = list!!.size
        try {
            for(i in 0..len-1){
                inputStream.add(assetManager!!.open("Collage/${list!![i]}"))
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