package com.example.checker_lib

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

internal object TestUtils {

    fun withExactOrder(vararg matchers: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with exact order: ")
                matchers.forEach { matcher -> matcher.describeTo(description) }
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is ViewGroup || view.childCount != matchers.size) return false
                return matchers.indices.all { matchers[it].matches(view.getChildAt(it)) }
            }
        }
    }

    @IdRes
    fun getIdFromString(context: Context, idString: String): Int {
        return context.resources.getIdentifier(idString, "id", context.packageName)
    }
}