package com.mhfs.synth

import java.io.Serializable

/**
 * Interface specifying generators for the synthesizer.
 * They should be stateless, as all data should be carried within the Activation object.
 */
interface WaveformGenerator: Serializable {

    /**
     * Important Note: Please generate all signals relative to the last hit time. Otherwise it will not work
     * with frequency shifting effects like vibrato.
     * DO NOT cache results. Following Generators are encouraged to rewrite array data and should avoid allocating new arrays.
     * @arg activation: Info about the current activation.
     * @return the amplitude of the signal at that time.
     */
    fun generate(activation: Activation): DoubleArray

    /**
     * Allows the generator to deactivate itself.
     * @return false if generator is done and can be ignored in the next cycle
     */
    fun update(activation: Activation): Boolean

    /**
     * Allows linkage to other generators as parameters
     * @arg linkType: A string indicating the generators usage.
     * @arg generator: The generator to be linked.
     */
    fun link(linkType: String, generator: WaveformGenerator)

    /**
     * @return true if all links are satisfied.
     */
    fun validate(): Boolean

    class Activation(val synth: Synthesizer, val noteFrequency: Double, val generator: WaveformGenerator) {
        val hitTime = synth.getTimeStamp()
        var vibratoActive = false
        var lastVibratoActivationTime = 0.0
        var releaseTime = 0.0
        val associatedData = HashMap<String, Any>()
    }
}