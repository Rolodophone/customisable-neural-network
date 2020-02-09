package com.rolodophone.customisableneuralnetwork

import android.content.Context
import android.os.Bundle
import android.text.*
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
import koma.matrix.Matrix

class MainActivity : AppCompatActivity() {
    lateinit var neuralNetwork: NeuralNetwork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    fun onClickCreate(view: View) {
        val spinner = findViewById<Spinner>(R.id.spinner)
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

    class DeleteEmptyRows(val row: TableRow) : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        //delete rows that have been made blank
        override fun afterTextChanged(text: Editable) {
            val allBlank = row.children.all { view -> view !is EditText || view.text.isEmpty() }

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

    class Filter0to1(val context: Context) : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            //get the full new value
            val newValue = (dest.subSequence(0, dstart).toString() + source.subSequence(
                start,
                end
            ).toString() + dest.subSequence(dstart, dest.length).toString()).toFloatOrNull()

            //if new value is valid return null else return ""
            if (newValue == null || newValue in 0.0..1.0) return null
            else {
                //TODO replace the passed in context with this@whatever annotation
                Toast.makeText(context, "Must be in range 0-1!", Toast.LENGTH_SHORT).show()

                return ""
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
            newEditText.addTextChangedListener(DeleteEmptyRows(newRow))
            newEditText.filters = arrayOf(Filter0to1(this))
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
        val seekBarNumberOfTrainingIterations =
            findViewById<IndicatorSeekBar>(R.id.trainingIterations)
        val inputTable = findViewById<TableLayout>(R.id.inputs)
        val outputTable = findViewById<TableLayout>(R.id.outputs)

        val trainingSetInputs = Matrix(
            inputTable.childCount - 1,
            (inputTable.getChildAt(0) as ViewGroup).childCount - 1
        ) { row, column ->
            val editText =
                (inputTable.getChildAt(row + 1) as ViewGroup).getChildAt(column + 1) as EditText
            editText.text.toString().toDouble()
        }

        val trainingSetOutputs = Matrix(
            outputTable.childCount - 1,
            (outputTable.getChildAt(0) as ViewGroup).childCount - 1
        ) { row, column ->
            val editText =
                (outputTable.getChildAt(row + 1) as ViewGroup).getChildAt(column + 1) as EditText
            editText.text.toString().toDouble()
        }

        val numberOfTrainingIterations =
            resources.getStringArray(R.array.iterationsArray)[seekBarNumberOfTrainingIterations.progress].toInt()

        Log.i("NN", "Started training")
        neuralNetwork.train(trainingSetInputs, trainingSetOutputs, numberOfTrainingIterations)
        Log.i("NN", "Finished training")
    }

    fun onClickTest(view: View) {

    }
}