package com.mhfs.synth

class ConstantGenerator(private val value: Double) : WaveformGenerator {
    override fun generate(timeStamp: Double, dT: Double, resultLength: Int) = DoubleArray(resultLength) { value }

    override fun hit(timeStamp: Double, synth: Synthesizer) {}

    override fun release(timeStamp: Double, synth: Synthesizer) {}

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean = false

    override fun link(linkType: String, generator: WaveformGenerator) {}

    override fun validate() = true
}