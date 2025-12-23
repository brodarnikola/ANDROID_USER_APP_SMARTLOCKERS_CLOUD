package hr.sil.android.myappbox.compose.home_screen

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.remote.model.RequiredAccessRequestTypes
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.events.MPLDevicesUpdatedEvent
import hr.sil.android.myappbox.events.NewNotificationEvent
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.utils.BaseViewModel
import hr.sil.android.myappbox.utils.UiEvent
import hr.sil.android.myappbox.utils.isEmailValid
//import hr.sil.android.schlauebox.cache.DataCache
//import hr.sil.android.schlauebox.cache.status.InstallationKeyHandler
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import kotlinx.coroutines.Dispatchers

class NavHomeViewModel : ViewModel() { //BaseViewModel<NavHomeUiState, HomeScreenEvent>() {

    val log = logger()

    private val _uiState = MutableStateFlow(NavHomeUiState())
    val uiState: StateFlow<NavHomeUiState> = _uiState.asStateFlow()

    init {
        App.ref.eventBus.register(this)
        loadUserInfo()

        val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
        val displayNameOrAddress = selectedMasterDevice?.name?.ifEmpty { selectedMasterDevice.address }
        _uiState.value = _uiState.value.copy(selectedLocker = displayNameOrAddress ?: "")
    }

    private fun calculateHomeState(
        activeKeys: List<RLockerKey>,
        pahKeys: List<RCreatedLockerKey>
    ): NavHomeUiState {

        val selectedDevice =
            MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]

        val deviceAddressConfirmed =
            selectedDevice?.requiredAccessRequestTypes
                ?.any { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                ?: false

        val isPublicLocker =
            selectedDevice != null &&
                    !(UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed)

        val deliveryKeysCount =
            activeKeys.count {
                it.purpose == RLockerKeyPurpose.DELIVERY ||
                        it.purpose == RLockerKeyPurpose.PAF
            }

        val pahKeysCount =
            pahKeys.count {
                it.lockerMasterMac.macRealToClean() ==
                        selectedDevice?.macAddress?.macRealToClean()
            }

        val canCollectParcel =
            isPublicLocker &&
                    UserUtil.user?.status == "ACTIVE" &&
                    deliveryKeysCount > 0

        val canSendParcel =
            isPublicLocker &&
                    SettingsHelper.userLastSelectedLocker.isNotEmpty() &&
                    UserUtil.user?.status == "ACTIVE" &&
                    (selectedDevice?.hasUserRightsOnSendParcelLocker() ?: false) &&
                    selectedDevice?.isUserAssigned == true

        val canShareAccess =
            isPublicLocker &&
                    UserUtil.user?.status == "ACTIVE" &&
                    (selectedDevice?.hasRightsToShareAccess() ?: false) &&
                    selectedDevice?.installationType == InstalationType.DEVICE

        return _uiState.value.copy(
            isPublicLocker = isPublicLocker,
            canCollectParcel = canCollectParcel,
            canSendParcel = canSendParcel,
            canShareAccess = canShareAccess,
            deliveryKeysCount = deliveryKeysCount,
            pahKeysCount = pahKeysCount,
            activeKeys = activeKeys,
            selectedLocker = selectedDevice?.name ?: "",
            lockerAddress = selectedDevice?.address ?: "",
            finalProductName = setFinalProductName()
        )
    }

    fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            val activeKeys = WSUser.getActiveKeys() ?: emptyList()
            val pahKeys = WSUser.getActivePaHCreatedKeys() ?: emptyList()

