package hr.sil.android.myappbox.compose.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.model.MPLDeviceType
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.AccessRequestResponseEnum
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RAccessDetaislResponse
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import hr.sil.android.myappbox.data.DeviceData

sealed class LockerListItem {
    data class Header(
        val title: String,
        val itemCount: Int,
        val headerIndex: Int,
        var isExpanded: Boolean = true
    ) : LockerListItem()

    data class LockerItem(
        val deviceData: DeviceData,
        val headerIndex: Int,
        var isExpanded: Boolean = true
    ) : LockerListItem()
}

data class SelectLockerUiState(
    val title: String = "",
    val lockerItems: List<LockerListItem> = emptyList(),
    val selectedMacAddress: String = "",
    val isLoading: Boolean = true,
    val showConfirmButton: Boolean = false,
    val errorMessage: String? = null
)

class SelectLockerViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SelectLockerUiState())
    val uiState: StateFlow<SelectLockerUiState> = _uiState.asStateFlow()

    private var devicesWithDetailsResponse = mutableListOf<RAccessDetaislResponse>()

    init {
        _uiState.update {
            it.copy(selectedMacAddress = SettingsHelper.userLastSelectedLocker)
        }
        App.ref.selectedMasterMacAddress = SettingsHelper.userLastSelectedLocker
        loadLockers()
    }

    fun loadLockers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                devicesWithDetailsResponse = withContext(Dispatchers.IO) {
                    WSUser.getAccessDetails()?.toMutableList() ?: mutableListOf()
                }

                val lockerList = MPLDeviceStore.uniqueDevices.values.filter {
                    val isThisDeviceAvailable = when {
                        UserUtil.user?.testUser == true -> true
                        else -> it.isProductionReady == true
                    }
                    isThisDeviceAvailable
                }.toMutableList()

                if (lockerList.isNotEmpty()) {
                    val items = buildLockerListItems(lockerList)
                    _uiState.update {
                        it.copy(
                            title = "Select Locker",
                            lockerItems = items,
                            showConfirmButton = true,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            title = "No lockers in proximity",
                            lockerItems = emptyList(),
                            showConfirmButton = false,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                log.error("Error loading lockers", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load lockers"
                    )
                }
            }
        }
    }

    private fun buildLockerListItems(lockerList: MutableList<MPLDevice>): List<LockerListItem> {
        val result = mutableListOf<LockerListItem>()
        val unsortedLockerList = mutableListOf<DeviceData>()

        for (device in lockerList) {
            if (device.isPublicDevice == false && device.isUserAssigned == false) {
                continue
            }

            val deviceData = DeviceData().apply {
                macAddress = device.macAddress
                deviceName = device.name
                deviceAddress = device.address
                isInBleProximity = device.isInBleProximity
                installationType = device.installationType ?: InstalationType.UNKNOWN
                bleDeviceType = device.type
                backendDeviceType = device.masterUnitType
                isSplActivate = device.isSplActivate
                latitude = device.latitude
                longitude = device.longitude
                publicDevice = device.isPublicDevice ?: false
                isUserAssigned = device.isUserAssigned ?: false
                activeAccessRequest = device.activeAccessRequest ?: false
                requiredAccessRequestTypes = device.requiredAccessRequestTypes
                isSelected = App.ref.selectedMasterMacAddress == macAddress
            }
            unsortedLockerList.add(deviceData)
        }

        val userHasRightsInProximityList = unsortedLockerList.filter {
            it.isUserAssigned && ((it.installationType == InstalationType.LINUX) || it.isInBleProximity)
        }

        val userHasRightsNotInProximityList = unsortedLockerList.filter {
            (it.installationType != InstalationType.LINUX && !it.isInBleProximity) && it.isUserAssigned
        }

        val userDisabledRightsInProximityList = unsortedLockerList.filter {
            val isCorrectDevice = if ((it.backendDeviceType == RMasterUnitType.SPL || it.backendDeviceType == RMasterUnitType.SPL_PLUS) && it.isSplActivate) {
                false
            } else true
            !it.isUserAssigned && isCorrectDevice
        }

        var headerIndex = 0

        if (userHasRightsInProximityList.isNotEmpty()) {
            result.add(LockerListItem.Header(
                title = "Registered lockers in proximity",
                itemCount = userHasRightsInProximityList.size,
                headerIndex = headerIndex
            ))
            userHasRightsInProximityList.forEach { device ->
                device.indexOfHeader = headerIndex
                result.add(LockerListItem.LockerItem(device, headerIndex))
            }
            headerIndex++
        }

        if (userHasRightsNotInProximityList.isNotEmpty()) {
            result.add(LockerListItem.Header(
                title = "Registered lockers not in proximity",
                itemCount = userHasRightsNotInProximityList.size,
                headerIndex = headerIndex
            ))
            userHasRightsNotInProximityList.forEach { device ->
                device.indexOfHeader = headerIndex
                result.add(LockerListItem.LockerItem(device, headerIndex))
            }
            headerIndex++
        }

        if (userDisabledRightsInProximityList.isNotEmpty()) {
            result.add(LockerListItem.Header(
                title = "Unregistered lockers",
                itemCount = userDisabledRightsInProximityList.size,
                headerIndex = headerIndex
            ))
            userDisabledRightsInProximityList.forEach { device ->
                device.indexOfHeader = headerIndex
                result.add(LockerListItem.LockerItem(device, headerIndex))
            }
        }

        return result
    }

    fun onLockerSelected(macAddress: String) {
        _uiState.update { state ->
            val updatedItems = state.lockerItems.map { item ->
                when (item) {
                    is LockerListItem.LockerItem -> {
                        val updatedDevice = item.deviceData.apply {
                            isSelected = this.macAddress == macAddress
                        }
                        item.copy(deviceData = updatedDevice)
                    }
                    else -> item
                }
            }
            state.copy(
                lockerItems = updatedItems,
                selectedMacAddress = macAddress
            )
        }
        App.ref.selectedMasterMacAddress = macAddress
    }

    fun onHeaderToggle(headerIndex: Int) {
        _uiState.update { state ->
            val updatedItems = state.lockerItems.map { item ->
                when (item) {
                    is LockerListItem.Header -> {
                        if (item.headerIndex == headerIndex) {
                            item.copy(isExpanded = !item.isExpanded)
                        } else item
                    }
                    is LockerListItem.LockerItem -> {
                        if (item.headerIndex == headerIndex) {
                            item.copy(isExpanded = !item.isExpanded)
                        } else item
                    }
                }
            }
            state.copy(lockerItems = updatedItems)
        }
    }

    fun onConfirm(onSuccess: () -> Unit) {
        SettingsHelper.userLastSelectedLocker = App.ref.selectedMasterMacAddress
        onSuccess()
    }

    fun requestAccess(macAddress: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    WSUser.requestMPlAccess(macAddress.macRealToClean())
                }
                when (response?.result) {
                    AccessRequestResponseEnum.PENDING -> {
                        updateLockerAccessRequest(macAddress, true)
                        onResult(true, "Request sent")
                    }
                    AccessRequestResponseEnum.GRANTED -> {
                        updateLockerUserAssigned(macAddress, true)
                        onResult(true, "Access granted")
                    }
                    else -> {
                        onResult(false, "Something went wrong")
                    }
                }
            } catch (e: Exception) {
                log.error("Error requesting access", e)
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    fun activateSPL(macAddress: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    WSUser.activateSPL(macAddress.macRealToClean())
                }
                if (success) {
                    onResult(true, "Success")
                } else {
                    onResult(false, "Something went wrong")
                }
            } catch (e: Exception) {
                log.error("Error activating SPL", e)
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    private fun updateLockerAccessRequest(macAddress: String, hasActiveRequest: Boolean) {
        _uiState.update { state ->
            val updatedItems = state.lockerItems.map { item ->
                when (item) {
                    is LockerListItem.LockerItem -> {
                        if (item.deviceData.macAddress == macAddress) {
                            val updatedDevice = item.deviceData.apply {
                                activeAccessRequest = hasActiveRequest
                            }
                            item.copy(deviceData = updatedDevice)
                        } else item
                    }
                    else -> item
                }
            }
            state.copy(lockerItems = updatedItems)
        }
    }

    private fun updateLockerUserAssigned(macAddress: String, isAssigned: Boolean) {
        _uiState.update { state ->
            val updatedItems = state.lockerItems.map { item ->
                when (item) {
                    is LockerListItem.LockerItem -> {
                        if (item.deviceData.macAddress == macAddress) {
                            val updatedDevice = item.deviceData.apply {
                                isUserAssigned = isAssigned
                            }
                            item.copy(deviceData = updatedDevice)
                        } else item
                    }
                    else -> item
                }
            }
            state.copy(lockerItems = updatedItems)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}