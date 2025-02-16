package ru.moevm.moevm_checker.ui.auth_content

import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.moevm.moevm_checker.core.utils.simpleLazy
import ru.moevm.moevm_checker.dagger.PluginComponent
import ru.moevm.moevm_checker.ui.BaseView
import ru.moevm.moevm_checker.ui.DialogPanelData

class AuthView(
    private val component: PluginComponent,
    private val navigateToCoursesTree: () -> Unit
) : BaseView() {

//  UI Components

    private lateinit var loginTextField: JBTextField
    private lateinit var passwordTextField: JBPasswordField

    override val viewModel: AuthViewModel by simpleLazy {
        component.authViewModel
    }

    override fun getDialogPanel(): DialogPanelData {
        val panelName = "MOEVM Checker"
        val dialogPanel = createDialogPanel()

        bindEvents()

        return DialogPanelData(panelName, dialogPanel)
    }

    private fun bindEvents() {
        viewModel.isLogged
            .onEach {
                navigateToCoursesTree()
            }
            .launchIn(viewScope)
    }

    private fun createDialogPanel() = panel {
        panel {
            row {
                label("Здравствуйте! Пожалуйста, введите логин и пароль")
            }

            row {
                loginTextField = textField()
                    .apply {
                        label("Логин: ")
                        onChanged { component ->
                            viewModel.setLogin(component.text)
                        }
                    }.component
            }

            row {
                passwordTextField = passwordField()
                    .apply {
                        label("Пароль: ")
                        // for showing password: "this.component.echoChar = 0.toChar()"
                        onChanged { component ->
                            viewModel.setPassword(component.password.toString())
                        }
                    }.component
            }

            row {
                button("Войти") {
                    viewModel.applyUserData()
                }
            }
        }.align(Align.CENTER)
    }
}
