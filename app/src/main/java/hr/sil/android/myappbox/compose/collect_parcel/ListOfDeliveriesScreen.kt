package hr.sil.android.myappbox.compose.collect_parcel

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.SignUpOnboardingActivity
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.SettingsRoundedBackground
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmToolbarBackgroundColor
import hr.sil.android.myappbox.compose.dialog.LogoutDialog
import hr.sil.android.myappbox.compose.main_activity.MainDestinations
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.uppercase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
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
    //deliveries: List<LockerKeyWithShareAccess>,
    //macAddress: String,
    //isLoading: Boolean = false,
    //onDeliveryClick: (LockerKeyWithShareAccess) -> Unit,
    onShareKeyClick: (Long) -> Unit,
    viewModel: ListOfDeliveriesViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val selectedMacAddress = rememberSaveable { mutableStateOf(SettingsHelper.userLastSelectedLocker) }
    val isLoading = rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Title (0.5 weight)
            Text(
                text = stringResource(R.string.list_of_deliveries_cpl),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .wrapContentHeight(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                letterSpacing = 0.1.em,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                //style = MaterialTheme.typography.titleLarge.copy(
                //    textTransform = TextTransform.Uppercase
                //)
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
                        macAddress = selectedMacAddress.value,
                        //onDeliveryClick = onDeliveryClick,
                        onShareKeyClick = onShareKeyClick
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
                Text(
                    text = stringResource(R.string.expired_deliveries_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .padding(top = 2.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
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
    macAddress: String,
    //onDeliveryClick: (LockerKeyWithShareAccess) -> Unit,
    onShareKeyClick: (Long) -> Unit
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

    val showShareButton = delivery.installationType != InstalationType.LINUX &&
            delivery.purpose != RLockerKeyPurpose.DELIVERY
    val showSharedWith = delivery.purpose != RLockerKeyPurpose.DELIVERY &&
            delivery.installationType != InstalationType.LINUX
    val showShareAccess = (delivery.installationType == InstalationType.LINUX && delivery.listOfShareAccess.isNotEmpty()) ||
            delivery.purpose == RLockerKeyPurpose.DELIVERY

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            //.padding(start = 10.dp)
            .padding(bottom = 7.dp)
            .clickable {
                //onDeliveryClick(delivery)
            },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (lockerImage, nameAddressSection, deliveryDataSection,
                sharedWithText, shareAccessList, shareButton) = createRefs()

            // Locker Picture
            Image(
                painter = painterResource(R.drawable.qr_code),
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
                Text(
                    text = nameValue ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = delivery.masterAddress ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
                    Text(
                        text = stringResource(R.string.tracking_number_cpl),
                        modifier = Modifier.weight(4.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = trackingNumber,
                        modifier = Modifier.weight(5.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Delivered
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.delivered_cpl),
                        modifier = Modifier.weight(4.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formattedDate,
                        modifier = Modifier.weight(5.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // TAN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.tan_cpl),
                        modifier = Modifier.weight(4.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = tan.toString(),
                        modifier = Modifier.weight(5.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Locker Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.locker_size_cpl),
                        modifier = Modifier.weight(4.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = delivery.lockerSize ?: "",
                        modifier = Modifier.weight(5.5f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Shared With Text
            if (showSharedWith && delivery.createdByName != null) {
                Text(
                    text = stringResource(
                        R.string.peripheral_settings_grant_access,
                        delivery.createdByName ?: "",
                        delivery.lockerSize ?: "",
                        formattedDate
                    ),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .constrainAs(sharedWithText) {
                            top.linkTo(deliveryDataSection.bottom)
                            start.linkTo(nameAddressSection.start)
                            end.linkTo(parent.end)
                        },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            // Share Access List
            if (showShareAccess) {
                LazyRow(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .constrainAs(shareAccessList) {
                            top.linkTo(
                                if (showSharedWith) sharedWithText.bottom else deliveryDataSection.bottom
                            )
                            start.linkTo(nameAddressSection.start)
                            end.linkTo(parent.end)
                            if (showShareButton) {
                                bottom.linkTo(shareButton.top)
                            }
                        }
                ) {
                    items(delivery.listOfShareAccess) { shareAccess ->
                        ShareAccessItem(shareAccess)
                    }
                }
            }

            // Share Button
            if (showShareButton) {
                Button(
                    onClick = { onShareKeyClick(delivery.id.toLong()) },
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(30.dp)
                        .padding(horizontal = 10.dp)
                        .constrainAs(shareButton) {
                            top.linkTo(
                                if (showShareAccess) shareAccessList.bottom else deliveryDataSection.bottom,
                                margin = 10.dp
                            )
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
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
    shareAccess: ShareAccessKey
) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .wrapContentWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = shareAccess.email,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 14.sp
        )
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