package com.mhfs.synth

import java.nio.ByteBuffer
import javax.sound.sampled.*
import kotlin.concurrent.thread


class Synthesizer(private val format: AudioFormat, private val reactionTime: Float, private val amplitude: Int) {

    private val audioOut: SourceDataLine
    private val activeGenerators = ArrayList<WaveformGenerator>()
    private var shutdown = false
    private var timestamp = 0
    var volumeMultiplier = 1.0

    var recorder = LoopRecorder()

    init {
        audioOut = getAudioLine()
        val bufferSize = (format.sampleRate * reactionTime * (format.sampleSizeInBits / 8)).toInt()
        audioOut.open(format, bufferSize)
        audioOut.start()
    }

    private fun getAudioLine() = AudioSystem.getSourceDataLine(format)

    constructor(format: AudioFormat) : this(format, 0.02f, Short.MAX_VALUE / 6)

    fun startup() = thread(isDaemon = true) {
        while (!shutdown) {
            writeWaveform()
        }
    }


    private fun writeWaveform() {
        val startTime = System.nanoTime()

        val sampleCount = (format.sampleRate * reactionTime).toInt()
        val startStamp = getTimeStamp()
        val dT = 1.0 / format.sampleRate

        val generators: List<WaveformGenerator> = activeGenerators.toList()
        val frameData = DoubleArray(sampleCount) { _ -> 0.0 }

        recorder.update()
        recorder.trigger()
        generators.forEach {
            val samples = it.generate(startStamp, dT, sampleCount)
            samples.forEachIndexed { i, d -> frameData[i] += d * amplitude * volumeMultiplier }
            if (it.update(getTimeStamp(), this)) {
                activeGenerators.remove(it)
            }
        }

        val byteBuf = ByteBuffer.allocate((sampleCount * (format.sampleSizeInBits / 8)))
        frameData.forEach {
            val sample = it.toInt()
            byteBuf.put(sample.toByte())
            byteBuf.put((sample.shr(8)).toByte())
        }
        timestamp += sampleCount
        val diff = (System.nanoTime() - startTime) * 1E-6
        if (diff * 1E-3 > reactionTime)
            println("Warning: Generating took longer than expected. $diff ms")
        audioOut.write(byteBuf.array(), 0, byteBuf.capacity())
    }

    fun activate(gen: WaveformGenerator) {
        gen.hit(getTimeStamp(), this)
        synchronized(activeGenerators) {
            if (!activeGenerators.contains(gen))
                activeGenerators += gen
        }
    }

    fun shutdown() {
        shutdown = true
        Thread.sleep(100)
        audioOut.close()
    }

    fun getTimeStamp(): Double {
        return (timestamp / format.sampleRate.toDouble())
    }
}