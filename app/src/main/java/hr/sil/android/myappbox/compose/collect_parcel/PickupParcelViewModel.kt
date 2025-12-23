package hr.sil.android.myappbox.compose.collect_parcel

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.sil.android.myappbox.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.constraintlayout.compose.ConstraintLayout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.MPLDeviceStatus
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.cache.DatabaseHandler
import hr.sil.android.myappbox.cache.status.ActionStatusHandler
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.dialog.DeleteAccessShareUserDialog
import hr.sil.android.myappbox.compose.home_screen.format
import hr.sil.android.myappbox.compose.main_activity.MainDestinations
import hr.sil.android.myappbox.compose.settings.SettingsItem
import hr.sil.android.myappbox.core.ble.comm.model.LockerFlagsUtil
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.ItemRGroupInfo
import hr.sil.android.myappbox.core.remote.model.REmptyGroupMembers
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersAdmin
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersHeader
import hr.sil.android.myappbox.core.remote.model.RLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.remote.model.RUserAccessRole
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macCleanToBytes
import hr.sil.android.myappbox.core.util.macCleanToReal
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.data.DeliveryKey
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.List

import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.utils.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

import hr.sil.android.rest.core.util.hexToByteArray

data class PickupParcelState(
    val isInProximity: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val isAnimating: Boolean = false,
    val animationRotation: Float = 0f,
    val isUnlocked: Boolean = false,
    val isError: Boolean = false,
    val statusText: String = "",
    val titleRes: Int = R.string.nav_pickup_parcel_lock,
    val circleDrawableRes: Int = R.drawable.progress_stopped,
    val keys: List<RCreatedLockerKey> = emptyList(),
    val showTelemetry: Boolean = false,
    val humidity: String = "-",
    val temperature: String = "-",
    val airPressure: String = "-",
    val showForceOpen: Boolean = false,
    val showFinishButton: Boolean = false,
    val showCleaningCheckbox: Boolean = false,
    val isCleaningCheckboxEnabled: Boolean = true,
    val isCleaningCheckboxChecked: Boolean = false,
    val showCleaningProgress: Boolean = false,
    val deviceName: String = "",
    val lockerMacAddress: String = "",
    val installationType: InstalationType = InstalationType.UNKNOWN,
    val masterUnitType: RMasterUnitType = RMasterUnitType.MPL,
    val loading: Boolean = false
)

sealed class PickupParcelScreenEvent {
    data class OnInit(val macAddress: String, val context: Context) : PickupParcelScreenEvent()
    data class OnOpenClick(val context: Context, val activity: Activity) : PickupParcelScreenEvent()
    data class OnForceOpenClick(val context: Context, val activity: Activity) :
        PickupParcelScreenEvent()

    data class OnFinishClick(val activity: Activity) : PickupParcelScreenEvent()
    data class OnConfirmPickAtFriendKeyClick(
        val email: String,
        val pickAtFriendKeyId: Int,
        val onSuccess: () -> Unit,
        val onInvitationCode: () -> Unit,
        val onError: () -> Unit
    ) : PickupParcelScreenEvent()

    data class OnDeleteKeyClick(
        val position: Int,
        val keyId: Int,
        val onSuccess: () -> Unit,
        val onError: () -> Unit
    ) : PickupParcelScreenEvent()

    data class OnQrCodeClick(val macAddress: String, val context: Context, val activity: Activity) :
        PickupParcelScreenEvent()

    data class OnCleaningCheckboxClick(
        val lockerMacAddress: String,
        val context: Context,
        val activity: Activity
    ) : PickupParcelScreenEvent()
}

sealed class PickupParcelScreenUiEvent {
    data class NavigateToQrCode(val macAddress: String) : PickupParcelScreenUiEvent()
    object NavigateToFinish : PickupParcelScreenUiEvent()
}

class PickupParcelViewModel() : ViewModel() {

    val log = logger()

    private val _state = MutableStateFlow(PickupParcelState())
    val state: StateFlow<PickupParcelState> = _state.asStateFlow()

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiEventsPickupParcel = Channel<PickupParcelScreenUiEvent>()
    val uiEventsPickupParcel = _uiEventsPickupParcel.receiveAsFlow()

