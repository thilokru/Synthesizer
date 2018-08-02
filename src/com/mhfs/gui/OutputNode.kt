package com.mhfs.gui

import com.mhfs.synth.WaveformGenerator


class OutputNode : Node("Synthesizer Output", hasOutput = false) {

    init {
        createInput("input", "Audio Data")
    }

    override fun buildAndLink(): WaveformGenerator {
        return this.getLink("input")!!.buildAndLink()
    }
}