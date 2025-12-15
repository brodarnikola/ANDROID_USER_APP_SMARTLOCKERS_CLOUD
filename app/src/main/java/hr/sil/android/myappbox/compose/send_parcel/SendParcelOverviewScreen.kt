package hr.sil.android.myappbox.compose.send_parcel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.R

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import hr.sil.android.myappbox.core.util.formatFromStringToDate
import hr.sil.android.myappbox.core.util.formatToViewDateTimeDefaults
import java.text.ParseException

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import hr.sil.android.myappbox.core.model.MPLDeviceType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.store.MPLDeviceStore

@Composable
fun SendParcelsOverviewScreen(
    viewModel: SendParcelOverviewViewModel = viewModel(),
    //onCancelKey: (RCreatedLockerKey) -> Unit,
    //onShareKey: (RCreatedLockerKey) -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // Title
        Text(
            text = stringResource(R.string.locker_pick_home_keys),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(top = 10.dp),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            letterSpacing = 0.1.em,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            //style = MaterialTheme.typography.titleLarge.copy(
            //    textTransform = TextTransform.Uppercase
            //)
        )

        // Description
        Text(
            text = stringResource(R.string.cancel_pick_at_home_text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(top = 20.dp),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )

        // Keys List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(uiState.allListOfKeys, key = { it.id }) { key ->
                SendParcelKeyItem(
                    keyObject = key,
                    onCancelClick = {
                        //onCancelKey(key)
                    },
                    onShareClick = {
                       // onShareKey(key)
                    }
                )
            }
        }
    }
}