    private var device: MPLDevice? = null
    private val openedParcels = mutableListOf<String>()
    private val connecting = AtomicBoolean(false)
    private val lockerLoaderRunning = AtomicBoolean(false)
    private val startingTime = Date()
    private var exitTime: Date? = null
    private val denyProcedureDuration = 60000L
    private val MAC_ADDRESS_7_BYTE_LENGTH = 14
    private val MAC_ADDRESS_6_BYTE_LENGTH = 12
    private val MAC_ADDRESS_LAST_BYTE_LENGTH = 2

    fun onEvent(event: PickupParcelScreenEvent) {
        when (event) {
            is PickupParcelScreenEvent.OnInit -> {
                device = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
                setupAdapterForKeys()
                setupOpenButton()
            }

            is PickupParcelScreenEvent.OnOpenClick -> {
                handleOpenClick(event.context, event.activity)
            }

            is PickupParcelScreenEvent.OnForceOpenClick -> {
                handleForceOpenClick(event.context, event.activity)
            }

            is PickupParcelScreenEvent.OnFinishClick -> {
                viewModelScope.launch {

//                    MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.activeKeys?.toMutableList()
//                        ?.removeAll { key -> key.lockerMasterMac.macCleanToReal() == SettingsHelper.userLastSelectedLocker }
//
//                    _state.value.keys.toMutableList()
//                        .removeAll { key -> key.lockerMasterMac.macCleanToReal() == SettingsHelper.userLastSelectedLocker }
//                    _state.update { it.copy(keys = listOf()) }

                    _uiEventsPickupParcel.send(PickupParcelScreenUiEvent.NavigateToFinish)
                }
            }

            is PickupParcelScreenEvent.OnDeleteKeyClick -> {
                deletePickAtFriendKey(event.position, event.keyId, event.onSuccess, event.onError)
            }

            is PickupParcelScreenEvent.OnConfirmPickAtFriendKeyClick -> {
                confirmPickAtFriendKey(
                    event.email,
                    event.pickAtFriendKeyId,
                    event.onSuccess,
                    event.onInvitationCode,
                    event.onError
                )
            }

            is PickupParcelScreenEvent.OnQrCodeClick -> {
                viewModelScope.launch {
                    _uiEventsPickupParcel.send(
                        PickupParcelScreenUiEvent.NavigateToQrCode(
                            SettingsHelper.userLastSelectedLocker
                        )
                    )
                    // _uiEvents.send(PickupParcelScreenUiEvent.NavigateToQrCode(SettingsHelper.userLastSelectedLocker))
                }
            }

            is PickupParcelScreenEvent.OnCleaningCheckboxClick -> {
                handleCleaningCheckbox(event.lockerMacAddress, event.context, event.activity)
            }
        }
    }

    private fun confirmPickAtFriendKey(
        email: String,
        pickAtFriendKeyId: Int,
        onSuccess: () -> Unit,
        onInvitationCode: () -> Unit,
        onError: () -> Unit
    ) {
        ActionStatusHandler.log.info("onConfirm email 33: ${email}, ${pickAtFriendKeyId}")
        viewModelScope.launch {

            val groups = WSUser.getGroupMembers() // DataCache.getGroupMembers()
            val groupMemberships = groups?.any { it.email == email } ?: false

            if (!groupMemberships) {
                //log.info("PaF share $email - $email")
                //log.info("P@h created $pickAtFriendKeyId mail: $email")
                val returnedData = WSUser.createPaF(pickAtFriendKeyId, email)
                log.info("Invitation key = ${returnedData?.invitationCode}")
                if (returnedData?.invitationCode.isNullOrEmpty()) {

                    _uiEvents.send(
                        UiEvent.ShowToast(
                            App.ref.getString(
                                R.string.app_generic_success
                            )
                        )
                    )

                    val keys = combineLockerKeys()
                    _state.update { it.copy(keys = keys) }
                    onSuccess()
                } else {
                    onInvitationCode()
                }

            } else {
                _uiEvents.send(
                    UiEvent.ShowToast(
                        App.ref.getString(
                            R.string.grant_access_error_exists
                        )
                    )
                )
                onError()
            }
        }
    }

    private fun setupAdapterForKeys() {
        viewModelScope.launch {
            val keys = combineLockerKeys()
            _state.update { it.copy(keys = keys) }
        }
    }

