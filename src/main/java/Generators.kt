import com.mhfs.synth.*
import java.awt.event.KeyEvent

object Generators {

    private val mapping: HashMap<Int, Int> = HashMap()
    private val tones: HashMap<Int, Double> = HashMap()
    private val hsc = Math.pow(2.0, 1 / 12.0)
    private const val baseFrequency = 442.0
    var transposition = 0

    private val keyboardSpectrum = HashMap<Int, Double>()

    init {
        for (i in 1..6) {
            keyboardSpectrum[i] = 1 / i.toDouble()
        }
    }

    init {
        setupFrequencies(baseFrequency * Math.pow(hsc, -9.0), 40)//C1
        setupFrequencies(baseFrequency * Math.pow(hsc, -8.0), 41)
        setupFrequencies(baseFrequency * Math.pow(hsc, -7.0), 42)
        setupFrequencies(baseFrequency * Math.pow(hsc, -6.0), 43)
        setupFrequencies(baseFrequency * Math.pow(hsc, -5.0), 44)
        setupFrequencies(baseFrequency * Math.pow(hsc, -4.0), 45)
        setupFrequencies(baseFrequency * Math.pow(hsc, -3.0), 46)
        setupFrequencies(baseFrequency * Math.pow(hsc, -2.0), 47)
        setupFrequencies(baseFrequency / hsc, 48)
        setupFrequencies(baseFrequency, 49)
        setupFrequencies(baseFrequency * hsc, 50)
        setupFrequencies(baseFrequency * Math.pow(hsc, 2.0), 51)//H1

        mapping[KeyEvent.VK_LESS] = 28 //C
        mapping[KeyEvent.VK_A] = 29 //CIS
        mapping[KeyEvent.VK_Y] = 30 //D
        mapping[KeyEvent.VK_S] = 31 //DIS
        mapping[KeyEvent.VK_X] = 32 //E
        mapping[KeyEvent.VK_C] = 33 //F
        mapping[KeyEvent.VK_F] = 34 //FIS
        mapping[KeyEvent.VK_V] = 35 //G
        mapping[KeyEvent.VK_G] = 36 //GIS
        mapping[KeyEvent.VK_B] = 37 //A
        mapping[KeyEvent.VK_H] = 38 //AIS
        mapping[KeyEvent.VK_N] = 39 //H
        mapping[KeyEvent.VK_M] = 40 //C

        mapping[KeyEvent.VK_TAB] = 39
        mapping[KeyEvent.VK_Q] = 40 //C
        mapping[KeyEvent.VK_2] = 41 //CIS
        mapping[KeyEvent.VK_W] = 42 //D
        mapping[KeyEvent.VK_3] = 43 //DIS
        mapping[KeyEvent.VK_E] = 44 //E
        mapping[KeyEvent.VK_R] = 45 //F
        mapping[KeyEvent.VK_5] = 46 //FIS
        mapping[KeyEvent.VK_T] = 47 //G
        mapping[KeyEvent.VK_6] = 48 //GIS
        mapping[KeyEvent.VK_Z] = 49 // A
        mapping[KeyEvent.VK_7] = 50 //AIS
        mapping[KeyEvent.VK_U] = 51 //H
        mapping[KeyEvent.VK_I] = 52 //C
        mapping[KeyEvent.VK_9] = 53 //CIS
        mapping[KeyEvent.VK_O] = 54 //D
        mapping[KeyEvent.VK_0] = 55 //DIS
        mapping[KeyEvent.VK_P] = 56 //E
        mapping[16777468] = 57 //F, The key is the german umlaut u
        mapping[KeyEvent.VK_DEAD_ACUTE] = 58 //FIS
        mapping[KeyEvent.VK_PLUS] = 59 //G
        mapping[KeyEvent.VK_BACK_SPACE] = 60 //GIS
        mapping[KeyEvent.VK_ENTER] = 61 //A
    }

    private fun setupFrequencies(frequency: Double, noteID: Int) {
        for (i in -2..3) {
            tones[noteID + i * 12] = frequency * Math.pow(2.0, i.toDouble())
        }
    }

    /*fun generateGuitarGenerators(): WaveformGenerator {
        val wave = AdderGenerator()
        keyboardSpectrum.forEach {
            val generator = SineGenerator()
            val frequency = VolumeControl()
            frequency.link("waveform", FrequencyReaderGenerator())
            frequency.link("volume", ConstantGenerator(it.key.toDouble()))
            generator.link("frequency", frequency)
            val const = ConstantGenerator(it.value)
            val controlled = VolumeControl()
            controlled.link("volume", const)
            controlled.link("waveform", generator)
            wave.link(it.key.toString(), controlled)
        }

        val volume = HitVolumeControl(0.02f, 2f, 0.1f, 0.0f)

        val mixer = VolumeControl()
        mixer.link("volume", volume)
        mixer.link("waveform", wave)

        return mixer
    }*/

    /*private fun generateKeyboardGenerators(frequency: Double): com.mhfs.synth.WaveformGenerator {
        return KeyboardHitControl(KeyboardSpectrum(frequency))
    }*/

    /*fun generateBeeper(): WaveformGenerator {
        val baseFrequency = FrequencyReaderGenerator()

        val square = SquarewaveGenerator(0.5f)
        square.link("frequency", buildVibrato(baseFrequency))

        val volume = HitVolumeControl(0.01f, 0.5f, 0.1f, 0.3f)

        val mixer = VolumeControl()
        mixer.link("volume", volume)
        mixer.link("waveform", square)

        return mixer
    }*/

    /*private fun buildVibrato(frequencyNode: WaveformGenerator, attackTime: Float = 0.5f, variationFrequency: Double = 6.0): WaveformGenerator {
        val variance = SineGenerator()
        variance.link("frequency", ConstantGenerator(variationFrequency))
        val vibratoIncrease = VolumeControl()
        vibratoIncrease.link("volume", HitVolumeControl(attackTime, Float.POSITIVE_INFINITY, 0f, 0f))
        vibratoIncrease.link("waveform", variance)
        val dampenedVibrato = VolumeControl()
        val vibratoIntensity = VolumeControl()
        vibratoIntensity.link("waveform", frequencyNode)
        vibratoIntensity.link("volume", ConstantGenerator(0.001 * (hsc - 1)))
        dampenedVibrato.link("volume", vibratoIntensity)
        dampenedVibrato.link("waveform", vibratoIncrease)
        val toggledVibrato = VolumeControl()
        toggledVibrato.link("volume", LogicNode { _ -> if (SpecialKeys.isVibrato) 1.0 else 0.0 })
        toggledVibrato.link("waveform", dampenedVibrato)
        val resultingFrequency = AdderGenerator()
        resultingFrequency.link("input1", frequencyNode)
        resultingFrequency.link("input2", toggledVibrato)
        return resultingFrequency
    }*/

    fun getByNote(note: Int) = tones[note + transposition]

    operator fun get(keyCode: Int): Double? {
        var noteID = mapping[keyCode]
        if (noteID != null) {
            noteID += transposition + if (SpecialKeys.increase) 12 else 0
            return tones[noteID]
        }
        return null
    }
}