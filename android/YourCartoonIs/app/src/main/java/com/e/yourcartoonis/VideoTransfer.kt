package com.e.yourcartoonis

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_video_select.*
import org.opencv.android.Utils
import org.opencv.core.Mat
import wseemann.media.FFmpegMediaMetadataRetriever
import android.app.ProgressDialog
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.ImageFormat.JPEG
import android.graphics.PorterDuff
import android.os.Handler
import android.view.DragEvent
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.e.yourcartoonis.Collage.Collage_3_001
import kotlinx.android.synthetic.main.collage.*
import java.io.*

class VideoTransfer : AppCompatActivity() {
    val model_name = "yolov4-tiny-416.tflite"
    val label_file = "coco.txt"
    var adapter : SelectAdapter? = null
    var asset_manager: AssetManager? = null
    init{
        System.loadLibrary("native-lib")
    }
    var testprogress:ProgressDialog? = null
    var check_progress:Boolean = false
    var handler = Handler()
    var thread = Runnable { testprogress?.cancel() }
    var KeyImage : ArrayList<Bitmap>? = null
    var recv_image : ArrayList<Bitmap>? = null
    var CollageList : ArrayList<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        album.setOnClickListener {
            submit_video.visibility = View.VISIBLE

            testprogress = ProgressDialog.show(this, "made by soo.", "")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("video/*")
            startActivityForResult(intent, 2)
            if(!check_progress) handler.postDelayed(thread,1000)
        }
    }
    private fun BitmaptoFloatArray(Bitmap:Bitmap?) : FloatArray?{
        if(Bitmap == null)
            return null
        var x : Int = Bitmap.width
        var y : Int = Bitmap.height
        var data : FloatArray = FloatArray(x*y)
        var tmp : IntArray = IntArray(x*y)
        Bitmap.getPixels(tmp,0,x,0,0,x,y)
        for (i in 0..(x*y-1))
            data[i] = tmp[i].toFloat()
        return data
    }
    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val model = YoloClassifier(this,model_name,label_file)
        lateinit var check: Array<Boolean>
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 2){
                var VideoUrl : Uri? = data?.data
                var input: InputStream? = contentResolver.openInputStream(VideoUrl!!)

                KeyImage = KeyFrameExtraction(this,input!!).execute().get()
                while(KeyImage == null) {}
                setContentView(R.layout.activity_video_select)
                adapter = SelectAdapter(this,KeyImage!!)
                gridView.adapter = adapter
            }
        }
        gonext.setOnClickListener() {
            var transfer_image=ArrayList<Bitmap>()
            check = adapter!!.getKey()
            for(i in 0..(KeyImage!!.size-1)){
                if(check[i]) transfer_image.add(KeyImage!![i])
            }

            val ip = "52.151.59.153"
            val port = 8081
            val cons = ConnectServer(this.applicationContext, ip, port, transfer_image)
            recv_image = cons.execute().get()
            while(recv_image == null){}
            var path = ArrayList<String>()
            saveRecvtmpfile(recv_image,path)

            val intent = Intent(this,MakeCollage::class.java)
            intent.putStringArrayListExtra("path",path)
            startActivity(intent)


        }
    }
    fun saveRecvtmpfile(recv : ArrayList<Bitmap>?,path: ArrayList<String>){
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