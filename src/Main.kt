import Main.listener
import Main.synth
import com.mhfs.gui.LinkTerminal
import com.mhfs.gui.LinkedTileContainer
import com.mhfs.gui.OutputNode
import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.sound.sampled.AudioFormat
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.border.Border

object Main {
    const val bitDepth = 16
    const val samplesPerSecond = 44000f
    const val channels = 1
    const val signed = true
    const val bigEndian = false

    val synth = Synthesizer(AudioFormat(samplesPerSecond, bitDepth, channels, signed, bigEndian), Generators.generateBeeper())
    val listener = object : KeyListener {

        private val keyState = HashMap<Int, WaveformGenerator.Activation>()

        override fun keyPressed(e: KeyEvent) {
            val specialHandler = SpecialKeys[e.extendedKeyCode]
            if (specialHandler != null && (keyState[e.extendedKeyCode] == null)) {
                val activation = WaveformGenerator.Activation(synth, 0.0) //Dummy
                keyState[e.extendedKeyCode] = activation
                specialHandler.keyDown(synth)
                if (SpecialKeys.canRegister(e.extendedKeyCode))
                    synth.recorder.registerEvent(e)
            }
            val frequency = Generators[e.extendedKeyCode]
            if (frequency != null && (keyState[e.extendedKeyCode] == null)) {
                val activation = WaveformGenerator.Activation(synth, frequency)
                keyState[e.extendedKeyCode] = activation
                synth.activate(activation)
                synth.recorder.registerEvent(e)
            }
        }

        override fun keyReleased(e: KeyEvent) {
            val activation = keyState[e.extendedKeyCode]
            if (activation != null) {
                activation.releaseTime = activation.synth.getTimeStamp()
            }
            keyState.remove(e.extendedKeyCode)
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

    val content = LinkedTileContainer()

    val tile = JPanel()
    tile.setBounds(100, 100, 150, 200)
    tile.layout = BorderLayout()

    val slider = JSlider(0, 100, 100)
    slider.addChangeListener {
        //synth.volumeMultiplier = slider.value.toDouble() / slider.maximum
    }
    slider.isFocusable = false
    tile.add(slider, BorderLayout.SOUTH)
    content += tile

    content += OutputNode()

    frame.contentPane = content
    frame.focusTraversalKeysEnabled = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}

operator fun Container.plusAssign(component: Component) {
    this.add(component)
}
