package com.e.yourcartoonis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.AsyncTask.execute
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_video_select.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.net.Socket
import java.util.stream.IntStream.range

class ConnectServer(c: Context, Ip:String,Port:Int,Data:ArrayList<Bitmap>,Linear:LinearLayout) : AsyncTask<Void,Void,Void>(){
        private val context: Context
        private val ip : String
        private val port: Int
        private var inStream : InputStream? = null
        private var outStream : OutputStream? = null
        private var mSock : Socket? = null
        private var data: ByteArray?= null
        private var BitmapArray : ArrayList<Bitmap>
        private var size: Int
        private var imgSize: Int? = null
        private var Sdata : ByteArray = ByteArray(4096*4)
        private var imView : LinearLayout
        private var imViewList : Array<ImageView>
        init {
            this.context = c
            this.ip = Ip
            this.port = Port
            this.BitmapArray = Data
            this.size = BitmapArray.size
            this.imView = Linear
            this.imViewList = Array<ImageView>(this.size){ImageView(this.context)}
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg p0: Void?): Void? {
            val mSock = Socket(ip,port)
            var i = 0
            inStream = mSock.getInputStream()
            outStream = mSock.getOutputStream()
            outStream!!.write(size.toString().toByteArray())

            for(i in 0..(size-1)){
                data = BitmaptoByteArray(BitmapArray[i])
                imgSize = data!!.size
                outStream!!.write(imgSize.toString().toByteArray())
                inStream!!.read(Sdata)
                outStream!!.write(data)
                inStream!!.read(Sdata)
            }

            var recvSize = 0
            var totalrecv = 0
            for(i in 0..(size-1)){
                recvSize = 0
                totalrecv = 0
                inStream!!.read(Sdata)
                //Log.e("###", "size string: ${String(Sdata)}")
                var index = String(Sdata).slice(IntRange(0,0)).toInt()
                Log.e("###","index : ${index}")
                imgSize = String(Sdata).slice(IntRange(1,index)).toInt()
                Log.e("###","imag size : ${imgSize}")
                data = ByteArray(0)
                Log.e("###","data array : ${data!!.size}")
                outStream!!.write("start".toByteArray())

                while(true){
                    recvSize = inStream!!.read(Sdata)
                    totalrecv += recvSize
                    Log.e("###","recvSize : ${recvSize}")
                    data = data!! + Sdata.slice(IntRange(0,recvSize-1))
                    if(totalrecv >= imgSize!!)
                        break
                }
                Log.e("###","total recv : ${totalrecv} total data size : ${data!!.size}")
                outStream!!.write("next".toByteArray())
                Log.e("###","sendNext")
                BitmapArray[i] = ByteArraytoBitmap(data)!!
                imViewList[i].layoutParams=LinearLayout.LayoutParams(500,500)
                imViewList[i].setPadding(20,0,0,0)
                imViewList[i].setImageBitmap(BitmapArray[i])
            }
            return null
        }
    override fun onPostExecute(result: Void?){
        super.onPostExecute(result)
        for (i in 0..(size-1))
            imView.addView(imViewList[i])
    }
        private fun BitmaptoByteArray(Bitmap:Bitmap?) : ByteArray?{
            if(Bitmap == null)
                return null
            var tmpStream = ByteArrayOutputStream()
            Bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG,100,tmpStream)
            var ByteArray = tmpStream.toByteArray()
            return ByteArray
        }
        private fun ByteArraytoBitmap(data:ByteArray?) : Bitmap?{
            Log.e("###","in function data size : ${data!!.size}")
            var bitmap = BitmapFactory.decodeByteArray(data,0,data!!.size)
            return bitmap
        }
}