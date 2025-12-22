package hr.sil.android.myappbox.compose.collect_parcel


import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.cache.status.ActionStatusHandler.log
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.RotatingRingIndicator
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmCPShareKeyEvenBC
import hr.sil.android.myappbox.compose.components.ThmCPShareKeyOddBC
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmShareKeyAdapterTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.dialog.DeletePickAtFriendKeyDialog
import hr.sil.android.myappbox.compose.dialog.PickAtFriendKeysDialog
import hr.sil.android.myappbox.compose.dialog.ShareApplicationDialog
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.utils.UiEvent


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PickupParcelScreen(
    viewModel: PickupParcelViewModel = viewModel(),
    navigateUp: () -> Unit = {}
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val activity = LocalContext.current as Activity


    val displayRemovePickAtFriendKeyDialog = rememberSaveable { mutableStateOf(false) }
    val positionPickAtFriendKeyId = rememberSaveable { mutableStateOf(-1) }
    val endUserEmail = rememberSaveable { mutableStateOf("") }

    val shareAppEmail = rememberSaveable { mutableStateOf("") }

    val displayPickAtFriendKeyDialog = rememberSaveable { mutableStateOf(false) }
    val pickAtFriendKeyId = rememberSaveable { mutableStateOf(-1) }

    val shareApplicationDialog = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(
            PickupParcelScreenEvent.OnInit(
                SettingsHelper.userLastSelectedLocker,
                context
            )
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
            }
        }
        viewModel.uiEventsPickupParcel.collect { event ->
            when (event) {
                is PickupParcelScreenUiEvent.NavigateToQrCode -> {
                    // Navigate to QR code
                    //navigateUp
                }

                is PickupParcelScreenUiEvent.NavigateToFinish -> {
                    navigateUp()
                }
            }
        }
    }

    if (displayPickAtFriendKeyDialog.value) {
        PickAtFriendKeysDialog(
            onDismiss = { displayPickAtFriendKeyDialog.value = false },
            onConfirm = { email ->
                log.info("onConfirm email 11: ${email}")
                shareAppEmail.value = email
                viewModel.onEvent(
                    PickupParcelScreenEvent.OnConfirmPickAtFriendKeyClick(
                        email = email,
                        pickAtFriendKeyId = pickAtFriendKeyId.value,
                        onSuccess = {
                            displayPickAtFriendKeyDialog.value = false
                        },
                        onInvitationCode = {
                            displayPickAtFriendKeyDialog.value = false
                            shareApplicationDialog.value = true
                        },
                        onError = {
                            displayPickAtFriendKeyDialog.value = false
                        }
                        //context,
                        //activity
                    )
                )
            },
            onCancel = {
                displayPickAtFriendKeyDialog.value = false
            }
        )
    }

    if (shareApplicationDialog.value) {
        val stringDownloadAndroidApp = stringResource(R.string.download_androdi_app)
        val stringDownloadIosApp = stringResource(R.string.download_ios_app)
        val stringDownloadWebApp = stringResource(R.string.web_portal)
        val shareAppTitle = stringResource(R.string.access_sharing_share_choose_sharing)
        ShareApplicationDialog(
            onDismiss = { shareApplicationDialog.value = false },
            onConfirm = {
                val appLink = BuildConfig.APP_ANDR_DOWNLOAD_URL
                val iOSLink = BuildConfig.APP_IOS_DOWNLOAD_URL
                val webPortal = BuildConfig.WEB_PORTAL

                var inviteUserText =  ""

                if(  App.ref.resources.getBoolean(R.bool.has_android_link) &&  App.ref.resources.getBoolean(R.bool.has_ios_link) &&  App.ref.resources.getBoolean(R.bool.has_web_portal_link) ) {
                    inviteUserText += "\n" + stringDownloadAndroidApp + appLink + "\n" + stringDownloadIosApp + iOSLink + "\n" + stringDownloadWebApp + " " + webPortal
                }
                else if(  App.ref.resources.getBoolean(R.bool.has_android_link) &&  App.ref.resources.getBoolean(R.bool.has_ios_link) ) {
                    inviteUserText += "\n" + stringDownloadAndroidApp + appLink + "\n" + stringDownloadIosApp + iOSLink
                }
                else if(  App.ref.resources.getBoolean(R.bool.has_android_link) && ! App.ref.resources.getBoolean(R.bool.has_ios_link) ) {
                    inviteUserText += "\n" + stringDownloadAndroidApp + appLink
                }

                val shareBodyText = inviteUserText
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.setType("message/rfc822")
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(shareAppEmail.value) /*arrayOf(userAccess.groupUserEmail)*/)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject/Title")
                emailIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)

                activity.startActivity(Intent.createChooser(emailIntent,  shareAppTitle))
                shareApplicationDialog.value = false
            },
            onCancel = {
                shareApplicationDialog.value = false
            }
        )
    }

    if (displayRemovePickAtFriendKeyDialog.value) {
        DeletePickAtFriendKeyDialog(
            endUserEmail = endUserEmail.value,
            onDismiss = { displayRemovePickAtFriendKeyDialog.value = false },
            onConfirm = {
                viewModel.onEvent(
                    PickupParcelScreenEvent.OnDeleteKeyClick(
                        positionPickAtFriendKeyId.value,
                        pickAtFriendKeyId.value,
                        onSuccess = {
                            displayRemovePickAtFriendKeyDialog.value = false
                        },
                        onError = {
                            displayRemovePickAtFriendKeyDialog.value = false
                        }
                    )
                )
            },
            onCancel = {
                displayRemovePickAtFriendKeyDialog.value = false
            }
        )
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (pickupParcelTitle, clInProximity, clNotInProximity) = createRefs()

        // Title
        TextViewWithFont(
            text = stringResource(id = state.titleRes).uppercase(),
            color = ThmTitleTextColor,
            fontSize = ThmTitleTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = ThmTitleLetterSpacing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .constrainAs(pickupParcelTitle) {
                    top.linkTo(parent.top)
                    height = Dimension.percent(0.05f)
                }
        )

        // In Proximity Layout
        if (state.isInProximity || state.isUnlocked) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(clInProximity) {
                        top.linkTo(pickupParcelTitle.bottom)
                        height = Dimension.percent(0.85f)
                    }
            ) {
                val (clOpenPickupParcel, statusText, keysList, clLockerTelemetry,
                    forceOpen, pickupParcelFinish, llClean, progressBarLockerCleaning) = createRefs()

                // Open Pickup Parcel Button Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = state.isButtonEnabled) {
                            viewModel.onEvent(
                                PickupParcelScreenEvent.OnOpenClick(
                                    context,
                                    activity
                                )
                            )
                        }
                        .constrainAs(clOpenPickupParcel) {
                            top.linkTo(parent.top)
                            height = Dimension.percent(0.35f)
                        },
                    contentAlignment = Alignment.Center
                ) {

                    val rotation by rememberInfiniteTransition(label = "spinner")
                        .animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 1000,
                                    easing = LinearEasing
                                ),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotation"
                        )

                    Icon(
                        painter = painterResource(id = state.circleDrawableRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .rotate(if (state.isAnimating) rotation else 0f)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_padlock_top),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(79.dp, 90.dp)
                            .offset(y = if (state.isUnlocked) (-30).dp else (5).dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_padlock_base),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.offset(y = 36.dp)
                    )
                }

                // Status Text
                TextViewWithFont(
                    text = state.statusText,
                    color = if (state.isError) ThmErrorTextColor else ThmDescriptionTextColor,
                    fontSize = ThmSubTitleTextSize,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .constrainAs(statusText) {
                            top.linkTo(clOpenPickupParcel.bottom)
                            height = Dimension.percent(0.1f)
                        }
                )

                val finalList = if(!state.isUnlocked) state.keys else listOf<RCreatedLockerKey>()
                    // Keys List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .constrainAs(keysList) {
                                top.linkTo(statusText.bottom)
                                height = Dimension.percent(0.37f)
                            }
                    ) {
                        itemsIndexed(finalList) { index, key ->
                            ParcelPickupKeyItem(
                                key = key,
                                index = index,
                                instalationType = state.installationType,
                                type = state.masterUnitType,
                                onShareClick = {
                                    pickAtFriendKeyId.value = key.id
                                    displayPickAtFriendKeyDialog.value = true
                                },
                                onDeleteClick = {
                                    pickAtFriendKeyId.value = key.id
                                    endUserEmail.value = key.createdForEndUserEmail ?: ""
                                    displayRemovePickAtFriendKeyDialog.value = true
                                    positionPickAtFriendKeyId.value = index
                                },
                                onQrCodeClick = {
                                    viewModel.onEvent(
                                        PickupParcelScreenEvent.OnQrCodeClick(
                                            SettingsHelper.userLastSelectedLocker, context, activity
                                        )
                                    )
                                }
                            )
                        }
                    }

                // Telemetry
