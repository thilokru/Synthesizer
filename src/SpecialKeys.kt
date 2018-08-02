import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator
import java.awt.event.KeyEvent

object SpecialKeys {

    val handlers = HashMap<Int, Handler>()
    private var isMoll = false
    private var isSept = false
    var increase = false
        private set
    internal var isVibrato = false

    init {
        handlers[KeyEvent.VK_INSERT] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {
                Generators.transposition++
                println("Transposition: " + Generators.transposition)
            }

            override fun keyUp(synthesizer: Synthesizer) {}
        }
        handlers[KeyEvent.VK_DELETE] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {
                Generators.transposition--
                println("Transposition: " + Generators.transposition)
            }

            override fun keyUp(synthesizer: Synthesizer) {}
        }
        handlers[KeyEvent.VK_NUMPAD1] = Chord(28)
        handlers[KeyEvent.VK_NUMPAD2] = Chord(30)
        handlers[KeyEvent.VK_NUMPAD3] = Chord(32)
        handlers[KeyEvent.VK_NUMPAD4] = Chord(33)
        handlers[KeyEvent.VK_NUMPAD5] = Chord(35)
        handlers[KeyEvent.VK_NUMPAD6] = Chord(37)
        handlers[KeyEvent.VK_NUMPAD7] = Chord(39)

        handlers[KeyEvent.VK_SUBTRACT] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {
                isMoll = true
            }

            override fun keyUp(synthesizer: Synthesizer) {
                isMoll = false
            }

            override fun canRegister(): Boolean {
                return true
            }
        }
        handlers[KeyEvent.VK_MULTIPLY] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {
                isSept = true
            }

            override fun keyUp(synthesizer: Synthesizer) {
                isSept = false
            }

            override fun canRegister(): Boolean {
                return true
            }
        }
        handlers[KeyEvent.VK_SPACE] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {
                isVibrato = true
            }

            override fun keyUp(synthesizer: Synthesizer) {
                isVibrato = false
            }

            override fun canRegister(): Boolean {
                return true
            }
        }
        handlers[KeyEvent.VK_F10] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {}

            override fun keyUp(synthesizer: Synthesizer) {
                synthesizer.recorder.waitingForTrigger = true
            }
        }
        handlers[KeyEvent.VK_F11] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {}

            override fun keyUp(synthesizer: Synthesizer) {
                synthesizer.recorder.recording = !synthesizer.recorder.recording
            }
        }
        handlers[KeyEvent.VK_F12] = object : Handler {
            override fun keyDown(synthesizer: Synthesizer) {}

            override fun keyUp(synthesizer: Synthesizer) {
                synthesizer.recorder.recording = false
                synthesizer.recorder.playback = !synthesizer.recorder.playback
            }
        }
        handlers[KeyEvent.VK_CAPS_LOCK] = object : Handler {
            override fun keyUp(synthesizer: Synthesizer) {
                increase = !increase
                println(increase)
            }

            override fun keyDown(synthesizer: Synthesizer) {}
        }
    }

    operator fun get(extendedKeyCode: Int): Handler? = handlers[extendedKeyCode]

    fun canRegister(extendedKeyCode: Int): Boolean {
        return handlers[extendedKeyCode]?.canRegister() ?: false
    }

    interface Handler {
        fun keyDown(synthesizer: Synthesizer)
        fun keyUp(synthesizer: Synthesizer)
        fun canRegister(): Boolean {
            return false
        }
    }

    class Chord(private val base: Int) : Handler {

        private val activations = HashSet<WaveformGenerator.Activation>()

        override fun keyDown(synthesizer: Synthesizer) {
            activate(base, synthesizer)
            if (isMoll)
                activate(base + 3, synthesizer)
            else
                activate(base + 4, synthesizer)

            activate(base + 7, synthesizer)

            if (isSept)
                activate(base + 10, synthesizer)
        }

        private fun activate(note: Int, synthesizer: Synthesizer) {
            val freq = Generators.getByNote(note) ?: 0.0
            val activation = WaveformGenerator.Activation(synthesizer, freq)
            synthesizer.activate(activation)
            activations += activation
        }

        override fun keyUp(synthesizer: Synthesizer) {
            activations.forEach { it.releaseTime = it.synth.getTimeStamp() }
        }

        override fun canRegister(): Boolean {
            return true
        }
    }
}