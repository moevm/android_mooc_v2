package ru.moevm.moevm_checker.utils

class ObservableList<T> {

    sealed class ChangeEvent<T> {
        data class Add<T>(val element: T, val index: Int? = null) : ChangeEvent<T>()
        data class Remove<T>(val element: T, val index: Int? = null) : ChangeEvent<T>()
    }

    private val backingList = mutableListOf<T>() // Внутренний список для хранения элементов
    private val listeners = mutableListOf<(ChangeEvent<T>) -> Unit>() // Список слушателей

    private val lock = Any()

    val size: Int
        get() {
            synchronized(lock) {
                return backingList.size
            }
        }

    fun addListener(listener: (ChangeEvent<T>) -> Unit) {
        synchronized(lock) {
            if (!listeners.contains(listener)) {
                listeners.add(listener)
            }
        }
    }

    fun removeListener(listener: (ChangeEvent<T>) -> Unit) {
        synchronized(lock) {
            listeners.remove(listener)
        }
    }

    fun add(element: T): Boolean {
        synchronized(lock) {
            val added = backingList.add(element)
            if (added) {
                notifyListeners(ChangeEvent.Add(element))
            }
            return added
        }
    }

    fun remove(element: T): Boolean {
        synchronized(lock) {
            val removed = backingList.remove(element)
            if (removed) {
                notifyListeners(ChangeEvent.Remove(element))
            }
            return removed
        }
    }

    private fun notifyListeners(event: ChangeEvent<T>) {
        val snapshotListeners: List<(ChangeEvent<T>) -> Unit>
        synchronized(lock) {
            snapshotListeners = listeners.toList()
        }
        snapshotListeners.forEach { it.invoke(event) }
    }

    fun clearSilently() {
        synchronized(lock) {
            backingList.clear()
        }
    }

    fun toList(): List<T> {
        synchronized(lock) {
            val snapshotList: List<T>
            synchronized(lock) {
                snapshotList = backingList.toList()
            }
            return snapshotList
        }
    }
}