            _uiState.update {
                calculateHomeState(activeKeys, pahKeys)
            }
        }
    }

    private fun loadUserInfo() {
        _uiState.value = _uiState.value.copy(
            userName = UserUtil.user?.name ?: "",
            address = UserUtil.user?.address ?: ""
        )
    }

    fun setFinalProductName() : String {
        val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]

        if (SettingsHelper.userLastSelectedLocker != "") {

            val productName = when {
                selectedMasterDevice?.customerProductName != "" -> {
                    selectedMasterDevice?.customerProductName
                }
                else -> ""
            }

            val uniqueUserNumber = when {
                UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L -> {
                    UserUtil.user?.uniqueId
                }
                else -> 0
            }

            val finalProductName = if (productName != "" && uniqueUserNumber != 0L)
                productName + " - " + uniqueUserNumber
            else if (productName != "" && uniqueUserNumber == 0L)
                productName
            else if (productName == "" && uniqueUserNumber != 0L)
                "" + uniqueUserNumber
            else ""

            return if ( finalProductName != "") {
                finalProductName ?: ""
            } else {
                ""
            }
        } else {
            val uniqueUserNumber =
                if (UserUtil.user?.uniqueId != null) UserUtil.user?.uniqueId else 0L

            val productName =
                if (MPLDeviceStore.uniqueDevices != null && MPLDeviceStore.uniqueDevices.values.isNotEmpty()) MPLDeviceStore.uniqueDevices.values.first().customerProductName else ""

            val finalProductName = if (productName != "" && uniqueUserNumber != 0L)
                productName + " - " + uniqueUserNumber
            else if (productName != "" && uniqueUserNumber == 0L)
                productName
            else if (productName == "" && uniqueUserNumber != 0L)
                "" + uniqueUserNumber
            else ""

            return if ( finalProductName != "") {
                finalProductName
            } else {
                ""
            }
        }

    }

    fun setLockerAddress() : String {
        val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
        return if (SettingsHelper.userLastSelectedLocker != "") {
            selectedMasterDevice?.address ?: ""
        } else {
            ""
        }
    }

    fun updateCollectSendParcel() {

        viewModelScope.launch(Dispatchers.IO) {

//            val masterUnits = WSUser.getMasterUnits()
//            val device = masterUnits?.filter { it.mac == MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.macAddress }
//
//
//
//            val test10 = WSUser.getDevicesInfo(listOf(MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.macAddress) as List<String>)
//            val aa = test10?.filter {
//                it.mac == MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.macAddress
//            }

            val activeKeys = WSUser.getActiveKeys()

            val deviceAddressConfirmed = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.requiredAccessRequestTypes
                ?.firstOrNull { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }

            val isPublicLocker = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker] != null &&
                    !(UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null)

            val canCollectParcel = isPublicLocker &&
                    UserUtil.user?.status == "ACTIVE" &&
                    activeKeys?.any {
                        it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF
                    } == true

            _uiState.update {
                it.copy(
                    canCollectParcel = canCollectParcel,
                    activeKeys = activeKeys ?: emptyList(),
                )
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNofiticationEvent(event: NewNotificationEvent) {
        loadUserData()
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onDeviceChange(event: MPLDevicesUpdatedEvent) {
//        updateCollectSendParcel()
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnauthorizedUser(event: UnauthorizedUserEvent) {
        _uiState.value = _uiState.value.copy(isUnauthorized = true)
    }
    
//    override fun initialState(): NavHomeUiState {
//        return NavHomeUiState()
//    }
//
//    override fun onEvent(event: HomeScreenEvent) {
//        when (event) {
//            is HomeScreenEvent.OnForgotPasswordRequest -> {
//
//                }
//        }
//    }

}

data class NavHomeUiState(
    val loading: Boolean = false,

    val selectedLocker: String = "",
    val lockerAddress: String = "",
    val finalProductName: String = "",

    val userName: String = "",
    val address: String = "",

    val isPublicLocker: Boolean = false,
    val canCollectParcel: Boolean = false,
    val canSendParcel: Boolean = false,
    val canShareAccess: Boolean = false,

    val deliveryKeysCount: Int = 0,
    val pahKeysCount: Int = 0,

    val activeKeys: List<RLockerKey> = emptyList(),

    val isUnauthorized: Boolean = false
)

sealed class HomeScreenEvent {
    data class OnForgotPasswordRequest(
        val email: String,
        val context: Context,
        val activity: Activity
    ) : HomeScreenEvent()
}

sealed class HomeScreenUiEvent : UiEvent {

    object NavigateBack : HomeScreenUiEvent()
}