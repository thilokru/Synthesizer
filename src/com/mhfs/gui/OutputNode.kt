package com.mhfs.gui

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel

class OutputNode : JPanel() {

    init {
        this.size = Dimension(150, 50)
        this.layout = GridLayout(0, 1)
        this.add(JLabel("Output"))

        val node = JPanel()
        node.add(LinkTerminal(20), BorderLayout.WEST)
        node.add(JLabel("Output Signal"), BorderLayout.CENTER)
        this.add(node)
    }
}