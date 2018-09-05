package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.VolumeControl
import com.mhfs.synth.WaveformGenerator

class VolumeNode : Node("Volume") {

    init {
        createInput("waveform", "Signal")
        createInput("volume", "Volume")
    }

    override fun buildAndLink(): WaveformGenerator {
        val generator = VolumeControl()
        generator.link("waveform", this.getLink("waveform")!!.buildAndLink())
        generator.link("volume", this.getLink("volume")!!.buildAndLink())
        return generator
    }
}