package hr.sil.android.myappbox.compose.send_parcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.ItemRGroupInfo
import hr.sil.android.myappbox.core.remote.model.RAvailableLockerSize
import hr.sil.android.myappbox.core.remote.model.REmptyGroupMembers
import hr.sil.android.myappbox.core.remote.model.REndUserGroupMember
import hr.sil.android.myappbox.core.remote.model.REndUserInfo
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersAdmin
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersHeader
import hr.sil.android.myappbox.core.remote.model.RGroupInfo
import hr.sil.android.myappbox.core.remote.model.RLockerSize
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.remote.model.RUserRemoveAccess
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.events.MPLDevicesUpdatedEvent
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SelectParcelSizeUiState(
    val isLoading: Boolean = true,
    val isInBleProximity: Boolean = false,
    val availableLockers: List<RAvailableLockerSize> = emptyList(),
    val selectedLockerSize: RLockerSize = RLockerSize.UNKNOWN,
    val device: MPLDevice? = null,
    val errorMessage: String? = null
)

private val ROLE_USER = "USER"

//
val EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP = ""

class SelectParcelSizeViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SelectParcelSizeUiState())
    val uiState: StateFlow<SelectParcelSizeUiState> = _uiState.asStateFlow()

    //private val _uiState = MutableStateFlow(SelectParcelSizeUiState())
    //val uiState: StateFlow<SelectParcelSizeUiState> = _uiState.asStateFlow()

    private val _events = Channel<SelectParcelSizeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadDevice()
        loadAvailableLockers()
    }

    // --------------------
    // Initial loading
    // --------------------

    private fun loadDevice() {
        val device = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]

        _uiState.update {
            it.copy(
                device = device,
                isInBleProximity = device?.isInBleProximity == true
            )
        }
    }

    private fun loadAvailableLockers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val availableLockers = WSUser.getAvailableLockerSizes( MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.masterUnitId ?: 0) ?: emptyList()
                _uiState.update {
                    it.copy(
                        availableLockers = availableLockers,
                        isLoading = false
                    )
                }
            }
            catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = it.errorMessage
                    )
                }
            }
        }
    }

    // --------------------
    // User actions
    // --------------------

    fun onLockerClicked(size: RLockerSize) {
        val state = _uiState.value
        val device = state.device ?: return

        val available = state.availableLockers
            .firstOrNull { it.size == size && it.count > 0 }
            ?: return

        _uiState.update { it.copy(selectedLockerSize = size) }

        viewModelScope.launch {
            if (device.pinManagementAllowed == true) {
                _events.send(
                    SelectParcelSizeEvent.ShowPinManagement(device, size)
                )
            } else {
                _events.send(
                    SelectParcelSizeEvent.ShowGeneratedPin(device, size)
                )
            }
        }
    }

    // --------------------
    // External updates
    // --------------------

    fun onDeviceUpdated(event: MPLDevicesUpdatedEvent) {
        val updatedDevice =
            MPLDeviceStore.uniqueDevices.values
                .find { it.macAddress == SettingsHelper.userLastSelectedLocker }

        _uiState.update {
            it.copy(
                device = updatedDevice,
                isInBleProximity = updatedDevice?.isInBleProximity == true
            )
        }
    }

    fun onUnauthorized() {
        viewModelScope.launch {
            _events.send(SelectParcelSizeEvent.Unauthorized)
        }
    }

}

sealed interface SelectParcelSizeEvent {
    data class ShowPinManagement(val device: MPLDevice, val size: RLockerSize) : SelectParcelSizeEvent
    data class ShowGeneratedPin(val device: MPLDevice, val size: RLockerSize) : SelectParcelSizeEvent
    object Unauthorized : SelectParcelSizeEvent
}