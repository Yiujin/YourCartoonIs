package com.e.yourcartoonis

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class YoloClassifier(context : Context, modelName:String, labelName:String,kinds :String = "tiny") {
    private lateinit var context : Context
    private lateinit var modelName : String
    private lateinit var labelName : String
    private lateinit var interpreter : Interpreter
    private lateinit var label_dict : HashMap<Int,String>
    private lateinit var imageData : ByteBuffer
    private var OUT_SIZE =2535
    private val INPUT_SIZE = 416
    private val CROP_SIZE = 416
    private val input_channel = 3
    private val IMAGE_MENA = 0
    private val IMAGE_STD = 255.0f
    private val NUM_THREAD = 4
    private val isGPU = true
    private val BATCH_SIZE = 1
    private val PIXEL_SIZE = 3
    private var threshold = 0.5

    init{
        this.context = context
        this.modelName = modelName
        this.labelName = labelName
        this.label_dict = loadLabel(labelName)
        try{
            val options = Interpreter.Options()
            options.setNumThreads(NUM_THREAD)
            if(isGPU){
                val Gpudelegate = GpuDelegate()
                options.addDelegate(Gpudelegate)
            }
            this.interpreter = Interpreter(loadModel(modelName),options)
        }catch(e:Exception){
            throw RuntimeException(e)
        }
        if(kinds == "tiny")
            OUT_SIZE = 2535
        else
            OUT_SIZE = 10647
        imageData = ByteBuffer.allocateDirect(1*INPUT_SIZE*INPUT_SIZE*3*4)
        imageData.order(ByteOrder.nativeOrder())
    }
    private fun loadModel(model_name:String) : MappedByteBuffer {
        val fileDes: AssetFileDescriptor = context.assets.openFd(model_name)
        val inputstream: FileInputStream = FileInputStream(fileDes.fileDescriptor)
        val channel = inputstream.channel
        val startOffset : Long = fileDes.startOffset
        val declaredLength: Long = fileDes.declaredLength
        return channel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength)
    }
    private fun loadLabel(label_file : String) : HashMap<Int,String> {
        val asset_manager = context.assets
        val reader = asset_manager!!.open(label_file)
        val label_name = arrayListOf<String>()
        val label_dict = HashMap<Int,String>()
        reader!!.bufferedReader().use { it.forEachLine { label_name.add(it) } }
        for(i in 0..label_name.size-1)
            label_dict.put(i,label_name[i])
        return label_dict
    }
    private fun BitmapToByteBuffer(bitmap:Bitmap) : ByteBuffer{
        val bytebuffer = ByteBuffer.allocateDirect(4 * BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        bytebuffer.order(ByteOrder.nativeOrder())
        val value = IntArray(INPUT_SIZE*INPUT_SIZE)
        bitmap.getPixels(value,0,bitmap.width,0,0,bitmap.width,bitmap.height)
        var pixel = 0
        for(i in 0..INPUT_SIZE-1){
            for(j in 0..INPUT_SIZE-1){
                val pixelvalue = value[pixel++]
                bytebuffer.putFloat((pixelvalue shr 16 and 0xFF) / 255.0f)
                bytebuffer.putFloat((pixelvalue shr 8 and 0xFF) / 255.0f)
                bytebuffer.putFloat((pixelvalue and 0xFF) / 255.0f)
            }
        }
        return bytebuffer
    }
    private fun preprocessing(mat : Mat) : Bitmap{
        val croppedBitmap = createBitmap(CROP_SIZE,CROP_SIZE,Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat,croppedBitmap)
        return croppedBitmap
    }
    public fun detection(mat : Mat) : ArrayList<String> {
        var returnClass = ArrayList<String>()
        var bitmap = preprocessing(mat)
        var bytebuffer = BitmapToByteBuffer(bitmap)
        var boxes = Array<Array<FloatArray>>(1,{i->Array<FloatArray>(OUT_SIZE,{i -> FloatArray(4)})})
        var scores = Array<Array<FloatArray>>(1,{i->Array<FloatArray>(OUT_SIZE,{i -> FloatArray(80)})})
        val imageprocessor = ImageProcessor.Builder().add(
            ResizeOp(416,416,
                ResizeOp.ResizeMethod.BILINEAR)
        ).build()
        var inputs = arrayOf(bytebuffer)
        var output : HashMap<Int,Any> = HashMap()
        var classes : MutableList<Int> = MutableList(0,{i->0})
        output.put(0,boxes)
        output.put(1,scores)

        //Log.e("###","label_dict size : ${label_dict.size}")
        /*
        Log.e("###","input tensor 1 size : ${interpreter.getInputTensor(0).shape().size}")
        for ( i in 0..interpreter.getInputTensor(0).shape().size-1)
            Log.e("###","${interpreter.getInputTensor(0).shape()[i]}")

        Log.e("###","buffer size : ${inputs.size}")

        Log.e("###","tensor 1 size : ${tflite.getOutputTensor(0).shape().size}")
        for ( i in 0..tflite.getOutputTensor(0).shape().size-1)
            Log.e("###","${tflite.getOutputTensor(0).shape()[i]}")
        Log.e("###","tensor 2 size : ${tflite.getOutputTensor(1).shape().size}")
        for ( i in 0..2)
            Log.e("###","${tflite.getOutputTensor(1).shape()[i]}")
        Log.e("###","in inference")
        Log.e("###","in : ${tflite.getInputTensor(0).shape()} out: ${tflite.getOutputTensor(0).shape()}")
        Log.e("###",": ${tflite.getOutputTensor(1).shape()}")
        Log.e("###","channel : ${input.get(0,1).size}")
        Log.e("###","image size : ${input.width()} , ${input.height()}")
         */
        interpreter.runForMultipleInputsOutputs(inputs,output)
        //tflite.runForMultipleInputsOutputs(inputs,output)
        //Log.e("###","${boxes.size}")
        //Log.e("###", "${scores[0].size}")
        for( i in 0..scores[0].size-1) {
            var maximum : Float = 0.toFloat()
            var detectedClass = -1
            for(j in 0..label_dict.size-1) {
                if (scores[0][i][j] > maximum) {
                    detectedClass = j
                    maximum = scores[0][i][j]
                }
            }
            if(maximum > threshold)
                classes.add(detectedClass)
        }
        val result = classes.distinct()
        Log.e("###","classes size : ${result.size}")
        for (i in 0..result.size-1) {
            returnClass.add(label_dict[result[i]]!!)
            Log.e("###", "classes : ${label_dict[result[i]]}")
        }
        return returnClass
    }
}