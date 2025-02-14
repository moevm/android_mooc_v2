package ru.moevm.moevm_checker.dagger

import javax.inject.Qualifier

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class Ui

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class Work

@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class Io
