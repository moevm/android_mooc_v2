package ru.moevm.moevm_checker.ui

import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Point
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.SwingUtilities

/* TODO linkListener */
class HtmlTextPreviewPanel : JPanel(BorderLayout()) {
    private val textPane = JTextPane()
    private val scrollPane = JBScrollPane(textPane)

    init {
        isOpaque = true

        textPane.isEditable = false
        textPane.isOpaque = false
        textPane.contentType = "text/html"
        textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true)
        scrollPane.isOpaque = false

        add(scrollPane, BorderLayout.CENTER)
    }

    fun updateText(newHtml: String) {
        textPane.text = newHtml
        SwingUtilities.invokeLater {
            scrollPane.viewport.viewPosition = Point(0, 0)
        }
    }

    fun updateSize(newWidth: Int) {
        this.preferredSize = this.preferredSize.apply {
            width = newWidth
        }
        revalidate()
        repaint()
    }
}