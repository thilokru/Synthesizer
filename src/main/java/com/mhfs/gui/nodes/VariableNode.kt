package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.VariableReadoutGenerator

class VariableNode: Node("Variable") {

    private val variableName = createTextInput("Name")

    override fun buildAndLink() = VariableReadoutGenerator(variableName.text)
}