//                if (state.showTelemetry) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(ThmCPTelemetryBackgroundColor)
//                            .alpha(0.4f)
//                            .padding(horizontal = 10.dp, vertical = 10.dp)
//                            .constrainAs(clLockerTelemetry) {
//                                top.linkTo(keysList.bottom)
//                                bottom.linkTo(parent.bottom)
//                                height = Dimension.percent(0.08f)
//                            },
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Row(
//                            modifier = Modifier.weight(3.3f),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_humidity),
//                                contentDescription = null,
//                                tint = Color.Unspecified
//                            )
//                            TextViewWithFont(
//                                text = state.humidity,
//                                color = ThmDescriptionTextColor,
//                                fontSize = 13.sp,
//                                fontWeight = FontWeight.Bold,
//                                maxLines = 1,
//                                modifier = Modifier.padding(start = 5.dp)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier.weight(2.8f),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_temperature),
//                                contentDescription = null,
//                                tint = Color.Unspecified
//                            )
//                            TextViewWithFont(
//                                text = state.temperature,
//                                color = ThmDescriptionTextColor,
//                                fontSize = 13.sp,
//                                fontWeight = FontWeight.Bold,
//                                maxLines = 1,
//                                modifier = Modifier.padding(start = 5.dp)
//                            )
//                        }
//
//                        Row(
//                            modifier = Modifier.weight(3.8f),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_air_pressure),
//                                contentDescription = null,
//                                tint = Color.Unspecified
//                            )
//                            TextViewWithFont(
//                                text = state.airPressure,
//                                color = ThmDescriptionTextColor,
//                                fontSize = 13.sp,
//                                fontWeight = FontWeight.Bold,
//                                maxLines = 1,
//                                modifier = Modifier.padding(start = 5.dp)
//                            )
//                        }
//                    }
//                }

                // Force Open Button
                if (state.showForceOpen) {
                    ButtonWithFont(
                        text = stringResource(id = R.string.app_generic_force_open).uppercase(),
                        onClick = {
                            viewModel.onEvent(
                                PickupParcelScreenEvent.OnForceOpenClick(
                                    context,
                                    activity
                                )
                            )
                        },
                        backgroundColor = ThmMainButtonBackgroundColor,
                        textColor = ThmLoginButtonTextColor,
                        fontSize = ThmButtonTextSize,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = ThmButtonLetterSpacing,
                        modifier = Modifier
                            .width(250.dp)
                            .constrainAs(forceOpen) {
                                top.linkTo(keysList.bottom, margin = 5.dp)
                                //bottom.linkTo(pickupParcelFinish.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                //height = Dimension.percent(0.08f)
                            },
                        enabled = true
                    )
                }

                // Finish Button
                if (state.showFinishButton) {
                    ButtonWithFont(
                        text = stringResource(id = R.string.app_generic_confirm).uppercase(),
                        onClick = {
                            viewModel.onEvent(PickupParcelScreenEvent.OnFinishClick(activity))
                        },
                        backgroundColor = ThmMainButtonBackgroundColor,
                        textColor = ThmLoginButtonTextColor,
                        fontSize = ThmButtonTextSize,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = ThmButtonLetterSpacing,
                        modifier = Modifier
                            .width(250.dp)
                            //.padding(bottom = 140.dp)
                            .constrainAs(pickupParcelFinish) {
                                //bottom.linkTo(parent.bottom)
                                top.linkTo(forceOpen.bottom, margin = 5.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                //height = Dimension.percent(0.08f)
                            },
                        enabled = true
                    )
                }

                // Locker Cleaning Checkbox
                if (state.showCleaningCheckbox) {

                    val topConstraint =
                        if (state.showFinishButton) pickupParcelFinish else if (state.showForceOpen) forceOpen else keysList

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .constrainAs(llClean) {
                                top.linkTo(topConstraint.bottom, margin = 5.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                // top.linkTo(pickupParcelFinish.bottom)
                                //bottom.linkTo(parent.bottom)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_needs_cleaning),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                        Row(
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .clickable(enabled = state.isCleaningCheckboxEnabled) {
                                    viewModel.onEvent(
                                        PickupParcelScreenEvent.OnCleaningCheckboxClick(
                                            state.lockerMacAddress,
                                            context,
                                            activity
                                        )
                                    )
                                }
                                .alpha(if (state.isCleaningCheckboxEnabled) 1f else 0.5f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.isCleaningCheckboxChecked,
                                onCheckedChange = null,
                                enabled = state.isCleaningCheckboxEnabled
                            )

                            TextViewWithFont(
                                text = stringResource(id = R.string.locker_needs_cleaning).uppercase(),
                                color = ThmDescriptionTextColor,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                // Progress Bar for Cleaning
                if (state.showCleaningProgress) {
                    RotatingRingIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .constrainAs(progressBarLockerCleaning) {
                                top.linkTo(llClean.bottom, margin = 12.dp)
                                //top.linkTo(llClean.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                }
            }
        }

        // Not In Proximity Layout
        if (!state.isInProximity) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .constrainAs(clNotInProximity) {
                        top.linkTo(pickupParcelTitle.bottom)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_not_in_proximity),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(bottom = 100.dp)
                )

                TextViewWithFont(
                    text = stringResource(id = R.string.not_in_proximity_first_description),
                    color = ThmDescriptionTextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                )

                TextViewWithFont(
                    text = stringResource(
                        id = R.string.not_in_proximity_second_description,
                        state.deviceName
                    ),
                    color = ThmDescriptionTextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 30.dp)
                )
            }
        }
    }
}

