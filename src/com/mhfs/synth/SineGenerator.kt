package com.mhfs.synth

class SineGenerator : WaveformGenerator {

    private lateinit var frequencyFunction: WaveformGenerator
    private var lastHitTime: Double = 0.0

    override fun link(linkType: String, generator: WaveformGenerator) {
        if (linkType.toLowerCase() == "frequency") {
            this.frequencyFunction = generator
        } else {
            throw IllegalArgumentException("Unknown link type: '$linkType'")
        }
    }

    override fun validate() = this::frequencyFunction.isInitialized

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val frequencyProfile = frequencyFunction.generate(timeStamp, dT, resultLength)
        return DoubleArray(size = resultLength) {
            val phase = ((timeStamp + it * dT - lastHitTime) * frequencyProfile[it] * 2 * Math.PI) % (2 * Math.PI)
            return@DoubleArray Math.sin(phase)
        }
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.lastHitTime = timeStamp
        frequencyFunction.hit(timeStamp, synth)
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        frequencyFunction.release(timeStamp, synth)
    }

    override fun update(timeStamp: Double, synth: Synthesizer) = frequencyFunction.update(timeStamp, synth)
}