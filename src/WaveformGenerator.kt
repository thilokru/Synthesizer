interface WaveformGenerator {

    /**
     * @arg timeStamp: The current time in seconds
     * @return the amplitude of the signal at that time.
     */
    fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray

    /**
     * Activates the generator, called when added to the synthesizer
     */
    fun hit(timeStamp: Double, synth: Synthesizer)

    /**
     * Deactivates the generator, called when the key was released
     */
    fun release(timeStamp: Double, synth: Synthesizer)

    /**
     * Allows the generator to deactivate itself.
     * @return false if generator is done and can be ignored in the next cycle
     */
    fun update(timeStamp: Double, synth: Synthesizer): Boolean

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
}