package com.e.yourcartoonis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.FrameMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.griditem.view.*
import kotlinx.android.synthetic.main.stickerframeitem.view.*

class FrameAdapter : BaseAdapter {
    var itemList = ArrayList<FrameLayout>()
    var context : Context? = null
    lateinit var check : Array<Boolean>
    constructor(context: Context, itemList : ArrayList<FrameLayout> ) : super() {
        this.context = context
        this.itemList = itemList
        this.check = Array<Boolean>(itemList.size,{i -> false})
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
        var itemView = inflator.inflate(R.layout.stickerframeitem, null)
        itemView.setOnClickListener{
            Log.e("###","selected img ${p0}")
            if(check[p0]){
                check[p0]=false
                it.item.setColorFilter(null)
                it.item.setBackgroundColor(Color.parseColor("#00000000"))
            }
            else{
                check[p0]=true
                it.item.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY)
                it.item.setBackgroundColor(Color.parseColor("#FF00FF"))
            }
        }

        return itemView
    }
    fun getKey() : Array<Boolean>{
        return check
    }

}