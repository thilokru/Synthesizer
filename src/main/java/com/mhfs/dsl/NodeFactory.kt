package com.mhfs.dsl

import com.mhfs.synth.*

fun add(block: AdderGenerator.() -> Unit) = AdderGenerator().apply(block)

fun WaveformGenerator.add(link: String, block: AdderGenerator.() -> Unit)
        = link(link, add(block))

fun constant(value: Double) = ConstantGenerator(value)

fun WaveformGenerator.constant(link: String, value: Double)
        = link(link, constant(value))

fun debug(block: DebugGenerator.() -> Unit) = DebugGenerator().apply(block)

fun WaveformGenerator.debug(link: String, block: DebugGenerator.() -> Unit)
        = link(link, debug(block))

fun frequency() = FrequencyReaderGenerator()

fun WaveformGenerator.frequency(link: String)
        = link(link, frequency())

fun volumeControl(block: HitVolumeControl.() -> Unit) = HitVolumeControl().apply(block)

fun WaveformGenerator.volumeControl(link: String, block: HitVolumeControl.() -> Unit)
        = link(link, volumeControl(block))

fun sine(block: SineGenerator.() -> Unit) = SineGenerator().apply(block)

fun WaveformGenerator.sine(link: String, block: SineGenerator.() -> Unit)
        = link(link, sine(block))

fun squarewave(block: SquarewaveGenerator.() -> Unit) = SquarewaveGenerator().apply(block)

fun WaveformGenerator.squarewave(link: String, block: SquarewaveGenerator.() -> Unit)
        = link(link, squarewave(block))

fun triangle(block: TriangleGenerator.() -> Unit) = TriangleGenerator().apply(block)

fun WaveformGenerator.triangle(link: String, block: TriangleGenerator.() -> Unit)
        = link(link, triangle(block))

fun volume(block: VolumeControl.() -> Unit) = VolumeControl().apply(block)

fun WaveformGenerator.volume(link: String, block: VolumeControl.() -> Unit)
        = link(link, volume(block))