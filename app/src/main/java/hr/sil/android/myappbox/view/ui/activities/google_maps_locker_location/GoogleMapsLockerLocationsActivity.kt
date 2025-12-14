package hr.sil.android.myappbox.view.ui.activities.google_maps_locker_location

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import hr.sil.android.retailuser.gps.GpsUtils
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.NoMasterSelectedDialog
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import hr.sil.android.myappbox.view.ui.activities.dialogs.TextCopiedToClipboardDialog
import hr.sil.android.view_util.permission.DroidPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.iterator

class GoogleMapsLockerLocationsActivity :
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener
{


    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }
    //    val log = logger()
//    private lateinit var mMap: GoogleMap
//
//    var macAddress: String = ""
//
//    var selectedMasterDevice: MPLDevice? = null
//    val mapOfMarkers: HashMap<String, Marker> = hashMapOf()
//
//    private val userLocationMarkerImage = "DONT_COMPARE_USER_LOCATION_MARKER_IMAGE_CLICK"
//
//    var lastSelectedMacAddres: String = ""
//
//    private lateinit var binding: ActivityGoogleMapsLocationLockersBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityGoogleMapsLocationLockersBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        viewLoaded = true
//
//        val toolbar = binding.toolbar
//        if (toolbar != null) {
//            this.setSupportActionBar(toolbar)
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.setDisplayShowHomeEnabled(true)
//            supportActionBar?.setDisplayShowTitleEnabled(false)
//        }
//
//        selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
//        displayCPLNameAndUsername(selectedMasterDevice?.macAddress ?: "")
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        binding.ivCopyCliboard.setOnClickListener {
//            if (SettingsHelper.userLastSelectedLocker == "") {
//                val shareAppDialog = NoMasterSelectedDialog(
//                    R.string.select_locker_to_copy_address
//                )
//                shareAppDialog.show(
//                    this@GoogleMapsLockerLocationsActivity.supportFragmentManager,
//                    ""
//                )
//            } else {
//                val name =
//                    if (UserUtil.user?.name != null && UserUtil.user?.name != "") "" + UserUtil.user?.name
//                    else if (UserUtil.user?.group___name != null && UserUtil.user?.group___name != "") "" + UserUtil.user?.group___name else ""
//
//                val productName = when {
//                    selectedMasterDevice?.customerProductName != "" -> {
//                        selectedMasterDevice?.customerProductName
//                    }
//                    else -> ""
//                }
//
//                val uniqueUserNumber = when {
//                    UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L -> {
//                        UserUtil.user?.uniqueId
//                    }
//                    else -> 0
//                }
//
//                val finalProductName = if( productName != "" && uniqueUserNumber != 0L )
//                    productName + " - " + uniqueUserNumber
//                else if( productName != "" && uniqueUserNumber == 0L )
//                    productName
//                else if( productName == "" && uniqueUserNumber != 0L )
//                    uniqueUserNumber
//                else ""
//
//                val clipboard: ClipboardManager =
//                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//
//                val clip: ClipData = ClipData.newPlainText(
//                    "Text copied",
//                    name + "\n" + finalProductName + "\n" + selectedMasterDevice?.address
//                )
//                clipboard.setPrimaryClip(clip)
//                val textCopiedToClipboard = TextCopiedToClipboardDialog()
//                textCopiedToClipboard.show(
//                    this@GoogleMapsLockerLocationsActivity.supportFragmentManager,
//                    ""
//                )
//            }
//        }
//
//        binding.btnConfirmLatLong.setOnClickListener {
//            SettingsHelper.userLastSelectedLocker = lastSelectedMacAddres
//            removeGoogleMapsFragmentFromMemory()
//            val intent = Intent()
//            val packageName = this@GoogleMapsLockerLocationsActivity.packageName
//            val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//            intent.component = componentName
//
//            startActivity(intent)
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//            finish()
//        }
//
//        if (this != null)
//            GpsUtils(this).turnGPSOn()
//
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.g_map) as SupportMapFragment?
//        if (mapFragment != null)
//            mapFragment.getMapAsync(this)
//
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        if( resources.getBoolean(R.bool.has_mobile_and_email_support) ) {
//            binding.ivSupportImage.visibility = View.VISIBLE
//            binding.ivSupportImage.setOnClickListener {
//                val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//                supportEmailPhoneDialog.show(
//                    supportFragmentManager,
//                    ""
//                )
//            }
//        }
//        else
//            binding.ivSupportImage.visibility = View.GONE
//    }
//
//    private val droidPermission by lazy { DroidPermission.init(this) }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        droidPermission.link(requestCode, permissions, grantResults)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//
//        mMap = googleMap
//        mMap.uiSettings.isMapToolbarEnabled = false
//        mMap.uiSettings.isZoomControlsEnabled = true
//        mMap.setOnMarkerClickListener(this)
//        log.info("GOOGLE da li ce tu uci: OOOO")
//
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            droidPermission
//                .request(Manifest.permission.ACCESS_FINE_LOCATION)
//                .done { _, deniedPermissions ->
//                    if (deniedPermissions.isNotEmpty()) {
//                        log.info("Permissions were denied!")
//                        Toast.makeText(
//                            this.baseContext,
//                            "Permission are denied",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                    }
//                }
//                .execute()// 1
//        } else {
//
//            mMap.isMyLocationEnabled = true
//
//            setupImageForUserLocation()
//
//            val lockersLocations = MPLDeviceStore.uniqueDevices.values.toList()
//            for (lockerItem in lockersLocations) {
//                //if( lockerItem.macAddress == SettingsHelper.userLastSelectedLocker ) {
//                if (lockerItem.macAddress == lastSelectedMacAddres) {
//                    val cameraMovePosition = LatLng(lockerItem.latitude, lockerItem.longitude)
//                    val marker = mMap.addMarker(
//                        MarkerOptions().position(cameraMovePosition).title(lockerItem.name)
//                            .snippet(lockerItem.address).icon(
//                            getMarkerIcon(
//                                ContextCompat.getColor(
//                                    this,
//                                    R.color.colorSelectedLockerInGoogleMaps
//                                )
//                            )
//                        )
//                    )
//                    if (marker != null) {
//                        marker.tag = lockerItem.macAddress
//                        mapOfMarkers.put(lockerItem.macAddress, marker)
//                    }
//                    lastSelectedMacAddres = lockerItem.macAddress
//                } else {
//                    val cameraMovePosition = LatLng(lockerItem.latitude, lockerItem.longitude)
//                    val marker = mMap.addMarker(
//                        MarkerOptions().position(cameraMovePosition).title(lockerItem.name)
//                            .snippet(lockerItem.address).icon(
//                            getMarkerIcon(
//                                ContextCompat.getColor(
//                                    this,
//                                    R.color.colorPrimaryDark
//                                )
//                            )
//                        )
//                    )
//                    if (marker != null) {
//                        marker.tag = lockerItem.macAddress
//                        mapOfMarkers.put(lockerItem.macAddress, marker)
//                    }
//                }
//            }
//
//            if (SettingsHelper.userLastSelectedLocker != "") {
//                val currentLatLng = LatLng(
//                    selectedMasterDevice?.latitude ?: 0.0,
//                    selectedMasterDevice?.longitude ?: 0.0
//                )
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 9.0f))
//            }
//
//            mMap.setInfoWindowAdapter(object : InfoWindowAdapter {
//                override fun getInfoWindow(arg0: Marker): View? {
//                    return null
//                }
//
//                override fun getInfoContents(marker: Marker): View {
//                    val info = LinearLayout(this@GoogleMapsLockerLocationsActivity)
//                    info.setOrientation(LinearLayout.VERTICAL)
//                    val title = TextView(this@GoogleMapsLockerLocationsActivity)
//                    title.setTextColor(Color.BLUE)
//                    title.setGravity(Gravity.CENTER)
//                    title.setTypeface(null, Typeface.BOLD)
//                    title.setText(marker.title)
//                    val snippet = TextView(this@GoogleMapsLockerLocationsActivity)
//                    snippet.setTextColor(Color.BLACK)
//                    snippet.setText(marker.snippet)
//                    info.addView(title)
//                    info.addView(snippet)
//                    return info
//                }
//            })
//
//            /* pictureOverGoogleMap.setOnTouchListener { v, event ->
//
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        scroolView.requestDisallowInterceptTouchEvent(true)
//                        // Disable touch on transparent view
//                        //false
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        // Allow ScrollView to intercept touch events.
//                        scroolView.requestDisallowInterceptTouchEvent(false)
//                        //true
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        scroolView.requestDisallowInterceptTouchEvent(true)
//                        //false;
//                    }
//                    MotionEvent.ACTION_CANCEL -> {
//
//                        scroolView.requestDisallowInterceptTouchEvent(false)
//                        //false;
//                    }
//                    else -> true
//                }
//                pictureOverGoogleMap.onTouchEvent(event)
//            }*/
//        }
//    }
//
//    fun getMarkerIcon(color: Int): BitmapDescriptor? {
//        val hsv = FloatArray(3)
//        Color.colorToHSV(color, hsv)
//        return BitmapDescriptorFactory.defaultMarker(hsv[0])
//    }
//
//    override fun onMarkerClick(mMarker: Marker): Boolean {
//
//        if (userLocationMarkerImage != mMarker.tag) {
//            for (map in mapOfMarkers) {
//
//                if (map.key == mMarker.tag) {
//                    lastSelectedMacAddres = mMarker.tag.toString()
//                    map.value.setIcon(
//                        getMarkerIcon(
//                            ContextCompat.getColor(
//                                this,
//                                R.color.colorSelectedLockerInGoogleMaps
//                            )
//                        )
//                    )
//                    displayCPLNameAndUsername(mMarker.tag.toString())
//                } else {
//                    if (userLocationMarkerImage != map.key) {
//                        map.value.setIcon(
//                            getMarkerIcon(
//                                ContextCompat.getColor(
//                                    this,
//                                    R.color.colorPrimaryDark
//                                )
//                            )
//                        )
//                    }
//                }
//            }
//
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMarker.position, 9.0f))
//            mMarker.showInfoWindow()
//            mMarker.setIcon(
//                getMarkerIcon(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.colorSelectedLockerInGoogleMaps
//                    )
//                )
//            )
//
//            return true
//        } else {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMarker.position, 9.0f))
//            return true
//        }
//    }
//
//    private fun displayCPLNameAndUsername(selectedLockerMacAddress: String) {
//        if (selectedLockerMacAddress != "") {
//
//            lastSelectedMacAddres = selectedLockerMacAddress
//            selectedMasterDevice = MPLDeviceStore.uniqueDevices[selectedLockerMacAddress]
//            val lockerName: String = selectedMasterDevice?.name ?: ""
//            if (binding.tvChoosenCityLocker != null)
//                binding.tvChoosenCityLocker.setText(lockerName)
//
//            val productName = when {
//                selectedMasterDevice?.customerProductName != "" -> {
//                    selectedMasterDevice?.customerProductName
//                }
//                else -> ""
//            }
//
//            val uniqueUserNumber = when {
//                UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L -> {
//                    UserUtil.user?.uniqueId
//                }
//                else -> 0
//            }
//
//            val finalProductName = if( productName != "" && uniqueUserNumber != 0L )
//                productName + " - " + uniqueUserNumber
//            else if( productName != "" && uniqueUserNumber == 0L )
//                productName
//            else if( productName == "" && uniqueUserNumber != 0L )
//                "" + uniqueUserNumber
//            else ""
//
//            if (binding.tvUniqueUserNumber != null && finalProductName != "") {
//                binding.tvUniqueUserNumber.visibility = View.VISIBLE
//                binding.tvUniqueUserNumber.setText(finalProductName)
//            }
//            else {
//                if (binding.tvUniqueUserNumber != null )
//                    binding.tvUniqueUserNumber.visibility = View.GONE
//            }
//
//            val addressLocker: String = selectedMasterDevice?.address ?: ""
//            if (binding.tvAddress != null) {
//                binding.tvAddress.setText(addressLocker)
//                binding.tvAddress.visibility = View.VISIBLE
//            }
//        } else {
//            val uniqueUserNumber =
//                if (UserUtil.user?.uniqueId != null) UserUtil.user?.uniqueId else 0L
//
//            val productName = if (MPLDeviceStore.uniqueDevices != null && MPLDeviceStore.uniqueDevices.values.isNotEmpty()) MPLDeviceStore.uniqueDevices.values.first().customerProductName else ""
//
//            val finalProductName = if( productName != "" && uniqueUserNumber != 0L )
//                productName + " - " + uniqueUserNumber
//            else if( productName != "" && uniqueUserNumber == 0L )
//                productName
//            else if( productName == "" && uniqueUserNumber != 0L )
//                "" + uniqueUserNumber
//            else ""
//
//            if (binding.tvUniqueUserNumber != null && finalProductName != "") {
//                binding.tvUniqueUserNumber.visibility = View.VISIBLE
//                binding.tvUniqueUserNumber.setText(finalProductName)
//            }
//            else {
//                if (binding.tvUniqueUserNumber != null )
//                    binding.tvUniqueUserNumber.visibility = View.GONE
//            }
//
//            binding.tvAddress.visibility = View.GONE
//        }
//    }
//
//    private fun setupImageForUserLocation() {
//        lifecycleScope.launch() {
//
//            val locale = Locale(SettingsHelper.languageName)
//            val gcd = Geocoder(this@GoogleMapsLockerLocationsActivity, locale)
//
//            var addresses: MutableList<Address> = mutableListOf()
//            try {
//                addresses =
//                    gcd.getFromLocationName(UserUtil.user?.address ?: "", 1)?.toMutableList() ?: mutableListOf()
//            } catch (e: Exception) {
//                log.info("Exception is: ${e}")
//            }
//            withContext(Dispatchers.Main) {
//                if (addresses != null && addresses.size > 0 && addresses[0] != null) {
//                    val location = LatLng(addresses.get(0).latitude, addresses.get(0).longitude)
//                    val userLocationMarker = mMap.addMarker(
//                        MarkerOptions().position(location)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))
//                    )
//                    if (userLocationMarker != null) {
//                        userLocationMarker.tag = userLocationMarkerImage
//                        mapOfMarkers.put(userLocationMarkerImage, userLocationMarker)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun removeGoogleMapsFragmentFromMemory() {
//        val mapFragment =
//            supportFragmentManager.findFragmentById(R.id.g_map) as SupportMapFragment?
//        if (mapFragment != null) {
//            supportFragmentManager.beginTransaction().remove(mapFragment)
//                .commit()
//        }
//    }


}