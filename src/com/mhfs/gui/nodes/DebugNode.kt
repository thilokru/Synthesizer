package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.DebugGenerator
import com.mhfs.synth.WaveformGenerator

class DebugNode: Node("Debug") {

    init {
        createInput("debug", "Debug")
    }

    override fun buildAndLink(): WaveformGenerator {
        val debug = DebugGenerator()
        debug.link("debug", getLink("debug")!!.buildAndLink())
        return debug
    }
}