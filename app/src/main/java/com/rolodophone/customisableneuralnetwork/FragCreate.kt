package com.rolodophone.customisableneuralnetwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams

class FragCreate : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_create, container, false)

        val noOfInputs = view.findViewById<IndicatorSeekBar>(R.id.noOfInputs)
        val neuronsPerLayer = view.findViewById<IndicatorSeekBar>(R.id.neuronsPerLayer)
        noOfInputs.onSeekChangeListener = MyOnSeekChangeListener(neuronsPerLayer)

        return view
    }

    class MyOnSeekChangeListener(val linkedSeekBar: IndicatorSeekBar) : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams?) {
            if (seekParams != null) linkedSeekBar.setProgress(seekParams.progressFloat)
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
    }
}