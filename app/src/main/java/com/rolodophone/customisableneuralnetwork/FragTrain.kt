package com.rolodophone.customisableneuralnetwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragTrain(val ma: MainActivity) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_train, container, false)

        ma.resetTable(view.findViewById(R.id.inputs), ma.neuralNetwork.noInputs)
        ma.resetTable(view.findViewById(R.id.outputs), ma.neuralNetwork.noOutputs, true)

        return view
    }
}