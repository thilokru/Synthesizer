package com.mhfs.gui

import java.awt.*
import java.awt.BasicStroke.CAP_ROUND
import java.awt.BasicStroke.JOIN_ROUND
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.event.MouseInputListener

class LinkedTileContainer : JPanel(), MouseInputListener {

    companion object {
        private const val LINE_INTERVAL = 30
    }

    private var lastMouseX: Int = 0
    private var lastMouseY: Int = 0
    private var totalOffsetX: Int = 0
    private var totalOffsetY: Int = 0

    private val scheduledLinks: MutableList<Link> = LinkedList()

    init {
        this.addMouseListener(this)
        this.addMouseMotionListener(this)
        this.layout = null
        this.isOpaque = false
    }

    /**
     * Mouse event handlers we don't need but have to override.
     */
    override fun mouseMoved(e: MouseEvent) {} //Don't care

    override fun mouseExited(e: MouseEvent) {} //Don't care
    override fun mouseClicked(e: MouseEvent) {} //Don't care
    override fun mouseEntered(e: MouseEvent) {} //Don't care
    override fun mouseReleased(e: MouseEvent) {} //Don't care

    override fun mousePressed(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            lastMouseX = e.x
            lastMouseY = e.y
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            val offsetX = (width / 2) - (components.sumBy { it.x + it.width / 2 } / components.size.toDouble()).toInt()
            val offsetY = (height / 2) - (components.sumBy { it.y + it.height / 2 } / components.size.toDouble()).toInt()
            components.forEach { it.setLocation(it.x + offsetX, it.y + offsetY) }
            totalOffsetX = 0
            totalOffsetY = 0
            this.repaint()
        }
    }

    override fun mouseDragged(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            val dX = e.x - lastMouseX
            val dY = e.y - lastMouseY
            totalOffsetX += dX
            totalOffsetY += dY
            lastMouseX = e.x
            lastMouseY = e.y
            components.forEach { it.setLocation(it.x + dX, it.y + dY) }
        }
        this.repaint()
    }

    override fun paintComponent(g: Graphics) { //Handles the Panel. Does not contain its children or links
        g.color = Color.DARK_GRAY
        g.fillRect(0, 0, width, height)
        g.color = Color.GRAY
        for (x in totalOffsetX % LINE_INTERVAL..this.width step LINE_INTERVAL) {
            g.drawLine(x, 0, x, height)
        }
        for (y in totalOffsetY % LINE_INTERVAL..this.height step LINE_INTERVAL) {
            g.drawLine(0, y, width, y)
        }
    }

    override fun paintChildren(g: Graphics) { //Handles the children. We consider the links to be children
        //Draw the children. Needed to get the links.
        super.paintChildren(g)
        //Drawing the links first to prevent redraws when children update
        val absX = this.locationOnScreen.x
        val absY = this.locationOnScreen.y
        scheduledLinks.forEach {
            val s = it.getStart()
            val e = it.getEnd() ?: return@forEach
            paintLink(s.x - absX, s.y - absY, e.x - absX, e.y - absY, g as Graphics2D)
        }
        scheduledLinks.clear()
        //Now paint the children over the links and background
        super.paintChildren(g)
    }

    fun scheduleLinkPaint(link: Link) = scheduledLinks.add(link)

    private fun paintLink(x1: Int, y1: Int, x2: Int, y2: Int, g2d: Graphics2D) {
        val dX = x2 - x1
        val dY = y2 - y1

        if (Math.abs(dX) > 1) {
            val a = -2 * dY / Math.pow(dX.toDouble(), 3.0)
            val b = 3 * dY / Math.pow(dX.toDouble(), 2.0)
            val f = { it: Int -> (a * Math.pow(it.toDouble(), 3.0) + b * Math.pow(it.toDouble(), 2.0)).toInt() }
            val draw = {
                for (x in Math.min(x1, x2)..Math.max(x1, x2)) {
                    g2d.drawLine(x, f(x - x1) + y1, x + 1, f(x + 1 - x1) + y1)
                }
            }
            g2d.color = Color.LIGHT_GRAY
            g2d.stroke = BasicStroke(10F, CAP_ROUND, JOIN_ROUND)
            draw()

            g2d.color = Color.DARK_GRAY
            g2d.stroke = BasicStroke(4F, CAP_ROUND, JOIN_ROUND)
            draw()
        } else {
            g2d.color = Color.LIGHT_GRAY
            g2d.stroke = BasicStroke(10F, CAP_ROUND, JOIN_ROUND)
            g2d.drawLine(x1, y1, x2, y2)

            g2d.color = Color.DARK_GRAY
            g2d.stroke = BasicStroke(4F, CAP_ROUND, JOIN_ROUND)
            g2d.drawLine(x1, y1, x2, y2)
        }
    }

    override fun addImpl(comp: Component, constraints: Any?, index: Int) {
        val listener = TileMouseListener(comp)
        comp.addMouseMotionListener(listener)
        comp.addMouseListener(listener)
        return super.addImpl(comp, constraints, index)
    }

    private class TileMouseListener(val owner: Component) : MouseInputListener {

        private var lastMouseX: Int = 0
        private var lastMouseY: Int = 0

        override fun mouseReleased(e: MouseEvent?) {}
        override fun mouseMoved(e: MouseEvent?) {}
        override fun mouseEntered(e: MouseEvent?) {}
        override fun mouseClicked(e: MouseEvent?) {}
        override fun mouseExited(e: MouseEvent?) {}

        override fun mousePressed(e: MouseEvent) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                lastMouseX = e.x
                lastMouseY = e.y
            }
        }

        override fun mouseDragged(e: MouseEvent) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                val dX = e.x - lastMouseX
                val dY = e.y - lastMouseY
                owner.setLocation(owner.x + dX, owner.y + dY)
                owner.parent.repaint() //Not only the directly affected area needs to be redrawn
            }
        }
    }

    interface Link {
        /**
         * Return absolute start position
         */
        fun getStart(): Point

        /**
         * Return absolute end position
         */
        fun getEnd(): Point?
    }
}