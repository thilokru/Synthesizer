package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.WaveformGenerator
import java.util.function.Consumer
import javax.swing.JButton


class OutputNode(private val updater: Consumer<WaveformGenerator>) : Node("Synthesizer Output", hasOutput = false) {

    init {
        createInput("input", "Audio Data")
        val buildButton = JButton("Build")
        buildButton.addActionListener {
            updater.accept(this.buildAndLink())
        }
        content.add(buildButton)
    }

    override fun buildAndLink(): WaveformGenerator {
        return this.getLink("input")!!.buildAndLink()
    }
}