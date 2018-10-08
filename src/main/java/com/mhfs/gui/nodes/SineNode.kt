package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.SineGenerator
import com.mhfs.synth.WaveformGenerator

class SineNode: Node("Sinus Generator") {

    init {
        createInput("frequency", "Frequency")
    }

    override fun buildAndLink(): WaveformGenerator {
        val gen = SineGenerator()
        gen.link("frequency", getLink("frequency")!!.buildAndLink())
        return gen
    }
}