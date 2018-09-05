package com.mhfs.gui

import java.awt.*
import java.util.function.Consumer
import javax.swing.JButton

class LinkTerminal(private val linkChangeCallback: Consumer<Node>, private val owner: Node, sideLength: Int = 5, val isOutput: Boolean = false) : JButton(" ") {

    companion object {
        var previousLinkElement: LinkTerminal? = null
    }

    private var endPoint: LinkTerminal? = null

    init {
        this.size = Dimension(sideLength, sideLength)
        this.addActionListener {
            this.endPoint?.endPoint = null
            this.endPoint = null
            if (previousLinkElement == null) { //We are the first element to be clicked
                LinkTerminal.previousLinkElement = this
            } else if (previousLinkElement == this) { //User aborted
                LinkTerminal.previousLinkElement = null
            } else {
                val foreign: LinkTerminal = previousLinkElement as LinkTerminal
                if (this.isOutput && !foreign.isOutput) {
                    foreign.endPoint = this
                    foreign.linkChangeCallback.accept(this.owner)
                    previousLinkElement = null
                } else if (!this.isOutput && foreign.isOutput) {
                    this.endPoint = foreign
                    this.linkChangeCallback.accept(foreign.owner)
                    previousLinkElement = null
                }
            }
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        if (previousLinkElement == this) {
            getLinkContainer().scheduleLinkPaint(object : LinkedTileContainer.Link {
                override fun getStart(): Point {
                    val loc = locationOnScreen
                    loc.translate(width / 2, height / 2)
                    return loc
                }

                override fun getEnd() = MouseInfo.getPointerInfo().location
            })
            getLinkContainer().repaint()
        }
        if (!isOutput && endPoint != null) {
            getLinkContainer().scheduleLinkPaint(object : LinkedTileContainer.Link {
                override fun getStart(): Point {
                    val loc = locationOnScreen
                    loc.translate(width / 2, height / 2)
                    return loc
                }

                override fun getEnd(): Point? {
                    val loc = endPoint?.locationOnScreen ?: null
                    loc?.translate(endPoint!!.width / 2, endPoint!!.height / 2)
                    return loc
                }
            })
            getLinkContainer().repaint()
        }
    }

    private fun getLinkContainer(): LinkedTileContainer {
        var current = this.parent
        while (current !is LinkedTileContainer) current = current.parent
        return current
    }
}