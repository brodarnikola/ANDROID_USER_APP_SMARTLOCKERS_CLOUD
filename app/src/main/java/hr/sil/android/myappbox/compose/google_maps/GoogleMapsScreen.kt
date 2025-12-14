package hr.sil.android.myappbox.compose.google_maps

import android.content.*
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.MapView
import hr.sil.android.myappbox.compose.dialog.TextCopiedToClipboardDialog
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.forEach
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty

@Composable
fun GoogleMapsLockerLocationsScreen(
    onMarkerClick: (String) -> Unit = {},
    navigateUp: () -> Unit = {}
) {

    val displayCopiedToClipboardDialog = remember { mutableStateOf(false) }
    val selectedMacAddressDevice = rememberSaveable { mutableStateOf(SettingsHelper.userLastSelectedLocker) }

    val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
    val lockerName = selectedMasterDevice?.name ?: ""
    val uniqueUserNumber = if (UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L) {
        UserUtil.user?.uniqueId
    } else 0L

    val productName = selectedMasterDevice?.customerProductName?.takeIf { it.isNotEmpty() } ?: ""

    val finalProductName = when {
        productName.isNotEmpty() && uniqueUserNumber != 0L -> "$productName - $uniqueUserNumber"
        productName.isNotEmpty() && uniqueUserNumber == 0L -> productName
        productName.isEmpty() && uniqueUserNumber != 0L -> "$uniqueUserNumber"
        else -> ""
    }

    val addressLocker = selectedMasterDevice?.address ?: ""

    if( displayCopiedToClipboardDialog.value )
        TextCopiedToClipboardDialog(
            onDismiss = { displayCopiedToClipboardDialog.value = false },
            onConfirm = { displayCopiedToClipboardDialog.value = false }
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top Divider
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )

            // Header Section (19% weight)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.19f)
                    .padding(bottom = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Welcome Text
                Text(
                    text = stringResource(R.string.selected_city_parcel_locker),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Start,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
//                    style = MaterialTheme.typography.titleLarge.copy(
//                        textTransform = TextTransform.Uppercase
//                    )
                )

                // Chosen City Locker
                Text(
                    text = lockerName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Start,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Address and Username Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier.weight(0.9f)
                    ) {
                        if (finalProductName.isNotEmpty()) {
                            Text(
                                text = finalProductName,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (addressLocker.isNotEmpty()) {
                            Text(
                                text = addressLocker,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            displayCopiedToClipboardDialog.value = true
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .padding(bottom = 5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_copy_address),
                            contentDescription = "Copy to clipboard"
                        )
                    }
                }
            }

            // Google Map Section (67% weight)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.67f)
            ) {
                GoogleMapView(
                    onMarkerClick = onMarkerClick,
                    selectedMacAddressDevice = selectedMacAddressDevice
                )
            }

            // Confirm Button Section (14% weight)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.14f),
                contentAlignment = Alignment.Center
            ) {

                ButtonWithFont(
                    text = stringResource(id = R.string.app_generic_confirm).uppercase(),
                    onClick = {
                        SettingsHelper.userLastSelectedLocker = selectedMacAddressDevice.value
                        navigateUp()
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
            }
        }
    }
}

// Google Maps Composable Component
@Composable
fun GoogleMapView(
    onMarkerClick: (String) -> Unit,
    selectedMacAddressDevice: MutableState<String>
) {
    val lockersLocations = MPLDeviceStore.uniqueDevices.values.toList()


    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        mapView.onCreate(Bundle())
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = Modifier
    ) { map ->

        map.getMapAsync { googleMap ->
            googleMap.uiSettings.isMapToolbarEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true

            googleMap.uiSettings.isMyLocationButtonEnabled = true

            val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]

            if (SettingsHelper.userLastSelectedLocker != "") {
                val currentLatLng = LatLng(
                    selectedMasterDevice?.latitude ?: 0.0,
                    selectedMasterDevice?.longitude ?: 0.0
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 9.0f))

                // Add user location marker if available
//                currentLatLng.let { location ->
//                    googleMap.addMarker(
//                        MarkerOptions()
//                            .position(location)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))
//                    )
//                }
            }

            setupImageForUserLocation(context, googleMap)

            // Add markers for lockers
            lockersLocations.forEach { locker ->
                val position = LatLng(locker.latitude, locker.longitude)
                val markerColor = if (locker.macAddress == SettingsHelper.userLastSelectedLocker) {
                    BitmapDescriptorFactory.HUE_GREEN
                } else {
                    BitmapDescriptorFactory.HUE_RED
                }

                googleMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(locker.name)
                        .snippet(locker.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                )?.tag = locker.macAddress
            }

            // Set marker click listener
            googleMap.setOnMarkerClickListener { marker ->
                marker.tag?.let { tag ->
                    if (tag is String) {
                        onMarkerClick(tag)
                        selectedMacAddressDevice.value = marker.tag as String
                    }
                }
                false
            }

            // Move camera to selected marker
            lockersLocations.find { it.macAddress == SettingsHelper.userLastSelectedLocker }?.let { selected ->
                val cameraPosition = LatLng(selected.latitude, selected.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 9.0f))
            }
        }
    }
}

