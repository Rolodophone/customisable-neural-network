package com.rolodophone.customisableneuralnetwork

import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.warkiz.widget.IndicatorSeekBar
import koma.extensions.get
import koma.matrix.Matrix

class MainActivity : AppCompatActivity() {
    lateinit var neuralNetwork: NeuralNetwork

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = MySectionsPagerAdapter(this, supportFragmentManager)
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

        val spa = findViewById<ViewPager>(R.id.view_pager).adapter as MySectionsPagerAdapter
        if (spa.noOfTabs == 1) {
            spa.noOfTabs = 2
            spa.notifyDataSetChanged()
        }

        resetTable(findViewById(R.id.inputs), noOfInputs)
        resetTable(findViewById(R.id.outputs), noOfOutputs, true)
    }

    inner class DeleteEmptyRows(val row: TableRow) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        //delete rows that have been made blank
        override fun afterTextChanged(text: Editable) {
            val table = row.parent as ViewGroup

            val otherRow = (
                    if (table.id == R.id.inputs) findViewById<TableLayout>(R.id.outputs).getChildAt(table.indexOfChild(row))
                    else findViewById<TableLayout>(R.id.inputs).getChildAt(table.indexOfChild(row))
                    ) as TableRow


            val allBlank = (row.children + otherRow.children).all { view -> view !is EditText || view.text.isEmpty() }
            if (allBlank && table.childCount > 2) {
                table.removeView(row)

                //update #1, #2, etc
                for ((i, eachRow) in table.children.withIndex()) {
                    val rowTitle = (eachRow as ViewGroup).getChildAt(0)

                    if (rowTitle is TextView) rowTitle.text = "#$i"
                }


                val otherTable = otherRow.parent as ViewGroup
                otherTable.removeView(otherRow)

                //update #1, #2, etc
                for ((i, eachRow) in otherTable.children.withIndex()) {
                    val rowTitle = (eachRow as ViewGroup).getChildAt(0)

                    if (rowTitle is TextView) rowTitle.text = "#$i"
                }
            }
        }
    }

    inner class Filter0to1 : InputFilter {
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            //get the full new value
            val newValue = (dest.subSequence(0, dstart).toString() + source.subSequence(start, end).toString() + dest.subSequence(dstart, dest.length).toString()).toFloatOrNull()

            //if new value is valid return null else return ""
            if (newValue == null || newValue in 0.0..1.0) return null
            else {
                Toast.makeText(this@MainActivity, "Please enter a number between 0 and 1", Toast.LENGTH_SHORT).show()

                return ""
            }
        }
    }

    fun resetTable(table: TableLayout, columns: Int, isLastTable: Boolean = false, showTitle: Boolean = true, isTextView: Boolean = false) {
        table.removeAllViews()

        val newRow = TableRow(this)
        newRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )

        if (showTitle) {
            val newInputRowSpace = Space(this)
            newInputRowSpace.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )
            newRow.addView(newInputRowSpace)
        }

        for (i in 1..columns) {
            val newColumnTitle = TextView(this)
            newColumnTitle.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            newColumnTitle.gravity = Gravity.CENTER_HORIZONTAL
            newColumnTitle.text = "N$i"
            newRow.addView(newColumnTitle)
        }

        table.addView(newRow)
        addRow(table, columns, isLastTable, showTitle, isTextView)
    }

    fun addRow(table: TableLayout, columns: Int, isLastTable: Boolean = false, showTitle: Boolean = true, isTextView: Boolean = false) {

        val newRow = TableRow(this)
        newRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )

        if (showTitle) {
            val newRowTitle = TextView(this)
            newRowTitle.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            newRowTitle.text = "#${table.childCount}"
            newRow.addView(newRowTitle)
        }


        for (i in 1..columns) {
            if (isTextView) {
                val newTextView = TextView(this)
                val newLayoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
                newLayoutParams.topMargin = 16
                newTextView.layoutParams = newLayoutParams

                newTextView.gravity = Gravity.CENTER_HORIZONTAL
                //newTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                newRow.addView(newTextView)
            } else {
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
                newEditText.filters = arrayOf(Filter0to1())

                if (i == columns && isLastTable) newEditText.imeOptions = IME_ACTION_DONE
                else newEditText.imeOptions = IME_ACTION_NEXT

                if (i == 1) {
                    val prevRow = table.getChildAt(table.childCount - 1) as ViewGroup
                    val prevElement = prevRow.getChildAt(prevRow.childCount - 1)

                    if (prevElement is EditText) {
                        prevElement.imeOptions = IME_ACTION_NEXT
                        prevElement.nextFocusForwardId = newEditText.id
                    }
                } else newRow.getChildAt(newRow.childCount - 1).nextFocusForwardId = newEditText.id


                newRow.addView(newEditText)
            }
        }

        table.addView(newRow)
    }

    fun removeRow(table: TableLayout, isLastTable: Boolean = false) {
        if (table.childCount > 2) {
            table.removeViewAt(table.childCount - 1)

            val prevRow = table.getChildAt(table.childCount - 1) as ViewGroup
            val prevElement = prevRow.getChildAt(prevRow.childCount - 1)
            if (isLastTable && prevElement is EditText) prevElement.imeOptions = IME_ACTION_DONE
        }
    }

    fun onClickAddSet(view: View?) {
        addRow(findViewById(R.id.inputs), neuralNetwork.noInputs)
        addRow(findViewById(R.id.outputs), neuralNetwork.noOutputs, true)
    }

    fun onClickRemoveLastSet(view: View) {
        removeRow(findViewById(R.id.inputs))
        removeRow(findViewById(R.id.outputs), true)
    }

    fun onClickTrain(view: View) {
        val seekBarNumberOfTrainingIterations = findViewById<IndicatorSeekBar>(R.id.trainingIterations)
        val inputTable = findViewById<TableLayout>(R.id.inputs)
        val outputTable = findViewById<TableLayout>(R.id.outputs)

        var anyEmptyInput = false

        val trainingSetInputs = Matrix(inputTable.childCount - 1, neuralNetwork.noInputs) { row, column ->
            val editText = (inputTable.getChildAt(row + 1) as ViewGroup).getChildAt(column + 1) as EditText
            val content = editText.text.toString()
            if (content.isEmpty()) {
                anyEmptyInput = true
                0.0
            } else content.toDouble()
        }

        val trainingSetOutputs = Matrix(outputTable.childCount - 1, neuralNetwork.noOutputs) { row, column ->
            val editText = (outputTable.getChildAt(row + 1) as ViewGroup).getChildAt(column + 1) as EditText
            val content = editText.text.toString()
            if (content.isEmpty()) {
                anyEmptyInput = true
                0.0
            } else content.toDouble()
        }

        if (anyEmptyInput) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val numberOfTrainingIterations = resources.getStringArray(R.array.iterationsArray)[seekBarNumberOfTrainingIterations.progress].toInt()


        Log.i("NN", "Started training\nTraining set inputs:\n$trainingSetInputs\nTraining set outputs:\n$trainingSetOutputs")
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressBar.max = numberOfTrainingIterations

        Thread(Runnable {
            neuralNetwork.train(trainingSetInputs, trainingSetOutputs, numberOfTrainingIterations) { i -> progressBar.post { progressBar.progress = i } }

            runOnUiThread {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "Training completed", Toast.LENGTH_SHORT).show()

                val spa = findViewById<ViewPager>(R.id.view_pager).adapter as MySectionsPagerAdapter
                if (spa.noOfTabs == 2) {
                    spa.noOfTabs = 3
                    spa.notifyDataSetChanged()
                }
            }

            Log.i("NN", "Finished training")
        }).start()
    }

    fun onClickTest(view: View) {
        val inputTable = findViewById<TableLayout>(R.id.testInputs)
        val outputTable = findViewById<TableLayout>(R.id.testOutputs)
        val outputTableTitle = findViewById<TextView>(R.id.testOutputsTitle)

        var anyEmptyInput = false

        val inputs = Matrix(1, (inputTable.getChildAt(0) as ViewGroup).childCount) { row, column ->
            val editText = (inputTable.getChildAt(1) as ViewGroup).getChildAt(column) as EditText
            val content = editText.text.toString()
            if (content.isEmpty()) {
                anyEmptyInput = true
                0.0
            } else content.toDouble()
        }

        if (anyEmptyInput) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val outputs = neuralNetwork.think(inputs)

        (outputTable.getChildAt(1) as ViewGroup).children.forEachIndexed { i: Int, input: View -> (input as TextView).text = "%.2f".format(outputs[0, i]) }

        outputTableTitle.visibility = View.VISIBLE
        outputTable.visibility = View.VISIBLE
    }
}