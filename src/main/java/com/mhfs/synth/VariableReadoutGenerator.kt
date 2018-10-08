package com.mhfs.synth

class VariableReadoutGenerator(val varName: String): WaveformGenerator {
    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        return DoubleArray(activation.synth.getSamplesPerFrame()) { activation.associatedData[varName] as Double}
    }

    override fun update(activation: WaveformGenerator.Activation) = true

    override fun link(linkType: String, generator: WaveformGenerator) = throw RuntimeException("No links available.")

    override fun validate() = true
}