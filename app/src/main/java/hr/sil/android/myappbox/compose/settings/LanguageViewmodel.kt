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

data class SettingsLanguageUiState(
    val availableLanguages: List<RLanguage> = emptyList(),
    val selectedLanguage: RLanguage? = null,
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val isNetworkAvailable: Boolean = true,
    val errorMessage: String? = null
)

class LanguageViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SettingsLanguageUiState())
    val uiState: StateFlow<SettingsLanguageUiState> = _uiState.asStateFlow()

    private var cachedUser = UserUtil.user
    private var cachedUserGroup = UserUtil.userGroup

    init {
        loadLanguageSettings()
    }

    private fun loadLanguageSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val languages = WSUser.getLanguages() ?: listOf()
                val languageName = SettingsHelper.languageName
                val selectedLang = languages.firstOrNull { it.code == languageName }
                cachedUser = UserUtil.user
                cachedUserGroup = UserUtil.userGroup

                _uiState.update {
                    it.copy(
                        availableLanguages = languages,
                        selectedLanguage = selectedLang,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                log.error("Error loading language settings", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load languages",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onLanguageSelected(language: RLanguage) {
        val currentSelected = _uiState.value.selectedLanguage
        if (currentSelected?.code != language.code) {
            _uiState.update {
                it.copy(
                    selectedLanguage = language,
                    isSaveEnabled = true
                )
            }
        }
    }

    fun onNetworkAvailabilityChanged(isAvailable: Boolean) {
        _uiState.update { it.copy(isNetworkAvailable = isAvailable) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveLanguageSettings(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _uiState.value

        if (!currentState.isNetworkAvailable) {
            onError("No network connection")
            return
        }

        val user = cachedUser
        val userGroup = cachedUserGroup
        val language = currentState.selectedLanguage

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
                    pushNotification = SettingsHelper.pushEnabled,
                    emailNotification = SettingsHelper.emailEnabled,
                    groupName = userGroup?.name ?: ""
                )

                if (result) {
                    log.info("Language settings saved successfully: ${language.code}")
                    SettingsHelper.languageName = language.code

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaveEnabled = false
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onError("Failed to save language settings")
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