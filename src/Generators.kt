import java.awt.event.KeyEvent

object Generators {

    private val mapping: HashMap<Int, Int> = HashMap()
    private val tones: HashMap<Int, WaveformGenerator> = HashMap()
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
        generateGenerator(baseFrequency * Math.pow(hsc, -9.0), 40)//C1
        generateGenerator(baseFrequency * Math.pow(hsc, -8.0), 41)
        generateGenerator(baseFrequency * Math.pow(hsc, -7.0), 42)
        generateGenerator(baseFrequency * Math.pow(hsc, -6.0), 43)
        generateGenerator(baseFrequency * Math.pow(hsc, -5.0), 44)
        generateGenerator(baseFrequency * Math.pow(hsc, -4.0), 45)
        generateGenerator(baseFrequency * Math.pow(hsc, -3.0), 46)
        generateGenerator(baseFrequency * Math.pow(hsc, -2.0), 47)
        generateGenerator(baseFrequency / hsc, 48)
        generateGenerator(baseFrequency, 49)
        generateGenerator(baseFrequency * hsc, 50)
        generateGenerator(baseFrequency * Math.pow(hsc, 2.0), 51)//H1

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

    private fun generateGenerator(frequency: Double, noteID: Int) {
        for (i in -2..3) {
            tones[noteID + i * 12] = generateGuitarGenerators(frequency * Math.pow(2.0, i.toDouble()))
        }
    }

    private fun generateGuitarGenerators(frequency: Double): WaveformGenerator {
        return KeyboardHitControl(SpectrumGenerator(VibratoFrequencyFunction(0.0002, 0.1, 6.0, frequency), keyboardSpectrum), 0.02f, 2f, 0.1f)
    }

    private fun generateKeyboardGenerators(frequency: Double): WaveformGenerator {
        return KeyboardHitControl(KeyboardSpectrum(frequency))
    }

    private fun generateBeeper(frequency: Double): WaveformGenerator {
        return KeyboardHitControl(SquarewaveGenerator(VibratoFrequencyFunction(0.00005, 0.1, 6.0, frequency)), 0.01f, Float.POSITIVE_INFINITY, 0.1f)
    }

    fun getByNote(note: Int): WaveformGenerator? {
        return tones[note + transposition]
    }

    operator fun get(keyCode: Int): WaveformGenerator? {
        var noteID = mapping[keyCode]
        if (noteID != null) {
            noteID += transposition
            return tones[noteID]
        }
        return null
    }
}