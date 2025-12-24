package hr.sil.android.myappbox.compose.send_parcel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.cache.status.ActionStatusType
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.remote.model.clone
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macCleanToReal
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun deletePickAtHomeKeyLinux(
        lockerMac: String, lockerMasterMac: String, context: Context,
        onSuccess: () -> Unit, onError: (errorId: Int, lockerMasterMac: String) -> Unit,
        idKey: Int
    ) {
        viewModelScope.launch {

            // 1. START LOADING: Create a NEW List where the target item is marked 'isDeleting = true'
            val listWithLoading = _uiState.value.allListOfKeys.map { key ->
                if (key.id == idKey && key.lockerMasterMac == lockerMasterMac) {
                    // *** USE THE CLONE FUNCTION HERE ***
                    key.clone(newIsDeleting = true)
                } else {
                    key
                }
            }

            // 2. FORCE REFRESH: Update state with the NEW list
            _uiState.update { it.copy(allListOfKeys = listWithLoading.toMutableList()) }

            val backendResponse = WSUser.cancelPickAtHomeLinuxDevices(lockerMac)

            // 3. HANDLE RESULT
            if (backendResponse?.success ?: false) {
                // SUCCESS: Create a NEW list EXCLUDING the deleted item
                val listAfterDelete = _uiState.value.allListOfKeys.filter {
                    !(it.id == idKey && it.lockerMasterMac == lockerMasterMac)
                }

                // Ensure the result list is mutable for the UiState (as per your data class)
                _uiState.update { it.copy(allListOfKeys = listAfterDelete.toMutableList()) }
                onSuccess()
            } else {
                // ERROR: Create a NEW list resetting the loading flag
                val listReset = _uiState.value.allListOfKeys.map { key ->
                    if (key.id == idKey && key.lockerMasterMac == lockerMasterMac) {
                        // *** USE THE CLONE FUNCTION HERE ***
                        key.clone(newIsDeleting = false)
                    } else {
                        key
                    }
                }

                // Ensure the result list is mutable for the UiState (as per your data class)
                _uiState.update { it.copy(allListOfKeys = listReset.toMutableList()) }
                onError(R.string.sent_parcel_error_delete, lockerMasterMac)
            }
        }
    }

    fun deletePickAtHomeKey(
        lockerMac: String, lockerMasterMac: String, context: Context,
        onSuccess: () -> Unit, onError: (errorId: Int, lockerMasterMac: String) -> Unit,
        idKey: Int
    ) {
        viewModelScope.launch {

            // 1. START LOADING: Create a NEW List where the target item is marked 'isDeleting = true'
            val listWithLoading = _uiState.value.allListOfKeys.map { key ->
                if (key.id == idKey && key.lockerMasterMac == lockerMasterMac) {
                    // *** USE THE CLONE FUNCTION HERE ***
                    key.clone(newIsDeleting = true)
                } else {
                    key
                }
            }

            // 2. FORCE REFRESH: Update state with the NEW list
            _uiState.update { it.copy(allListOfKeys = listWithLoading.toMutableList()) }

            val communicator = MPLDeviceStore.uniqueDevices[lockerMasterMac.macCleanToReal()]?.createBLECommunicator(context)
            val userId = UserUtil.user?.id ?: 0
            var success = false

            if (communicator != null && communicator.connect() && userId != 0) {
                val response = communicator.requestParcelSendCancel(lockerMac, userId)
                communicator.disconnect()
                success = response.isSuccessful
            }

            // 3. HANDLE RESULT
            if (success) {
                // SUCCESS: Create a NEW list EXCLUDING the deleted item
                val listAfterDelete = _uiState.value.allListOfKeys.filter {
                    !(it.id == idKey && it.lockerMasterMac == lockerMasterMac)
                }

                // Ensure the result list is mutable for the UiState (as per your data class)
                _uiState.update { it.copy(allListOfKeys = listAfterDelete.toMutableList()) }
                onSuccess()
            } else {
                // ERROR: Create a NEW list resetting the loading flag
                val listReset = _uiState.value.allListOfKeys.map { key ->
                    if (key.id == idKey && key.lockerMasterMac == lockerMasterMac) {
                        // *** USE THE CLONE FUNCTION HERE ***
                        key.clone(newIsDeleting = false)
                    } else {
                        key
                    }
                }

                // Ensure the result list is mutable for the UiState (as per your data class)
                _uiState.update { it.copy(allListOfKeys = listReset.toMutableList()) }
                onError(R.string.sent_parcel_error_delete, lockerMasterMac)
            }
        }
    }

    fun loadSendParcelOverview() {
        viewModelScope.launch(Dispatchers.IO) {

            _uiState.update { it.copy(isLoading = true) }

            val listOfKeys = WSUser.getActivePaHCreatedKeys() ?: mutableListOf()

            //listOfKeys.filter { ActionStatusHandler.actionStatusDb.get(it.lockerId.toString() + ActionStatusType.PAH_ACCESS_CANCEL) == null }

            val listOfDevices =
                if (SettingsHelper.userLastSelectedLocker != "") MPLDeviceStore.uniqueDevices.values.filter { SettingsHelper.userLastSelectedLocker.macRealToClean() == it.macAddress.macRealToClean() } //&& (it.isInBleProximity || it.installationType == InstalationType.LINUX) }
                else MPLDeviceStore.uniqueDevices.values.filter { it.isInBleProximity || it.installationType == InstalationType.LINUX }

            for (keyItem in listOfKeys) {

                var isInBLEProximity = false

                for (lockerDevice in listOfDevices) {

                    if (keyItem.lockerMasterMac == lockerDevice.macAddress.macRealToClean()) {
                        keyItem.isLinuxKeyDevice =
                            lockerDevice.installationType ?: InstalationType.LINUX
                        if (lockerDevice.installationType == InstalationType.LINUX) {
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
}