    private suspend fun combineLockerKeys(): List<RCreatedLockerKey> {
        val keysAssignedToUser =
            MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.activeKeys?.filter {
                it.lockerMasterMac.macCleanToReal() == SettingsHelper.userLastSelectedLocker &&
                        it.purpose != RLockerKeyPurpose.PAH &&
                        isUserPartOfGroup(it.createdForGroup, it.createdForId)
            }?.map {
                RCreatedLockerKey().apply {
                    this.id = it.id
                    this.createdById = it.createdById
                    this.lockerMac = it.lockerMac
                    this.lockerId = it.lockerId
                    this.lockerMasterId = it.lockerMasterId
                    this.lockerMasterMac = it.lockerMasterMac
                    this.createdByName = it.createdByName
                    this.purpose = it.purpose
                    this.masterAddress = it.masterAddress
                    this.masterName = it.masterName
                    this.lockerSize = it.lockerSize
                    this.timeCreated = it.timeCreated ?: ""
                    this.qrCode = it.qrCode ?: ""
                }
            }?.toMutableList() ?: mutableListOf()

        log.info("Assigned keys ${keysAssignedToUser.size}")

        val remotePaFKeys = WSUser.getActivePaFCreatedKeys()
        val createdPaFKeys = remotePaFKeys?.filter {
            it.lockerMasterMac.macCleanToReal() == SettingsHelper.userLastSelectedLocker //.macRealToClean()
        } ?: mutableListOf()

        log.info("PAF keys ${createdPaFKeys.size}")

        keysAssignedToUser.addAll(createdPaFKeys)
        return keysAssignedToUser.sortedByDescending { it.purpose }.sortedBy { it.timeCreated }
    }

    private fun isUserPartOfGroup(createdForGroup: Int?, createdForId: Int?): Boolean {
        return UserUtil.userMemberships.find {
            it.groupId == createdForGroup && it.role == RUserAccessRole.ADMIN.name
        } != null || UserUtil.userGroup?.id == createdForGroup || UserUtil.user?.id == createdForId
    }

//    private fun setupOpenButton() {
//        displayTelemetryOfDevice()
//
//        val isLinux = device?.installationType == InstalationType.LINUX
//        val isBleProximity = device?.isInBleProximity == true
//        val canOpenDoor = isOpenDoorPossible()
//
//        val isInProximity = isLinux || isBleProximity
//        val isButtonEnabled = isLinux || (isBleProximity && canOpenDoor)
//
//        _state.update { currentState ->
//            currentState.copy(
//                // visibility flags
//                isInProximity = isInProximity,
//                isButtonEnabled = isButtonEnabled,
//
//                // device info
//                deviceName = device?.name.orEmpty(),
//                installationType = device?.installationType ?: InstalationType.UNKNOWN,
//                masterUnitType = device?.masterUnitType ?: RMasterUnitType.MPL,
//
//                // title logic
//                titleRes = when {
//                    isButtonEnabled ->
//                        R.string.nav_pickup_parcel_lock
//
//                    isBleProximity ->
//                        R.string.nav_pickup_parcel_unlock
//
//                    else ->
//                        R.string.app_generic_enter_ble
//                },
//
//                // status text logic
//                statusText = when {
//                    isButtonEnabled ->
//                        App.ref.getString(R.string.nav_pickup_parcel_content_lock)
//
//                    isBleProximity ->
//                        App.ref.getString(R.string.nav_pickup_parcel_content_unlock)
//
//                    else ->
//                        App.ref.getString(R.string.not_in_proximity_first_description)
//                }
//            )
//        }
//    }

    private fun setupOpenButton() {
        displayTelemetryOfDevice()

        val anyKeysToPickup = device?.installationType == InstalationType.LINUX ||
                (device?.isInBleProximity == true && isOpenDoorPossible())

        val isInProximity = device?.installationType == InstalationType.LINUX ||
                (device?.isInBleProximity == true )

        val isButtonEnabledToOpen = device?.installationType == InstalationType.LINUX ||
                (device?.isInBleProximity == true && isOpenDoorPossible())

        val allKeysAlreadyPickup = device?.installationType == InstalationType.LINUX ||
                (device?.isInBleProximity == true && !isOpenDoorPossible())

        _state.update { currentState ->
            currentState.copy(
                isUnlocked = allKeysAlreadyPickup,
                isInProximity = isInProximity,
                isButtonEnabled = isButtonEnabledToOpen,
                deviceName = device?.name ?: "",
                installationType = device?.installationType ?: InstalationType.UNKNOWN,
                masterUnitType = device?.masterUnitType ?: RMasterUnitType.MPL,
                titleRes = if (anyKeysToPickup) R.string.nav_pickup_parcel_lock else if (allKeysAlreadyPickup) R.string.nav_pickup_parcel_unlock else R.string.app_generic_enter_ble,
                statusText = if (anyKeysToPickup) {
                    App.ref.getString(R.string.nav_pickup_parcel_content_lock)
                }
                else if (allKeysAlreadyPickup) App.ref.getString(R.string.nav_pickup_parcel_content_unlock)
                else {
                    App.ref.getString(R.string.not_in_proximity_first_description)
                }
            )
        }
    }

