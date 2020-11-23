package com.e.yourcartoonis

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.griditem.view.*
import java.io.File
import java.io.FileOutputStream

class SelectAdapter : BaseAdapter {
    var itemList = ArrayList<Bitmap>()
    var context : Context? = null
    var check : Array<Boolean>
    var flag : Int
    var F : ((position:Int)->Unit)?
    constructor(context:Context,itemList : ArrayList<Bitmap>,flag:Int,F: ((position:Int)->Unit)? = null) : super() {
        this.context = context
        this.itemList = itemList
        this.check = Array<Boolean>(itemList.size,{i -> false})
        this.flag = flag //flag = 0 -> select transfer image , flag = 1 -> select image attach sticker
        this.F = F
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(p0: Int): Any {
        return itemList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val item = itemList[p0]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var itemView = inflator.inflate(R.layout.griditem, null)
        itemView.item.setImageBitmap(item)
        if(flag == 0) {
            itemView.setOnClickListener {
                Log.e("###", "selected img ${p0}")
                if (check[p0]) {
                    check[p0] = false
                    it.item.setColorFilter(null)
                    it.item.setBackgroundColor(Color.parseColor("#00000000"))
                } else {
                    check[p0] = true
                    it.item.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY)
                    it.item.setBackgroundColor(Color.parseColor("#FF00FF"))
                }
            }
        }
        else{
            itemView.setOnClickListener {
                F!!(p0)
            }
        }
        return itemView
    }
    fun getKey() : Array<Boolean>{
        return check
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