private fun setupImageForUserLocation(context: Context, googleMap: GoogleMap) {
    CoroutineScope(Dispatchers.IO).launch() {

        val locale = Locale(SettingsHelper.languageName)
        val gcd = Geocoder(context,locale)

        var addresses: MutableList<Address> = mutableListOf()
        try {
            addresses =
                gcd.getFromLocationName(UserUtil.user?.address ?: "", 1)?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            //log.info("Exception is: ${e}")
        }
        withContext(Dispatchers.Main) {
            if (addresses != null && addresses.size > 0 && addresses[0] != null) {
                val location = LatLng(addresses.get(0).latitude, addresses.get(0).longitude)
                val userLocationMarker = googleMap.addMarker(
                    MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))
                )
//                if (userLocationMarker != null) {
//                    userLocationMarker.tag = userLocationMarkerImage
//                    mapOfMarkers.put(userLocationMarkerImage, userLocationMarker)
//                }
            }
        }
    }
}

// Activity wrapper
//class GoogleMapsLockerLocationsActivity : ComponentActivity() {
//
//    private var lastSelectedMacAddress by mutableStateOf("")
//    private var selectedMasterDevice by mutableStateOf<MPLDevice?>(null)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
//        lastSelectedMacAddress = SettingsHelper.userLastSelectedLocker
//
//        setContent {
//            MaterialTheme {
//                GoogleMapsLockerLocationsScreen(
//                    selectedMasterDevice = selectedMasterDevice,
//                    lastSelectedMacAddress = lastSelectedMacAddress,
//                    onMarkerClick = { macAddress ->
//                        lastSelectedMacAddress = macAddress
//                        selectedMasterDevice = MPLDeviceStore.uniqueDevices[macAddress]
//                    },
//                    onCopyClick = {
//                        handleCopyClick()
//                    },
//                    onConfirmClick = {
//                        handleConfirmClick()
//                    }
//                )
//            }
//        }
//    }
//
//    private fun handleCopyClick() {
////        if (SettingsHelper.userLastSelectedLocker.isEmpty()) {
////            val dialog = NoMasterSelectedDialog(R.string.select_locker_to_copy_address)
////            dialog.show(supportFragmentManager, "")
////        } else {
////            val name = UserUtil.user?.name?.takeIf { it.isNotEmpty() }
////                ?: UserUtil.user?.group___name?.takeIf { it.isNotEmpty() }
////                ?: ""
////
////            val productName = selectedMasterDevice?.customerProductName?.takeIf { it.isNotEmpty() } ?: ""
////            val uniqueUserNumber = UserUtil.user?.uniqueId?.takeIf { it != 0L } ?: 0L
////
////            val finalProductName = when {
////                productName.isNotEmpty() && uniqueUserNumber != 0L -> "$productName - $uniqueUserNumber"
////                productName.isNotEmpty() && uniqueUserNumber == 0L -> productName
////                productName.isEmpty() && uniqueUserNumber != 0L -> "$uniqueUserNumber"
////                else -> ""
////            }
////
////            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
////            val clip = ClipData.newPlainText(
////                "Text copied",
////                "$name\n$finalProductName\n${selectedMasterDevice?.address}"
////            )
////            clipboard.setPrimaryClip(clip)
////
////            val dialog = TextCopiedToClipboardDialog()
////            dialog.show(supportFragmentManager, "")
////        }
//    }
//
//    private fun handleConfirmClick() {
//        SettingsHelper.userLastSelectedLocker = lastSelectedMacAddress
//
////        val intent = Intent().apply {
////            component = ComponentName(
////                packageName,
////                "$packageName.aliasMainActivity"
////            )
////        }
////        startActivity(intent)
////        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
////        finish()
//    }
//}