    private fun displayTelemetryOfDevice() {
        if (device?.mplMasterDeviceStatus == MPLDeviceStatus.REGISTERED &&
            device?.isInBleProximity == true
        ) {
            val temperatureS = device?.temperature?.format(2) ?: "-"
            val pressureS = device?.pressure?.format(2) ?: "-"
            val humidityS = device?.humidity?.format(2) ?: "-"

            _state.update {
                it.copy(
                    showTelemetry = true,
                    humidity = "$humidityS %",
                    temperature = "$temperatureS C",
                    airPressure = "$pressureS hPa"
                )
            }
        } else {
            _state.update { it.copy(showTelemetry = false) }
        }
    }

    private fun isOpenDoorPossible(): Boolean {

        var hasUnusedKeys = false
        val keys = DatabaseHandler.deliveryKeyDb.get(SettingsHelper.userLastSelectedLocker)
//        if (keys == null) {
//            return device?.activeKeys?.filter { it.purpose != RLockerKeyPurpose.PAH }?.isNotEmpty()
//                ?: false
//        }
//        else {

//           device?.activeKeys?.forEach {
//                if (it.purpose != RLockerKeyPurpose.PAH && !keys.keyIds.contains(it.id)) {
//                    hasUnusedKeys = true
//                    return@forEach
//                }
//            }
//        }

        return device?.isInBleProximity ?: false &&  device?.hasUserRightsOnLocker() ?: false && keys?.keyIds?.isNotEmpty() == true // && hasUnusedKeys

    }

//    private fun isOpenDoorPossibleWrong(): Boolean {
//
//        val keys = device?.activeKeys?.any { it.purpose != RLockerKeyPurpose.PAH }
//        val userRights = device?.hasUserRightsOnLocker() ?: false
//
//        return if (keys == true && userRights) {
//            true
//        } else {
//            device?.isInBleProximity ?: false && userRights
//        }
//    }

    private fun handleOpenClick(context: Context, activity: Activity) {
        if (connecting.compareAndSet(false, true)) {
            if (device?.installationType == InstalationType.LINUX) {
                viewModelScope.launch {
                    //_uiEvents.send(PickupParcelScreenUiEvent.NavigateToQrCode(SettingsHelper.userLastSelectedLocker))
                }
            } else {
                startOpenProcedure(context, activity)
            }
        }
    }

    fun removeAllStorageKeys() {
       DatabaseHandler.deliveryKeyDb.clear()
    }

    private fun startOpenProcedure(context: Context, activity: Activity) {
        _state.update {
            it.copy(
                isAnimating = true,
                isButtonEnabled = false,
                circleDrawableRes = R.drawable.progress_spinning,
                statusText = context.getString(R.string.nav_pickup_parcel_connecting),
                isError = false
            )
        }

        viewModelScope.launch {
            if (isOpenDoorPossible() && UserUtil.user?.id != null) {
                val comunicator =
                    MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.createBLECommunicator(
                        activity
                    )

                if (comunicator?.connect() == true) {
                    log.info("Connected!")
                    var actionSuccessful = true

                    MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.activeKeys?.filter {
                        it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF
                    }?.forEach { key ->
                        log.info("Requesting pickup for ${key.lockerMac}, ${UserUtil.user?.id ?: 0}")
                        val bleResponse = comunicator.requestParcelPickup(
                            key.lockerMac,
                            UserUtil.user?.id ?: 0
                        )

                        if (!bleResponse.isSuccessful) {
                            log.error(bleResponse.toString())
                            actionSuccessful = false
                        } else {
                            openedParcels.add(key.lockerMac)
                            val updatedKeys =
                                _state.value.keys.filter { it.lockerMac != key.lockerMac }
                            _state.update { it.copy(keys = updatedKeys) }
                            //persistActionOpenKey(key.id)
                            //NotificationHelper(App.ref).clearNotification()
                            denyOpenProcedure()
                        }
                    }

                    comunicator.disconnect()

                    if (actionSuccessful) {
                        setSuccessOpenView(context)
                        if ((MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.activeKeys?.size
                                ?: 0) == 1
                        ) {
                            _state.update {
                                it.copy(
                                    showCleaningCheckbox = true,
                                    showFinishButton = true,
                                    lockerMacAddress = openedParcels.firstOrNull() ?: ""
                                )
                            }
                        }
                        else {
                            _state.update {
                                it.copy(
                                    showFinishButton = true
                                )
                            }
                        }
                    } else {
                        setUnSuccessOpenView(context.getString(R.string.something_went_wrong))
                    }

                    connecting.set(false)
                } else {
                    comunicator?.disconnect()
                    setUnSuccessOpenView(context.getString(R.string.main_locker_ble_connection_error))
                }
            } else {
                setUnSuccessOpenView(
                    context.getString(
                        R.string.toast_pickup_parcel_error,
                        UserUtil.user?.id.toString()
                    )
                )
            }

            connecting.set(false)
        }
    }

