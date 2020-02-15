package com.rolodophone.customisableneuralnetwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragTest(val ma: MainActivity) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_test, container, false)

        ma.resetTable(view.findViewById(R.id.testInputs), ma.neuralNetwork.noInputs, true, false)
        ma.resetTable(view.findViewById(R.id.testOutputs), ma.neuralNetwork.noOutputs, false, false, true)

        return view
    }
}