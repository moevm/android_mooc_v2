# Укажите обфускацию для вашего модуля
-obfuscate
-keepattributes Exception, Interface

# Укажите, какие классы и методы нужно игнорировать (например, тестовые).
-keep class ru.jengle88.* { * }

# Если ваш проект использует рефлексию, сохраните её классы:
-keepclasseswithmembers class * {
    @kotlin.Metadata *;
}

# Исключите из обфускации методы стандартных библиотек Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.serialization.** { *; }

# Не удаляйте названия методов и классов JUnit для тестов
-keepclassmembers class org.junit.** { *; }