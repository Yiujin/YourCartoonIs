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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Pictransfer : AppCompatActivity() {
    private var KeyImage = ArrayList<Bitmap>()
    private lateinit var bitmap: Bitmap
    private lateinit var transferBitmap : Bitmap
    var recv_image : ArrayList<Bitmap>? = null

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
            when(requestCode){
                1 -> {
                    var currentImageUrl: Uri? = data?.data

                    try {
                        var input: InputStream? = contentResolver.openInputStream(currentImageUrl!!)
                        bitmap = BitmapFactory.decodeStream(input)
                        imageView2.setImageBitmap(bitmap)
                        transferBitmap = bitmap
                        submit.visibility = VISIBLE
                        imageView2.setOnClickListener {
                            val tmpFile = File.createTempFile("recv_image_${0}","jpg")
                            tmpFile.deleteOnExit()
                            val output = FileOutputStream(tmpFile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG,90,output)
                            val path = arrayListOf(tmpFile.absolutePath)
                            output.close()
                            val intent = Intent(this,StickerActivity::class.java)
                            intent.putStringArrayListExtra("path",path)
                            startActivityForResult(intent,3)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                3 -> {
                        val path = data!!.getStringExtra("path").toString()
                        transferBitmap = BitmapFactory.decodeFile(path)
                        imageView2.setImageBitmap(transferBitmap)
                }
            }
        }
        submit.setOnClickListener(){
            val ip = "52.151.59.153"
            val port = 8081
            KeyImage.add(transferBitmap)
            val cons = ConnectServer(this.applicationContext,ip,port,KeyImage)
            recv_image = cons.execute().get()
            while (recv_image == null) {
            }
            var path = ArrayList<String>()
            savetmpfile(recv_image, path)
            val intent = Intent(this, ShowPictransferResult::class.java)
            intent.putStringArrayListExtra("path", path)
            startActivity(intent)
        }
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
}