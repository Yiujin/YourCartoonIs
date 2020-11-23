package com.e.yourcartoonis

import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_select.*
import kotlinx.android.synthetic.main.progress.*
import org.opencv.video.Video
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProgressActivity : AppCompatActivity() {
    var adapter : SelectAdapter? = null
    var KeyImage = ArrayList<Bitmap>()
    var recv_image : ArrayList<Bitmap>? = null
    private var VideoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress)
        VideoUri = Uri.parse(intent.extras!!.getString("VideoUri"))
        frame_progressbar.setProgress(0)
        callFrameExtraction()
        getresult.visibility= View.INVISIBLE
        getresult.setOnClickListener {
            showResult()
        }
    }
    fun callFrameExtraction(){
        val input: InputStream? = contentResolver.openInputStream(VideoUri!!)
        KeyFrameExtraction(this,KeyImage,input!!,frame_progressbar,getresult).execute()
        Log.e("###","done ${frame_progressbar.progress}")
        //KeyImage = KeyFrameExtraction(this,input!!,frame_progressbar).execute().get()
        /*

        }*/
    }
    fun showResult(){
        lateinit var check: Array<Boolean>
        setContentView(R.layout.activity_video_select)
        adapter = SelectAdapter(this,KeyImage!!)
        gridView.adapter = adapter

        gonext.setOnClickListener() {
            val transfer_image = ArrayList<Bitmap>()
            check = adapter!!.getKey()
            for (i in 0..(KeyImage!!.size - 1)) {
                if (check[i]) transfer_image.add(KeyImage!![i])
            }

            val ip = "52.151.59.153"
            val port = 8081
            val cons = ConnectServer(this.applicationContext, ip, port, transfer_image)
            recv_image = cons.execute().get()
            while (recv_image == null) {
            }
            var path = ArrayList<String>()
            saveRecvtmpfile(recv_image, path)

            val intent = Intent(this, MakeCollage::class.java)
            intent.putStringArrayListExtra("path", path)
            startActivity(intent)
        }
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