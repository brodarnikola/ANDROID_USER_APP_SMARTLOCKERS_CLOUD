package hr.sil.android.myappbox.compose.collect_parcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.data.LockerKeyWithShareAccess
import hr.sil.android.myappbox.data.ShareAccessKey
import hr.sil.android.myappbox.util.SettingsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListOfDeliveriesUiState(
    val listOfDeliveries: List<LockerKeyWithShareAccess> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successKeyDelete: Boolean = false
)

class ListOfDeliveriesViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(ListOfDeliveriesUiState())
    val uiState: StateFlow<ListOfDeliveriesUiState> = _uiState.asStateFlow()

    init {
        loadListOfDeliveries()
    }

    fun deletePickAtFriendKey(keyId: Int) {
        viewModelScope.launch {
            if (WSUser.deletePaF(keyId)) {
                val updatedList = _uiState.value.listOfDeliveries.filter { it.id != keyId }
                _uiState.update {
                    it.copy(
                        listOfDeliveries = updatedList,
                        successKeyDelete = true
                    )
                }
            }
            else {
                val copy = _uiState.value.listOfDeliveries
                _uiState.update {
                    it.copy(
                        listOfDeliveries = copy,
                        successKeyDelete = false
                    )
                }
            }
        }
    }

    private fun loadListOfDeliveries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {

                val data = if (SettingsHelper.userLastSelectedLocker != "") WSUser.getActiveKeys()
                    ?.filter {
                        it.purpose != RLockerKeyPurpose.PAH
                                // && it.lockerMasterMac == SettingsHelper.userLastSelectedLocker.macRealToClean()
//                    && isUserPartOfGroup(
//                it.createdForGroup,
//                it.createdForId)
                    }?.toMutableList() ?: mutableListOf()
                else WSUser.getActiveKeys()?.filter { it.purpose != RLockerKeyPurpose.PAH }
                    ?.toMutableList() ?: mutableListOf()
                log.info("data is: ${data}")

                val remotePaFKeys =
                    WSUser.getActivePaFCreatedKeys()?.toMutableList() ?: mutableListOf()

                val listOfDeliveries = mutableListOf<LockerKeyWithShareAccess>()

                for (activeLockerKey in data) {
                    val lockerKeyWithShareAccess = LockerKeyWithShareAccess()
                    lockerKeyWithShareAccess.id = activeLockerKey.id
                    lockerKeyWithShareAccess.createdById = activeLockerKey.createdById
                    lockerKeyWithShareAccess.purpose = activeLockerKey.purpose
                    lockerKeyWithShareAccess.createdByName = activeLockerKey.createdByName
                    lockerKeyWithShareAccess.masterName = activeLockerKey.masterName
                    lockerKeyWithShareAccess.masterAddress = activeLockerKey.masterAddress
                    lockerKeyWithShareAccess.trackingNumber = activeLockerKey.trackingNumber
                    lockerKeyWithShareAccess.tan = activeLockerKey.tan
                    lockerKeyWithShareAccess.installationType = activeLockerKey.keyInstallationtype
                    lockerKeyWithShareAccess.timeCreated = activeLockerKey.timeCreated
                    lockerKeyWithShareAccess.lockerSize = activeLockerKey.lockerSize

                    for (pafKey in remotePaFKeys) {
                        if (pafKey.lockerMac == activeLockerKey.lockerMac) {
                            val shareAccessKey = ShareAccessKey()
                            shareAccessKey.id = pafKey.id
                            shareAccessKey.email = pafKey.createdForEndUserEmail ?: ""
                            lockerKeyWithShareAccess.listOfShareAccess.add(shareAccessKey)
                        }
                    }
                    listOfDeliveries.add(lockerKeyWithShareAccess)
                }

                //listOfDeliveries.addAll(listOfDeliveries)

                _uiState.update {
                    it.copy(
                        listOfDeliveries = listOfDeliveries,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                log.error("Error loading list od deliveries", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "FError loading list od deliveries",
                        isLoading = false
                    )
                }
            }
        }
    }

