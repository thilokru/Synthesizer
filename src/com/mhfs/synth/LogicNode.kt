package com.mhfs.synth

import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator

class LogicNode(val data: (time: Double) -> Double): WaveformGenerator {
    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        return DoubleArray(resultLength) {data(timeStamp + it * dT)}
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {}

    override fun release(timeStamp: Double, synth: Synthesizer) {}

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean = false

    override fun link(linkType: String, generator: WaveformGenerator) {
        throw RuntimeException("Not linkable!")
    }

    override fun validate(): Boolean = true
}