    private fun handleForceOpenClick(context: Context, activity: Activity) {
        if (connecting.compareAndSet(false, true)) {
            _state.update {
                it.copy(
                    isAnimating = true,
                    isButtonEnabled = false,
                    circleDrawableRes = R.drawable.progress_spinning,
                    statusText = context.getString(R.string.nav_pickup_parcel_connecting),
                    isError = false
                )
            }

            viewModelScope.launch {
                val comunicator = device?.createBLECommunicator(activity)

                if (comunicator?.connect() == true) {
                    var actionSuccessful = true

                    openedParcels.forEach { mac ->
                        log.info("Requesting force pickup for $mac")
                        val bleResponse = comunicator.forceOpenDoor(mac)
                        if (!bleResponse) {
                            actionSuccessful = false
                            log.error(bleResponse.toString())
                        } else {
                            log.info("Success delivery on $mac")
                        }
                    }

                    comunicator.disconnect()

                    if (actionSuccessful) {
                        setSuccessOpenView(context)
                    } else {
                        setUnSuccessOpenView(context.getString(R.string.something_went_wrong))
                    }
                    connecting.set(false)
                } else {

                    connecting.set(false)
                    _uiEvents.send(UiEvent.ShowToast(context.getString(R.string.app_generic_error)))
                    setUnSuccessOpenView(context.getString(R.string.nav_pickup_parcel_content_lock))
                }
            }
        }
    }

    private fun setSuccessOpenView(context: Context) {
        _state.update {
            it.copy(
                isAnimating = false,
                isButtonEnabled = false,
                circleDrawableRes = R.drawable.progress_stopped,
                isUnlocked = true,
                statusText = context.getString(R.string.nav_pickup_parcel_content_unlock),
                titleRes = R.string.nav_pickup_parcel_unlock,
                showForceOpen = true,
                showFinishButton = true
            )
        }
    }

    private fun setUnSuccessOpenView(errorText: String) {
        log.info("Connection failed!")
        _state.update {
            it.copy(
                isAnimating = false,
                isButtonEnabled = true,
                circleDrawableRes = R.drawable.progress_stopped,
                statusText = errorText,
                isError = true,
                titleRes = R.string.nav_pickup_parcel_lock,
                showCleaningCheckbox = false
            )
        }
    }

    private fun denyOpenProcedure() {
        if (lockerLoaderRunning.compareAndSet(false, true)) {
            viewModelScope.launch {
                val time = exitTime?.time ?: 0L
                val compare = Math.abs(time - startingTime.time)
                var timeForOpen = denyProcedureDuration

                if (compare in 1..denyProcedureDuration) {
                    timeForOpen = denyProcedureDuration - compare
                }

                delay(timeForOpen)
                _state.update { it.copy(showForceOpen = false) }
            }
        }
    }

//    private fun persistActionOpenKey(id: Int) {
//        val deliveryKeys = DatabaseHandler.deliveryKeyDb.get(SettingsHelper.userLastSelectedLocker)
//        if (deliveryKeys == null) {
//            DatabaseHandler.deliveryKeyDb.put(
//                DeliveryKey(
//                    SettingsHelper.userLastSelectedLocker,
//                    listOf(id)
//                )
//            )
//        } else {
//            if (!deliveryKeys.keyIds.contains(id)) {
//                val listOfIds = deliveryKeys.keyIds.plus(id)
//                DatabaseHandler.deliveryKeyDb.put(DeliveryKey(SettingsHelper.userLastSelectedLocker, listOfIds))
//            }
//        }
//    }

