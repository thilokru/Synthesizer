package com.mhfs.gui

import com.mhfs.synth.HitVolumeControl
import javax.swing.JFormattedTextField

class HitVolumeControlNode : Node("Hit Volume") {
    private val attackTime = createNumberInput("Attack time:")
    private val decayTime = createNumberInput("Decay time:")
    private val stopTime = createNumberInput("Stop time:")
    private val sustain = createNumberInput("Sustain level:")

    override fun buildAndLink() = HitVolumeControl(attackTime.toFloat(), decayTime.toFloat(), stopTime.toFloat(), sustain.toFloat())

    private fun JFormattedTextField.toFloat(): Float = (this.value as Number).toFloat()
}