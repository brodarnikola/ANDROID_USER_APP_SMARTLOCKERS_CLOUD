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

data class SettingsViewModelUiState(
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val errorMessage: String? = null
)

class SettingsViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SettingsViewModelUiState())
    val uiState: StateFlow<SettingsViewModelUiState> = _uiState.asStateFlow()

    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                UserUtil.logout()
                onSuccess()
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