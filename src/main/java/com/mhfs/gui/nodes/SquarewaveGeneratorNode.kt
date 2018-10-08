package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.SquarewaveGenerator
import com.mhfs.synth.WaveformGenerator
import javax.swing.JFormattedTextField

class SquarewaveGeneratorNode : Node("Squarewave") {

    init {
        createInput("frequency", "Frequency")
        createNumberInput("highTime", "High time:")
    }

    override fun buildAndLink(): WaveformGenerator {
        val generator = SquarewaveGenerator()
        generator.link("highTime", this.getLink("highTime")!!.buildAndLink())
        generator.link("frequency", this.getLink("frequency")!!.buildAndLink())
        return generator
    }
}