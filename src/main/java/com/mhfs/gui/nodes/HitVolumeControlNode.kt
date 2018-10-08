package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.HitVolumeControl
import com.mhfs.synth.WaveformGenerator
import javax.swing.JFormattedTextField

class HitVolumeControlNode : Node("Hit Volume") {
    private val attackTime = createNumberInput("attack", "Attack time:")
    private val decayTime = createNumberInput("decay", "Decay time:")
    private val stopTime = createNumberInput("stop", "Stop time:")
    private val sustain = createNumberInput("sustain","Sustain level:")

    override fun buildAndLink(): WaveformGenerator {
        val generator = HitVolumeControl()
        generator.link("attack", this.getLink("attack")!!.buildAndLink())
        generator.link("decay", this.getLink("decay")!!.buildAndLink())
        generator.link("stop", this.getLink("stop")!!.buildAndLink())
        generator.link("sustain", this.getLink("sustain")!!.buildAndLink())
        return generator
    }
}