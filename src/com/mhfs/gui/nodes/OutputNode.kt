package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator
import javax.swing.JButton


class OutputNode(private val synthesizer: Synthesizer) : Node("Synthesizer Output", hasOutput = false) {

    init {
        createInput("input", "Audio Data")
        val buildButton = JButton("Build")
        buildButton.addActionListener {
            synthesizer.changeGenerator(this.buildAndLink())
        }
        this.add(buildButton)
    }

    override fun buildAndLink(): WaveformGenerator {
        return this.getLink("input")!!.buildAndLink()
    }
}