package com.mhfs.synth

import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator

class HitVolumeControl(val attack: Float, val decayTime: Float, val stopTime: Float, val sustain: Float) : WaveformGenerator {

    private var lastHitTime: Double = 0.0
    private var hit = false
    private var lastDecayStart: Double = 0.0
    private var scheduledRelease = false

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.hit = true
        lastHitTime = timeStamp
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        lastDecayStart = timeStamp
    }

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean {
        if (scheduledRelease) {
            scheduledRelease = false
            return true
        }
        return false
    }

    override fun link(linkType: String, generator: WaveformGenerator) {} //Do nothing, as no inputs are required

    override fun validate(): Boolean {
        return true
    }

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        return DoubleArray(resultLength) { getAmplitude(timeStamp + dT * it) }
    }

    private fun getAmplitude(timeStamp: Double): Double {
        val delta =
                if (lastDecayStart > lastHitTime)
                    lastDecayStart - lastHitTime
                else
                    timeStamp - lastHitTime

        var amplitude = (1 - Math.exp(-delta / attack)) * (sustain + (1 - sustain) * Math.exp(-delta / decayTime))
        if (lastDecayStart > lastHitTime) {
            amplitude *= Math.exp(-(timeStamp - lastDecayStart) / stopTime)
            if (amplitude < 0.05) {
                this.hit = false
                this.scheduledRelease = true
            }
        }
        return amplitude
    }
}