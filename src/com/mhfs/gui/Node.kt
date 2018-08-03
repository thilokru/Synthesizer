package com.mhfs.gui

import com.mhfs.synth.WaveformGenerator
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import java.text.NumberFormat
import java.util.function.Consumer
import javax.swing.JFormattedTextField
import javax.swing.JLabel
import javax.swing.JPanel

abstract class Node(title: String, hasOutput: Boolean = true) : JPanel() {

    private val links = HashMap<String, Node>()

    init {
        this.layout = GridLayout(0, 1)
        this.size = Dimension(150, 200)
        this.add(JLabel(title))
        if (hasOutput) createOutput()
    }

    fun createInput(connectionID: String, connectionName: String) {
        val node = JPanel()
        node.layout = FlowLayout(FlowLayout.LEADING)
        node.add(LinkTerminal(LinkChangeCallbackImpl(this, connectionID), this))
        node.add(JLabel(connectionName))
        this.add(node)
        //this.size = this.layout.preferredLayoutSize(this)
    }

    private fun createOutput() {
        val node = JPanel()
        node.layout = FlowLayout(FlowLayout.TRAILING)
        //The Callback should never be called.
        node.add(JLabel("Output"))
        node.add(LinkTerminal(LinkChangeCallbackImpl(this, "output"), this, isOutput = true))
        this.add(node)
    }

    fun createNumberInput(title: String): JFormattedTextField {
        val panel = JPanel()
        val value = JFormattedTextField(NumberFormat.getNumberInstance())
        value.value = 0.0
        value.columns = 5
        panel.layout = FlowLayout(FlowLayout.LEADING)
        panel.add(JLabel(title))
        panel.add(value)
        this.add(panel)
        return value
    }

    protected fun getLink(connectionID: String) = links[connectionID]

    abstract fun buildAndLink(): WaveformGenerator

    private class LinkChangeCallbackImpl(private val owner: Node, private val connectionID: String) : Consumer<Node> {
        override fun accept(t: Node) {
            owner.links[connectionID] = t
        }
    }
}