//    private fun mockLoadListOfDeliveries() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//
//                val data = if (SettingsHelper.userLastSelectedLocker != "") WSUser.getActiveKeys()
//                    ?.filter {
//                        it.purpose != RLockerKeyPurpose.PAH &&
//                                it.lockerMasterMac == SettingsHelper.userLastSelectedLocker.macRealToClean()
////                    && isUserPartOfGroup(
////                it.createdForGroup,
////                it.createdForId)
//                    }?.toMutableList() ?: mutableListOf()
//                else WSUser.getActiveKeys()?.filter { it.purpose != RLockerKeyPurpose.PAH }
//                    ?.toMutableList() ?: mutableListOf()
//                log.info("data is: ${data}")
//
//                val remotePaFKeys =
//                    WSUser.getActivePaFCreatedKeys()?.toMutableList() ?: mutableListOf()
//
//                val listOfDeliveries = mutableListOf<LockerKeyWithShareAccess>()
//
//                // Mock active locker keys
//                val mockActiveKeys = listOf(
//                    createMockRCreatedLockerKey(
//                        id = 1,
//                        lockerId = 101,
//                        lockerMac = "AA:BB:CC:DD:EE:01",
//                        lockerMasterId = 201,
//                        lockerMasterMac = "AA:BB:CC:DD:EE:10",
//                        tan = "123456",
//                        pin = "1234",
//                        purpose = RLockerKeyPurpose.DELIVERY,
//                        createdById = 301,
//                        createdByName = "John Doe",
//                        createdForId = 401,
//                        createdForEndUserName = "Jane Smith",
//                        createdForEndUserEmail = "jane.smith@example.com",
//                        lockerSize = "Medium",
//                        masterName = "Downtown Locker Station",
//                        masterAddress = "123 Main Street, City Center",
//                        installationType = InstalationType.LINUX,
//                        trackingNumber = "TRK123456789",
//                        timeCreated = "2024-12-14T10:30:00Z",
//                        latitude = 46.3089,
//                        longitude = 16.3365
//                    ),
//                    createMockRCreatedLockerKey(
//                        id = 2,
//                        lockerId = 102,
//                        lockerMac = "AA:BB:CC:DD:EE:02",
//                        lockerMasterId = 202,
//                        lockerMasterMac = "AA:BB:CC:DD:EE:11",
//                        tan = "789012",
//                        pin = "5678",
//                        purpose = RLockerKeyPurpose.DELIVERY,
//                        createdById = 302,
//                        createdByName = "Alice Johnson",
//                        createdForId = 402,
//                        createdForEndUserName = "Bob Wilson",
//                        createdForEndUserEmail = "bob.wilson@example.com",
//                        lockerSize = "Large",
//                        masterName = "Shopping Mall Locker Hub",
//                        masterAddress = "456 Oak Avenue, Shopping District",
//                        installationType = InstalationType.DEVICE,
//                        trackingNumber = "TRK987654321",
//                        timeCreated = "2024-12-13T14:45:00Z",
//                        latitude = 46.3105,
//                        longitude = 16.3380
//                    )
//                )
//
//                // Mock PaF keys (share access keys)
//                val mockPaFKeys = listOf(
//                    createMockShareAccessKey(
//                        id = 501,
//                        email = "shared.user1@example.com",
//                        lockerMac = "AA:BB:CC:DD:EE:01"
//                    ),
//                    createMockShareAccessKey(
//                        id = 602,
//                        email = "shared.user2@example.com",
//                        lockerMac = "AA:BB:CC:DD:EE:01"
//                    ),
//                    createMockShareAccessKey(
//                        id = 703,
//                        email = "shared.user3@example.com",
//                        lockerMac = "AA:BB:CC:DD:EE:02"
//                    )
//                )
//
//                // Build the list of deliveries with share access
//                for (activeLockerKey in mockActiveKeys) {
//                    val lockerKeyWithShareAccess = LockerKeyWithShareAccess()
//                    lockerKeyWithShareAccess.id = activeLockerKey.id
//                    lockerKeyWithShareAccess.createdById = activeLockerKey.createdById
//                    lockerKeyWithShareAccess.purpose = activeLockerKey.purpose
//                    lockerKeyWithShareAccess.createdByName = activeLockerKey.createdByName
//                    lockerKeyWithShareAccess.masterName = activeLockerKey.masterName
//                    lockerKeyWithShareAccess.masterAddress = activeLockerKey.masterAddress
//                    //lockerKeyWithShareAccess.trackingNumber = activeLockerKey.trackingNumber
//                    lockerKeyWithShareAccess.tan = activeLockerKey.tan.toInt()
//                    lockerKeyWithShareAccess.installationType = activeLockerKey.keyInstallationtype
//                    lockerKeyWithShareAccess.timeCreated = activeLockerKey.timeCreated
//                    lockerKeyWithShareAccess.lockerSize = activeLockerKey.lockerSize
//
//                    for (pafKey in mockPaFKeys) {
//                        if (pafKey.lockerMac == activeLockerKey.lockerMac) {
//                            val shareAccessKey = ShareAccessKey()
//                            shareAccessKey.id = pafKey.id
//                            shareAccessKey.email = pafKey.email
//                            lockerKeyWithShareAccess.listOfShareAccess.add(shareAccessKey)
//                        }
//                    }
//
//                    listOfDeliveries.add(lockerKeyWithShareAccess)
//                }
//
////                for (activeLockerKey in data) {
////                    val lockerKeyWithShareAccess = LockerKeyWithShareAccess()
////                    lockerKeyWithShareAccess.id = activeLockerKey.id
////                    lockerKeyWithShareAccess.createdById = activeLockerKey.createdById
////                    lockerKeyWithShareAccess.purpose = activeLockerKey.purpose
////                    lockerKeyWithShareAccess.createdByName = activeLockerKey.createdByName
////                    lockerKeyWithShareAccess.masterName = activeLockerKey.masterName
////                    lockerKeyWithShareAccess.masterAddress = activeLockerKey.masterAddress
////                    lockerKeyWithShareAccess.trackingNumber = activeLockerKey.trackingNumber
////                    lockerKeyWithShareAccess.tan = activeLockerKey.tan
////                    lockerKeyWithShareAccess.installationType = activeLockerKey.keyInstallationtype
////                    lockerKeyWithShareAccess.timeCreated = activeLockerKey.timeCreated
////                    lockerKeyWithShareAccess.lockerSize = activeLockerKey.lockerSize
////
////                    for (pafKey in remotePaFKeys) {
////                        if (pafKey.lockerMac == activeLockerKey.lockerMac) {
////                            val shareAccessKey = ShareAccessKey()
////                            shareAccessKey.id = pafKey.id
////                            shareAccessKey.email = pafKey.createdForEndUserEmail ?: ""
////                            lockerKeyWithShareAccess.listOfShareAccess.add(shareAccessKey)
////                        }
////                    }
////                    listOfDeliveries.add(lockerKeyWithShareAccess)
////                }
////
////                listOfDeliveries.addAll(listOfDeliveries)
//
//                _uiState.update {
//                    it.copy(
//                        listOfDeliveries = listOfDeliveries,
//                        isLoading = false
//                    )
//                }
//            } catch (e: Exception) {
//                log.error("Error loading list od deliveries", e)
//                _uiState.update {
//                    it.copy(
//                        errorMessage = "FError loading list od deliveries",
//                        isLoading = false
//                    )
//                }
//            }
//        }
//    }
//
//    private fun createMockRCreatedLockerKey(
//        id: Int,
//        lockerId: Int,
//        lockerMac: String,
//        lockerMasterId: Int,
//        lockerMasterMac: String,
//        tan: String,
//        pin: String,
//        purpose: RLockerKeyPurpose,
//        createdById: Int?,
//        createdByName: String?,
//        createdForId: Int?,
//        createdForEndUserName: String?,
//        createdForEndUserEmail: String?,
//        lockerSize: String?,
//        masterName: String?,
//        masterAddress: String?,
//        installationType: InstalationType,
//        trackingNumber: String,
//        timeCreated: String,
//        latitude: Double,
//        longitude: Double
//    ): RCreatedLockerKey {
//        return RCreatedLockerKey().apply {
//            this.id = id
//            this.lockerId = lockerId
//            this.lockerMac = lockerMac
//            this.lockerMasterId = lockerMasterId
//            this.lockerMasterMac = lockerMasterMac
//            this.tan = tan
//            this.pin = pin
//            this.purpose = purpose
//            this.createdById = createdById
//            this.createdByName = createdByName
//            this.createdForId = createdForId
//            this.createdForEndUserName = createdForEndUserName
//            this.createdForEndUserEmail = createdForEndUserEmail
//            this.lockerSize = lockerSize
//            this.masterName = masterName
//            this.masterAddress = masterAddress
//            this.keyInstallationtype = installationType
//            //this.trackingNumber = trackingNumber
//            this.timeCreated = timeCreated
//            this.deviceLatitude = latitude
//            this.deviceLongitude = longitude
//            this.isInBleProximityOrLinuxDevice = true
//            this.isLinuxKeyDevice = installationType
//            this.qrCode = "QR_${id}_${tan}"
//        }
//    }
//
//    private fun createMockShareAccessKey(
//        id: Int,
//        email: String,
//        lockerMac: String
//    ): MockPaFKey {
//        return MockPaFKey(
//            id = id,
//            email = email,
//            lockerMac = lockerMac
//        )
//    }
//
//    data class MockPaFKey(
//        val id: Int,
//        val email: String,
//        val lockerMac: String
//    )


}