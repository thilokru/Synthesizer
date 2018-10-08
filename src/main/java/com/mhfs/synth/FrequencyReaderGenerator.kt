package com.mhfs.synth

class FrequencyReaderGenerator : WaveformGenerator {
    override fun generate(activation: WaveformGenerator.Activation) = DoubleArray(activation.synth.getSamplesPerFrame()) { activation.noteFrequency }

    override fun update(activation: WaveformGenerator.Activation): Boolean = false

    override fun link(linkType: String, generator: WaveformGenerator) {
        throw RuntimeException("FrequencyReader cannot be linked!")
    }

    override fun validate() = true
}