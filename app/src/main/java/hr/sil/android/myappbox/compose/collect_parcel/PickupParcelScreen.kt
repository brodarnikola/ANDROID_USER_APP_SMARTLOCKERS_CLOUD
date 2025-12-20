package hr.sil.android.myappbox.compose.collect_parcel


import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.RotatingRingIndicator
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmCPShareKeyEvenBC
import hr.sil.android.myappbox.compose.components.ThmCPShareKeyOddBC
import hr.sil.android.myappbox.compose.components.ThmCPTelemetryBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmShareKeyAdapterTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
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

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(PickupParcelScreenEvent.OnInit(SettingsHelper.userLastSelectedLocker, context))
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
//                is PickupParcelScreenUiEvent.NavigateToQrCode -> {
//                    // Navigate to QR code screen
//                }
//                is PickupParcelScreenUiEvent.NavigateToFinish -> {
//                    activity.finish()
//                }
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, event.toastLength).show()
                }
            }
        }
    }

    GradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
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
            if (state.isInProximity) {
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
                                viewModel.onEvent(PickupParcelScreenEvent.OnOpenClick(context, activity))
                            }
                            .constrainAs(clOpenPickupParcel) {
                                top.linkTo(parent.top)
                                height = Dimension.percent(0.35f)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = state.circleDrawableRes),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(40.dp)
                                .then(
                                    if (state.isAnimating) {
                                        Modifier.rotate(state.animationRotation)
                                    } else Modifier
                                )
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_padlock_top),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(79.dp, 90.dp)
                                .offset(y = if (state.isUnlocked) (-60).dp else (-30).dp)
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_padlock_base),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.offset(y = 46.dp)
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

                    // Keys List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .constrainAs(keysList) {
                                top.linkTo(statusText.bottom)
                                height = Dimension.percent(0.47f)
                            }
                    ) {
                        itemsIndexed(state.keys) { index, key ->
                            ParcelPickupKeyItem(
                                key = key,
                                index = index,
                                instalationType = state.installationType,
                                type = state.masterUnitType,
                                onShareClick = {
                                    viewModel.onEvent(PickupParcelScreenEvent.OnShareKeyClick(key))
                                },
                                onDeleteClick = {
                                    viewModel.onEvent(PickupParcelScreenEvent.OnDeleteKeyClick(index, key.id))
                                },
                                onQrCodeClick = {
                                    viewModel.onEvent(PickupParcelScreenEvent.OnQrCodeClick(
                                        SettingsHelper.userLastSelectedLocker, context, activity))
                                }
                            )
                        }
                    }

                    // Telemetry
                    if (state.showTelemetry) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ThmCPTelemetryBackgroundColor )
                                .alpha(0.4f)
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                                .constrainAs(clLockerTelemetry) {
                                    top.linkTo(keysList.bottom)
                                    bottom.linkTo(parent.bottom)
                                    height = Dimension.percent(0.08f)
                                },
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(3.3f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_humidity),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                                TextViewWithFont(
                                    text = state.humidity,
                                    color = ThmDescriptionTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.weight(2.8f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_temperature),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                                TextViewWithFont(
                                    text = state.temperature,
                                    color = ThmDescriptionTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.weight(3.8f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_air_pressure),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                                TextViewWithFont(
                                    text = state.airPressure,
                                    color = ThmDescriptionTextColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        }
                    }

                    // Force Open Button
                    if (state.showForceOpen) {
                        ButtonWithFont(
                            text = stringResource(id = R.string.app_generic_force_open),
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
                                .padding(bottom = 20.dp)
                                .constrainAs(forceOpen) {
                                    bottom.linkTo(pickupParcelFinish.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    height = Dimension.percent(0.08f)
                                },
                            enabled = true
                        )
                    }

                    // Finish Button
                    if (state.showFinishButton) {
                        ButtonWithFont(
                            text = stringResource(id = R.string.app_generic_confirm),
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
                                .padding(bottom = 140.dp)
                                .constrainAs(pickupParcelFinish) {
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    height = Dimension.percent(0.08f)
                                },
                            enabled = true
                        )
                    }

                    // Locker Cleaning Checkbox
                    if (state.showCleaningCheckbox) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .constrainAs(llClean) {
                                    top.linkTo(pickupParcelFinish.bottom)
                                    bottom.linkTo(parent.bottom)
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
                                    top.linkTo(llClean.bottom)
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
                        text = stringResource(id = R.string.not_in_proximity_second_description, state.deviceName),
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
    val backgroundColor = if (index % 2 == 0) ThmCPShareKeyOddBC else ThmCPShareKeyEvenBC

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .alpha(0.4f)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = key.timeCreated, // key.displayText,
            color = ThmShareKeyAdapterTextColor,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(0.83f)
        )

        when (key.purpose) {
            RLockerKeyPurpose.DELIVERY -> {
                if (instalationType != InstalationType.LINUX) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.weight(0.15f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                } else {
                    IconButton(
                        onClick = onQrCodeClick,
                        modifier = Modifier.weight(0.15f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.qr_code),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            RLockerKeyPurpose.PAF -> {
                if (key.createdForId != null && instalationType != InstalationType.LINUX) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.weight(0.15f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            else -> {}
        }
    }
}