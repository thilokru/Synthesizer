import Main.generator
import Main.listener
import Main.synth
import com.mhfs.dsl.*
import com.mhfs.gui.*
import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.*
import javax.sound.sampled.AudioFormat
import javax.swing.*

object Main {
    const val bitDepth = 16
    const val samplesPerSecond = 44000f
    const val channels = 1
    const val signed = true
    const val bigEndian = false

    val synth = Synthesizer(AudioFormat(samplesPerSecond, bitDepth, channels, signed, bigEndian))
    var generator: WaveformGenerator? = null
    val listener = object : KeyListener {

        private val keyState = HashMap<Int, WaveformGenerator.Activation>()

        override fun keyPressed(e: KeyEvent) {
            val specialHandler = SpecialKeys[e.extendedKeyCode]
            if (specialHandler != null && (keyState[e.extendedKeyCode] == null) && generator != null) {
                val activation = WaveformGenerator.Activation(synth, 0.0, generator!!) //Dummy
                keyState[e.extendedKeyCode] = activation
                specialHandler.keyDown(synth)
                if (SpecialKeys.canRegister(e.extendedKeyCode))
                    synth.recorder.registerEvent(e)
            }
            val frequency = Generators[e.extendedKeyCode]
            if (frequency != null && (keyState[e.extendedKeyCode] == null) && generator != null) {
                val activation = WaveformGenerator.Activation(synth, frequency, generator!!)
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
    frame.size = Dimension(400, 50)

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

    content += button("Create Instrument") {
        SwingUtilities.invokeLater {
            val dialog = InstrumentCreationDialog(frame) {
                generator = it
            }
            dialog.isVisible = true
        }
    }

    content += button("Save Instrument") {
        SwingUtilities.invokeLater {
            val dialog = JFileChooser()
            val state = dialog.showSaveDialog(frame)
            if (state == JFileChooser.APPROVE_OPTION) {
                val file = dialog.selectedFile
                val out = ObjectOutputStream(FileOutputStream(file))
                out.writeObject(generator)
            }
        }
    }

    content += button("Load Instrument") {
        SwingUtilities.invokeLater {
            val dialog = JFileChooser()
            val state = dialog.showOpenDialog(frame)
            if (state == JFileChooser.APPROVE_OPTION) {
                val file = dialog.selectedFile
                val input = ObjectInputStream(FileInputStream(file))
                generator = input.readObject() as WaveformGenerator
            }
        }
    }

    generator = volume {
        volumeFunction = volumeControl {
            attackGenerator = constant(0.02)
            decayGenerator = constant(3.0)
            stopGenerator = constant(0.1)
            sustainGenerator = constant(0.3)
        }
        waveformFunction = squarewave {
            frequencyFunction = frequency()
            highTimeGenerator = constant(0.5)
        }
    }

    frame.contentPane = content
    frame.focusTraversalKeysEnabled = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}

fun button(title: String, actionListener: () -> Unit): Button {
    val ret = Button(title)
    ret.addActionListener{ actionListener() }
    return ret
}

operator fun Container.plusAssign(component: Component) {
    this.add(component)
}
