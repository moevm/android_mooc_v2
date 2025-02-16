package ru.moevm.moevm_checker.ui.auth_content

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ru.moevm.moevm_checker.core.controller.AuthController
import ru.moevm.moevm_checker.core.utils.coroutine.EventSharedFlow
import ru.moevm.moevm_checker.dagger.Ui
import ru.moevm.moevm_checker.ui.BaseViewModel
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authController: AuthController,
    @Ui uiDispatcher: CoroutineDispatcher,
): BaseViewModel(uiDispatcher) {

    var uiLogin = ""
    private set

    var uiPassword = ""
    private set

    val isLogged = EventSharedFlow<Unit>()

    fun setLogin(newLogin: String) {
        uiLogin = newLogin
    }

    fun setPassword(newPassword: String) {
        uiPassword = newPassword
    }

    fun applyUserData() {
        authController.setUserData(uiLogin, uiPassword)
        // TODO переделать под получение токена
        viewModelScope.launch {
            isLogged.emit(Unit)
        }
    }
}