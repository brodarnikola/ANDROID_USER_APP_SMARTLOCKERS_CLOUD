package hr.sil.android.myappbox.compose.home_screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.cache.status.ActionStatusHandler.log
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.main_activity.MainDestinations
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper

// --- 1. MOCK THEME & RESOURCES (Replace with your actual Theme/R values) ---

object AppColors {
    val LoginBackground = Color(0xFFF5F5F5)
    val ToolbarBackground = Color(0xFFF5F5F5) // ?attr/thmLoginBackground
    val TitleText = Color(0xFF333333)
    val DescriptionText = Color(0xFF666666)
    val ButtonText = Color.White
    val BadgeBackground = Color.Red
    val BadgeText = Color.White
    val PrimaryDark = Color(0xFF00574B)
    val DarkerBlack80 = Color(0xCC000000)

    // Simulating the red background drawable
    val RedButtonBackground = Color(0xFFD32F2F)
}

// Simulating your R.string and R.drawable
object Res {
    object string {
        const val no_deliveries = "No deliveries to locker possible"
        const val enter_pin = "ENTER VERIFICATION PIN"
        const val selected_locker = "SELECTED LOCKER"
        const val pickup_parcel = "PICKUP PARCEL"
        const val send_parcel = "SEND PARCEL"
        const val key_sharing = "KEY SHARING"
        const val my_config = "MY CONFIGURATION"
        const val humidity = "50%" // Example data
        const val temperature = "22°C"
        const val pressure = "1013 hPa"
    }
    object drawable {
        // Placeholders - replace with actual resource IDs
//        val ic_map = R.drawable.ic_dialog_map
//        val ic_chevron_right = R.drawable.ic_media_play // Placeholder
//        val ic_copy_address = R.drawable.ic_menu_save // Placeholder
//        val ic_collect_parcel = R.drawable.ic_input_add // Placeholder
//        val ic_send_parcel = R.drawable.ic_menu_send // Placeholder
//        val ic_share_access = R.drawable.ic_menu_share // Placeholder
//        val ic_settings = R.drawable.ic_menu_preferences // Placeholder
//        val ic_humidity = R.drawable.ic_lock_idle_low_battery // Placeholder
//        val ic_temperature = R.drawable.ic_lock_idle_charging // Placeholder
//        val ic_pressure = R.drawable.ic_lock_power_off // Placeholder
    }
}

