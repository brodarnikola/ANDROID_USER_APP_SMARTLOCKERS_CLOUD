package hr.sil.android.myappbox.compose.collect_parcel

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlin.text.isNotEmpty

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.dialog.DeletePickAtFriendDialog
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.util.formatFromStringToDate
import hr.sil.android.myappbox.core.util.formatToViewDateTimeDefaults
import hr.sil.android.myappbox.data.LockerKeyWithShareAccess
import hr.sil.android.myappbox.data.ShareAccessKey
import hr.sil.android.myappbox.util.SettingsHelper
import java.text.ParseException

@Composable
fun ListOfDeliveriesScreen(
    onShareKeyClick: (id: Int, selectedMacAddress: String) -> Unit,
    viewModel: ListOfDeliveriesViewModel = viewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedMacAddress = rememberSaveable { mutableStateOf(SettingsHelper.userLastSelectedLocker) }
    val isLoading = rememberSaveable { mutableStateOf(false) }

    val keyId = rememberSaveable { mutableStateOf(-1) }
    val shareAccessEmail = rememberSaveable { mutableStateOf("") }
    val showDeleteShareKey = rememberSaveable { mutableStateOf(false) }

    val keySuccessDelete = stringResource(R.string.peripheral_settings_remove_access_success, keyId.toString())
    val keyWrongDelete = stringResource(R.string.peripheral_settings_remove_access_error, keyId.toString() )

    if(showDeleteShareKey.value) {
        DeletePickAtFriendDialog(
            shareAccessEmail = shareAccessEmail.value,
            onDismiss = { showDeleteShareKey.value = false },
            onConfirm = {
                viewModel.deletePickAtFriendKey(keyId.value)
                if (uiState.successKeyDelete) {
                    showDeleteShareKey.value = false
                    Toast.makeText(context, keySuccessDelete, Toast.LENGTH_SHORT).show()

                } else {
                    showDeleteShareKey.value = false
                    Toast.makeText(context, keyWrongDelete, Toast.LENGTH_SHORT).show()
                }
            },
            onCancel = {
                showDeleteShareKey.value = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Title (0.5 weight)
            TextViewWithFont(
                text = stringResource(id = R.string.list_of_deliveries_cpl).uppercase(),
                color = ThmTitleTextColor,
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = ThmTitleLetterSpacing,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .wrapContentHeight(Alignment.CenterVertically),
            )

            // RecyclerView List (8.4 weight)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(8.4f)
                    .padding(top = 10.dp)
            ) {
                items(uiState.listOfDeliveries) { delivery ->
                    DeliveryItemCard(
                        delivery = delivery,
                        selectedMacAddress = selectedMacAddress.value,
                        //onDeliveryClick = onDeliveryClick,
                        onShareKeyClick = onShareKeyClick,
                        showDeleteShareKey = showDeleteShareKey,
                        keyId = keyId,
                        shareAccessEmail = shareAccessEmail
                    )
                }
            }

            // Bottom Section (1.1 weight)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f),
                contentAlignment = Alignment.TopCenter
            ) {
                TextViewWithFont(
                    text = stringResource(R.string.expired_deliveries_description),
                    color = ThmDescriptionTextColor,
                    fontSize = ThmSubTitleTextSize,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .padding(top = 2.dp),
                )
            }
        }

        // Progress Bar
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DeliveryItemCard(
    delivery: LockerKeyWithShareAccess,
    selectedMacAddress: String,
    //onDeliveryClick: (LockerKeyWithShareAccess) -> Unit,
    onShareKeyClick: (id: Int, selectedMacAddress: String) -> Unit,
    showDeleteShareKey: MutableState<Boolean>,
    keyId: MutableState<Int>,
    shareAccessEmail: MutableState<String>
) {
    val nameValue = when {
        delivery.masterName?.isNotEmpty() == true && UserUtil.user?.uniqueId != null ->
            "${delivery.masterName?.trim()} - ${UserUtil.user?.uniqueId}"
        delivery.masterName?.isNotEmpty() == true -> delivery.masterName?.trim()
        else -> "-"
    }

    val trackingNumber = delivery.trackingNumber?.takeIf { it.isNotEmpty() } ?: "-"
    val tan = delivery.tan ?: "-"
    val formattedDate = formatCorrectDate(delivery.timeCreated)

    val showShareButton = delivery.installationType != InstalationType.LINUX
            && delivery.purpose != RLockerKeyPurpose.DELIVERY
    val showSharedWith = delivery.purpose != RLockerKeyPurpose.DELIVERY &&
            delivery.installationType != InstalationType.LINUX
    val showShareAccess = (delivery.installationType == InstalationType.LINUX && delivery.listOfShareAccess.isNotEmpty()) ||
            delivery.purpose == RLockerKeyPurpose.DELIVERY

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            //.padding(start = 10.dp)
            .padding(bottom = 7.dp),
            //.clickable {
                //onDeliveryClick(delivery)
            //},
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (lockerImage, nameAddressSection, deliveryDataSection,
                sharedWithText, shareAccessList, shareButton) = createRefs()

            // Locker Picture
            Image(
                painter = painterResource(R.drawable.ic_parcel),
                contentDescription = "Locker",
                modifier = Modifier
                    .constrainAs(lockerImage) {
                        top.linkTo(parent.top, margin = 5.dp)
                        start.linkTo(parent.start)
                        end.linkTo(nameAddressSection.start)
                        bottom.linkTo(nameAddressSection.bottom)
                        width = Dimension.percent(0.15f)
                    }
            )

            // Name and Address Section
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
                    .constrainAs(nameAddressSection) {
                        top.linkTo(parent.top)
                        start.linkTo(lockerImage.end)
                        end.linkTo(parent.end)
                        width = Dimension.percent(0.85f)
                    }
            ) {
                TextViewWithFont(
                    text =  nameValue ?: "",
                    color = ThmDescriptionTextColor,
                    fontSize = ThmSubTitleTextSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                )

                TextViewWithFont(
                    text = delivery.masterAddress ?: "",
                    color = ThmDescriptionTextColor,
                    fontSize = ThmSubTitleTextSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                )

            }

            // Delivery Data Section
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .padding(end = 20.dp)
                    .constrainAs(deliveryDataSection) {
                        top.linkTo(nameAddressSection.bottom, margin = 5.dp)
                        start.linkTo(nameAddressSection.start)
                        end.linkTo(parent.end)
                    }
            ) {
                // Tracking Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextViewWithFont(
                        text = stringResource(R.string.tracking_number_cpl),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(4.5f),
                        maxLines = 1,
                    )

                    TextViewWithFont(
                        text =  trackingNumber,
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(5.5f),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                    )
                }

                // Delivered
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextViewWithFont(
                        text = stringResource(R.string.delivered_cpl),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(4.5f),
                        maxLines = 1,
                    )

                    TextViewWithFont(
                        text = formattedDate,
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(5.5f),
                        maxLines = 1,
                    )
                }

                // TAN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextViewWithFont(
                        text = stringResource(R.string.tan_cpl),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(4.5f),
                        maxLines = 1,
                    )

                    TextViewWithFont(
                        text = tan.toString(),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(5.5f),
                        maxLines = 1,
                    )
                }

                // Locker Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextViewWithFont(
                        text = stringResource(R.string.locker_size_cpl),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(4.5f),
                        maxLines = 1,
                    )

                    TextViewWithFont(
                        text = delivery.lockerSize ?: "",
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(5.5f),
                        maxLines = 1,
                    )
                }
            }

            println("Delivered with 11: $showSharedWith")
            // Shared With Text
            if (showSharedWith
                //&& delivery.createdByName != null
                ) {

                TextViewWithFont(
                    text = stringResource(
                        R.string.shared_with
                    ),
                    color = ThmDescriptionTextColor,
                    fontSize = ThmSubTitleTextSize,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .constrainAs(sharedWithText) {
                            top.linkTo(deliveryDataSection.bottom)
                            start.linkTo(deliveryDataSection.start)
                            //end.linkTo(parent.end)
                        },
                )
            }

            println("Delivered with 22: $showShareAccess")
            // Share Access List
            if (showShareAccess) {

                val topConstraints = if(showSharedWith) sharedWithText else deliveryDataSection
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .constrainAs(shareAccessList) {
                            top.linkTo( topConstraints.bottom )
                            start.linkTo(deliveryDataSection.start)
                            //end.linkTo(parent.end)
                            if(showShareButton) {
                                bottom.linkTo(shareButton.top)
                            }
                            else{
                                bottom.linkTo(parent.bottom)
                            }
                        }
                ) {
                    delivery.listOfShareAccess.forEach { shareAccess ->
                        ShareAccessItem(
                            shareAccess = shareAccess,
                            showDeleteShareKey = showDeleteShareKey,
                            keyId = keyId,
                            shareAccessEmail = shareAccessEmail
                        )
                    }
                }
            }

            // Share Button
            if (showShareButton) {
                Button(
                    onClick = { onShareKeyClick(delivery.id, selectedMacAddress) },
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(30.dp)
                        .padding(horizontal = 10.dp)
                        .constrainAs(shareButton) {
                            top.linkTo( shareAccessList.bottom,
                                margin = 10.dp
                            )
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, margin = 10.dp)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_generic_key_sharing),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.em,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ShareAccessItem(
    shareAccess: ShareAccessKey,
    showDeleteShareKey: MutableState<Boolean>,
    keyId: MutableState<Int>,
    shareAccessEmail: MutableState<String>
) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp, top = 5.dp, bottom = 5.dp)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Delete Icon
        IconButton(
            onClick = {
                showDeleteShareKey.value = true
                keyId.value = shareAccess.id
                shareAccessEmail.value = shareAccess.email
            },
            modifier = Modifier
                .size(24.dp)
                .padding(end = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = "Remove user",
                tint = MaterialTheme.colorScheme.error
            )
        }

        TextViewWithFont(
            text = shareAccess.email,
            color = ThmDescriptionTextColor,
            fontSize = ThmDescriptionTextSize,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            maxLines = 1,
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 4.dp),
        )
        // Email Text
//        Text(
//            text = shareAccess.email,
//            modifier = Modifier
//                .wrapContentWidth()
//                .padding(start = 4.dp),
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            fontSize = 14.sp,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            fontWeight = FontWeight.Normal
//        )
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