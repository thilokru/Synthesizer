package com.mhfs.synth

class HitVolumeControl : WaveformGenerator {

    lateinit var attackGenerator: WaveformGenerator
    lateinit var decayGenerator: WaveformGenerator
    lateinit var stopGenerator: WaveformGenerator
    lateinit var sustainGenerator: WaveformGenerator

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
        val decay = decayGenerator.generate(activation)
        val stop = stopGenerator.generate(activation)
        val sustain = sustainGenerator.generate(activation)
        return DoubleArray(samples) { getAmplitude(timeStamp + dT * it, activation, attack[it], decay[it], stop[it], sustain[it]) }
    }

    private fun getAmplitude(timeStamp: Double, activation: WaveformGenerator.Activation, attack: Double,
                             decay: Double, stop: Double, sustain: Double): Double {
        try {
            val lastDecayStart = activation.releaseTime
            val lastHitTime = activation.hitTime
            val tHit = if (lastHitTime > lastDecayStart) timeStamp - lastHitTime else lastDecayStart - lastHitTime

            var amplitude =
                    if (tHit < attack) {
                        1.0 - (tHit / attack - 1) * (tHit / attack - 1)
                    } else {
                        sustain + (1 - sustain) * Math.exp(-(tHit - attack) / decay)
                    }
            if (lastDecayStart > lastHitTime) {
                amplitude *= Math.exp(-(timeStamp - lastDecayStart) / stop)
                if (amplitude < 0.05) {
                    activation.associatedData["${this.hashCode()}.scheduledRelease"] = true
                }
            }
            return amplitude
        } catch (e: Exception) {
            e.printStackTrace()
            return 1.0
        }
    }
}

fun volumeControl(block: HitVolumeControl.() -> Unit) = HitVolumeControl().apply(block)

fun WaveformGenerator.volumeControl(link: String, block: HitVolumeControl.() -> Unit) = link(link, volumeControl(block))