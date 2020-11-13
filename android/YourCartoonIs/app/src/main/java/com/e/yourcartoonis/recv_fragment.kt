package com.e.yourcartoonis

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.recv_fragment.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [recv_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class recv_fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recv_image : ArrayList<Bitmap> = ArrayList<Bitmap>()
    private var path : ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        recv_image = (activity as MakeCollage).getBitmap()!!
        path = (activity as MakeCollage).getPath()!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recv_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for( i in 0..recv_image.size-1){
            val im = ImageView(context)
            im.adjustViewBounds = true
            im.layoutParams= LinearLayout.LayoutParams(300, 300)
            im.scaleType = ImageView.ScaleType.FIT_XY
            im.setPadding(5,5,5,5)
            im.setBackgroundColor(Color.parseColor("#00000000"))
            im.isClickable = false
            im.setImageBitmap(recv_image[i])
            recv_img.addView(im)
            im.tag = path!![i]
            im.setOnLongClickListener { v: View ->
                val item = ClipData.Item(v.tag as CharSequence)
                val dragData = ClipData(
                    v.tag as CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val ShadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(dragData,ShadowBuilder,null,0)
            }
            im.setOnDragListener(DragListener(context!!))
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment recv_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            recv_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}