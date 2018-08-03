package com.mhfs.gui

import com.mhfs.synth.FrequencyReaderGenerator

class FreqeuncyNode : Node("Frequency") {
    companion object {
        private val generator = FrequencyReaderGenerator()
    }

    override fun buildAndLink() = generator
}