package com.mhfs.gui

import com.mhfs.synth.SquarewaveGenerator
import com.mhfs.synth.WaveformGenerator
import javax.swing.JFormattedTextField

class SquarewaveGeneratorNode : Node("Squarewave") {

    init {
        createInput("frequency", "Frequency")
    }

    private val highTime = createNumberInput("High time:")

    override fun buildAndLink(): WaveformGenerator {
        val generator = SquarewaveGenerator(highTime.toFloat())
        generator.link("frequency", this.getLink("frequency")!!.buildAndLink())
        return generator
    }

    private fun JFormattedTextField.toFloat(): Float = (this.value as Number).toFloat()
}