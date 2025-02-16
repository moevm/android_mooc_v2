package ru.moevm.moevm_checker.core.controller

class AuthController {

    var login = ""
        private set

    var password = ""
        private set

    var token = ""
        private set

    fun setUserData(login: String, password: String) {
        this.login = login
        this.password = password
        println("Login and password received")
    }
}