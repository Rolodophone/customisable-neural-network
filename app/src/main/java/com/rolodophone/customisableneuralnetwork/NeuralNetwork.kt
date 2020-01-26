package com.rolodophone.customisableneuralnetwork

import koma.exp
import koma.extensions.*
import koma.matrix.Matrix
import koma.rand
import koma.setSeed

// for some reason koma doesn't have an element-wise way to divide a number by a matrix so I made it myself
operator fun Double.div(other: Matrix<Double>) = other.map { 1.0 / it }

interface NonLinFunc {
    // the "squishification" function e.g. sigmoid, ReLU
    fun nonDeriv(matrix: Matrix<Double>): Matrix<Double>

    // the derivative of the "squishification function
    fun deriv(matrix: Matrix<Double>): Matrix<Double>
}


object Sigmoid : NonLinFunc {
    // the sigmoid function which takes any integer and smoothly converts it to an integer between 0 and 1
    override fun nonDeriv(matrix: Matrix<Double>) = 1.0 / (1 + exp(-matrix))

    // the derivative of the sigmoid function
    // this calculates the gradient of the sigmoid curve at a certain point
    // it indicates how confident we are about the existing weight
    // input: output of neuron
    // output: how confident the neuron was about its output
    // (emul and - are element-wise multiplication and subtraction)
    override fun deriv(matrix: Matrix<Double>) = matrix emul (1 - matrix)
}


class NeuralNetwork(
    val nonLinFunc: NonLinFunc,
    val noInputs: Int,
    val depth: Int = 1,
    val noNeuronsPerLayer: Int = noInputs,
    val noOutputs: Int
) {

    init {
        // seed the random number generator for reproducibility
        setSeed(53)

        //for now make sure depth and noNeuronsPerLayer are unchanged
        require(depth == 1) { "Depth must be 1" }
        require(depth == 1) { "noNeuronsPerLayer must be equal to noInputs" }
    }

    // generate initial synapse weights in a noInputs x noOutputs matrix with values from -1 to 1 (* and - are element-wise)
    var synapticWeights = 2 * rand(noInputs, noOutputs) - 1


    // pass the inputs through the neural network to see what it predicts
    // input: input values to pass through the matrix (each row is a set of inputs)
    // output: output values that the NN predicts (each row is a set of values)
    fun think(inputs: Matrix<Double>): Matrix<Double> {
        require(inputs.numCols() == noInputs) { "Number of columns in `inputs` must be equal to noInputs" }

        return nonLinFunc.nonDeriv(inputs * synapticWeights)
    }

    // train the neural network on a data set with known correct answers
    fun train(
        trainingSetInputs: Matrix<Double>,
        trainingSetOutputs: Matrix<Double>,
        numberOfTrainingIterations: Int
    ) {
        require(trainingSetInputs.numRows() == trainingSetOutputs.numRows()) { "Number of rows in `trainingSetInputs` must be equal to number of rows in `trainingSetOutputs`" }
        require(trainingSetInputs.numCols() == noInputs) { "Number of columns in `trainingSetInputs` must be equal to noInputs" }
        require(trainingSetOutputs.numCols() == noOutputs) { "Number of columns in `trainingSetOutputs` must be equal to noOutputs" }

        repeat(numberOfTrainingIterations) {
            // pass the training set through the neural network (guess the outputs based on the inputs)
            val output = think(trainingSetInputs)

            // calculate the matrix of errors (which is the difference between the desired output and the actual output)
            val error = trainingSetOutputs - output

            // multiply the error by input and gradient of sigmoid curve
            val adjustment = trainingSetInputs.T * (error emul nonLinFunc.deriv(output))

            synapticWeights += adjustment
        }
    }
}