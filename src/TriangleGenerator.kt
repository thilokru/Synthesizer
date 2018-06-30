class TriangleGenerator() : WaveformGenerator {

    private lateinit var frequencyFunction: WaveformGenerator

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
            if (!active)
                0.0
            val phase = ((timeStamp + it * dT) * frequencyProfile[it] * 2 * Math.PI) % (2 * Math.PI)
            Math.abs(2*(Math.PI-phase)/Math.PI) - 1
        }
    }

    private var active = false

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.active = true
        frequencyFunction.hit(timeStamp, synth)
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        this.active = false
        frequencyFunction.release(timeStamp, synth)
    }

    override fun update(timeStamp: Double, synth: Synthesizer) = frequencyFunction.update(timeStamp, synth)
}