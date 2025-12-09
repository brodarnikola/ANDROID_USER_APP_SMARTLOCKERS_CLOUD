
package hr.sil.android.myappbox.compose.login_forgot_password

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.model.UserStatus
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.util.backend.UserUtil.user
import hr.sil.android.myappbox.utils.BaseViewModel
import hr.sil.android.myappbox.utils.UiEvent
import hr.sil.android.myappbox.utils.isEmailValid
//import hr.sil.android.schlauebox.cache.DataCache
//import hr.sil.android.schlauebox.cache.status.InstallationKeyHandler
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.text.isBlank

class LoginViewModel : BaseViewModel<LoginScreenUiState, LoginScreenEvent>() {

    val log = logger()

    override fun initialState(): LoginScreenUiState {
        return LoginScreenUiState()
    }

    init {
        log.info("collecting event: start new viewmodel")
    }

    override fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnLogin -> {

                viewModelScope.launch {
                    _state.update { it.copy(loading = true) }
                    val userStatus = UserUtil.loginCheckUserStatus(
                        event.email,
                        event.password
                    )
                    log.info("userStatus is: $userStatus")
                    _state.update { it.copy(loading = false) }
                    if (userStatus == UserStatus.ACTIVE) {
                        //InstallationKeyHandler.key.clear()
                        log.info("UserUtil.user?.hasAcceptedTerms is: ${UserUtil.user?.hasAcceptedTerms}")
                        if (user?.hasAcceptedTerms == false) {
                            SettingsHelper.userPasswordWithoutEncryption = event.password
                            sendUiEvent(LoginScreenUiEvent.NavigateToTCInvitedUserActivityScreen)
                        } else {
                            log.info("event.password is: ${event.password}")
                            SettingsHelper.userPasswordWithoutEncryption = event.password
                            SettingsHelper.userRegisterOrLogin = true
//                            val startIntent = Intent(event.context, MainActivity1::class.java)
//                            event.context.startActivity(startIntent)
//                            event.activity.finish()
                            sendUiEvent(LoginScreenUiEvent.NavigateToMainActivityScreen)
                        }
                    } else if (userStatus == UserStatus.INVITED) {
                        sendUiEvent(LoginScreenUiEvent.NavigateToTCInvitedUserActivityScreen)
                    } else {
                        sendUiEvent(
                            UiEvent.ShowToast(
                                "Email and password don't match, or your account has been disabled.",
                                Toast.LENGTH_SHORT
                            )
                        )
                    }
                }

//                viewModelScope.launch {
//                    _state.update { it.copy(loading = true) }
//                    login(email = event.email, password = event.password, context = event.context)
//                    _state.update { it.copy(loading = false) }
//                }
            }
            LoginScreenEvent.OnForgottenPassword -> {
                sendUiEvent(LoginScreenUiEvent.NavigateToForgotPasswordScreen)
            }
        }
    }

    fun getEmailError(email: String, context: Context): String {
        var emailError = ""
        if (email.isBlank()) {
            emailError = context.getString(R.string.forgot_password_error)
        } else if (!email.isEmailValid()) {
            emailError = context.getString(R.string.pickup_parcel_email_error)
        }

        return emailError
    }

    fun getPasswordError(password: String, context: Context): String {
        var passwordError = ""
        if (password.isBlank()) {
            passwordError = context.getString(R.string.forgot_password_error)
        }

        return passwordError
    }

    //fun getUserEmail(): String = sharedPrefsStorage.getUserEmail()
}

data class LoginScreenUiState(
    val loading: Boolean = false
)

sealed interface LoginScreenEvent {
    data class OnLogin(val email: String, val password: String, val context: Context, val activity: Activity) : LoginScreenEvent
    object OnForgottenPassword : LoginScreenEvent
}

sealed class LoginScreenUiEvent: UiEvent {
    object NavigateToNextScreen : LoginScreenUiEvent()
    object NavigateToTCInvitedUserActivityScreen : LoginScreenUiEvent()

    object NavigateToMainActivityScreen : LoginScreenUiEvent()

    object NavigateToForgotPasswordScreen : LoginScreenUiEvent()
    object NavigateBack : LoginScreenUiEvent()
}