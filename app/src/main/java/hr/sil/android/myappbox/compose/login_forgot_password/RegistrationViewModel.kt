
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

class RegistrationViewModel : BaseViewModel<RegistrationScreenUiState, RegistrationScreenEvent>() {

    val log = logger()

    override fun initialState(): RegistrationScreenUiState {
        return RegistrationScreenUiState()
    }

    init {
        log.info("collecting event: start new viewmodel")
    }

    override fun onEvent(event: RegistrationScreenEvent) {
        when (event) {
            is RegistrationScreenEvent.OnRegister -> {

                viewModelScope.launch {
                    _state.update { it.copy(loading = true) }
                    val groupName = event.groupNameFirstRow.trim() + " " +  event.groupNameSecondRow.trim()

                    val result = UserUtil.register(
                        event.name,
                        event.address,
                        event.phone,
                        event.email,
                        event.password,
                        groupName
                    )
                    log.info("result is: $result")
                    _state.update { it.copy(loading = false) }
                    if (result) {
                        //InstallationKeyHandler.key.clear()
                        log.info("UserUtil.user?.hasAcceptedTerms is: ${user?.hasAcceptedTerms}")
                        if (user?.hasAcceptedTerms == false) {

                            SettingsHelper.userPasswordWithoutEncryption = event.password
                            sendUiEvent(RegistrationScreenUiEvent.NavigateToTCInvitedUserActivityScreen)
                        } else {
                            log.info("event.password is: ${event.password}")
                            SettingsHelper.userPasswordWithoutEncryption = event.password
                            SettingsHelper.userRegisterOrLogin = true
                            sendUiEvent(RegistrationScreenUiEvent.NavigateToMainActivityScreen)
                        }
                    }  else {
                        sendUiEvent(
                            UiEvent.ShowToast(
                                "Something went wrong. Please try again latter",
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
            RegistrationScreenEvent.OnForgottenPassword -> {
                sendUiEvent(LoginScreenUiEvent.NavigateToForgotPasswordScreen)
            }
        }
    }

    fun getNameError(name: String, context: Context) : String {
        var nameError = ""
        if (name.isBlank()) {
            nameError = context.getString(R.string.edit_user_validation_blank_fields_exist)
        } else if (name.length < 4) {
            nameError = context.getString(R.string.edit_user_validation_username_min_4_characters)
        }

        return nameError
    }

    fun getGroupNameError(groupNameFirstRow: String, groupNameSecondRow: String, context: Context) : String {
        var groupNameError = ""
        if (groupNameFirstRow.isBlank()  ) {
            groupNameError = context.getString(R.string.edit_user_validation_blank_fields_exist)
        } else if (groupNameFirstRow.length < 4) {
            groupNameError = context.getString(R.string.edit_user_validation_group_name_min_4_characters)
        }

        return groupNameError
    }

    fun getRepeatPasswordError(password: String, repeatPassword: String, context: Context) : String  {
        var passwordError = ""
        if (repeatPassword.isBlank()) {
            passwordError = context.getString(R.string.edit_user_validation_blank_fields_exist)
        }
        else if( password != repeatPassword ) {
            passwordError = context.getString(R.string.edit_user_validation_passwords_do_not_match)
        }

        return passwordError
    }

    fun getAddressError(phone: String, context: Context) : String  {
        var addressError = ""
        if (phone.isBlank()) {
            addressError = context.getString(R.string.edit_user_validation_blank_fields_exist)
        }

        return addressError
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

data class RegistrationScreenUiState(
    val loading: Boolean = false
)

sealed interface RegistrationScreenEvent {
    // name = name,
    //                                    groupNameFirstRow = groupNameFirstRow,
    //                                    groupNameSecondRow = groupNameSecondRow,
    //                                    phone = phone,
    //                                    address = address,
    //                                    email = email,
    //                                    password = password,
    //                                    context = context
    data class OnRegister(val name: String, val groupNameFirstRow: String, val groupNameSecondRow: String, val phone: String,
                          val address: String, val email: String, val password: String, val context: Context) : RegistrationScreenEvent
    object OnForgottenPassword : RegistrationScreenEvent
}

sealed class RegistrationScreenUiEvent: UiEvent {
    object NavigateToNextScreen : RegistrationScreenUiEvent()
    object NavigateToTCInvitedUserActivityScreen : RegistrationScreenUiEvent()

    object NavigateToMainActivityScreen : RegistrationScreenUiEvent()

    object NavigateToForgotPasswordScreen : RegistrationScreenUiEvent()
    object NavigateBack : RegistrationScreenUiEvent()
}