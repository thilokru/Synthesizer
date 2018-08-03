package com.mhfs.gui

import com.mhfs.synth.SquarewaveGenerator
import com.mhfs.synth.WaveformGenerator

class SquarewaveGeneratorNode : Node("Squarewave") {

    init {
        createInput("frequency", "Frequency")
    }

    private val highTime = createNumberInput("High time:")

    override fun buildAndLink(): WaveformGenerator {
        val dutyCycle = (highTime.value as Double).toFloat()
        val generator = SquarewaveGenerator(dutyCycle)
        generator.link("frequency", this.getLink("frequency")!!.buildAndLink())
        return generator
    }
}