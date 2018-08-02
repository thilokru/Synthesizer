package com.mhfs.synth

class VolumeControl : WaveformGenerator {

    private lateinit var volumeFunction: WaveformGenerator
    private lateinit var waveformFunction: WaveformGenerator

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val resultLength = activation.synth.getSamplesPerFrame()

        val volumeProfile = volumeFunction.generate(activation)
        val waveformProfile = waveformFunction.generate(activation)
        return DoubleArray(resultLength) { volumeProfile[it] * waveformProfile[it] }
    }

    override fun update(activation: WaveformGenerator.Activation): Boolean {
        return volumeFunction.update(activation) || waveformFunction.update(activation)
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        when {
            linkType.toLowerCase() == "volume" -> this.volumeFunction = generator
            linkType.toLowerCase() == "waveform" -> this.waveformFunction = generator
            else -> throw IllegalArgumentException("Invalid link type: '$linkType'")
        }
    }

    override fun validate(): Boolean {
        return ::waveformFunction.isInitialized && ::volumeFunction.isInitialized && waveformFunction.validate() && volumeFunction.validate()
    }
}