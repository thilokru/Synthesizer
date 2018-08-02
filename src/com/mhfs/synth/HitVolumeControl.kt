package com.mhfs.synth

class HitVolumeControl(val attack: Float, val decayTime: Float, val stopTime: Float, val sustain: Float) : WaveformGenerator {

    private var scheduledRelease = false

    override fun update(activation: WaveformGenerator.Activation): Boolean {
        val tmp = activation.associatedData["${hashCode()}.scheduledRelease"]
        if (tmp != null && tmp as Boolean) {
            return true
        }
        return false
    }

    override fun link(linkType: String, generator: WaveformGenerator) {} //Do nothing, as no inputs are required

    override fun validate(): Boolean {
        return true
    }

    override fun generate(activation: WaveformGenerator.Activation): DoubleArray {
        val samples = activation.synth.getSamplesPerFrame()
        val timeStamp = activation.synth.getTimeStamp()
        val dT = activation.synth.getDT()
        return DoubleArray(samples) { getAmplitude(timeStamp + dT * it, activation) }
    }

    private fun getAmplitude(timeStamp: Double, activation: WaveformGenerator.Activation): Double {
        val lastDecayStart = activation.releaseTime
        val lastHitTime = activation.hitTime
        val delta =
                if (lastDecayStart > lastHitTime)
                    lastDecayStart - lastHitTime
                else
                    timeStamp - lastHitTime

        var amplitude = (1 - Math.exp(-delta / attack)) * (sustain + (1 - sustain) * Math.exp(-delta / decayTime))
        if (lastDecayStart > lastHitTime) {
            amplitude *= Math.exp(-(timeStamp - lastDecayStart) / stopTime)
            if (amplitude < 0.05) {
                activation.associatedData["${this.hashCode()}.scheduledRelease"] = true
            }
        }
        return amplitude
    }
}