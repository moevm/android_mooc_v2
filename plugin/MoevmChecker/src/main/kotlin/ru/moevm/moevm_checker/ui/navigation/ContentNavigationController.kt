package ru.moevm.moevm_checker.ui.navigation

import com.intellij.openapi.observable.util.whenDisposed
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.auth_content.AuthView
import ru.moevm.moevm_checker.ui.courses_tree_content.CoursesTreeView
import ru.moevm.moevm_checker.ui.task.android.AndroidTaskView

class ContentNavigationController(
    private val contentManager: ContentManager,
    private val pluginComponent: PluginComponent
) {
    private enum class ContentName {
        AUTH,
        COURSES_TREE,
        TASK,
    }

    private val mapOfContents = mutableMapOf<ContentName, Content>()

    fun setAuthContent() {
        if (mapOfContents.containsKey(ContentName.AUTH)) {
            return
        }
        val authView = AuthView(
            pluginComponent,
            navigateToCoursesTree = ::setCoursesTreeContent
        )
        val authViewPanelData = authView.getDialogPanel()
        val content = ContentFactory.getInstance()
            .createContent(authViewPanelData.dialogPanel, authViewPanelData.panelName, /* isLocked = */ false).apply {
                whenDisposed { authView.destroy() }
            }

        mapOfContents[ContentName.AUTH] = content
        mapOfContents.removeContentIf { name -> name != ContentName.AUTH }
        contentManager.addContent(content)
    }

    fun setCoursesTreeContent() {
        if (mapOfContents.containsKey(ContentName.COURSES_TREE)) {
            return
        }
        val coursesTreeView = CoursesTreeView(
            pluginComponent,
        )
        val coursesTreeViewPanelData = coursesTreeView.getDialogPanel()
        val content = ContentFactory.getInstance()
            .createContent(coursesTreeViewPanelData.dialogPanel, coursesTreeViewPanelData.panelName, /* isLocked = */ false).apply {
                whenDisposed { coursesTreeView.destroy() }
            }

        mapOfContents[ContentName.COURSES_TREE] = content
        mapOfContents.removeContentIf { name -> name != ContentName.COURSES_TREE }
        contentManager.addContent(content)
    }

    fun setAndroidTaskContent(courseId: String, taskId: String) {
        mapOfContents.removeContentIf { name -> name == ContentName.TASK }

        val taskView = AndroidTaskView(pluginComponent, courseId, taskId)
        val taskViewPanelData = taskView.getDialogPanel()
        val content = ContentFactory.getInstance()
            .createContent(taskViewPanelData.dialogPanel, taskViewPanelData.panelName, /* isLocked = */ false).apply {
                whenDisposed { taskView.destroy() }
            }
        mapOfContents[ContentName.TASK] = content
        contentManager.addContent(content)
    }

    private fun MutableMap<ContentName, Content>.removeContentIf(predicate: (key: ContentName) -> Boolean) {
        val keysForRemove = mapOfContents.keys.filter { key -> predicate(key) }
        keysForRemove.forEach { key ->
            contentManager.removeContent(requireNotNull(mapOfContents[key]), true)
            this.remove(key)
        }
    }
}