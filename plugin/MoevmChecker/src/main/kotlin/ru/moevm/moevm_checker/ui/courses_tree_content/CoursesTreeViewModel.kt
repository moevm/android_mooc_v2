package ru.moevm.moevm_checker.ui.courses_tree_content

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.moevm.moevm_checker.core.controller.CoursesRepository
import ru.moevm.moevm_checker.core.data.course.Course
import ru.moevm.moevm_checker.core.data.course.CourseTask
import ru.moevm.moevm_checker.core.tasks.*
import ru.moevm.moevm_checker.core.utils.coroutine.EventSharedFlow
import ru.moevm.moevm_checker.dagger.Io
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import ru.moevm.moevm_checker.ui.courses_tree_content.data.CourseVO
import ru.moevm.moevm_checker.ui.courses_tree_content.data.TaskVO
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.CoursesTreeNode
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.RootTreeNode
import ru.moevm.moevm_checker.ui.courses_tree_content.tree.node.TaskTreeNode
import ru.moevm.moevm_checker.utils.ObservableList
import javax.inject.Inject
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

class CoursesTreeViewModel @Inject constructor(
    private val taskManager: TaskManager,
    private val taskFileManager: TaskFileManager,
    private val coursesRepository: CoursesRepository,
    @Ui private val uiDispatcher: CoroutineDispatcher,
    @Io private val ioDispatcher: CoroutineDispatcher,
): BaseViewModel(uiDispatcher) {

    val coursesTreeModel = CoursesTreeModel(RootTreeNode.buildEmptyTree())

    private val shouldTreeInvalidateMutable = EventSharedFlow<Unit>()
    val shouldTreeInvalidate = shouldTreeInvalidateMutable.asSharedFlow()

    private val isDescriptionLoadingMutable = MutableStateFlow(false)
    val isDescriptionLoading = isDescriptionLoadingMutable.asStateFlow()
    private val descriptionMutable = MutableStateFlow<String?>(PLUGIN_DESCRIPTION_TEXT)
    val descriptionState = descriptionMutable.asStateFlow()
    private var descriptionLoadingJob: Job? = null

    private val shouldTreeRepaintMutable = MutableSharedFlow<List<TreePath>>()
    val shouldTreeRepaint = shouldTreeRepaintMutable.asSharedFlow()
    private var treeRepaintJob: Job? = null

    private val nodesInProgress = ObservableList<TaskReference>()
    private val listener: (ObservableList.ChangeEvent<TaskReference>) -> Unit = { event ->
        when (event) {
            is ObservableList.ChangeEvent.Add<*> -> {
                if (nodesInProgress.size == 1) {
                    treeRepaintJob = launchRepaintJob()
                }
            }
            is ObservableList.ChangeEvent.Remove<*> -> {
                if (nodesInProgress.size == 0) {
                    treeRepaintJob?.cancel()
                    treeRepaintJob = null
                }
            }
        }
    }

    private fun launchRepaintJob() = viewModelScope.launch {
        while (isActive) {
            delay(100)
            shouldTreeRepaintMutable.emit(nodesInProgress.toList().map { coursesTreeModel.getPathToNode(it) })
        }
    }

    fun onViewCreated() {
        nodesInProgress.addListener(listener)
        viewModelScope.launch {
            // TODO проверять версию репозитория
            val listOfCourses = coursesRepository.getCoursesInfoFlow()
                .flowOn(ioDispatcher)
                .single()?.courses?.map { course ->
                    CourseVO(
                        course.id,
                        course.name,
                        course.courseTasks
                            .map { task ->
                                val taskType = TaskManager.getCoursesItemType(task.type)
                                val taskFileStatus = getTaskFileStatus(taskType, course, task)
                                TaskVO(course.id, task.id, task.name, taskFileStatus, taskType)
                            }
                    )
                } ?: emptyList()
            withContext(uiDispatcher) {
                coursesTreeModel.updateTree(listOfCourses)
            }
            shouldTreeInvalidateMutable.emit(Unit)
        }
    }

    private suspend fun getTaskFileStatus(
        taskType: CoursesItemType,
        course: Course,
        task: CourseTask
    ) = if (TaskManager.isTaskOpenable(taskType)) {
        val isTaskFileExists = taskFileManager.isTaskFileExists(TaskReference(course.id, task.id)).last()
        if (isTaskFileExists) {
            TaskFileStatus.DOWNLOADED
        } else {
            TaskFileStatus.AVAILABLE
        }
    } else {
        TaskFileStatus.NOT_DOWNLOADABLE
    }

    fun onCourseTreeNodeChanged(newNode: DefaultMutableTreeNode?) {
        descriptionMutable.value = null
        descriptionLoadingJob?.cancel()
        when (newNode) {
            is RootTreeNode -> {
                isDescriptionLoadingMutable.value = true
                descriptionMutable.value = PLUGIN_DESCRIPTION_TEXT
                isDescriptionLoadingMutable.value = false
            }
            is CoursesTreeNode -> {
                isDescriptionLoadingMutable.value = true
                val courseId = newNode.courseId
                descriptionLoadingJob = coursesRepository.getCourseDescriptionFlow(courseId)
                    .flowOn(ioDispatcher)
                    .onEach { description ->
                        println("course $courseId description updated:\n$description")
                        descriptionMutable.value = description
                        isDescriptionLoadingMutable.value = false
                    }
                    .launchIn(viewModelScope)
            }
            is TaskTreeNode -> {
                isDescriptionLoadingMutable.value = true
                val taskReference = newNode.taskReference
                descriptionLoadingJob = coursesRepository.getTaskDescriptionFlow(taskReference)
                    .flowOn(ioDispatcher)
                    .onEach { description ->
                        println("task ${taskReference.taskId} in course\n${taskReference.courseId} description updated: $description")
                        descriptionMutable.value = description
                        isDescriptionLoadingMutable.value = false
                    }
                    .launchIn(viewModelScope)
            }
            else -> {
                isDescriptionLoadingMutable.value = false
            }
        }
    }

    fun onOpenTaskClick(taskReference: TaskReference) {
        taskManager.openTask(taskReference)
            .flowOn(ioDispatcher)
            .onEach {
                println("opening task ${taskReference.taskId} in course ${taskReference.courseId}")
            }
            .launchIn(viewModelScope)
    }

    fun onDownloadTaskClick(taskReference: TaskReference) {
        taskFileManager.downloadTaskFiles(taskReference)
            .flowOn(ioDispatcher)
            .onEach { status ->
                println("downloading task ${taskReference.taskId} in course ${taskReference.courseId}, status = $status")
                when (status) {
                    TaskDownloadStatus.DOWNLOADING -> {
                        nodesInProgress.add(taskReference)
                    }
                    TaskDownloadStatus.FAILED_BEFORE_START, TaskDownloadStatus.DOWNLOAD_FAILED,
                    TaskDownloadStatus.UNZIPPING_FINISH, TaskDownloadStatus.UNZIPPING_FAILED -> {
                        nodesInProgress.remove(taskReference)
                    }
                    else -> {}
                }
                val fileStatus = when (status) {
                    TaskDownloadStatus.FAILED_BEFORE_START -> TaskFileStatus.AVAILABLE
                    TaskDownloadStatus.DOWNLOADING -> TaskFileStatus.DOWNLOADING
                    TaskDownloadStatus.DOWNLOAD_FINISH -> TaskFileStatus.DOWNLOADING
                    TaskDownloadStatus.DOWNLOAD_FAILED -> TaskFileStatus.AVAILABLE
                    TaskDownloadStatus.UNZIPPING -> TaskFileStatus.DOWNLOADING
                    TaskDownloadStatus.UNZIPPING_FINISH -> TaskFileStatus.DOWNLOADED
                    TaskDownloadStatus.UNZIPPING_FAILED -> TaskFileStatus.AVAILABLE
                }
                coursesTreeModel.updateTaskFileStatus(taskReference, fileStatus)
            }
            .launchIn(viewModelScope)
    }

    fun onRemoveTaskClick(taskReference: TaskReference) {
        taskFileManager.removeTaskFiles(taskReference)
            .flowOn(ioDispatcher)
            .onEach { status ->
                println("removing task ${taskReference.taskId} in course ${taskReference.courseId}, status = $status")
                when (status) {
                    TaskRemoveStatus.REMOVING -> {
                        nodesInProgress.add(taskReference)
                    }
                    TaskRemoveStatus.FAILED_BEFORE_START, TaskRemoveStatus.REMOVE_FINISHED, TaskRemoveStatus.REMOVE_FAILED -> {
                        nodesInProgress.remove(taskReference)
                    }
                }
                val fileStatus = when (status) {
                    TaskRemoveStatus.FAILED_BEFORE_START -> TaskFileStatus.DOWNLOADED
                    TaskRemoveStatus.REMOVING -> TaskFileStatus.REMOVING
                    TaskRemoveStatus.REMOVE_FAILED -> TaskFileStatus.DOWNLOADED
                    TaskRemoveStatus.REMOVE_FINISHED -> TaskFileStatus.AVAILABLE
                }
                coursesTreeModel.updateTaskFileStatus(taskReference, fileStatus)
            }
            .launchIn(viewModelScope)
    }

    override fun destroy() {
        nodesInProgress.clearSilently()
        super.destroy()
    }

    private companion object {
        private const val PLUGIN_DESCRIPTION_TEXT = "# Добро пожаловать в плагин **Moevm Checker**!\n" +
                "\n" +
                "С помощью него вы можете решать задачи прямо из вашей IDE.  \n" +
                "Для начала:\n" +
                "\n" +
                "1. **Выберите** необходимый курс из доступных.\n" +
                "2. **Скачайте** интересующую задачу.\n" +
                "3. **Убедитесь**, что у вас подходящая IDE для решения задачи  \n" +
                "   *(например, для **Kotlin** — это IntelliJ IDEA, а не Android Studio)*.\n" +
                "4. **Откройте** её и решите.\n" +
                "\n" +
                "---\n" +
                "\n" +
                "После того, как вы решили задачу, вернитесь в плагин и нажмите **Проверить**.  \n" +
                "По итогу решения вас будет ждать **код**, который плагин отобразит вам при успешном выполнении задания.  \n" +
                "**Удачи!** \uD83D\uDE80"
    }
}