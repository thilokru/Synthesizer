import java.util.function.DoubleFunction

class SpectrumGenerator(private val baseFrequency: DoubleFunction<Double>, private val amplitudes: Map<Int, Double>) : WaveformGenerator {

    private var active = false
    private var lastHit = 0.0

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val result = DoubleArray(resultLength) { _ -> 0.0 }
        if (!active)
            return result
        for ((overtone, amplitude) in amplitudes) {
            for (index in result.indices) {
                val baseTime = (timeStamp + index * dT - lastHit)
                val phase = baseTime * 2 * Math.PI * baseFrequency.apply(baseTime) * overtone
                result[index] += amplitude * Math.sin(phase)
            }
        }
        return result
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.lastHit = timeStamp
        this.active = true
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        this.active = false
    }

    override fun update(timeStamp: Double, synth: Synthesizer) {}
}