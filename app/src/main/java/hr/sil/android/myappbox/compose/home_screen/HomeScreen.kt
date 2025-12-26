package hr.sil.android.myappbox.compose.home_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmEdittextColor
import hr.sil.android.myappbox.compose.components.ThmNavigationDrawerMenuTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.main_activity.MainDestinations
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper

import hr.sil.android.myappbox.compose.dialog.NoMasterSelectedDialog
import hr.sil.android.myappbox.compose.dialog.TextCopiedToClipboardDialog
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.remote.model.RequiredAccessRequestTypes
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: NavHomeViewModel,
    nextScreen: (route: String) -> Unit = {},
    nextScreenQrCode: (route: String, typeOfNextScreen: Int, macAddress: String) -> Unit = { _, _, _ -> },
    navigateUp: () -> Unit = {}
) {
    //val state = viewModel.state.collectAsStateWithLifecycle().value
    val uiState by viewModel.uiState.collectAsState()

    val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
    val lockerNameOrAddress = rememberSaveable { mutableStateOf("") }
    val finalProductName = rememberSaveable { mutableStateOf("") }
    val lockerAddress = rememberSaveable { mutableStateOf("") }

    //val pahKeysCount = rememberSaveable { mutableStateOf(0) }

    val displayCopiedToClipboardDialog = remember { mutableStateOf(false) }
    val displayNoLockerSelected = rememberSaveable { mutableStateOf(false) }
    val displayNoAccessDialog = rememberSaveable { mutableStateOf(false) }
    val noAccessMessage = rememberSaveable { mutableStateOf("") }

    val noSelectedLocker = stringResource(R.string.no_selected_locker)
    val noDeliverisToLockerPossible = stringResource(R.string.no_deliveris_to_locker_possible)
    val appGenericRequestAccess = stringResource(R.string.app_generic_request_access)
    val adminAapproveRequestAccess = stringResource(R.string.admin_approve_request_access)
    val appGenericNoAccessForDevice = stringResource(R.string.app_generic_no_access_for_device)
    val noDeliveriesToPickup = stringResource(R.string.no_deliveries_to_pickup)

    // Check conditions
//    val deviceAddressConfirmed = selectedMasterDevice?.requiredAccessRequestTypes
//        ?.firstOrNull { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
//
//    val isPublicLocker = selectedMasterDevice != null &&
//            !(UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null)

//    val canCollectParcel = isPublicLocker &&
//            UserUtil.user?.status == "ACTIVE" &&
//            selectedMasterDevice?.activeKeys?.any {
//                it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF
//            } == true

//    val canSendParcel = isPublicLocker &&
//            SettingsHelper.userLastSelectedLocker.isNotEmpty() &&
//            UserUtil.user?.status == "ACTIVE" &&
//            (selectedMasterDevice?.hasUserRightsOnSendParcelLocker() ?: false) &&
//            selectedMasterDevice?.isUserAssigned == true

//    val canShareAccess = isPublicLocker &&
//            UserUtil.user?.status == "ACTIVE" &&
//            (selectedMasterDevice?.hasRightsToShareAccess() ?: false) &&
//            selectedMasterDevice?.installationType == InstalationType.DEVICE

//    val deliveryKeysCount = selectedMasterDevice?.activeKeys
//        ?.count { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF } ?: 0

    if (displayNoLockerSelected.value) {
        NoMasterSelectedDialog(
            messageResId = R.string.no_selected_locker,
            onConfirm = { displayNoLockerSelected.value = false },
            onDismiss = { displayNoLockerSelected.value = false }
        )
    }

    if (displayNoAccessDialog.value) {
        NoMasterSelectedDialog(
            message = noAccessMessage.value,
            onConfirm = { displayNoAccessDialog.value = false },
            onDismiss = { displayNoAccessDialog.value = false }
        )
    }

    LaunchedEffect(Unit) {
        lockerNameOrAddress.value = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
            ?.name?.ifEmpty {
                MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.address ?: ""
            } ?: ""
        finalProductName.value = viewModel.setFinalProductName()
        lockerAddress.value = viewModel.setLockerAddress()
        //viewModel.loadUserData()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
                lockerNameOrAddress.value = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
                    ?.name?.ifEmpty {
                        MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.address ?: ""
                    } ?: ""
                finalProductName.value = viewModel.setFinalProductName()
                lockerAddress.value = viewModel.setLockerAddress()

                print("NEW NOTIF ... WILL IT ENTER HERE ... LOAD USER DATA")
                viewModel.loadUserData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (displayCopiedToClipboardDialog.value)
        TextCopiedToClipboardDialog(
            onDismiss = { displayCopiedToClipboardDialog.value = false },
            onConfirm = { displayCopiedToClipboardDialog.value = false }
        )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 10.dp, bottom = 20.dp)
        ) {
            //if (isPublicLocker) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextViewWithFont(
                        text = stringResource(R.string.app_generic_selected_locker).uppercase(),
                        color = ThmTitleTextColor,
                        fontSize = ThmTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 18.dp, end = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(0.91f)
                                .height(40.dp)
                                .padding(end = 10.dp)
                                .background(
                                    color = colorResource(R.color.colorPrimary),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable { nextScreen(MainDestinations.SELECT_LOCKER) }
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextViewWithFont(
                                text = lockerNameOrAddress.value,
                                color = colorResource(R.color.colorBlackText),
                                fontSize = ThmTitleTextSize,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.weight(0.9f)
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.ic_map),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .weight(0.11f)
                                .clickable { nextScreen(MainDestinations.GOOGLE_MAPS_SELECT_LOCKER) }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 18.dp, end = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(9f)) {
                            TextViewWithFont(
                                text = finalProductName.value,
                                color = ThmTitleTextColor,
                                fontSize = ThmTitleTextSize,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                            )

                            TextViewWithFont(
                                text = lockerAddress.value,
                                color = ThmTitleTextColor,
                                fontSize = ThmTitleTextSize,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.ic_copy_address),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (SettingsHelper.userLastSelectedLocker.isEmpty()) {
                                        displayNoLockerSelected.value = true
                                    } else {
                                        // Copy to clipboard logic
                                        displayCopiedToClipboardDialog.value = true
                                    }
                                }
                        )
                    }
                }
            //}

            // First Row - Collect and Send Parcel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 5.dp, end = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Collect Parcel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            when {
                                SettingsHelper.userLastSelectedLocker.isEmpty() -> {
                                    noAccessMessage.value = noSelectedLocker
                                    displayNoAccessDialog.value = true
                                }
                                !uiState.isPublicLocker -> {
                                    noAccessMessage.value = noDeliverisToLockerPossible
                                    displayNoAccessDialog.value = true
                                }
                                selectedMasterDevice?.isUserAssigned == false &&
                                        selectedMasterDevice?.activeAccessRequest == false -> {
                                    noAccessMessage.value = appGenericRequestAccess
                                    displayNoAccessDialog.value = true
                                }
                                selectedMasterDevice?.isUserAssigned == false &&
                                        selectedMasterDevice?.activeAccessRequest == true -> {
                                    noAccessMessage.value = adminAapproveRequestAccess
                                    displayNoAccessDialog.value = true
                                }
                                selectedMasterDevice?.hasUserRightsOnSendParcelLocker() == false -> {
                                    noAccessMessage.value = appGenericNoAccessForDevice
                                    displayNoAccessDialog.value = true
                                }
                                uiState.canCollectParcel -> {
                                    nextScreen(MainDestinations.PARCEL_PICKUP)
                                }
                                else -> {
                                    noAccessMessage.value = noDeliveriesToPickup
                                    displayNoAccessDialog.value = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            Icon(
                                painter = painterResource(R.drawable.ic_collect_parcel),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.alpha(if (uiState.deliveryKeysCount > 0) 1.0f else 0.2f)
                            )

                            if (uiState.deliveryKeysCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .offset(x = 105.dp)
                                        .background(
                                            color = colorResource(R.color.colorRedBadgeNumber),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextViewWithFont(
                                        text = uiState.deliveryKeysCount.toString(),
                                        color = ThmNavigationDrawerMenuTextColor,
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }
                        }

                        TextViewWithFont(
                            text = stringResource(R.string.app_generic_pickup_parcel),
                            color = ThmDescriptionTextColor,
                            fontSize = ThmTitleTextSize,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Send Parcel
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            Icon(
                                painter = painterResource(R.drawable.ic_send_parcel),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .alpha(if (uiState.canSendParcel) 1.0f else 0.2f)
                                    .clickable {
                                        when {
                                            SettingsHelper.userLastSelectedLocker.isEmpty() -> {
                                                noAccessMessage.value = noSelectedLocker
                                                displayNoAccessDialog.value = true
                                            }
                                            !uiState.isPublicLocker -> {
                                                noAccessMessage.value = noDeliverisToLockerPossible
                                                displayNoAccessDialog.value = true
                                            }
                                            selectedMasterDevice?.isUserAssigned == false &&
                                                    selectedMasterDevice?.activeAccessRequest == false -> {
                                                noAccessMessage.value = appGenericRequestAccess
                                                displayNoAccessDialog.value = true
                                            }
                                            selectedMasterDevice?.isUserAssigned == false &&
                                                    selectedMasterDevice?.activeAccessRequest == true -> {
                                                noAccessMessage.value = adminAapproveRequestAccess
                                                displayNoAccessDialog.value = true
                                            }
                                            selectedMasterDevice?.hasUserRightsOnSendParcelLocker() == false -> {
                                                noAccessMessage.value = appGenericNoAccessForDevice
                                                displayNoAccessDialog.value = true
                                            }
                                            selectedMasterDevice?.installationType == InstalationType.LINUX -> {
                                                nextScreenQrCode(MainDestinations.SETTINGS_QR_CODE, 1,
                                                    SettingsHelper.userLastSelectedLocker) // TODO: CHECK THIS IF IS WORKING GOOD
                                            }
                                            uiState.canSendParcel -> {
                                                nextScreen(MainDestinations.SELECT_PARCEL_SIZE)
                                            }
                                        }
                                    }
                            )

                            if (uiState.pahKeysCount > 0) {
                                Row(
                                    modifier = Modifier
                                        .offset(x = 105.dp)
                                        .background(
                                            color = colorResource(R.color.colorRedBadgeNumber),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            nextScreen(MainDestinations.PICK_AT_HOME_KEYS)
                                        }
                                        .padding(horizontal = 7.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_pick_at_home),
                                        contentDescription = null,
                                        tint = Color.Unspecified
                                    )

                                    TextViewWithFont(
                                        text = uiState.pahKeysCount.toString(),
                                        color = ThmNavigationDrawerMenuTextColor,
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Light,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )

                                }
                            }
                        }

                        TextViewWithFont(
                            text = stringResource(R.string.app_generic_send_parcel),
                            color = ThmDescriptionTextColor,
                            fontSize = ThmTitleTextSize,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Second Row - Share Access and Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp, start = 5.dp, end = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Share Access
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            when {
                                SettingsHelper.userLastSelectedLocker.isEmpty() -> {
                                    noAccessMessage.value = noSelectedLocker
                                    displayNoAccessDialog.value = true
                                }
                                !uiState.isPublicLocker -> {
                                    noAccessMessage.value = noDeliverisToLockerPossible
                                    displayNoAccessDialog.value = true
                                }
                                selectedMasterDevice?.isUserAssigned == false &&
                                        selectedMasterDevice?.activeAccessRequest == false -> {
                                    noAccessMessage.value = appGenericRequestAccess
                                    displayNoAccessDialog.value = true
                                }
                                selectedMasterDevice?.isUserAssigned == false &&
                                        selectedMasterDevice?.activeAccessRequest == true -> {
                                    noAccessMessage.value = adminAapproveRequestAccess
                                    displayNoAccessDialog.value = true
                                }
                                selectedMasterDevice?.installationType != InstalationType.DEVICE -> {
                                    // Do nothing for non-device installations
                                }
                                selectedMasterDevice?.hasUserRightsOnSendParcelLocker() == false -> {
                                    noAccessMessage.value = appGenericNoAccessForDevice
                                    displayNoAccessDialog.value = true
                                }
                                uiState.canShareAccess -> {
                                    nextScreen(MainDestinations.ACCESS_SHARING_SCREEN)
                                }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_key_sharing),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.alpha(if (uiState.canShareAccess) 1.0f else 0.2f)
                    )

                    TextViewWithFont(
                        text = stringResource(R.string.app_generic_key_sharing),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                // Settings
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { nextScreen(MainDestinations.SETTINGS) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_configure),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )

                    TextViewWithFont(
                        text = stringResource(R.string.app_generic_my_configuration),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Telemetry Section
            if (selectedMasterDevice?.isInBleProximity == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 40.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier.weight(0.25f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_humidity),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                        TextViewWithFont(
                            text = "${selectedMasterDevice?.humidity?.format(2) ?: "-"} %",
                            color = ThmDescriptionTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.weight(0.25f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_temperature),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                        TextViewWithFont(
                            text = "${selectedMasterDevice?.temperature?.format(2) ?: "-"} C",
                            color = ThmDescriptionTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.weight(0.25f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_air_pressure),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                        TextViewWithFont(
                            text = "${selectedMasterDevice?.pressure?.format(2) ?: "-"} hPa",
                            color = ThmDescriptionTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//fun NoMasterSelectedDialog(
//    messageResId: Int? = null,
//    message: String? = null,
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = {
//            Text(
//                text = stringResource(R.string.app_generic_info),
//                fontWeight = FontWeight.Bold
//            )
//        },
//        text = {
//            Text(
//                text = message ?: stringResource(messageResId ?: R.string.no_selected_locker)
//            )
//        },
//        confirmButton = {
//            Button(onClick = onConfirm) {
//                Text(stringResource(R.string.app_generic_ok))
//            }
//        }
//    )
//}

// Extension function for formatting
fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)