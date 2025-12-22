package hr.sil.android.myappbox.compose.send_parcel

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.dialog.GeneratedPinDialog
import hr.sil.android.myappbox.compose.dialog.PinManagementDialog
import hr.sil.android.myappbox.core.remote.model.RAvailableLockerSize
import hr.sil.android.myappbox.core.remote.model.RLockerSize
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.SettingsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SelectParcelSizeScreen(
    viewModel: SelectParcelSizeViewModel = viewModel(),
    onNavigateToDelivery: (macAddress: String, pin: Int, size: String) -> Unit = { _, _, _ -> },
) {
    val state by viewModel.uiState.collectAsState()

    val showPinManagementDialog = rememberSaveable { mutableStateOf(false) }
    val showGeneratedPinDialog = rememberSaveable { mutableStateOf(false) }


    if (showGeneratedPinDialog.value) {
        GeneratedPinDialog(
            macAddress = SettingsHelper.userLastSelectedLocker,
            lockerSize = state.selectedLockerSize,
            onDismiss = { showGeneratedPinDialog.value = false },
            onConfirm = { mac, pin, size ->
                showGeneratedPinDialog.value = false
                onNavigateToDelivery(mac, pin, size)
            }
        )
    }

    if (showPinManagementDialog.value) {
        PinManagementDialog(
            macAddress = SettingsHelper.userLastSelectedLocker,
            lockerSize = state.selectedLockerSize,
            onDismiss = { showPinManagementDialog.value = false },
            onConfirm = { mac, pin, size ->
                showPinManagementDialog.value = false
                onNavigateToDelivery(mac, pin, size)
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SelectParcelSizeEvent.ShowPinManagement -> {
                    showPinManagementDialog.value = true
                    // showPinManagement(event.device, event.size)
                }

                is SelectParcelSizeEvent.ShowGeneratedPin -> {
                    showGeneratedPinDialog.value = true
                    //showGeneratedPin(event.device, event.size)
                }

                SelectParcelSizeEvent.Unauthorized -> {
                    //onUnauthorized()
                }
            }
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        }

        else -> {
            SelectParcelContentScreen(
                isInBleProximity = state.isInBleProximity,
                availableLockers = state.availableLockers,
                onLockerSelected = viewModel::onLockerClicked,
                //lockerSizes = state.
            )
        }
    }
}

@Composable
fun SelectParcelContentScreen(
    isInBleProximity: Boolean,
    availableLockers: List<RAvailableLockerSize>,
    onLockerSelected: (RLockerSize) -> Unit,
    //lockerSizes: List<LockerSize>,
) {

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            SendParcelTitle(
                text = if (isInBleProximity)
                    stringResource(R.string.app_generic_send_parcel)
                else
                    stringResource(R.string.app_generic_enter_ble)
            )

            if (isInBleProximity) {
                ParcelSizeContent(
                    availableLockers = availableLockers,
                    onLockerSelected = onLockerSelected
                )
            } else {
                NotInProximityContent()
            }
        }
    }
}

@Composable
fun SendParcelTitle(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        //.weight(0.05f),
        contentAlignment = Alignment.Center
    ) {
        TextViewWithFont(
            text = text.uppercase(),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            letterSpacing = 0.1.em,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )
    }
}

@Composable
fun ParcelSizeContent(
    availableLockers: List<RAvailableLockerSize>,
    onLockerSelected: (RLockerSize) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 10.dp)
    ) {

        TextViewWithFont(
            text = stringResource(R.string.send_parcel_description),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            letterSpacing = 0.05.em,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        )

        Spacer(Modifier.height(20.dp))

        LockerRow(
            left = RLockerSize.XS,
            right = RLockerSize.S,
            availableLockers = availableLockers,
            onLockerSelected = onLockerSelected
        )

        Spacer(Modifier.height(10.dp))

        LockerRow(
            left = RLockerSize.M,
            right = RLockerSize.L,
            availableLockers = availableLockers,
            onLockerSelected = onLockerSelected
        )

        Spacer(Modifier.height(10.dp))

        LockerRow(
            left = RLockerSize.XL,
            right = null,
            availableLockers = availableLockers,
            onLockerSelected = onLockerSelected
        )
    }
}

@Composable
fun LockerRow(
    left: RLockerSize,
    right: RLockerSize?,
    availableLockers: List<RAvailableLockerSize>,
    onLockerSelected: (RLockerSize) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LockerItem(left, availableLockers, onLockerSelected)

        if (right != null) {
            LockerItem(right, availableLockers, onLockerSelected)
        }
    }
}

@Composable
fun LockerItem(
    size: RLockerSize,
    availableLockers: List<RAvailableLockerSize>,
    onLockerSelected: (RLockerSize) -> Unit
) {
    val available =
        availableLockers.firstOrNull { it.size == size && it.count > 0 }

    val isEnabled = available != null
    val count = available?.count ?: 0

    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(enabled = isEnabled) {
                onLockerSelected(size)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box() {
            //ic_dimensions
            Image(
                painter = painterResource(
                        R.drawable.ic_dimensions
                ),
                contentDescription = null,
                modifier = Modifier.size(100.dp).align(Alignment.Center)
            )

            Image(
                painter = painterResource(
                    if (isEnabled)
                        R.drawable.btn_parcel_size
                    else
                        R.drawable.btn_parcel_size_disabled
                ),
                contentDescription = null
            )
        }

        TextViewWithFont(
            text = size.name,
            color = ThmDescriptionTextColor,
            fontSize = 40.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )

        TextViewWithFont(
            text = stringResource(
                R.string.send_parcel_available,
                count.toString()
            ),
            color = ThmDescriptionTextColor,
            fontSize = ThmDescriptionTextSize,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )
    }
}

@Composable
fun NotInProximityContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.ic_not_in_proximity),
            contentDescription = null,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        TextViewWithFont(
            text = stringResource(R.string.not_in_proximity_first_description),
            color = ThmDescriptionTextColor,
            fontSize = ThmTitleTextSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 15.dp)
        )

//        Text(
//            text = stringResource(R.string.not_in_proximity_first_description),
//            textAlign = TextAlign.Center,
//            fontSize = 20.sp,
//            modifier = Modifier.padding(horizontal = 15.dp)
//        )

        Spacer(Modifier.height(30.dp))

        TextViewWithFont(
            text = stringResource(R.string.nav_pickup_parcel_content_lock),
            color = ThmDescriptionTextColor,
            fontSize = ThmTitleTextSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 15.dp)
        )

//        Text(
//            text = stringResource(R.string.nav_pickup_parcel_content_lock),
//            textAlign = TextAlign.Center,
//            fontSize = 20.sp,
//            modifier = Modifier.padding(horizontal = 15.dp)
//        )
    }
}
