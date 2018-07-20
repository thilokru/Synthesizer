class DebugNode: WaveformGenerator {

    private lateinit var parentNode: WaveformGenerator

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val res = parentNode.generate(timeStamp, dT, resultLength)
        println("Debug node report: min=${res.min()}, max=${res.max()}, average=${res.average()}")
        return res
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        parentNode.hit(timeStamp, synth)
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        parentNode.release(timeStamp, synth)
    }

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean {
       return parentNode.update(timeStamp, synth)
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        if (linkType == "debug") {
            this.parentNode = generator
        } else {
            throw RuntimeException("Unknown link type '$linkType'.")
        }
    }

    override fun validate() = ::parentNode.isInitialized

}