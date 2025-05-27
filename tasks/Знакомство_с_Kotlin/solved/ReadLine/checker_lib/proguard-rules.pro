# Сохраняем все публичные классы, методы и свойства в библиотеке
-keep public class com.example.checker_lib.Checker {
    *;
}
# Минимизировать имена методов и классов
-keepclasseswithmembers public final class * {
    # Обфусцировать методы API класса
    public *;
}

