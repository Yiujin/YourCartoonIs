package com.e.yourcartoonis

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity


import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.e.yourcartoonis.R.layout.activity_main
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.Utils
import org.opencv.core.Mat
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    external fun setText() : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        dummy.visibility = View.INVISIBLE
        pictransfer.setOnClickListener {
            var testprogress:ProgressDialog? = null
            testprogress = ProgressDialog.show(this, "타이틀입니다.", "메시지입니다.")

            val intent = Intent(this, Pictransfer::class.java)


            // 핸들러를 통해서 종료 작업을 한다.

            startActivity(intent)
        }
        videotransfer.setOnClickListener {
            val intent = Intent(this, VideoTransfer::class.java)
            startActivity(intent)
        }
        dummy.setOnClickListener {
            val intent = Intent(this, Dummy::class.java)
            startActivity(intent)
        }
    }
}
