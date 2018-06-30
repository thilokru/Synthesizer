class AdditionGenerator: WaveformGenerator {

    private lateinit var input1: WaveformGenerator
    private lateinit var input2: WaveformGenerator
    private var hitTime = 0.0
    private var hit = false

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val profile1 = input1.generate(timeStamp, dT, resultLength)
        val profile2 = input2.generate(timeStamp, dT, resultLength)
        return DoubleArray(resultLength) {
            profile1[it] + profile2[it]
        }
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        input1.hit(timeStamp, synth)
        input2.hit(timeStamp, synth)
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        input1.release(timeStamp, synth)
        input2.release(timeStamp, synth)
    }

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean {
        return input1.update(timeStamp, synth) && input2.update(timeStamp, synth)
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        when (linkType) {
            "input1" -> input1 = generator
            "input2" -> input2 = generator
            else -> throw RuntimeException("Unknown link type: '$linkType")
        }
    }

    override fun validate() = ::input1.isInitialized && ::input2.isInitialized && input1.validate() && input2.validate()
}