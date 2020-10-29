package com.e.yourcartoonis

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sub.*
import kotlinx.android.synthetic.main.frame_recved.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Pictransfer : AppCompatActivity() {
    private var KeyImage = ArrayList<Bitmap>(2)
    private lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        upload.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent,1)
        }
    }
    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                var currentImageUrl : Uri? = data?.data

                try {
                    var input: InputStream? = contentResolver.openInputStream(currentImageUrl!!)
                    bitmap = BitmapFactory.decodeStream(input)
                    //KeyImage[0] = bitmap
                    //var bitmap = BitmapFactory.decodeStream(input)
                    imageView2.setImageBitmap(bitmap)
                    submit.visibility = VISIBLE
                }catch(e:Exception) {
                    e.printStackTrace()
                }
            }
        }
        submit.setOnClickListener(){
            //val ip = "52.151.59.153"
            //val port = 8081
            //val cons = ConnectServer(this.applicationContext,ip,port,KeyImage,receved_linear)
            //cons.execute()
            val tmpFile = File.createTempFile("recv_image_${0}","jpg")
            tmpFile.deleteOnExit()
            val output = FileOutputStream(tmpFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,output)
            val path = arrayListOf(tmpFile.absolutePath)
            output.close()
            val intent = Intent(this,StickerActivity::class.java)
            intent.putStringArrayListExtra("path",path)
            startActivity(intent)
        }
    }

}