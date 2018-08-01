import Main.listener
import Main.synth
import com.mhfs.synth.Synthesizer
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.sound.sampled.AudioFormat
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider

object Main {
    const val bitDepth = 16
    const val samplesPerSecond = 44000f
    const val channels = 1
    const val signed = true
    const val bigEndian = false

    val synth = Synthesizer(AudioFormat(samplesPerSecond, bitDepth, channels, signed, bigEndian))
    val listener = object : KeyListener {

        private val keyState = HashMap<Int, Boolean>()

        override fun keyPressed(e: KeyEvent) {
            val specialHandler = SpecialKeys[e.extendedKeyCode]
            if (specialHandler != null && (keyState[e.extendedKeyCode] != true)) {
                keyState[e.extendedKeyCode] = true
                specialHandler.keyDown(synth)
                if (SpecialKeys.canRegister(e.extendedKeyCode))
                    synth.recorder.registerEvent(e)
            }
            val generator = Generators[e.extendedKeyCode]
            if (generator != null && (keyState[e.extendedKeyCode] != true)) {
                keyState[e.extendedKeyCode] = true
                synth.activate(generator)
                synth.recorder.registerEvent(e)
            }
        }

        override fun keyReleased(e: KeyEvent) {
            keyState[e.extendedKeyCode] = false
            Generators[e.extendedKeyCode]?.release(synth.getTimeStamp(), synth)
            SpecialKeys[e.extendedKeyCode]?.keyUp(synth)
            if (Generators[e.extendedKeyCode] != null || SpecialKeys.canRegister(e.extendedKeyCode))
                synth.recorder.registerEvent(e)
        }

        override fun keyTyped(e: KeyEvent?) {}
    }
}

fun main(args: Array<String>) {
    println("Keyboard synth.")

    synth.startup()
    Runtime.getRuntime().addShutdownHook(Thread(synth::shutdown))
    val frame = JFrame("Keyboard Synth")
    frame.size = Dimension(800, 600)

    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher {
        if (it.id == KeyEvent.KEY_PRESSED) {
            listener.keyPressed(it)
        } else if (it.id == KeyEvent.KEY_RELEASED) {
            listener.keyReleased(it)
        }
        return@addKeyEventDispatcher false
    }

    val content = JPanel()
    content.layout = FlowLayout()

    val slider = JSlider(0, 100, 100)
    slider.addChangeListener {
        synth.volumeMultiplier = slider.value.toDouble() / slider.maximum
    }
    slider.isFocusable = false
    content += slider

    frame.contentPane = content
    frame.focusTraversalKeysEnabled = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}

operator fun Container.plusAssign(component: Component) {
    this.add(component)
}
