package hr.sil.android.myappbox.compose.main_activity


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.dialog.NoMasterSelectedDialog
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.launch
import kotlin.text.contains
import kotlin.text.uppercase


data class BottomNavigationBarItem(
    val route: String,
    val icon: Int,
    val badgeAmount: Int? = null
)


// Main Composable with Overlays
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(
    systemStateViewModel: SystemStateViewModel,
    onNavigateToLogin: () -> Unit
) {
    val systemState by systemStateViewModel.systemState.collectAsState()

    val appState = rememberMainAppState()

    //val bottomNavigationItems = bottomNavigationItems()

    val showBottomBar = rememberSaveable { mutableStateOf(true) }

    val navBackStackEntry =
        appState.navController.currentBackStackEntryAsState() // navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry.value?.destination?.route

    showBottomBar.value = when {
        currentRoute == null -> true
        navBackStackEntry.value?.destination?.route == MainDestinations.HOME -> true
        else -> false
    }

    val isHomeScreen = remember(currentRoute) {
        currentRoute == null || currentRoute == MainDestinations.HOME
    }

    val noAccessMessage = rememberSaveable { mutableStateOf("") }

    val noSelectedLocker = stringResource(R.string.no_selected_locker)
    val noDeliverisToLockerPossible = stringResource(R.string.no_deliveris_to_locker_possible)
    val appGenericRequestAccess = stringResource(R.string.app_generic_request_access)
    val adminAapproveRequestAccess = stringResource(R.string.admin_approve_request_access)
    val appGenericNoAccessForDevice = stringResource(R.string.app_generic_no_access_for_device)
    val noDeliveriesToPickup = stringResource(R.string.no_deliveries_to_pickup)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.70f

    val lastSelectedMasterDevice = rememberSaveable { mutableStateOf(SettingsHelper.userLastSelectedLocker) }
    val selectedMasterDevice = MPLDeviceStore.uniqueDevices[lastSelectedMasterDevice ?: ""]

    val displayNoLockerSelected = rememberSaveable { mutableStateOf(false) }
    if (displayNoLockerSelected.value) {
        NoMasterSelectedDialog(
            message = noAccessMessage.value,
            onConfirm = {
                displayNoLockerSelected.value = false
            },
            onDismiss = {
                displayNoLockerSelected.value = false
            }
        )
    }

    val devicesWithKeys =
        MPLDeviceStore.uniqueDevices.values.filter { it.activeKeys.isNotEmpty() }
    var counterPickupDeliveryKeys = 0
    for (item in devicesWithKeys) {
        if (item.activeKeys.any { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF })
            counterPickupDeliveryKeys += item.activeKeys.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }.size
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isHomeScreen,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(drawerWidth),
                drawerContainerColor = colorResource(R.color.colorPrimaryDark)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(colorResource(R.color.colorPrimaryDark))
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Add your header content here from nav_header_main
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_navigation_drawer),
                            contentDescription = null
                        )
                    }
                }

                Divider(color = colorResource(R.color.colorWhite).copy(alpha = 0.2f))

                // Menu Items
                NavigationDrawerItem(
                    label = {
                        val alphaValue = if(UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.activeKeys?.any { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF } == true) 1.0f else 0.5f
                        Text(
                            text = stringResource(R.string.app_generic_pickup_parcel),
                            color = colorResource(R.color.colorWhite).copy(alpha = alphaValue)
                        )
                    },
                    selected = false,
                    onClick = {
                        if (SettingsHelper.userLastSelectedLocker != "" && UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.activeKeys?.any { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF } == true)
                            scope.launch { drawerState.close() }
                        else {
                            noAccessMessage.value = noSelectedLocker
                            displayNoLockerSelected.value = true
                        }
                        //onNavigateToPickupParcel()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        val alphaValue = if(SettingsHelper.userLastSelectedLocker != "" && UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.hasUserRightsOnSendParcelLocker() ?: false
                            && selectedMasterDevice?.isUserAssigned == true) 1.0f else 0.5f
                        Text(
                            text = stringResource(R.string.app_generic_send_parcel),
                            color = colorResource(R.color.colorWhite).copy(alphaValue)
                        )
                    },
                    selected = false,
                    onClick = {
                        if (SettingsHelper.userLastSelectedLocker == "") {
                            noAccessMessage.value = noSelectedLocker
                            displayNoLockerSelected.value = true
                        }
                        else {
                            scope.launch { drawerState.close() }
                        }
                        //onNavigateToSendParcel()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        val alphaValue = if (UserUtil.user?.status == "ACTIVE" && counterPickupDeliveryKeys > 0) 1.0f else 0.5f
                        Text(
                            text = stringResource(R.string.list_of_deliveries_cpl),
                            color = colorResource(R.color.colorWhite).copy(alpha = alphaValue)
                        )
                    },
                    badge = {
                        if (counterPickupDeliveryKeys > 0) {
                            Badge(
                                modifier = Modifier.size(25.dp),
                                containerColor = colorResource(R.color.colorRedBadgeNumber),
                                contentColor = colorResource(R.color.colorWhite)
                            ) {
                                Text(text = counterPickupDeliveryKeys.toString(),
                                     //modifier = Modifier.size(20.dp),
                                     //textAlign = TextAlign.Center
                                )
                            }
                        }
                    },
                    selected = false,
                    onClick = {
                        if (UserUtil.user?.status == "ACTIVE" && counterPickupDeliveryKeys > 0
                        /*&& SettingsHelper.userLastSelectedLocker != "" && selectedMasterDevice?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
                            ?.isNotEmpty() == true */) {

                            if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                appState.navController.navigate(MainDestinations.LIST_OF_DELIVERIES) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            scope.launch { drawerState.close() }
                        }
                        else {
                            noAccessMessage.value = noSelectedLocker
                            displayNoLockerSelected.value = true
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        val alphaValue = if (UserUtil.pahKeys.isNotEmpty() && UserUtil.user?.status == "ACTIVE") 1.0f else 0.5f
                        Text(
                            text = stringResource(R.string.locker_pick_home_keys),
                            color = colorResource(R.color.colorWhite).copy(alpha = alphaValue)
                        )
                    },
                    badge = {
                        //if (pahKeysCount > 0) {
                        if (UserUtil.pahKeys.isNotEmpty() && UserUtil.user?.status == "ACTIVE") {
                            Badge(
                                modifier = Modifier.size(25.dp),
                                containerColor = colorResource(R.color.colorRedBadgeNumber),
                                contentColor = colorResource(R.color.colorWhite)
                            ) {
                                Text(text = UserUtil.pahKeys.size.toString() )
                                //Text(text = pahKeysCount.toString())
                            }
                        }
                    },
                    selected = false,
                    onClick = {
                        if (UserUtil.pahKeys.isNotEmpty() && UserUtil.user?.status == "ACTIVE") {
                            if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                appState.navController.navigate(MainDestinations.PICK_AT_HOME_KEYS) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            scope.launch { drawerState.close() }
                        }
                        else {
                            noAccessMessage.value = noSelectedLocker
                            displayNoLockerSelected.value = true
                        }
                        //onNavigateToPahKeys()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        val alphaValue = if (UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.hasRightsToShareAccess() ?: false && selectedMasterDevice?.installationType == InstalationType.DEVICE) 1.0f else 0.5f
                        Text(
                            text = stringResource(R.string.app_generic_key_sharing),
                            color = colorResource(R.color.colorWhite).copy(alpha = alphaValue)
                        )
                    },
                    selected = false,
                    onClick = {
                        if (SettingsHelper.userLastSelectedLocker != "" && UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.hasRightsToShareAccess() ?: false && selectedMasterDevice?.installationType == InstalationType.DEVICE)
                            scope.launch { drawerState.close() }
                        else {
                            noAccessMessage.value = noSelectedLocker
                            displayNoLockerSelected.value = true
                        }
                        //onNavigateToShareAccess()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.app_generic_my_configuration),
                            color = colorResource(R.color.colorWhite)
                        )
                    },
                    selected = false,
                    onClick = {
                        if (navBackStackEntry.value?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                            appState.navController.navigate(MainDestinations.SETTINGS) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        scope.launch { drawerState.close() }
                        //onNavigateToSettings()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val imageLogoPadding = if (!showBottomBar.value) {
                            50.dp
                        } else {
                            20.dp
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = imageLogoPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_header_zwick),
                                contentDescription = "Logo",
                                modifier = Modifier.height(40.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (!showBottomBar.value) {
                                appState.upPress()
                            } else {
                                scope.launch {
                                    if (drawerState.isOpen) drawerState.close()
                                    else drawerState.open()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (!showBottomBar.value)
                                    Icons.AutoMirrored.Default.ArrowBack
                                else
                                    Icons.Default.Menu,
                                contentDescription = if (!showBottomBar.value) "Back" else "Menu",
                                tint = colorResource(R.color.colorBlack)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            GradientBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                MainComposeApp(appState, navBackStackEntry)

                // Overlays - shown in priority order
                when {
                    !systemState.bluetoothAvailable -> {
                        SystemOverlay(
                            message = stringResource(R.string.app_generic_no_ble),
                            backgroundDrawable = R.drawable.bg_bluetooth
                        )
                    }

                    !systemState.networkAvailable -> {
                        SystemOverlay(
                            message = stringResource(R.string.app_generic_no_network),
                            backgroundDrawable = R.drawable.bg_wifi_internet
                        )
                    }

                    !systemState.locationGPSAvailable -> {
                        LocationGPSOverlay()
                    }
                }

            }
        }
    }
}

@Composable
fun SystemOverlay(
    message: String,
    backgroundDrawable: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundDrawable),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.Crop
        )

        Text(
            text = message.uppercase(),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LocationGPSOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.rectangle_transparent_dark),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_no_location_services),
                contentDescription = "No Location",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 20.dp)
            )

            Text(
                text = stringResource(R.string.no_location_services).uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
