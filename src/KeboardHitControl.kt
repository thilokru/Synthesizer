class KeyboardHitControl(private val waveformGenerator: WaveformGenerator, val hitTime: Float, val decayTime: Float, val stopTime: Float) : WaveformGenerator {

    constructor(waveformGenerator: WaveformGenerator) : this(waveformGenerator, 0.01f, 0.6f, 0.1f)

    private var lastHitTime: Double = 0.0
    private var hit = false
    private var lastDecayStart: Double = 0.0
    private lateinit var currentSynthesizer: Synthesizer


    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        if (!hit)
            return DoubleArray(resultLength, { _ -> 0.0 })
        return waveformGenerator.generate(timeStamp, dT, resultLength)
                .mapIndexed { i, d -> return@mapIndexed getAmplitude(timeStamp + dT * i) * d }
                .toDoubleArray()
    }

    /**
     * Tries to simulate the energy loss of a string due to air friction and sound.
     * Modelled by an Poisson-Distribution and exponential decay when the key is released
     */
    private fun getAmplitude(timeStamp: Double): Double {
        val delta =
                if (lastDecayStart > lastHitTime)
                    lastDecayStart - lastHitTime
                else
                    timeStamp - lastHitTime

        var amplitude = (1 - Math.exp(-delta / hitTime)) * Math.exp(-delta / decayTime)//Math.pow(delta, hitExponent)*Math.exp(-delta / decayTime) //Normalized
        if (lastDecayStart > lastHitTime) {
            amplitude *= Math.exp(-(timeStamp - lastDecayStart) / stopTime)
            if (amplitude < 0.05) {
                this.hit = false
                this.waveformGenerator.release(timeStamp, currentSynthesizer)
            }
        }
        return amplitude
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        this.hit = true
        lastHitTime = timeStamp
        currentSynthesizer = synth
        waveformGenerator.hit(timeStamp, synth)
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        lastDecayStart = timeStamp
    }

    override fun update(timeStamp: Double, synth: Synthesizer) {
        if (!hit) {
            synth.deactivate(this)

        }
    }
}