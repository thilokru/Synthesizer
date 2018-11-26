package com.mhfs.gui

import com.mhfs.gui.nodes.*
import com.mhfs.synth.WaveformGenerator
import plusAssign
import java.awt.Dimension
import java.awt.Frame
import java.util.function.Consumer
import javax.swing.JDialog
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class InstrumentCreationDialog(parent: Frame, onSuccess: (WaveformGenerator) -> Unit) : JDialog(parent, true) {

    private val content = LinkedTileContainer()

    init {
        this.size = Dimension(800, 600)
        val contextMenu = JPopupMenu()
        contextMenu += createItem(content, "New Adder Node", ::AdderNode)
        contextMenu += createItem(content, "New Debug Node", ::DebugNode)
        contextMenu += createItem(content, "New Output Node") {
            OutputNode(Consumer {
                onSuccess.invoke(it)
                this.dispose()
            })
        }

        contextMenu += JPopupMenu.Separator()

        contextMenu += createItem(content, "New Squarewave Node", ::SquarewaveGeneratorNode)
        contextMenu += createItem(content, "New Triangle Node", ::TriangleNode)
        contextMenu += createItem(content, "New Sine Node", ::SineNode)

        contextMenu += JPopupMenu.Separator()

        contextMenu += createItem(content, "New Frequency Node", ::FreqeuncyNode)
        contextMenu += createItem(content, "New Variable Node", ::VariableNode)
        contextMenu += createItem(content, "New Volume Node", ::VolumeNode)
        contextMenu += createItem(content, "New Hit Volume Control Node", ::HitVolumeControlNode)

        content.componentPopupMenu = contextMenu
        this.contentPane = content
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
}