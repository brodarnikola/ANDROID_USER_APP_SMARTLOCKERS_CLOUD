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

data class SettingsNotificationsUiState(
    val pushNotifications: Boolean = false,
    val emailNotifications: Boolean = false,
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val isNetworkAvailable: Boolean = true,
    val errorMessage: String? = null
)

class SettingsNotificationsViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SettingsNotificationsUiState())
    val uiState: StateFlow<SettingsNotificationsUiState> = _uiState.asStateFlow()

    private var cachedUser = UserUtil.user
    private var cachedUserGroup = UserUtil.userGroup
    private var cachedLanguage: RLanguage? = null

    init {
        loadNotificationSettings()
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            try {
                val languages = WSUser.getLanguages() ?: listOf()
                val languageName = SettingsHelper.languageName
                cachedLanguage = languages.firstOrNull { it.code == languageName }
                cachedUser = UserUtil.user
                cachedUserGroup = UserUtil.userGroup

                _uiState.update {
                    it.copy(
                        pushNotifications = SettingsHelper.pushEnabled,
                        emailNotifications = SettingsHelper.emailEnabled,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                log.error("Error loading notification settings", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load settings",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onPushNotificationsChanged(enabled: Boolean) {
        _uiState.update {
            it.copy(
                pushNotifications = enabled,
                isSaveEnabled = true
            )
        }
    }

    fun onEmailNotificationsChanged(enabled: Boolean) {
        _uiState.update {
            it.copy(
                emailNotifications = enabled,
                isSaveEnabled = true
            )
        }
    }

    fun onNetworkAvailabilityChanged(isAvailable: Boolean) {
        _uiState.update { it.copy(isNetworkAvailable = isAvailable) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveNotificationSettings(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value

        if (!currentState.isNetworkAvailable) {
            onError("No network connection")
            return
        }

        val user = cachedUser
        val userGroup = cachedUserGroup
        val language = cachedLanguage

        if (user == null || language == null) {
            onError("User data not available")
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val result = UserUtil.userUpdate(
                    name = user.name ?: "",
                    address = user.address ?: "",
                    phone = user.telephone ?: "",
                    language = language,
                    pushNotification = currentState.pushNotifications,
                    emailNotification = currentState.emailNotifications,
                    groupName = userGroup?.name ?: ""
                )

                if (result) {
                    log.info("Notification settings saved successfully")
                    SettingsHelper.pushEnabled = currentState.pushNotifications
                    SettingsHelper.emailEnabled = currentState.emailNotifications

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaveEnabled = false
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onError("Failed to save notification settings")
                }
            } catch (e: Exception) {
                log.error("Error saving notification settings", e)
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