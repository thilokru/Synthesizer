package com.mhfs.synth

class SquarewaveGenerator(private val highTime: Float) : WaveformGenerator {

    private lateinit var frequencyFunction: WaveformGenerator

    override fun link(linkType: String, generator: WaveformGenerator) {
        if (linkType.toLowerCase() == "frequency") {
            this.frequencyFunction = generator
        } else {
            throw IllegalArgumentException("Unknown link type: '$linkType'")
        }
    }

    override fun validate() = this::frequencyFunction.isInitialized

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val timeStamp = activation.synth.getTimeStamp()
        val dT = activation.synth.getDT()
        val resultLength = activation.synth.getSamplesPerFrame()

        val frequencyProfile = frequencyFunction.generate(activation)
        return DoubleArray(size = resultLength) {
            val phase = ((timeStamp + it * dT - activation.hitTime) * frequencyProfile[it] * 2 * Math.PI) % (2 * Math.PI)
            if (phase < Math.PI * (highTime / 0.5))
                return@DoubleArray 1.0
            else
                return@DoubleArray -1.0
        }
    }

    override fun update(activation: WaveformGenerator.Activation) = frequencyFunction.update(activation)
}