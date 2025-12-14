package hr.sil.android.myappbox.compose.collect_parcel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.remote.model.RLanguage
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.data.LockerKeyWithShareAccess
import hr.sil.android.myappbox.data.ShareAccessKey
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.utils.isEmailValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ShareAccessKeyUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addSuccessKey: Boolean = false
)

class ShareAccessKeyViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(ShareAccessKeyUiState())
    val uiState: StateFlow<ShareAccessKeyUiState> = _uiState.asStateFlow()


    fun addShareAccessKey(
        email: String, shareAccessKeyId: Int, onSuccess: () -> Unit, onError: (errorId: Int) -> Unit
    ) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            val exists = WSUser.getGroupMembers()
                ?.any { it.email == email } == true

            if (!exists) {
                val result = WSUser.createPaF(shareAccessKeyId, email)
                if (result?.invitationCode.isNullOrEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            addSuccessKey = true
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            addSuccessKey = false
                        )
                    }
                    onError(R.string.app_generic_error)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        addSuccessKey = false
                    )
                }
                onError(R.string.grant_access_error_exists)
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
}