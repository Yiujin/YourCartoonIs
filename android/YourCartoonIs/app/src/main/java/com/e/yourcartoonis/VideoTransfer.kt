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
import kotlinx.android.synthetic.main.activity_sub.*
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_video_select.*
import org.opencv.android.Utils
import org.opencv.core.Mat
import wseemann.media.FFmpegMediaMetadataRetriever
import android.app.ProgressDialog
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Trace
import android.renderscript.ScriptGroup
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.marginLeft
import kotlinx.android.synthetic.main.frame_recved.*
import kotlinx.android.synthetic.main.imview.*
import org.opencv.core.CvType.CV_32FC1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class VideoTransfer : AppCompatActivity() {
    val model_name = "yolov4-tiny-416.tflite"
    val label_file = "coco.txt"
    var asset_manager: AssetManager? = null
    init{
        System.loadLibrary("native-lib")
    }
    var testprogress:ProgressDialog? = null
    var check_progress:Boolean = false
    var handler = Handler()
    var thread = Runnable { testprogress?.cancel() }
    val KeyImage = ArrayList<Bitmap>()

    external fun extractKeyFrame(matArrayaddr:LongArray,size:Int) : Array<IntArray>
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


                try {
                        var input: InputStream? = contentResolver.openInputStream(VideoUrl!!)
                        var media = FFmpegMediaMetadataRetriever()
                        val tmpFile = File.createTempFile("tmpfile","mp4")
                        tmpFile.deleteOnExit()
                        val output = FileOutputStream(tmpFile)
                        var read = 0
                        val buffer = ByteArray(4*1024)

                        while (true) {
                            read = input!!.read(buffer,0,4*1024 -2)
                            if( read == -1)
                                break
                            output.write(buffer,0,read)
                        }
                        output.close()
                    var context: Context = this.applicationContext
                    var path = tmpFile.absolutePath
                    media.setDataSource(path)
                    //getFrame(path,0)

                    //var media = MediaMetadataRetriever()
                    //media.setDataSource(path)
                    var time = media.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
                    var videotime = ((Integer.parseInt(time)/1000)-1)*3

                    var MatList = Array<Mat>(videotime,{ Mat() })
                    var bitmapList = arrayOfNulls<Bitmap>(videotime)
                    var MatAddrList = LongArray(videotime,{0})
                    for(i in 0..videotime-1) {
                        bitmapList[i] = media.getFrameAtTime(i.toLong()*1000000/3)
                        //Log.v("###","${i} th frame time : ${i.toLong()*1000000/3}")
                        if(bitmapList[i] == null)
                            break
                        Utils.bitmapToMat(bitmapList[i],MatList[i])
                        MatAddrList[i] = MatList[i].nativeObjAddr
                    }
                    var MInput = MatList[5]
                    var bitmap: Bitmap? = Bitmap.createBitmap(MInput.cols(),MInput.rows(), Bitmap.Config.RGB_565)
                    var clustersize:Int = 0
                    var compare = extractKeyFrame(MatAddrList,clustersize)
                    //var bitmap = BitmapFactory.decodeStream(input)

                    input!!.close()
                    check_progress=true
                    handler.postDelayed(thread,1000) // 딜레이는 1초
                    setContentView(R.layout.activity_video_select)
                    var keysize = 0
                    for (i in 0..(compare.size-1)){
                        val classes = model.detection(MatList[compare[i][0]]!!)
                        if( classes.contains("person"))
                            compare[i][1] += 3
                        if(compare[i][1] >= 0.01*videotime)
                            KeyImage.add(bitmapList[compare[i][0]]!!)
                    }
                    var imViewList = Array<ImageView>(KeyImage.size) { ImageView(this) }
                    check=Array<Boolean>(KeyImage.size){false}
                    for(i in 0..(KeyImage.size-1)){
                        imViewList[i].adjustViewBounds = true
                        imViewList[i].layoutParams=LinearLayout.LayoutParams(800,LinearLayout.LayoutParams.WRAP_CONTENT)
                        imViewList[i].setPadding(5,5,5,5)
                        imViewList[i].setBackgroundColor(Color.parseColor("#00000000"))
                        imViewList[i].setImageBitmap(KeyImage[i])
                        linear.addView(imViewList[i])
                        imViewList[i].setOnClickListener{
                            Log.e("###","selected img ${i}")
                            if(check[i]){
                                check[i]=false
                                imViewList[i].setColorFilter(null)
                                imViewList[i].setBackgroundColor(Color.parseColor("#00000000"))
                            }
                            else{
                                check[i]=true
                                imViewList[i].setColorFilter(Color.parseColor("#BDBDBD"),PorterDuff.Mode.MULTIPLY)
                                imViewList[i].setBackgroundColor(Color.parseColor("#FF00FF"))
                            }
                        }
                    }
                    var MResult = Mat(MInput.rows(),MInput.cols(),MInput.type())
                    //ConvertRGBtoGray(MInput.nativeObjAddr,MResult.nativeObjAddr)
                    //Utils.matToBitmap(MResult,bitmap)
                    //image_view.setImageBitmap(bitmap)


                }catch(e:Exception) {
                    e.printStackTrace()
                }
            }
        }
        gonext.setOnClickListener() {
            setContentView(R.layout.frame_recved)
            var transfer_image=ArrayList<Bitmap>()
            for(i in 0..(KeyImage.size-1)){
                if(check[i]) transfer_image.add(KeyImage[i])
            }
            val ip = "52.151.59.153"
            val port = 8081
            val cons = ConnectServer(this.applicationContext, ip, port, transfer_image, receved_linear)
            cons.execute()
        }
    }
}