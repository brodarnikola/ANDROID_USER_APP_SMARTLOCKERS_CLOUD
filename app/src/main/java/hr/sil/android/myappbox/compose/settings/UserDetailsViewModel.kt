package hr.sil.android.myappbox.compose.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.RLanguage
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUserDetailsUiState(
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val isNetworkAvailable: Boolean = true,
    val errorMessage: String? = null
)

class UserDetailsViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SettingsUserDetailsUiState())
    val uiState: StateFlow<SettingsUserDetailsUiState> = _uiState.asStateFlow()

  //  name, address, phone, language,
    //                                user.isNotifyPush,
//                                user.isNotifyEmail, groupName
    fun updateUserDetails(
        name: String,
        address: String,
        phone: String,
        isNotifyPush: Boolean,
        isNotifyEmail: Boolean,
        groupName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentState = _uiState.value

        if (!currentState.isNetworkAvailable) {
            onError("No network connection")
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {

                val languagesList = WSUser.getLanguages()?.toList()
                val languageName = SettingsHelper.languageName
                val language: RLanguage? =
                    languagesList?.firstOrNull { it.code == languageName }!!

                val result = UserUtil.userUpdate(
                     name, address, phone, language ?: RLanguage(),
                     isNotifyPush,
                     isNotifyEmail, groupName
                )

                log.info("new name is: $name and new address is: $address")

                if (result) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaveEnabled = true
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaveEnabled = true
                        )
                    }
                    onError("Failed to save password settings")
                }
            } catch (e: Exception) {
                log.error("Error saving language settings", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
                onError("Error: ${e.message}")
            }
        }
    }
}