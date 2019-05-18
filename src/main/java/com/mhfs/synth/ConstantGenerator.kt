package com.mhfs.synth

class ConstantGenerator(private val value: Double) : WaveformGenerator {
    override fun generate(activation: WaveformGenerator.Activation) = DoubleArray(activation.synth.getSamplesPerFrame()) { value }

    override fun update(activation: WaveformGenerator.Activation): Boolean = false

    override fun link(linkType: String, generator: WaveformGenerator) {}

    override fun validate() = true
}

fun constant(value: Double) = ConstantGenerator(value)

fun WaveformGenerator.constant(link: String, value: Double) = link(link, constant(value))