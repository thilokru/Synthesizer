class KeyboardSpectrum(private val baseFrequency: Double) : WaveformGenerator {

    private var active = false
    private var lastHit = 0.0

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        if (!active)
            lastHit = timeStamp
        active = true
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        active = false
    }

    override fun update(timeStamp: Double, synth: Synthesizer) {}

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val result = DoubleArray(resultLength) { _ -> 0.0 }
        if (!active)
            return result
        for (freq in 1..10) {
            val amplitude = 1 / freq//Math.pow(Math.E, -Math.pow((freq.toDouble() - 1)/2, 2.0))
            for (index in result.indices) {
                val phase = (timeStamp + index * dT - lastHit) * 2 * Math.PI * baseFrequency
                result[index] += amplitude * Math.sin(phase * freq)
            }
        }
        return result
    }

}