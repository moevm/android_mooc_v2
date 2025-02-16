package ru.moevm.moevm_checker.core.utils

fun <T> simpleLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
