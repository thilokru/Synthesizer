package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.AdderGenerator
import com.mhfs.synth.WaveformGenerator

class AdderNode: Node("Adder") {

    init {
        createInput("input1", "Signal 1")
        createInput("input2", "Signal 2")
    }

    override fun buildAndLink(): WaveformGenerator {
        val sig1 = getLink("input1")!!.buildAndLink()
        val sig2 = getLink("input2")!!.buildAndLink()
        val gen = AdderGenerator()
        gen.link("input1", sig1)
        gen.link("input2", sig2)
        return gen
    }
}