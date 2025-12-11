package hr.sil.android.myappbox.compose.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsChangePasswordUiState(
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val isNetworkAvailable: Boolean = true,
    val errorMessage: String? = null
)

class ChangePasswordViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SettingsChangePasswordUiState())
    val uiState: StateFlow<SettingsChangePasswordUiState> = _uiState.asStateFlow()

    fun saveNewPassword(
        oldPassword: String, newPassword: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        val currentState = _uiState.value

        if (!currentState.isNetworkAvailable) {
            onError("No network connection")
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {

                val result = UserUtil.passwordUpdate(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )

                log.info("Old password is: $oldPassword and new password is: $newPassword")

                if (result) {
                    UserUtil.updateUserHash(
                        UserUtil.user?.email,
                        newPassword
                    )
                    SettingsHelper.userPasswordWithoutEncryption = newPassword
                }

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