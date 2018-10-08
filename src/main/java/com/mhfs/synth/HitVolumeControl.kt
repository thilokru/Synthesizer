package com.mhfs.synth

class HitVolumeControl: WaveformGenerator {

    private lateinit var attackGenerator: WaveformGenerator
    private lateinit var decayGenerator: WaveformGenerator
    private lateinit var stopGenerator: WaveformGenerator
    private lateinit var sustainGenerator: WaveformGenerator

    override fun update(activation: WaveformGenerator.Activation): Boolean {
        val tmp = activation.associatedData["${hashCode()}.scheduledRelease"]
        if (tmp != null && tmp as Boolean) {
            return true
        }
        return false
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        when (linkType) {
            "attack" -> attackGenerator = generator
            "decay" -> decayGenerator = generator
            "stop" -> stopGenerator = generator
            "sustain" -> sustainGenerator = generator
            else -> throw RuntimeException("Unknown link '$linkType'")
        }
    }

    override fun validate() = ::attackGenerator.isInitialized && ::decayGenerator.isInitialized
            && ::stopGenerator.isInitialized && ::sustainGenerator.isInitialized

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val samples = activation.synth.getSamplesPerFrame()
        val timeStamp = activation.synth.getTimeStamp()
        val dT = activation.synth.getDT()
        val attack = attackGenerator.generate(activation)
        val decay = attackGenerator.generate(activation)
        val stop = attackGenerator.generate(activation)
        val sustain = attackGenerator.generate(activation)
        return DoubleArray(samples) { getAmplitude(timeStamp + dT * it, activation, attack[it], decay[it], stop[it], sustain[it]) }
    }

    private fun getAmplitude(timeStamp: Double, activation: WaveformGenerator.Activation, attack: Double,
                             decay: Double, stop: Double, sustain: Double): Double {
        val lastDecayStart = activation.releaseTime
        val lastHitTime = activation.hitTime
        val delta =
                if (lastDecayStart > lastHitTime)
                    lastDecayStart - lastHitTime
                else
                    timeStamp - lastHitTime

        var amplitude = (1 - Math.exp(-delta / attack)) * (sustain + (1 - sustain) * Math.exp(-delta / decay))
        if (lastDecayStart > lastHitTime) {
            amplitude *= Math.exp(-(timeStamp - lastDecayStart) / stop)
            if (amplitude < 0.05) {
                activation.associatedData["${this.hashCode()}.scheduledRelease"] = true
            }
        }
        return amplitude
    }
}