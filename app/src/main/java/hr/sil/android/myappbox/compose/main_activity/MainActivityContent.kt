package hr.sil.android.myappbox.compose.main_activity


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.GradientBackground
import kotlinx.coroutines.launch
import kotlin.text.contains
import kotlin.text.uppercase


data class BottomNavigationBarItem(
    val route: String,
    val icon: Int,
    val badgeAmount: Int? = null
)


fun bottomNavigationItems(): List<BottomNavigationBarItem> {
    // setting up the individual tabs
    val homeTab = BottomNavigationBarItem(
        route = MainDestinations.HOME,
        icon = R.drawable.ic_help_access_sharing
    )
    val tcTab = BottomNavigationBarItem(
        route = MainDestinations.TERMS_AND_CONDITION_SCREEN,
        icon = R.drawable.ic_pick_at_home
    )
    val settingsTab = BottomNavigationBarItem(
        route = MainDestinations.SETTINGS,
        icon = R.drawable.ic_send_parcel
    )

    // creating a list of all the tabs
    val tabBarItems = listOf(homeTab, tcTab, settingsTab)
    return tabBarItems
}

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
        navBackStackEntry.value?.destination?.route?.contains(MainDestinations.HOME) == true -> true
        else -> false
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
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
                }

                Divider(color = colorResource(R.color.colorWhite).copy(alpha = 0.2f))

                // Menu Items
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.app_generic_pickup_parcel),
                            color = colorResource(R.color.colorWhite)
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        //onNavigateToPickupParcel()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.app_generic_send_parcel),
                            color = colorResource(R.color.colorWhite)
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        //onNavigateToSendParcel()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.list_of_deliveries_cpl),
                            color = colorResource(R.color.colorWhite)
                        )
                    },
                    badge = {
                        //if (deliveriesCount > 0) {
                            Badge(
                                containerColor = colorResource(R.color.colorRedBadgeNumber),
                                contentColor = colorResource(R.color.colorWhite)
                            ) {
                                //Text(text = deliveriesCount.toString())
                            }
                        //}
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        //onNavigateToListOfDeliveries()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.locker_pick_home_keys),
                            color = colorResource(R.color.colorWhite)
                        )
                    },
                    badge = {
                        //if (pahKeysCount > 0) {
                            Badge(
                                containerColor = colorResource(R.color.colorRedBadgeNumber),
                                contentColor = colorResource(R.color.colorWhite)
                            ) {
                                //Text(text = pahKeysCount.toString())
                            }
                        //}
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        //onNavigateToPahKeys()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = colorResource(R.color.colorPrimary).copy(alpha = 0.3f)
                    )
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = stringResource(R.string.app_generic_key_sharing),
                            color = colorResource(R.color.colorWhite)
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
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
                            modifier = Modifier.fillMaxWidth().padding(end = imageLogoPadding),
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
                modifier = Modifier.fillMaxSize()
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
