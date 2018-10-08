import Main.listener
import Main.synth
import com.mhfs.gui.*
import com.mhfs.gui.nodes.*
import com.mhfs.synth.Synthesizer
import com.mhfs.synth.WaveformGenerator
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.function.Consumer
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
    val contextMenu = JPopupMenu()
    contextMenu += createItem(content, "New Adder Node", ::AdderNode)
    contextMenu += createItem(content, "New Debug Node", ::DebugNode)
    contextMenu += createItem(content, "New Output Node") { OutputNode(Consumer {
        Main.generator = it
    })}

    contextMenu += JPopupMenu.Separator()

    contextMenu += createItem(content, "New Squarewave Node", ::SquarewaveGeneratorNode)
    contextMenu += createItem(content, "New Triangle Node", ::TriangleNode)
    contextMenu += createItem(content, "New Sine Node", ::SineNode)

    contextMenu += JPopupMenu.Separator()

    contextMenu += createItem(content, "New Frequency Node", ::FreqeuncyNode)
    contextMenu += createItem(content, "New Volume Node", ::VolumeNode)
    contextMenu += createItem(content, "New Hit Volume Control Node", ::HitVolumeControlNode)

    content.componentPopupMenu = contextMenu

    frame.contentPane = content
    frame.focusTraversalKeysEnabled = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
}

private fun createItem(content: LinkedTileContainer, text: String, action: () -> Node): JMenuItem {
    val item = JMenuItem(text)
    item.addActionListener {
        val addition = action()
        content.add(addition)
        content.revalidate()
        addition.repaint()
    }
    return item
}

operator fun Container.plusAssign(component: Component) {
    this.add(component)
}
