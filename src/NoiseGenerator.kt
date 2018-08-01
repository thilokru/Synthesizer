import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator
import java.lang.Math.*

class NoiseGenerator(val sequenceLength: Int): WaveformGenerator { //rec: Math.pow(2.0, 12.0).toInt()

    private val random = DoubleArray(sequenceLength){ random() * 2 - 1}
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
            val phase = ((timeStamp + it * dT) * frequencyProfile[it] * 2 * PI) % (2 * PI)
            val index = (sequenceLength * (phase % (2 * PI)) / (2 * PI)).toInt()
            random[index]
        }
    }

    private var active = false

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.active = true
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        this.active = false
    }

    override fun update(timeStamp: Double, synth: Synthesizer) = false
}