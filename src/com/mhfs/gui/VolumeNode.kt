package com.mhfs.gui

import com.mhfs.synth.VolumeControl
import com.mhfs.synth.WaveformGenerator

class VolumeNode : Node("Volume") {

    private var generator: WaveformGenerator? = null

    init {
        createInput("waveform", "Signal")
        createInput("volume", "Volume")
    }

    override fun buildAndLink(): WaveformGenerator {
        if (generator == null) {
            generator = VolumeControl()
            generator!!.link("waveform", this.getLink("waveform")!!.buildAndLink())
            generator!!.link("volume", this.getLink("volume")!!.buildAndLink())
        }
        return generator!!
    }
}