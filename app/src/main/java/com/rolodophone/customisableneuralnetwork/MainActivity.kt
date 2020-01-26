package com.rolodophone.customisableneuralnetwork

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams

class MainActivity : AppCompatActivity() {
    lateinit var neuralNetwork: NeuralNetwork

    class MyOnSeekChangeListener(val linkedSeekBar: IndicatorSeekBar) : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams?) {
            if (seekParams != null) linkedSeekBar.setProgress(seekParams.progressFloat)
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // activity_main
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    fun onClickCreate(view: View) {
        val spinner: Spinner = findViewById(R.id.spinner)
        val noOfInputs = findViewById<IndicatorSeekBar>(R.id.noOfInputs).progress
        val depth = findViewById<IndicatorSeekBar>(R.id.depth).progress
        val neuronsPerLayer = findViewById<IndicatorSeekBar>(R.id.neuronsPerLayer).progress
        val noOfOutputs = findViewById<IndicatorSeekBar>(R.id.noOfOutputs).progress

        val nonLinFunc = when (spinner.selectedItem) {
            resources.getStringArray(R.array.nonLinFunArray)[0] -> Sigmoid

            //impossible
            else -> Sigmoid
        }

        neuralNetwork = NeuralNetwork(nonLinFunc, noOfInputs, depth, neuronsPerLayer, noOfOutputs)
        Log.i(
            "NN",
            "Created neural network with initial synaptic weights of ${neuralNetwork.synapticWeights}"
        )

        //prepare input table

        val inputTable: TableLayout = findViewById(R.id.inputs)
        inputTable.removeAllViews()

        val newInputRow = TableRow(this)
        newInputRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )

        val newInputRowSpace = Space(this)
        newInputRowSpace.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        newInputRow.addView(newInputRowSpace)

        for (i in 1..noOfInputs) {
            val newColumnTitle = TextView(this)
            newColumnTitle.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            newColumnTitle.gravity = Gravity.CENTER_HORIZONTAL
            newColumnTitle.text = "N$i"
            newInputRow.addView(newColumnTitle)
        }

        inputTable.addView(newInputRow)

        onClickAddInput(null)

        //prepare output table

        val outputTable: TableLayout = findViewById(R.id.outputs)
        outputTable.removeAllViews()

        val newOutputRow = TableRow(this)
        newOutputRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )

        val newOutputRowSpace = Space(this)
        newOutputRowSpace.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        newOutputRow.addView(newOutputRowSpace)

        for (i in 1..noOfOutputs) {
            val newColumnTitle = TextView(this)
            newColumnTitle.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            newColumnTitle.gravity = Gravity.CENTER_HORIZONTAL
            newColumnTitle.text = "N$i"
            newOutputRow.addView(newColumnTitle)
        }

        outputTable.addView(newOutputRow)

        onClickAddOutput(null)
    }

    class MyTextWatcher(val row: TableRow) : TextWatcher {

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable) {
            var allBlank = true
            for (view in row.children) {
                if (view is EditText && view.text.isNotEmpty()) {
                    allBlank = false
                    break
                }
            }

            val table = row.parent as ViewGroup
            if (allBlank && table.childCount > 2) {
                table.removeView(row)

                //update #1, #2, etc
                for ((i, eachRow) in table.children.withIndex()) {
                    val rowTitle = (eachRow as ViewGroup).getChildAt(0)

                    if (rowTitle is TextView) rowTitle.text = "#$i"
                }
            }
        }
    }

    fun addRow(table: TableLayout, columns: Int) {
        val newRowIndex = table.childCount.toString()

        val newRow = TableRow(this)
        newRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )

        val newRowTitle = TextView(this)
        newRowTitle.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        newRowTitle.text = "#$newRowIndex"
        newRow.addView(newRowTitle)

        for (i in 1..columns) {
            val newEditText = EditText(this)
            newEditText.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            newEditText.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            newEditText.textAlignment = EditText.TEXT_ALIGNMENT_CENTER
            newEditText.addTextChangedListener(MyTextWatcher(newRow))
            newRow.addView(newEditText)
        }

        table.addView(newRow)
    }

    fun onClickAddInput(view: View?) {
        addRow(findViewById(R.id.inputs), neuralNetwork.noInputs)
    }

    fun removeRow(table: TableLayout) {
        if (table.childCount > 2) table.removeViewAt(table.childCount - 1)
    }

    fun onClickRemoveInput(view: View) {
        removeRow(findViewById(R.id.inputs))
    }

    fun onClickAddOutput(view: View?) {
        addRow(findViewById(R.id.outputs), neuralNetwork.noOutputs)
    }

    fun onClickRemoveOutput(view: View) {
        removeRow(findViewById(R.id.outputs))
    }

    fun onClickTrain(view: View) {
        val seekBarNumberOfTrainingIterations: IndicatorSeekBar =
            findViewById(R.id.trainingIterations)

        val numberOfTrainingIterations =
            resources.getStringArray(R.array.iterationsArray)[seekBarNumberOfTrainingIterations.progress].toInt()

        //neuralNetwork.train(, , numberOfTrainingIterations)
    }

    fun onClickTest(view: View) {

    }
}