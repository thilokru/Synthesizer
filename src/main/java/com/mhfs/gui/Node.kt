package com.mhfs.gui

import com.mhfs.synth.ConstantGenerator
import com.mhfs.synth.WaveformGenerator
import java.awt.*
import java.text.NumberFormat
import java.util.function.Consumer
import javax.swing.*
import javax.swing.border.BevelBorder
import javax.swing.plaf.basic.BasicBorders

abstract class Node(title: String, hasOutput: Boolean = true) : JPanel() {

    private val links = HashMap<String, Node?>()
    private val fieldVariables = HashMap<String, JFormattedTextField>()
    private val terminals: MutableList<LinkTerminal> = ArrayList()

    protected val content = JPanel()

    init {
        this.size = Dimension(200, 250)
        this.layout = BorderLayout()
        val headerPanel = JPanel()
        headerPanel.layout = BorderLayout()

        val label = JLabel(title)
        headerPanel.add(label, BorderLayout.CENTER)

        val removeButton = JButton("X")
        removeButton.addActionListener {
            terminals.forEach {
                it.disconnect()
            }
            val formerParent = this.parent
            this.parent.remove(this)
            formerParent.repaint()
        }
        headerPanel.add(removeButton, BorderLayout.EAST)
        headerPanel.size.height = removeButton.preferredSize.height
        this.add(headerPanel, BorderLayout.NORTH)

        content.layout = BoxLayout(content, BoxLayout.PAGE_AXIS)
        content.border = BorderFactory.createLoweredSoftBevelBorder()
        this.add(content, BorderLayout.CENTER)
        if (hasOutput) createOutput()
    }

    fun createInput(connectionID: String, connectionName: String) {
        content.add(createInput0(connectionID, connectionName))
    }

    private fun createInput0(connectionID: String, connectionName: String): JPanel {
        val node = JPanel()
        node.layout = FlowLayout(FlowLayout.LEADING)
        val terminal = LinkTerminal(LinkChangeCallbackImpl(this, connectionID), this)
        node.add(terminal)
        node.add(JLabel(connectionName))
        this.terminals += terminal
        return node
    }

    private fun createOutput() {
        val node = JPanel()
        node.layout = FlowLayout(FlowLayout.TRAILING)
        //The Callback should never be called.
        node.add(JLabel("Output"))
        val terminal = LinkTerminal(LinkChangeCallbackImpl(this, "output"), this, isOutput = true)
        node.add(terminal)
        content.add(node)
        this.terminals += terminal
    }

    fun createNumberInput(connectionID: String, title: String) {
        val panel = JPanel()
        val value = JFormattedTextField(NumberFormat.getNumberInstance())
        fieldVariables[connectionID] = value
        val link = createInput0(connectionID, title)
        value.value = 0.0
        value.columns = 5
        panel.layout = FlowLayout(FlowLayout.LEADING)
        panel.add(link)
        panel.add(value)
        content.add(panel)
    }

    fun createTextInput(title: String): JTextField {
        val panel = JPanel()
        val value = JTextField()
        value.minimumSize.width = 100
        panel.layout = FlowLayout(FlowLayout.LEADING)
        panel.add(JLabel(title))
        panel.add(value)
        content.add(panel)
        return value
    }

    protected fun getLink(connectionID: String): Node? {
        var node = links[connectionID]
        if (node == null) {
            val field = fieldVariables[connectionID]
            if (field != null) {
                node = object: Node("Dummy") {
                    override fun buildAndLink() = ConstantGenerator((field.value as Number).toDouble())
                }
            }
        }
        return node
    }

    abstract fun buildAndLink(): WaveformGenerator

    private class LinkChangeCallbackImpl(private val owner: Node, private val connectionID: String) : Consumer<Node?> {
        override fun accept(t: Node?) {
            owner.links[connectionID] = t
        }
    }
}