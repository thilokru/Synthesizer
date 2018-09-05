package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.TriangleGenerator
import com.mhfs.synth.WaveformGenerator

class TriangleNode: Node("Triangle Generator") {

    init {
        createInput("frequency", "Frequency")
    }

    override fun buildAndLink(): WaveformGenerator {
        val gen = TriangleGenerator()
        gen.link("frequency", getLink("frequency")!!.buildAndLink())
        return gen
    }
}