@Composable
fun SendParcelKeyItem(
    keyObject: RCreatedLockerKey,
    onCancelClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val locker = MPLDeviceStore.uniqueDevices[keyObject.getMasterBLEMacAddress()]
    val formattedDate = formatCorrectDate(keyObject.timeCreated)
    val isSPL = locker?.type == MPLDeviceType.SPL

    var isDeleting by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(4.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            val (lockerImage, nameAddressSection, deliveryDataSection,
                leftButton, rightButton) = createRefs()

            // Locker Picture
            Image(
                painter = painterResource(R.drawable.ic_dimensions),
                contentDescription = "Locker",
                modifier = Modifier
                    .constrainAs(lockerImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(nameAddressSection.start)
                        bottom.linkTo(nameAddressSection.bottom)
                        width = Dimension.percent(0.15f)
                    }
            )

            // Name and Address Section
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .constrainAs(nameAddressSection) {
                        top.linkTo(parent.top)
                        start.linkTo(lockerImage.end)
                        end.linkTo(parent.end)
                        width = Dimension.percent(0.85f)
                    }
            ) {
                Text(
                    text = keyObject.masterName ?: "-",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = keyObject.masterAddress ?: "-",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Delivery Data Section
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, end = 20.dp, top = 5.dp)
                    .constrainAs(deliveryDataSection) {
                        top.linkTo(nameAddressSection.bottom)
                        start.linkTo(nameAddressSection.start)
                        end.linkTo(parent.end)
                    }
            ) {
                // PIN/TAN
                when {
                    keyObject.pin != null -> {
                        Text(
                            text = stringResource(R.string.app_generic_parcel_pin, keyObject.pin  ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    keyObject.tan != null -> {
                        Text(
                            text = stringResource(R.string.app_generic_parcel_pin, keyObject.tan  ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Date Created
                if (!isSPL) {
                    Text(
                        text = stringResource(R.string.app_generic_time_created, formattedDate),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Size or Time Created
                Text(
                    text = if (isSPL) {
                        stringResource(R.string.app_generic_time_created, formattedDate)
                    } else {
                        stringResource(R.string.app_generic_size, keyObject.lockerSize ?: "")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Share Button
            Box(
                modifier = Modifier
                    .constrainAs(leftButton) {
                        top.linkTo(deliveryDataSection.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(rightButton.start)
                        width = Dimension.fillToConstraints
                    },
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onShareClick,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_generic_key_sharing),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.em,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Cancel Button / Progress
            Box(
                modifier = Modifier
                    .constrainAs(rightButton) {
                        top.linkTo(deliveryDataSection.bottom, margin = 10.dp)
                        start.linkTo(leftButton.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = {
                            isDeleting = true
                            onCancelClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 3.dp),
                        enabled = keyObject.isInBleProximityOrLinuxDevice
                    ) {
                        Text(
                            text = stringResource(R.string.cancel_pick_home),
                            fontSize = 14.sp,
                            letterSpacing = 0.1.em,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

fun formatCorrectDate(timeCreated: String): String {
    return try {
        val fromStringToDate = timeCreated.formatFromStringToDate()
        fromStringToDate.formatToViewDateTimeDefaults()
    } catch (e: ParseException) {
        timeCreated
    }
}

//private fun loadKeys() {
//    lifecycleScope.launch {
//        var listOfKeys = WSUser.getActivePaHCreatedKeys() ?: mutableListOf()
//
//        listOfKeys = listOfKeys.filter {
//            ActionStatusHandler.actionStatusDb.get(it.lockerId.toString() + ActionStatusType.PAH_ACCESS_CANCEL) == null
//        }.toMutableList()
//
//        val listOfDevices = if (macAddress.isNotEmpty()) {
//            MPLDeviceStore.uniqueDevices.values.filter {
//                macAddress.macRealToClean() == it.macAddress.macRealToClean() &&
//                        (it.isInBleProximity == true || it.installationType == InstalationType.LINUX)
//            }
//        } else {
//            MPLDeviceStore.uniqueDevices.values.filter {
//                it.isInBleProximity == true || it.installationType == InstalationType.LINUX
//            }
//        }
//
//        listOfKeys?.forEach { keyItem ->
//            var isInBLEproximity = false
//
//            for (lockerDevice in listOfDevices) {
//                if (keyItem.lockerMasterMac == lockerDevice.macAddress.macRealToClean()) {
//                    keyItem.isLinuxKeyDevice =
//                        lockerDevice.installationType ?: InstalationType.LINUX
//                    if (lockerDevice.installationType == InstalationType.LINUX) {
//                        keyItem.deviceLatitude = lockerDevice.latitude
//                        keyItem.deviceLongitude = lockerDevice.longitude
//                    }
//                    isInBLEproximity = true
//                    break
//                }
//            }
//
//            keyItem.isInBleProximityOrLinuxDevice = isInBLEproximity
//        }
//
//        withContext(Dispatchers.Main) {
//            keys = listOfKeys ?: emptyList()
//        }
//    }
//}
//
//private fun handleShareKey(key: RCreatedLockerKey) {
//    val shareBodyText = when {
//        key.pin != null -> {
//            getString(R.string.share_pin_device_name, key.masterName) + "\n" +
//                    getString(R.string.share_pin_device_address, key.masterAddress) + "\n" +
//                    getString(R.string.share_pin_device_locker_size, key.lockerSize) + "\n" +
//                    getString(R.string.share_pin_device_pin, key.pin) + "\n" +
//                    getString(R.string.app_generic_date_created, formatCorrectDate(key.timeCreated))
//        }
//
//        key.tan != null -> {
//            getString(R.string.share_pin_device_name, key.masterName) + "\n" +
//                    getString(R.string.share_pin_device_address, key.masterAddress) + "\n" +
//                    getString(R.string.share_pin_device_locker_size, key.lockerSize) + "\n" +
//                    getString(R.string.share_pin_device_pin, key.tan) + "\n" +
//                    getString(R.string.app_generic_date_created, formatCorrectDate(key.timeCreated))
//        }
//
//        else -> {
//            getString(R.string.share_pin_device_name, key.masterName) + "\n" +
//                    getString(R.string.share_pin_device_address, key.masterAddress) + "\n" +
//                    getString(R.string.share_pin_device_locker_size, key.lockerSize) + "\n" +
//                    getString(R.string.app_generic_date_created, formatCorrectDate(key.timeCreated))
//        }
//    }
//
//    val emailIntent = Intent(Intent.ACTION_SEND).apply {
//        type = "text/plain"
//        putExtra(Intent.EXTRA_TEXT, shareBodyText)
//    }
//    startActivity(
//        Intent.createChooser(
//            emailIntent,
//            getString(R.string.access_sharing_share_choose_sharing)
//        )
//    )
//}
//
//private suspend fun cancelOtherDevicesPickAtHomeKey(keyObject: RCreatedLockerKey) {
//    log.info("SPl unit mac = ${keyObject.lockerMasterMac.macCleanToReal()}")
//    val communicator = MPLDeviceStore.uniqueDevices[keyObject.lockerMasterMac.macCleanToReal()]
//        ?.createBLECommunicator(this)
//    val userId = UserUtil.user?.id ?: 0
//
//    if (communicator != null && communicator.connect() && userId != 0) {
//        log.info("Connected to ${keyObject.lockerMasterMac} - deleting ${keyObject.lockerMac}")
//        val response = communicator.requestParcelSendCancel(keyObject.lockerMac, userId)
//
//        if (response.isSuccessful) {
//            val action = ActionStatusKey().apply {
//                keyId = keyObject.lockerId.toString() + ActionStatusType.PAH_ACCESS_CANCEL
//            }
//            ActionStatusHandler.actionStatusDb.put(action)
//
//            withContext(Dispatchers.Main) {
//                log.error("Success delete ${keyObject.lockerId}")
//                keys = keys.filter { it.id != keyObject.id }
//            }
//        } else {
//            log.error("Error while deleting the key ${response.bleDeviceErrorCode} - ${response.bleSlaveErrorCode}")
//            withContext(Dispatchers.Main) {
//                App.ref.toast(
//                    getString(
//                        R.string.sent_parcel_error_delete,
//                        keyObject.lockerId.toString()
//                    )
//                )
//            }
//        }
//        communicator.disconnect()
//    } else {
//        log.error("Error while connecting the main unit ${keyObject.lockerMac}")
//        withContext(Dispatchers.Main) {
//            App.ref.toast(
//                getString(
//                    R.string.sent_parcel_error_delete,
//                    keyObject.lockerId.toString()
//                )
//            )
//        }
//    }
//}

@Composable
fun CancelPickedHomeDialog(
    keyObject: RCreatedLockerKey,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.app_generic_are_you_sure),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.cancel_pick_at_home_description)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.app_generic_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.app_generic_cancel))
            }
        }
    )
}

@Composable
fun CancelPickAtHomeLinuxDialog(
    pinOrTan: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Please press collect button on device and enter pin, to cancel pick at home key",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
//            Column {
//                Text(stringResource(R.string.cancel_pick_home_linux_message))
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = pinOrTan,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp
//                )
//            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.app_generic_confirm))
            }
        }
    )
}