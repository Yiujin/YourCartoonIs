package com.e.yourcartoonis

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class Dummy : AppCompatActivity() {
    private var dummyList: ArrayList<Bitmap> = ArrayList<Bitmap>()
    private var path : ArrayList<String> = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dummyList.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.dummy_image_1))
        dummyList.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.dummy_image_2))
        dummyList.add(BitmapFactory.decodeResource(applicationContext.resources,R.drawable.dummy_image_3))
        saveRecvtmpfile(dummyList,path)
        val intent = Intent(this,MakeCollage::class.java)
        intent.putStringArrayListExtra("path",path)
        startActivity(intent)
    }
    fun saveRecvtmpfile(recv : ArrayList<Bitmap>?, path: ArrayList<String>){
        val len = recv!!.size-1
        for(i in 0..len){
            val tmpFile = File.createTempFile("recv_image_${i}","jpg")
            tmpFile.deleteOnExit()
            val output = FileOutputStream(tmpFile)
            recv[i].compress(Bitmap.CompressFormat.JPEG,90,output)
            path.add(tmpFile.absolutePath)
            output.close()
        }
    }
}