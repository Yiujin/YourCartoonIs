package com.e.yourcartoonis

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.e.yourcartoonis.R
import kotlinx.android.synthetic.main.pic_transfer_result.*
import kotlinx.android.synthetic.main.pic_transfer_result.save
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowPictransferResult : AppCompatActivity() {
    private var path = ArrayList<String>()
    private var recv_image = ArrayList<Bitmap>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pic_transfer_result)
        path = intent.getStringArrayListExtra("path")
        decodeTmpBitmapList(path,recv_image)
        result_image.setImageBitmap(Bitmap.createScaledBitmap(recv_image[0], 1000, (recv_image[0].height * 1000) / recv_image[0].width, true))

        save.setOnClickListener {
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            val saveformat = SimpleDateFormat("yyyyMMdd_HHmmss")
            val time = Date()
            val current: String = df.format(time)
            val saveFileName :String = saveformat.format(time)
            result_frame.buildDrawingCache()
            val bitmap = result_frame.getDrawingCache()
            val fileOutputStream : FileOutputStream
            val folder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/")
            if(!folder.exists())
                Log.e("###","folder not found")
            val dst_path = Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera/"
            try {
                fileOutputStream =
                    FileOutputStream(dst_path + saveFileName + ".jpg") // 경로 + 제목 + .jpg로 FileOutputStream Setting
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try { // TODO : 미디어 스캔
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val file = File(dst_path + saveFileName + ".jpg")
                intent.setData(Uri.fromFile(file))
                sendBroadcast(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                println("::::ERROR:::: $e")
            }
            Toast.makeText(this.applicationContext,"저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    fun decodeTmpBitmapList(path : ArrayList<String>?,dest : ArrayList<Bitmap>){
        val len = path!!.size-1
        for(i in 0..len){
            dest.add(BitmapFactory.decodeFile(path[i]))
        }
    }
}