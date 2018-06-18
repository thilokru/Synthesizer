class AdderGenerator : WaveformGenerator {

    private val generators = HashMap<String, WaveformGenerator>()

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val profiles = HashMap<String, DoubleArray>()
        generators.forEach {
            profiles[it.key] = it.value.generate(timeStamp, dT, resultLength)
        }
        return DoubleArray(resultLength) { index -> profiles.values.sumByDouble { profile -> profile[index] } }
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        generators.values.forEach {
            it.hit(timeStamp, synth)
        }
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        generators.values.forEach {
            it.release(timeStamp, synth)
        }
    }

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean {
        return generators.all { it.value.update(timeStamp, synth) }
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        generators[linkType] = generator
    }

    override fun validate() = true
}