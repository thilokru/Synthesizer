package com.mhfs.gui

import com.mhfs.synth.ConstantGenerator
import com.mhfs.synth.WaveformGenerator
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import java.text.NumberFormat
import java.util.function.Consumer
import javax.swing.*

abstract class Node(title: String, hasOutput: Boolean = true) : JPanel() {

    private val links = HashMap<String, Node?>()
    private val fieldVariables = HashMap<String, JFormattedTextField>()
    private val terminals: MutableList<LinkTerminal> = ArrayList()

    init {
        this.layout = GridLayout(0, 1)
        this.size = Dimension(200, 250)
        val headerPanel = JPanel()
        headerPanel.layout = BorderLayout()
        headerPanel.add(JLabel(title), BorderLayout.CENTER)
        val removeButton = JButton("X")
        removeButton.addActionListener {
            terminals.forEach {
                it.disconnect()
            }
            this.parent.remove(this)
        }
        headerPanel.add(removeButton, BorderLayout.EAST)
        this.add(headerPanel)
        if (hasOutput) createOutput()
    }

    fun createInput(connectionID: String, connectionName: String) {
        this.add(createInput0(connectionID, connectionName))
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
        this.add(node)
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
        this.add(panel)
    }

    fun createTextInput(title: String): JTextField {
        val panel = JPanel()
        val value = JTextField()
        panel.layout = FlowLayout(FlowLayout.LEADING)
        panel.add(JLabel(title))
        panel.add(value)
        this.add(panel)
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