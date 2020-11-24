package com.e.yourcartoonis

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_video_select.*
import kotlinx.android.synthetic.main.griditem.view.*
import kotlinx.android.synthetic.main.progress.*
import org.opencv.video.Video
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProgressActivity : AppCompatActivity() {
    var adapter : SelectAdapter? = null
    var KeyImage = ArrayList<Bitmap>()
    var recv_image : ArrayList<Bitmap>? = null
    val transfer_image = ArrayList<Bitmap>()
    var bitmap_width : Int = 0
    var bitmap_heigth : Int = 0
    private var VideoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress)
        VideoUri = Uri.parse(intent.extras!!.getString("VideoUri"))
        val input: InputStream? = contentResolver.openInputStream(VideoUri!!)
        KeyFrameExtraction(this,KeyImage,input!!,frame_progressbar,::showResult).execute()
    }
    fun showResult(){
        lateinit var check: Array<Boolean>
        setContentView(R.layout.activity_video_select)
        transfer.visibility = View.INVISIBLE
        adapter = SelectAdapter(this,KeyImage!!,0)
        gridView.adapter = adapter
        rotate.setOnClickListener {
            val matrix = Matrix()
            matrix.preRotate(90f,0f,0f)
            for(i in 0..(KeyImage!!.size - 1)){
                KeyImage[i] = Bitmap.createBitmap(KeyImage[i],0,0,KeyImage[i].width,KeyImage[i].height,matrix,false)
            }
            adapter = SelectAdapter(this,KeyImage!!,0)
            gridView.adapter = adapter
        }
        gonext.setOnClickListener() {
            bitmap_heigth = KeyImage[0].height
            bitmap_width = KeyImage[0].width
            check = adapter!!.getKey()
            for (i in 0..(KeyImage!!.size - 1)) {
                if (check[i]) transfer_image.add(KeyImage!![i])
            }
            adapter = SelectAdapter(this,transfer_image,1,::goStickerActivity)
            gridView.numColumns = 2
            gridView.adapter = adapter
            gonext.visibility = View.INVISIBLE
            transfer.visibility = View.VISIBLE
        }
        transfer.setOnClickListener {
            val ip = "52.151.59.153"
            val port = 8081
            val cons = ConnectServer(this.applicationContext, ip, port, transfer_image)
            recv_image = cons.execute().get()
            while (recv_image == null) {
            }
            var path = ArrayList<String>()
            savetmpfile(recv_image, path)

            val intent = Intent(this, MakeCollage::class.java)
            intent.putStringArrayListExtra("path", path)
            startActivity(intent)
        }
    }
    fun goStickerActivity(position: Int){
        Log.e("###","positoin ${position}")
        var path = ArrayList<String>()
        savetmpfile(arrayListOf(transfer_image[position]),path)
        val intent = Intent(this,StickerActivity::class.java)
        intent.putExtra("path",path)
        startActivityForResult(intent,position)
    }
    fun savetmpfile(recv : ArrayList<Bitmap>?, path: ArrayList<String>){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            val path = data!!.getStringExtra("path").toString()
            transfer_image[requestCode] = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path),bitmap_width,bitmap_heigth,true)
            adapter = SelectAdapter(this,transfer_image,1,::goStickerActivity)
            gridView.adapter = adapter
        }
    }
}