private fun buildKeyText(
    key: RCreatedLockerKey,
    type: RMasterUnitType
): String {

    return when (key.purpose) {

        RLockerKeyPurpose.DELIVERY -> {
            val formattedDate = formatCorrectDate(key.timeCreated)

            if (type == RMasterUnitType.SPL) {
                App.ref.getString(
                    R.string.peripheral_settings_share_access_spl,
                    formattedDate
                )
            } else {
                App.ref.getString(
                    R.string.peripheral_settings_share_access,
                    key.lockerSize,
                    formattedDate
                )
            }
        }

        RLockerKeyPurpose.PAF -> {
            val formattedDate = formatCorrectDate(key.baseTimeCreated)

            if (key.createdForId != null) {
                if (type == RMasterUnitType.MPL) {
                    App.ref.getString(
                        R.string.peripheral_settings_remove_access,
                        key.createdForEndUserName,
                        key.lockerSize,
                        formattedDate
                    )
                } else {
                    App.ref.getString(
                        R.string.peripheral_settings_remove_access_spl,
                        key.createdForEndUserName,
                        formattedDate
                    )
                }
            } else {
                if (type == RMasterUnitType.MPL) {
                    App.ref.getString(
                        R.string.peripheral_settings_grant_access,
                        key.createdByName,
                        key.lockerSize,
                        formattedDate
                    )
                } else {
                    App.ref.getString(
                        R.string.peripheral_settings_grant_access_spl,
                        key.createdByName,
                        formattedDate
                    )
                }
            }
        }

        else -> ""
    }
}

@Composable
fun ParcelPickupKeyItem(
    key: RCreatedLockerKey,
    index: Int,
    instalationType: InstalationType,
    type: RMasterUnitType,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onQrCodeClick: () -> Unit
) {

    val backgroundColor =
        if (index % 2 == 0) ThmCPShareKeyOddBC else ThmCPShareKeyEvenBC

    val displayText = remember(key) {
        buildKeyText(key, type)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ===== TEXT =====
        TextViewWithFont(
            text = displayText,
            color = ThmShareKeyAdapterTextColor,
            fontWeight = FontWeight.Normal,
            maxLines = 4,
            modifier = Modifier.weight(1f)
        )

        // ===== ACTION ICON =====
        when (key.purpose) {

            RLockerKeyPurpose.DELIVERY -> {
                if (instalationType != InstalationType.LINUX) {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_share),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                } else {
                    IconButton(onClick = onQrCodeClick) {
                        Icon(
                            painter = painterResource(R.drawable.qr_code),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            RLockerKeyPurpose.PAF -> {
                if (key.createdForId != null && instalationType != InstalationType.LINUX) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            else -> Unit
        }
    }
}