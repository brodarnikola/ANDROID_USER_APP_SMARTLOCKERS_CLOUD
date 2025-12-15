package hr.sil.android.myappbox.compose.send_parcel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.cache.status.ActionStatusHandler
import hr.sil.android.myappbox.cache.status.ActionStatusType
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.utils.isEmailValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SendParcelOverviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val allListOfKeys: MutableList<RCreatedLockerKey> = mutableListOf()
)

class SendParcelOverviewViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SendParcelOverviewUiState())
    val uiState: StateFlow<SendParcelOverviewUiState> = _uiState.asStateFlow()

    init {
        loadSendParcelOverview()
    }

    fun loadSendParcelOverview( ) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {


            val listOfKeys = WSUser.getActivePaHCreatedKeys() ?: mutableListOf()

            //listOfKeys.filter { ActionStatusHandler.actionStatusDb.get(it.lockerId.toString() + ActionStatusType.PAH_ACCESS_CANCEL) == null }

            val listOfDevices = if( SettingsHelper.userLastSelectedLocker != "" ) MPLDeviceStore.uniqueDevices.values.filter { SettingsHelper.userLastSelectedLocker.macRealToClean() == it.macAddress.macRealToClean() && (it.isInBleProximity || it.installationType == InstalationType.LINUX) }
            else MPLDeviceStore.uniqueDevices.values.filter { it.isInBleProximity || it.installationType == InstalationType.LINUX }

            for (keyItem in listOfKeys) {

                var isInBLEProximity = false

                for (lockerDevice in listOfDevices) {

                    if (keyItem.lockerMasterMac == lockerDevice.macAddress.macRealToClean()) {
                        keyItem.isLinuxKeyDevice = lockerDevice.installationType ?: InstalationType.LINUX
                        if( lockerDevice.installationType == InstalationType.LINUX ) {
                            keyItem.deviceLatitude = lockerDevice.latitude
                            keyItem.deviceLongitude = lockerDevice.longitude
                        }
                        isInBLEProximity = true
                        break
                    }
                }

                keyItem.isInBleProximityOrLinuxDevice = isInBLEProximity
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    allListOfKeys = listOfKeys
                )
            }
        }
    }

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
                            isLoading = false
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    onError(R.string.app_generic_error)
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false
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