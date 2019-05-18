package com.mhfs.synth

class VolumeControl : WaveformGenerator {

    lateinit var volumeFunction: WaveformGenerator
    lateinit var waveformFunction: WaveformGenerator

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

fun volume(block: VolumeControl.() -> Unit) = VolumeControl().apply(block)

fun WaveformGenerator.volume(link: String, block: VolumeControl.() -> Unit) = link(link, volume(block))