    private fun deletePickAtFriendKey(
        position: Int,
        keyId: Int,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (WSUser.deletePaF(keyId)) {
                _uiEvents.send(
                    UiEvent.ShowToast(
                        App.ref.getString(
                            R.string.peripheral_settings_remove_access_success,
                            keyId.toString()
                        )
                    )
                )
                val updatedKeys = _state.value.keys.toMutableList().apply {
                    removeAt(position)
                }
                _state.update { it.copy(keys = updatedKeys) }
                onSuccess()
            } else {
                _uiEvents.send(
                    UiEvent.ShowToast(
                        App.ref.getString(
                            R.string.peripheral_settings_remove_access_error,
                            keyId.toString()
                        )
                    )
                )
                onError()
            }
        }
    }

    private fun handleCleaningCheckbox(
        lockerMacAddress: String,
        context: Context,
        activity: Activity
    ) {
        _state.update {
            it.copy(
                isCleaningCheckboxEnabled = false,
                showCleaningProgress = true
            )
        }

        viewModelScope.launch {
            if (device?.installationType == InstalationType.LINUX) {
                // Handle Linux devices
            } else {
                setupCleaningNeededForOtherDevices(lockerMacAddress, context, activity)
            }
        }
    }

    
    private suspend fun setupCleaningNeededForOtherDevices(
        lockerMacAddress: String,
        context: Context,
        activity: Activity
    ) {
        val comunicator =
            MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.createBLECommunicator(
                activity
            )

        if (comunicator?.connect() == true) {
            log.info("Connected!")

            val lockerMacAddressList: MutableList<LockerFlagsUtil.LockerInfo> = mutableListOf()
            val lockerInfo = LockerFlagsUtil.LockerInfo(byteArrayOf(), byteArrayOf())

            when {
                lockerMacAddress.length == MAC_ADDRESS_7_BYTE_LENGTH -> {
                    lockerInfo.mac = lockerMacAddress.take(MAC_ADDRESS_6_BYTE_LENGTH)
                        .macCleanToBytes()
                        .reversedArray()
                    lockerInfo.index = lockerMacAddress.takeLast(MAC_ADDRESS_LAST_BYTE_LENGTH)
                        .hexToByteArray()
                }

                else -> {
                    lockerInfo.mac = lockerMacAddress.macCleanToBytes().reversedArray()
                    lockerInfo.index = byteArrayOf(0x00)
                }
            }

            lockerMacAddressList.add(lockerInfo)
            val byteArrayCleaningNeeded = LockerFlagsUtil.generateCleaningRequiredData(
                lockerMacAddressList,
                true
            )
            val response = comunicator.lockerIsDirty(byteArrayCleaningNeeded)
            comunicator.disconnect()

            log.info("Cleaning function is successfully: $response")

            if (response) {
                _state.update {
                    it.copy(
                        isCleaningCheckboxEnabled = false,
                        isCleaningCheckboxChecked = true,
                        showCleaningProgress = false
                    )
                }
                _uiEvents.send(UiEvent.ShowToast(context.getString(R.string.app_generic_success)))
            } else {
                _state.update {
                    it.copy(
                        isCleaningCheckboxEnabled = true,
                        showCleaningProgress = false
                    )
                }
                _uiEvents.send(UiEvent.ShowToast(context.getString(R.string.app_generic_error)))
            }
        } else {
            _state.update {
                it.copy(
                    isCleaningCheckboxEnabled = true,
                    showCleaningProgress = false
                )
            }
            _uiEvents.send(UiEvent.ShowToast(context.getString(R.string.main_locker_ble_connection_error)))
        }
    }

    fun onMplDeviceUpdate() {
        if (!connecting.get() && device?.installationType != InstalationType.LINUX) {
            device =
                MPLDeviceStore.uniqueDevices.values.find { it.macAddress == SettingsHelper.userLastSelectedLocker }
            setupOpenButton()
        }
    }

    fun onPause() {
        exitTime = Date()
    }
}