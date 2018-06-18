class VolumeControl : WaveformGenerator {

    private lateinit var volumeFunction: WaveformGenerator
    private lateinit var waveformFunction: WaveformGenerator

    override fun generate(timeStamp: Double, dT: Double, resultLength: Int): DoubleArray {
        val volumeProfile = volumeFunction.generate(timeStamp, dT, resultLength)
        val waveformProfile = waveformFunction.generate(timeStamp, dT, resultLength)
        return DoubleArray(resultLength) { volumeProfile[it] * waveformProfile[it] }
    }

    override fun hit(timeStamp: Double, synth: Synthesizer) {
        volumeFunction.hit(timeStamp, synth)
        waveformFunction.hit(timeStamp, synth)
    }

    override fun release(timeStamp: Double, synth: Synthesizer) {
        volumeFunction.release(timeStamp, synth)
        waveformFunction.release(timeStamp, synth)
    }

    override fun update(timeStamp: Double, synth: Synthesizer): Boolean {
        return volumeFunction.update(timeStamp, synth) || waveformFunction.update(timeStamp, synth)
    }

    override fun link(linkType: String, generator: WaveformGenerator) {
        when {
            linkType.toLowerCase() == "volume" -> this.volumeFunction = generator
            linkType.toLowerCase() == "waveform" -> this.waveformFunction = generator
            else -> throw IllegalArgumentException("Invalid link type: '$linkType'")
        }
    }

    override fun validate(): Boolean {
        return ::waveformFunction.isInitialized && ::volumeFunction.isInitialized
    }
}