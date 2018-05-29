import java.util.function.DoubleFunction

class SquarewaveGenerator(private val frequency: DoubleFunction<Double>) : WaveformGenerator {

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        return DoubleArray(size = resultLength) {
            if (!active)
                0.0
            val phase = ((timeStamp + it * dT) * frequency.apply(timeStamp + it * dT) * 2 * Math.PI) % (2 * Math.PI)
            if (phase > Math.PI)
                1.0
            else
                -1.0
        }
    }

    private var active = false

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.active = true
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        this.active = false
    }

    override fun update(timeStamp: Double, synth: Synthesizer) {}
}