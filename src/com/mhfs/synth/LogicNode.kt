package com.mhfs.synth

class LogicNode(val data: (time: Double) -> Double): WaveformGenerator {
    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val resultLength = activation.synth.getSamplesPerFrame()
        val timeStamp = activation.synth.getTimeStamp()
        val dT = activation.synth.getDT()
        return DoubleArray(resultLength) {data(timeStamp + it * dT)}
    }

    override fun update(activation: WaveformGenerator.Activation): Boolean = false

    override fun link(linkType: String, generator: WaveformGenerator) {
        throw RuntimeException("Not linkable!")
    }

    override fun validate(): Boolean = true
}