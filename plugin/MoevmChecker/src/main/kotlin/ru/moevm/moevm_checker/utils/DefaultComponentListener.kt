package ru.moevm.moevm_checker.utils

import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener

class DefaultComponentListener(
    private val onComponentResized: (e: ComponentEvent?) -> Unit = { _ -> },
    private val onComponentMoved: (e: ComponentEvent?) -> Unit = { _ -> },
    private val onComponentShown: (e: ComponentEvent?) -> Unit = { _ -> },
    private val onComponentHidden: (e: ComponentEvent?) -> Unit = { _ -> },
) : ComponentListener {
    override fun componentResized(e: ComponentEvent?) {
        onComponentResized(e)
    }

    override fun componentMoved(e: ComponentEvent?) {
        onComponentMoved(e)
    }

    override fun componentShown(e: ComponentEvent?) {
        onComponentShown(e)
    }

    override fun componentHidden(e: ComponentEvent?) {
        onComponentHidden(e)
    }
}