// --- 2. MAIN SCREEN COMPOSABLE ---

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: NavHomeViewModel,
    nextScreen: (route: String) -> Unit = {},
    navigateUp: () -> Unit = {}
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    val lockerNameOrAddress = rememberSaveable { mutableStateOf("") }

    // Initial load
    LaunchedEffect(Unit) {
        lockerNameOrAddress.value =
            MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
                ?.name?.ifEmpty {
                    MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.address ?: "YEAH"
                } ?: "YEAH 2"
    }

    // Update again on Resume
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
                lockerNameOrAddress.value =
                    MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
                        ?.name?.ifEmpty {
                            MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]?.address
                                ?: "YEAH"
                        } ?: "YEAH 2"
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            // Add your drawer content here
//            // ModalDrawerSheet { /* drawer items */ }
//        }
//    ) {
//        GradientBackground(
//            modifier = Modifier.fillMaxSize()
//        ) {
        Box(modifier = Modifier.fillMaxSize()) {
                //Column(modifier = Modifier.fillMaxSize()) {
                    // AppBar
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight()
//                            .windowInsetsPadding(WindowInsets.statusBars)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Max)
//                        ) {
//                            Spacer(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(56.dp)
//                            )
//
//                            Icon(
//                                painter = painterResource(id = R.drawable.logo_header_zwick),
//                                contentDescription = null,
//                                tint = Color.Unspecified,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_help_access_sharing),
//                                contentDescription = null,
//                                tint = Color.Unspecified,
//                                modifier = Modifier
//                                    .align(Alignment.CenterEnd)
//                                    .padding(end = 20.dp)
//                                    .clickable {
//                                        //viewModel.onEvent(MainScreenEvent.OnSupportClick)
//                                    }
//                            )
//                        }
//                    }

                    // ScrollView Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 10.dp, bottom = 20.dp)
                    ) {
                        // Verification Pin Disabled Section
                        //if (state.showVerificationPinDisabled) {
//                        if(true) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(top = 5.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
//                                    .border(
//                                        width = 2.dp,
//                                        color = colorResource(R.color.colorPrimary),
//                                        shape = RoundedCornerShape(8.dp)
//                                    )
//                                    .background(
//                                        color = colorResource(R.color.transparentColor),
//                                        shape = RoundedCornerShape(8.dp)
//                                    )
//                                    .padding(horizontal = 5.dp, vertical = 10.dp)
//                            ) {
//                                TextViewWithFont(
//                                    text = stringResource(id = R.string.no_deliveris_to_locker_possible),
//                                    color = colorResource(R.color.colorDarkerBlack80Percent),
//                                    fontSize = ThmTitleTextSize,
//                                    fontWeight = FontWeight.Normal,
//                                    textAlign = TextAlign.Center,
//                                    //maxLines = 3,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(top = 5.dp)
//                                )
//
//                                ButtonWithFont(
//                                    text = stringResource(id = R.string.enter_verification_pin).uppercase(),
//                                    onClick = {
//                                        //viewModel.onEvent(MainScreenEvent.OnEnterVerificationPinClick)
//                                    },
//                                    backgroundColor = ThmMainButtonBackgroundColor,
//                                    textColor = ThmLoginButtonTextColor,
//                                    fontSize = ThmButtonTextSize,
//                                    fontWeight = FontWeight.Medium,
//                                    modifier = Modifier
//                                        .width(230.dp)
//                                        .padding(top = 10.dp)
//                                        .align(Alignment.CenterHorizontally),
//                                    letterSpacing = 15.sp,
//                                    enabled = true
//                                )
//                            }
//                        }

                        // Verification Pin Approved Section
                        //if (state.showVerificationPinApproved) {
                        if(true) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextViewWithFont(
                                    text = stringResource(id = R.string.app_generic_selected_locker).uppercase(),
                                    color = ThmTitleTextColor,
                                    fontSize = ThmTitleTextSize,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                )

                                // Choose City Locker and Google Maps
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
                                            .clickable {
                                                nextScreen(MainDestinations.SELECT_LOCKER)
                                                //viewModel.onEvent(MainScreenEvent.OnChooseCityLockerClick)
                                            }
                                            .padding(horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextViewWithFont(
                                            text = lockerNameOrAddress.value,
                                            color = colorResource(R.color.colorBlackText),
                                            fontSize = ThmTitleTextSize,
                                            fontWeight = FontWeight.Normal,
                                            //maxLines = 1,
                                            modifier = Modifier.weight(0.9f)
                                        )

                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_chevron_right),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                    }

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_map),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .weight(0.11f)
                                            .clickable {
                                                //viewModel.onEvent(MainScreenEvent.OnGoogleMapsClick)
                                            }
                                    )
                                }

                                // Address and Username Section
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 18.dp, end = 18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(9f)
                                    ) {
                                        TextViewWithFont(
                                            text = "AWESOME 2", //state.uniqueUserNumber ?: "",
                                            color = ThmTitleTextColor,
                                            fontSize = ThmTitleTextSize,
                                            fontWeight = FontWeight.Normal,
                                            //maxLines = 2,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 5.dp)
                                        )

                                        TextViewWithFont(
                                            text = state.address ?: "",
                                            color = ThmTitleTextColor,
                                            fontSize = ThmTitleTextSize,
                                            fontWeight = FontWeight.Normal,
                                            //maxLines = 2,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 5.dp)
                                        )
                                    }

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_copy_address),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                //viewModel.onEvent(MainScreenEvent.OnCopyAddressClick)
                                            }
                                    )
                                }
                            }
                        }

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
                                        //viewModel.onEvent(MainScreenEvent.OnCollectParcelClick)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_collect_parcel),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.alpha(0.2f)
                                        )

                                        //if (state.deliveryKeysCount > 0) {
                                        if( true ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .offset(x = 105.dp)
                                                    .background(
                                                        color = ThmDescriptionTextColor, //ThmMainBadgeBackgroundColor,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "98", //state.deliveryKeysCount.toString(),
                                                    color = ThmTitleTextColor, //ThmMainBadgeTextColor,
                                                    fontSize = 25.sp
                                                )
                                            }
                                        }
                                    }

                                    TextViewWithFont(
                                        text = stringResource(id = R.string.app_generic_pickup_parcel),
                                        color = ThmDescriptionTextColor,
                                        fontSize = ThmTitleTextSize,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        //maxLines = 2
                                    )
                                }
                            }

                            // Send Parcel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        //viewModel.onEvent(MainScreenEvent.OnSendParcelClick)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_send_parcel),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.alpha(0.2f)
                                        )

                                        //if (state.showCancelPickAtHome && state.cancelPickAtHomeCount > 0) {
                                        if(true) {
                                            Row(
                                                modifier = Modifier
                                                    .offset(x = 105.dp)
                                                    .background(
                                                        color = colorResource(R.color.colorRedBadgeNumber),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 7.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_pick_at_home),
                                                    contentDescription = null,
                                                    tint = Color.Unspecified
                                                )

                                                Text(
                                                    text = "25", //state.cancelPickAtHomeCount.toString(),
                                                    color = ThmDescriptionTextColor, //ThmMainBadgeTextColor,
                                                    fontSize = 25.sp,
                                                    modifier = Modifier.padding(start = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    TextViewWithFont(
                                        text = stringResource(id = R.string.app_generic_send_parcel),
                                        color = ThmDescriptionTextColor,
                                        fontSize = ThmTitleTextSize,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        //maxLines = 2
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
                                        //viewModel.onEvent(MainScreenEvent.OnShareAccessClick)
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_key_sharing),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.alpha(0.2f)
                                )

                                TextViewWithFont(
                                    text = stringResource(id = R.string.app_generic_key_sharing),
                                    color = ThmDescriptionTextColor,
                                    fontSize = ThmTitleTextSize,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    //maxLines = 2
                                )
                            }

                            // Settings
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        nextScreen(MainDestinations.SETTINGS)
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_configure),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )

                                TextViewWithFont(
                                    text = stringResource(id = R.string.app_generic_my_configuration),
                                    color = ThmDescriptionTextColor,
                                    fontSize = ThmTitleTextSize,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    //maxLines = 2
                                )
                            }
                        }

                        // Telemetry Section
                        //if (state.showTelemetry) {
                        if(true) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, start = 40.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Humidity
                                Row(
                                    modifier = Modifier.weight(0.25f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_humidity),
                                        contentDescription = null,
                                        tint = Color.Unspecified
                                    )

                                    TextViewWithFont(
                                        text = "AWESOME 7", //state.humidity ?: "",
                                        color = ThmDescriptionTextColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        //maxLines = 1,
                                        modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
                                    )
                                }

                                // Temperature
                                Row(
                                    modifier = Modifier.weight(0.25f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_temperature),
                                        contentDescription = null,
                                        tint = Color.Unspecified
                                    )

                                    TextViewWithFont(
                                        text = "AWESOME TEMPERATURE", //state.temperature ?: "",
                                        color = ThmDescriptionTextColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        //maxLines = 1,
                                        modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
                                    )
                                }

                                // Air Pressure
                                Row(
                                    modifier = Modifier.weight(0.25f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_air_pressure),
                                        contentDescription = null,
                                        tint = Color.Unspecified
                                    )

                                    TextViewWithFont(
                                        text = "AWESOME PRESSURE", // state.airPressure ?: "",
                                        color = ThmDescriptionTextColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        //maxLines = 1,
                                        modifier = Modifier.padding(start = 5.dp, bottom = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            //}
       // }
    //}
}