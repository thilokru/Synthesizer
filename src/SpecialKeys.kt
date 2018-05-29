import java.awt.event.KeyEvent

object SpecialKeys {

    val handlers = HashMap<Int, Handler>()
    private var isMoll = false
    private var isSept = false
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
        private var wasMoll = false
        private var wasSept = false

        override fun keyDown(synthesizer: Synthesizer) {
            Generators.getByNote(base)?.let { synthesizer.activate(it) }
            wasMoll = isMoll
            wasSept = isSept
            if (wasMoll)
                Generators.getByNote(base + 3)?.let { synthesizer.activate(it) }
            else
                Generators.getByNote(base + 4)?.let { synthesizer.activate(it) }
            Generators.getByNote(base + 7)?.let { synthesizer.activate(it) }
            if (wasSept)
                Generators.getByNote(base + 10)?.let { synthesizer.activate(it) }
        }

        override fun keyUp(synthesizer: Synthesizer) {
            Generators.getByNote(base)?.release(synthesizer.getTimeStamp(), synthesizer)
            if (wasMoll)
                Generators.getByNote(base + 3)?.release(synthesizer.getTimeStamp(), synthesizer)
            else
                Generators.getByNote(base + 4)?.release(synthesizer.getTimeStamp(), synthesizer)
            Generators.getByNote(base + 7)?.release(synthesizer.getTimeStamp(), synthesizer)
            if (wasSept)
                Generators.getByNote(base + 10)?.release(synthesizer.getTimeStamp(), synthesizer)
        }

        override fun canRegister(): Boolean {
            return true
        }
    }
}