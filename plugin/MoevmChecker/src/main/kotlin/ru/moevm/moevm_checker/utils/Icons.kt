package ru.moevm.moevm_checker.utils

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.AnimatedIcon

object Icons {
    val rootNode = AllIcons.Nodes.HomeFolder
    val course = AllIcons.Nodes.Folder
    val available = AllIcons.Actions.Download
    val progress = AnimatedIcon.Default()
    val outdated = IconLoader.getIcon("/icons/outdated.svg", Icons::class.java)
    val downloaded = IconLoader.getIcon("/icons/downloaded.svg", Icons::class.java)
}
