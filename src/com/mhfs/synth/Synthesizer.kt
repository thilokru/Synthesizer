package com.mhfs.synth

import java.nio.ByteBuffer
import java.util.*
import javax.sound.sampled.*
import kotlin.concurrent.thread


class Synthesizer(private val format: AudioFormat, private val generator: WaveformGenerator, private val reactionTime: Float) {

    private val audioOut: SourceDataLine
    private val currentActivations = ArrayList<WaveformGenerator.Activation>()
    private var shutdown = false
    private var timestamp = 0

    var recorder = LoopRecorder()

    init {
        audioOut = getAudioLine()
        val bufferSize = (format.sampleRate * reactionTime * (format.sampleSizeInBits / 8)).toInt()
        audioOut.open(format, bufferSize)
        audioOut.start()
    }

    private fun getAudioLine() = AudioSystem.getSourceDataLine(format)

    constructor(format: AudioFormat, generator: WaveformGenerator) : this(format, generator, 0.02f)

    fun startup() = thread(isDaemon = true) {
        while (!shutdown) {
            writeWaveform()
        }
    }


    private fun writeWaveform() {
        val startTime = System.nanoTime()
        val activations: List<WaveformGenerator.Activation> = currentActivations.toList()
        val frameData = DoubleArray(getSamplesPerFrame()) { _ -> 0.0 }

        recorder.update()
        recorder.trigger()
        activations.forEach {
            val samples = generator.generate(it)
            samples.forEachIndexed { i, d -> frameData[i] += d * Short.MAX_VALUE / 3 }
            if (generator.update(it)) {
                currentActivations.remove(it)
            }
        }

        val byteBuf = ByteBuffer.allocate((getSamplesPerFrame() * (format.sampleSizeInBits / 8)))
        frameData.forEach {
            val sample = it.toInt()
            byteBuf.put(sample.toByte())
            byteBuf.put((sample.shr(8)).toByte())
        }
        timestamp += getSamplesPerFrame()
        val diff = (System.nanoTime() - startTime) * 1E-6
        if (diff * 1E-3 > reactionTime)
            println("Warning: Generating took longer than expected. $diff ms")
        audioOut.write(byteBuf.array(), 0, byteBuf.capacity())
    }

    fun activate(activation: WaveformGenerator.Activation) = synchronized(currentActivations) {
        currentActivations += activation
    }

    fun shutdown() {
        shutdown = true
        Thread.sleep(100)
        audioOut.close()
    }

    fun getTimeStamp() = timestamp / format.sampleRate.toDouble()

    fun getDT() = 1 / format.sampleRate.toDouble()

    fun getSamplesPerFrame() = (format.sampleRate * reactionTime).toInt()
}