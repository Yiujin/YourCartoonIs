package com.e.yourcartoonis

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sub.*
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.activity_video_select.*
import kotlinx.android.synthetic.main.progress.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class VideoTransfer : AppCompatActivity() {
    private var VideoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        album.setOnClickListener {
            submit_video.visibility = View.VISIBLE

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("video/*")
            startActivityForResult(intent, 2)
        }
        submit_video.setOnClickListener {
            val intent = Intent(this,ProgressActivity::class.java)
            intent.putExtra("VideoUri",VideoUri.toString())
            startActivity(intent)
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
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 2){
                VideoUri = data?.data
                var id: Long = -1
                val projection = arrayOf<String>(MediaStore.Video.Thumbnails._ID)
                val videoCursor: Cursor? = managedQuery(VideoUri, projection, null, null, null)

                if (videoCursor != null && videoCursor.moveToNext()) {
                    val videoIDCol: Int =
                        videoCursor.getColumnIndex(MediaStore.Video.Thumbnails._ID)
                    id = videoCursor.getLong(videoIDCol)
                }

                try {
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    options.inSampleSize = 1
                    val curThumb: Bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                        contentResolver,
                        id,
                        MediaStore.Video.Thumbnails.MICRO_KIND,
                        options
                    ) //비트맵에 이미지가 저장된다.
                    videoView.setImageBitmap(curThumb)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}