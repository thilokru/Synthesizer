package com.mhfs.gui.nodes

import com.mhfs.gui.Node
import com.mhfs.synth.ConstantGenerator

class ConstantNode : Node("Variable") {

    private val value = createNumberInput("Value:")

    override fun buildAndLink() = ConstantGenerator(value.text.toDouble())
}