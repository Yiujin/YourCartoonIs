package com.e.yourcartoonis.Collage

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.GestureDetectorCompat
import com.e.yourcartoonis.*
import kotlinx.android.synthetic.main.collage.*
import kotlinx.android.synthetic.main.collage_3_001.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Collage_001.newInstance] factory method to
 * create an instance of this fragment.
 */
class Collage_3_001 : CollageSuper() {
    // TODO: Rename and change types of parameters
    var im : ArrayList<ScrollView>? = null
    var frameList : ArrayList<View>? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.collage_3_001, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imList = (activity as MakeCollage).getBitmap()
        frameList = arrayListOf(c1,c2,c3)
        im = arrayListOf(scroll1,scroll2,scroll3)
        for (i in 0..2){
            im!![i].setOnDragListener(DragListener(context!!))
        }
    }
    override fun setScrollTouch() {
        for (i in 0..2) {
            im!![i].setOnTouchListener { view, motionEvent ->
                Log.e("###","click")
                for(j in 0..2){
                    if(i==j)
                        continue
                    frameList!![j].visibility = View.INVISIBLE
                }
                im!![i].setOnDragListener(null)
                frameList!![i].setOnDragListener(DragListenerSticker(context!!))
                (activity as MakeCollage).startSticker(i)
                frameList!![i].animate().scaleX(2.5f).scaleY(2.5f).setDuration(50).start()
                true
            }
        }
    }
    override fun goLayout() {
        for(i in 0..2){
            im!![i].setOnTouchListener(null)
        }
    }
    override fun stickerDone(id:Int) {
        im!![id].setOnDragListener(DragListener(context!!))
        for(i in 0..2){
            if(i==id)
                frameList!![id].animate().scaleX(1f).scaleY(1f).setDuration(50).start()
            frameList!![i].visibility = View.VISIBLE
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Collage_001.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Collage_3_001().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}