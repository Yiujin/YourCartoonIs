package com.e.yourcartoonis

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class KeyFrameExtraction(context: Context,input : InputStream) : AsyncTask<Any, Int, ArrayList<Bitmap>?>(){
    private val input: InputStream
    private val model_name = "yolov4-tiny-416.tflite"
    private val label_file = "coco.txt"
    private val context: Context
    private val model : YoloClassifier
    val KeyImage = ArrayList<Bitmap>()

    init{
        this.input = input
        this.context = context
        this.model = YoloClassifier(context,model_name,label_file)
    }
    external fun extractKeyFrame(matArrayaddr:LongArray,size:Int) : Array<IntArray>

    override fun doInBackground(vararg p0: Any?): ArrayList<Bitmap>? {
        try {
            var media = FFmpegMediaMetadataRetriever()
            val tmpFile = File.createTempFile("tmpfile", "mp4")
            tmpFile.deleteOnExit()
            val output = FileOutputStream(tmpFile)
            var read = 0
            val buffer = ByteArray(4 * 1024)
            Log.e("###", "start create mp4 tmp file")
            while (true) {
                read = input!!.read(buffer, 0, 4 * 1024 - 2)
                if (read == -1)
                    break
                output.write(buffer, 0, read)
            }
            output.close()
            Log.e("###", "end create mp4 tmp file")

            var path = tmpFile.absolutePath
            media.setDataSource(path)
            //getFrame(path,0)

            //var media = MediaMetadataRetriever()
            //media.setDataSource(path)
            var time = media.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)
            var videotime = ((Integer.parseInt(time) / 1000) - 1) * 3
            var bitmapList = ArrayList<Bitmap>()
            for (i in 0..videotime - 1) {
                val frame = media.getFrameAtTime(i.toLong() * 1000000 / 3)
                if (frame == null)
                    break
                bitmapList.add(frame)
                //Log.v("###","${i} th frame time : ${i.toLong()*1000000/3}")
            }
            val len = bitmapList.size
            var MatList = Array<Mat>(len, { Mat() })
            var MatAddrList = LongArray(len, { 0 })
            for (i in 0..len - 1) {
                Utils.bitmapToMat(bitmapList[i], MatList[i])
                MatAddrList[i] = MatList[i].nativeObjAddr
            }
            var MInput = MatList[5]
            var bitmap: Bitmap? =
                Bitmap.createBitmap(MInput.cols(), MInput.rows(), Bitmap.Config.RGB_565)
            var clustersize: Int = 0
            var compare = extractKeyFrame(MatAddrList, clustersize)
            //var bitmap = BitmapFactory.decodeStream(input)

            input!!.close()

            val tmpMatList = ArrayList<Long>()
            val tmpIndex = ArrayList<Int>()

            for (i in 0..(compare.size - 1)) {
                val classes = model.detection(MatList[compare[i][0]]!!)
                if (classes.contains("person"))
                    compare[i][1] += 3
                if (compare[i][1] >= 0.01 * videotime) {
                    tmpMatList.add(MatList[compare[i][0]].nativeObjAddr)
                    tmpIndex.add(compare[i][0])
                }
            }
            compare = extractKeyFrame(tmpMatList.toLongArray(), clustersize)
            for (i in 0..(compare.size - 1))
                KeyImage.add(bitmapList[tmpIndex[compare[i][0]]])
        }catch(e:Exception) {
            e.printStackTrace()
        }
        return KeyImage
    }
}