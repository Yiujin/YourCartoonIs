package com.e.yourcartoonis.Collage

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.e.yourcartoonis.R
import com.e.yourcartoonis.VideoTransfer
import kotlinx.android.synthetic.main.collage_001.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Collage_001.newInstance] factory method to
 * create an instance of this fragment.
 */
class Collage_001 : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.collage_001, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imList = (activity as VideoTransfer).getBitmap()
        val scroll = arrayListOf(scroll1,scroll2,scroll3)
        for (i in 0..2){
            var imView = ImageView(this.context)
            imView.layoutParams = LinearLayout.LayoutParams(1000, 1000)
            imView.setImageBitmap(Bitmap.createScaledBitmap(imList!![i], 1000, 1000, true))
            scroll[i].addView(imView)
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
            Collage_001().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}