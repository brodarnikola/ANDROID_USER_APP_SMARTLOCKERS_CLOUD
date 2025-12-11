package hr.sil.android.myappbox.compose.home_screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.components.strokeBackground
import hr.sil.android.myappbox.compose.dialog.MplRequestAccessDialog
import hr.sil.android.myappbox.core.model.MPLDeviceType
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType

@Composable
fun SelectLockerScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectLockerViewModel,
    navigateUp: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var displayMplRequestAccessDialog by remember {
        mutableStateOf(false)
    }

    if (displayMplRequestAccessDialog) {
        MplRequestAccessDialog (
            onDismiss = {
                displayMplRequestAccessDialog = false
            },
            onConfirm = {
                displayMplRequestAccessDialog
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextViewWithFont(
                text = uiState.title.uppercase(),
                color = ThmTitleTextColor,
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = ThmTitleLetterSpacing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 15.dp, end = 15.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                items(uiState.lockerItems) { item ->
                    when (item) {
                        is LockerListItem.Header -> {
                            LockerHeaderItem(
                                header = item,
                                onToggle = { viewModel.onHeaderToggle(item.headerIndex) }
                            )
                        }
                        is LockerListItem.LockerItem -> {
                            AnimatedVisibility(visible = item.isExpanded) {
                                LockerDetailItem(
                                    locker = item,
                                    onSelected = { viewModel.onLockerSelected(item.deviceData.macAddress) },
                                    onRequestAccess = {
                                        viewModel.requestAccess(item.deviceData.macAddress) { success, message ->
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            if( success )
                                                displayMplRequestAccessDialog = true
                                        }
                                    },
                                    onActivateSPL = {
                                        viewModel.activateSPL(item.deviceData.macAddress) { success, message ->
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.showConfirmButton) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    ButtonWithFont(
                        text = stringResource(id = R.string.app_generic_confirm).uppercase(),
                        onClick = {
                            viewModel.onConfirm {
                                navigateUp()
                            }
                        },
                        backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                        textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                        fontSize = ThmButtonTextSize, // ?attr/thmButtonTextSize
                        fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                        letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                        modifier = Modifier
                            .width(250.dp)
                            .height(40.dp) ,
                        enabled = true
                    )

//                    ButtonWithFont(
//                        text = stringResource(id = R.string.app_generic_apply).uppercase(),
//                        onClick = {
//                            viewModel.onConfirm {
//                                onConfirm()
//                            }
//                        },
//                        modifier = Modifier
//                            .width(210.dp)
//                            .height(40.dp),
//                        shape = RoundedCornerShape(4.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = colorResource(id = R.color.colorAccentZwick)
//                        )
//                    ) {
//                        TextViewWithFont(
//                            text = stringResource(id = R.string.app_generic_confirm).uppercase(),
//                            color = Color.White,
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            letterSpacing = 1.sp
//                        )
//                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.colorAccentZwick)
                )
            }
        }
    }
}

@Composable
private fun LockerHeaderItem(
    header: LockerListItem.Header,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextViewWithFont(
            text = header.title.uppercase(),
            color = ThmTitleTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(
                id = if (header.isExpanded) R.drawable.arrow_up else R.drawable.arrow_down
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun LockerDetailItem(
    locker: LockerListItem.LockerItem,
    onSelected: () -> Unit,
    onRequestAccess: () -> Unit,
    onActivateSPL: () -> Unit
) {
    val device = locker.deviceData
    val isSelected = device.isSelected

    val backgroundColor = if (isSelected) {
        colorResource(id = R.color.colorPrimaryDarkZwick)
    } else {
        colorResource(id = R.color.colorPrimaryTransparentZwick)
    }

    val textColor = if (isSelected) Color.White else ThmDescriptionTextColor

    val lockerIcon = when {
        device.isUserAssigned && (device.installationType == InstalationType.LINUX || device.isInBleProximity) -> {
            if (isSelected) R.drawable.ic_locker_green_inverted else R.drawable.ic_locker_green
        }
        !device.isInBleProximity && device.isUserAssigned -> {
            if (isSelected) R.drawable.ic_locker_yellow_inverted else R.drawable.ic_locker_yellow
        }
        else -> {
            if (isSelected) R.drawable.ic_locker_grey_inverted else R.drawable.ic_locker_grey
        }
    }

    val showRequestAccess = !device.isUserAssigned
    val isMPL = device.backendDeviceType == RMasterUnitType.MPL ||
            device.bleDeviceType == MPLDeviceType.MASTER ||
            device.installationType == InstalationType.TABLET ||
            device.installationType == InstalationType.LINUX

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .background(backgroundColor, RoundedCornerShape(5.dp))
            .clickable { onSelected() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = lockerIcon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                TextViewWithFont(
                    text = device.deviceName.ifEmpty { device.macAddress },
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                TextViewWithFont(
                    text = device.deviceAddress,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }

        if (showRequestAccess) {
            Spacer(modifier = Modifier.height(10.dp))

            val buttonText = when {
                isMPL && device.activeAccessRequest -> stringResource(id = R.string.locker_request_send)
                isMPL -> stringResource(id = R.string.locker_details_registration_btn)
                !device.isSplActivate -> stringResource(id = R.string.locker_details_activate_btn)
                else -> stringResource(id = R.string.locker_no_access)
            }

            val isClickable = when {
                isMPL && device.activeAccessRequest -> false
                isMPL -> true
                !device.isSplActivate -> true
                else -> false
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .strokeBackground(
                        backgroundColor = Color.Transparent,
                        strokeColor = colorResource(R.color.colorBlackText),
                        strokeWidth = 1.5.dp,
                        cornerRadius = 5.dp
                    )
//                    .background(
//                        color = if (isSelected)
//                            Color.White.copy(alpha = 0.2f)
//                        else
//                            colorResource(id = R.color.colorBlackText).copy(alpha = 0.2f),
//                        shape = RoundedCornerShape(4.dp)
//                    )
                    .clickable(enabled = isClickable) {
                        if (isMPL) {
                            onRequestAccess()
                        } else {
                            onActivateSPL()
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                TextViewWithFont(
                    text = buttonText.uppercase(),
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}