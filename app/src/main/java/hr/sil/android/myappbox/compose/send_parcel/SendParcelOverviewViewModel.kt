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

object MockRCreatedLockerKeyData {

    fun createMockRCreatedLockerKeys(): MutableList<RCreatedLockerKey> {
        return mutableListOf(
            createMockKey1(),
            createMockKey2(),
            createMockKey3()
        )
    }

    private fun createMockKey1(): RCreatedLockerKey {
        return RCreatedLockerKey().apply {
            id = 1001
            isDeleting = false
            timeCreated = "2024-12-10T14:30:00Z"
            lockerId = 5001
            lockerMac = "AABBCCDDEE11"
            tan = "789456"
            pin = "1234"
            lockerMasterId = 3001
            lockerMasterMac = "AABBCCDDEE01"
            purpose = RLockerKeyPurpose.DELIVERY
            createdById = 2001
            createdByName = "John Smith"
            createdForId = 4001
            createdForEndUserName = "Alice Johnson"
            createdForEndUserEmail = "alice.johnson@example.com"
            baseId = 6001
            baseTimeCreated = "2024-12-10T10:00:00Z"
            baseGroupId = 7001
            basePurpose = "PICKUP"
            lockerSize = "Large"
            masterName = "City Center Locker Hub"
            masterAddress = "123 Main Street, Downtown"
            keyInstallationtype = InstalationType.LINUX
            isInBleProximityOrLinuxDevice = true
            isLinuxKeyDevice = InstalationType.LINUX
            deviceLatitude = 46.3089
            deviceLongitude = 16.3365
            qrCode = "QR_1001_789456"
        }
    }

    private fun createMockKey2(): RCreatedLockerKey {
        return RCreatedLockerKey().apply {
            id = 1002
            isDeleting = false
            timeCreated = "2024-12-11T09:15:00Z"
            lockerId = 5002
            lockerMac = "AABBCCDDEE22"
            tan = ""
            pin = "5678"
            lockerMasterId = 3002
            lockerMasterMac = "AABBCCDDEE02"
            purpose = RLockerKeyPurpose.DELIVERY
            createdById = 2002
            createdByName = "Sarah Williams"
            createdForId = 4002
            createdForEndUserName = "Bob Martinez"
            createdForEndUserEmail = "bob.martinez@example.com"
            baseId = 6002
            baseTimeCreated = "2024-12-11T08:00:00Z"
            baseGroupId = 7002
            basePurpose = "DELIVERY"
            lockerSize = "Medium"
            masterName = "Shopping Mall Lockers"
            masterAddress = "456 Oak Avenue, Shopping District"
            keyInstallationtype = InstalationType.DEVICE
            isInBleProximityOrLinuxDevice = true
            isLinuxKeyDevice = InstalationType.DEVICE
            deviceLatitude = 46.3105
            deviceLongitude = 16.3380
            qrCode = "QR_1002_5678"
        }
    }

    private fun createMockKey3(): RCreatedLockerKey {
        return RCreatedLockerKey().apply {
            id = 1003
            isDeleting = false
            timeCreated = "2024-12-12T16:45:00Z"
            lockerId = 5003
            lockerMac = "AABBCCDDEE33"
            tan = "123789"
            pin = ""
            lockerMasterId = 3003
            lockerMasterMac = "AABBCCDDEE03"
            purpose = RLockerKeyPurpose.DELIVERY
            createdById = 2003
            createdByName = "Michael Brown"
            createdForId = 4003
            createdForEndUserName = "Emma Davis"
            createdForEndUserEmail = "emma.davis@example.com"
            baseId = 6003
            baseTimeCreated = "2024-12-12T15:00:00Z"
            baseGroupId = 7003
            basePurpose = "STORAGE"
            lockerSize = "Small"
            masterName = "Train Station Parcel Point"
            masterAddress = "789 Railway Road, Central Station"
            keyInstallationtype = InstalationType.LINUX
            isInBleProximityOrLinuxDevice = false
            isLinuxKeyDevice = InstalationType.LINUX
            deviceLatitude = 46.3120
            deviceLongitude = 16.3395
            qrCode = "QR_1003_123789"
        }
    }

    // Additional mock keys with different scenarios
    fun createMockKeyWithoutProximity(): RCreatedLockerKey {
        return RCreatedLockerKey().apply {
            id = 1004
            isDeleting = false
            timeCreated = "2024-12-13T11:20:00Z"
            lockerId = 5004
            lockerMac = "AABBCCDDEE44"
            tan = "654321"
            pin = "9876"
            lockerMasterId = 3004
            lockerMasterMac = "AABBCCDDEE04"
            purpose = RLockerKeyPurpose.DELIVERY
            createdById = 2004
            createdByName = "Lisa Anderson"
            createdForId = 4004
            createdForEndUserName = "Tom Wilson"
            createdForEndUserEmail = "tom.wilson@example.com"
            lockerSize = "Large"
            masterName = "Airport Terminal Lockers"
            masterAddress = "999 Airport Drive, Terminal 2"
            keyInstallationtype = InstalationType.DEVICE
            isInBleProximityOrLinuxDevice = false // Not in proximity
            isLinuxKeyDevice = InstalationType.DEVICE
            deviceLatitude = 46.3150
            deviceLongitude = 16.3420
            qrCode = "QR_1004_654321"
        }
    }

    fun createMockKeyLinuxOnly(): RCreatedLockerKey {
        return RCreatedLockerKey().apply {
            id = 1005
            isDeleting = false
            timeCreated = "2024-12-14T08:00:00Z"
            lockerId = 5005
            lockerMac = "AABBCCDDEE55"
            tan = "987654"
            pin = ""
            lockerMasterId = 3005
            lockerMasterMac = "AABBCCDDEE05"
            purpose = RLockerKeyPurpose.PAH
            createdById = 2005
            createdByName = "David Lee"
            createdForId = 4005
            createdForEndUserName = "Sophie Taylor"
            createdForEndUserEmail = "sophie.taylor@example.com"
            lockerSize = "Medium"
            masterName = "University Campus Lockers"
            masterAddress = "321 Campus Drive, Building A"
            keyInstallationtype = InstalationType.LINUX
            isInBleProximityOrLinuxDevice = true
            isLinuxKeyDevice = InstalationType.LINUX
            deviceLatitude = 46.3075
            deviceLongitude = 16.3350
            qrCode = "QR_1005_987654"
        }
    }

    // Function to create mixed list with all scenarios
    fun createMixedMockKeys(): MutableList<RCreatedLockerKey> {
        return mutableListOf(
            createMockKey1(),
            createMockKey2(),
            createMockKey3(),
            createMockKeyWithoutProximity(),
            createMockKeyLinuxOnly()
        )
    }
}

class SendParcelOverviewViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(SendParcelOverviewUiState())
    val uiState: StateFlow<SendParcelOverviewUiState> = _uiState.asStateFlow()

    init {
        //loadSendParcelOverview()
        loadMixedMockData()
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

            // --- Execute BLE / API Logic ---
            delay(2000)

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

            // --- Execute BLE / API Logic ---
            delay(2000)
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

    fun loadMixedMockData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            delay(500)

            val listOfKeys = MockRCreatedLockerKeyData.createMixedMockKeys()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    allListOfKeys = listOfKeys
                )
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