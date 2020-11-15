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

class ShowCollage (c: Context, collage: LinearLayout): AsyncTask<Any,Int, ArrayList<ImageView>>() {
    private val context: Context
    var assetManager: AssetManager? = null
    var inputStream: ArrayList<InputStream> = ArrayList()
    var len:Int = 0
    var collageView : LinearLayout? = null
    var list : Array<String>? = null
    var imList = ArrayList<ImageView>()
    init {
        this.context = c
        assetManager = context.assets
        this.collageView = collage
    }
    override fun onPreExecute() {
        super.onPreExecute()
    }
    override fun onProgressUpdate(vararg values: Int?) {
        for(i in 0..len-1){
            collageView!!.addView(imList[i])
        }
        super.onProgressUpdate(*values)
    }
    override fun doInBackground(vararg params: Any?): ArrayList<ImageView>? {
        list = context.assets.list("Collage")
        len = list!!.size
        try {
            for(i in 0..len-1){
                inputStream.add(assetManager!!.open("Collage/${list!![i]}"))
            }
            for(i in 0..len-1) {
                val im = ImageView(context)
                im.adjustViewBounds = true
                im.layoutParams = LinearLayout.LayoutParams(200, 200)
                im.scaleType = ImageView.ScaleType.FIT_XY
                im.setPadding(5)
                im.setImageBitmap(BitmapFactory.decodeStream(inputStream[i]))
                im.tag = list!![i]
                imList.add(im)
            }
            publishProgress()
        }
        catch(e: IOException) {
            e.printStackTrace()
        }
        return imList
    }
    override fun onPostExecute(result: ArrayList<ImageView>?) {
        super.onPostExecute(result)
    }
}