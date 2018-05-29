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
     */
    fun update(timeStamp: Double, synth: Synthesizer)
}