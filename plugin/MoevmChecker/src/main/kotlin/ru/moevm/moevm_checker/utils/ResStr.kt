package ru.moevm.moevm_checker.utils

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "strings.Strings"

object ResStr : DynamicBundle(BUNDLE) {
    @JvmStatic
    fun getString(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): @Nls String {
        return getMessage(key, *params)
    }


}