package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.FrequencyReaderGenerator

class FreqeuncyNode : Node("Frequency") {
    companion object {
        private val generator = FrequencyReaderGenerator()
    }

    override fun buildAndLink() = generator
}