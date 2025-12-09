package hr.sil.android.myappbox.activities

// import kotlinx.android.synthetic.main.activity_google_maps_location_lockers.*
// import kotlinx.android.synthetic.main.activity_login.*
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import hr.sil.android.myappbox.activities.collect_parcel.ListOfDeliveriesActivity
import hr.sil.android.myappbox.activities.collect_parcel.PickupParcelActivity
import hr.sil.android.myappbox.activities.send_parcel.SelectParcelSizeActivity
import hr.sil.android.myappbox.customControl.DrawerBadge
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.InstalationType
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerKeyPurpose
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.core.util.macRealToClean
import hr.sil.android.smartlockers.enduser.databinding.ActivityMainBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivityShareAccessKeyBinding
import hr.sil.android.smartlockers.enduser.events.MPLDevicesUpdatedEvent
import hr.sil.android.smartlockers.enduser.events.UnauthorizedUserEvent
import hr.sil.android.smartlockers.enduser.store.MPLDeviceStore
import hr.sil.android.smartlockers.enduser.store.model.MPLDevice
import hr.sil.android.smartlockers.enduser.util.SettingsHelper
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil.logout
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.DisplayQrCodeActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.access_sharing.AccessSharingActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.NoMasterSelectedDialog
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.SupportEmailPhoneDialog
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.TextCopiedToClipboardDialog
import hr.sil.android.smartlockers.enduser.view.ui.activities.google_maps_locker_location.GoogleMapsLockerLocationsActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.sendparcel.SendParcelsOverviewActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.settings.SettingsActivity
import hr.sil.android.util.general.extensions.format
import hr.sil.android.view_util.permission.DroidPermission
import hr.sil.smartlockers.smartlockers.enduser.core.remote.model.RequiredAccessRequestTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout),
    NavigationView.OnNavigationItemSelectedListener {

    private val log = logger()

    private val droidPermission by lazy { DroidPermission.init(this) }

    private lateinit var pickupParcel: DrawerBadge
    private lateinit var sendParcel: DrawerBadge
    var selectedMasterDevice: MPLDevice? = null
    var counterPickupDeliveryKeys = 0

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    val GO_TO_PICKUP_PARCEL_SCREEN = 1
    val GO_TO_SELECT_LOCKER_SIZE_SCREEN = 2
    val GO_TO_CANCEL_PICK_AT_HOME_SCREEN = 3

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!UserUtil.isUserLoggedIn()) {
            lifecycleScope.launch(Dispatchers.Main) {
                if (UserUtil.login(SettingsHelper.usernameLogin)) {
                    continueOnCreate()
                } else {
                    logout()
                }
            }
        } else {
            continueOnCreate()
        }
    }

    private fun continueOnCreate() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewLoaded = true

        setupToolbarProperties()

        setNotification()

        selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
        val uniqueUserNumber =
            if (UserUtil.user?.uniqueId != null) "" + UserUtil.user?.uniqueId else ""
        val productNameUniqueUserNumber: String =
            selectedMasterDevice?.customerProductName + " - " + uniqueUserNumber

        log.info("Customer product name: ${selectedMasterDevice?.customerProductName} + unique user number: -->   ${uniqueUserNumber}")


        //if( UserUtil.user?.status == "PENDING_VERIFICATION" && UserUtil.twoFactoryAuth?.twoFactorAuth == "MAIL" )
        log.info("Address is confirmed: ${UserUtil.user?.addressConfirmed}")
        val deviceAddressConfirmed =
            selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                ?.firstOrNull()
        if (selectedMasterDevice == null)
            setupUIBasedOnPinStatus(pinEntered = true)
        else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name)
            setupUIBasedOnPinStatus(pinEntered = false)
        else
            setupUIBasedOnPinStatus(pinEntered = true)

        displayCPLNameAndUsername()
        displayTelemetryFromSelectedDevice()

        setupClickListeners()

        val permissions = mutableListOf<String>().apply {
            addAll(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                addAll(arrayOf(Manifest.permission.BLUETOOTH_SCAN,  Manifest.permission.BLUETOOTH_CONNECT))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (hr.sil.android.smartlockers.enduser.BuildConfig.DEBUG) {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        droidPermission
            .request(*permissions)
            .done { _, deniedPermissions ->
                if (deniedPermissions.isNotEmpty()) {
                    log.info("Some permissions were denied!")
                    App.ref.permissionCheckDone = true
                } else {
                    log.info("Enabling bluetooth...")
                    App.ref.permissionCheckDone = true
//                    App.ref.btMonitor.enable {
//                        log.info("Bluetooth enabled!")
//                        App.ref.permissionCheckDone = true
//                    }
                }
            }
            .execute()
    }

    private fun setupUIBasedOnPinStatus(pinEntered: Boolean) {
        if (pinEntered) {
            binding.mainLayout.llVerificationPinDisabled.visibility = View.GONE
            binding.mainLayout.llVerificationPinApproved.visibility = View.VISIBLE
            val params = binding.mainLayout.constraintLayoutFirst.layoutParams as ConstraintLayout.LayoutParams
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToBottom = binding.mainLayout.llVerificationPinApproved.id
            binding.mainLayout.constraintLayoutFirst.requestLayout()
        } else {
            binding.mainLayout.llVerificationPinDisabled.visibility = View.VISIBLE
            binding.mainLayout.llVerificationPinApproved.visibility = View.VISIBLE
            val params = binding.mainLayout.constraintLayoutFirst.layoutParams as ConstraintLayout.LayoutParams
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToBottom = binding.mainLayout.llVerificationPinApproved.id
            binding.mainLayout.constraintLayoutFirst.requestLayout()
            binding.mainLayout.btnEnterVerificationPin.setOnClickListener {
                val intent = Intent(this, EnterVerificationPinActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupClickListeners() {

        binding.mainLayout.ivCopyCliboard.setOnClickListener {
            if (SettingsHelper.userLastSelectedLocker == "") {
                val shareAppDialog = NoMasterSelectedDialog(
                    R.string.select_locker_to_copy_address
                )
                shareAppDialog.show(
                    this@MainActivity.supportFragmentManager,
                    ""
                )
            } else {
                val name =
                    if (UserUtil.user?.name != null && UserUtil.user?.name != "") "" + UserUtil.user?.name
                    else if (UserUtil.user?.group___name != null && UserUtil.user?.group___name != "") "" + UserUtil.user?.group___name else ""

                val productName = when {
                    selectedMasterDevice?.customerProductName != "" -> {
                        selectedMasterDevice?.customerProductName
                    }
                    else -> ""
                }

                val uniqueUserNumber = when {
                    UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L -> {
                        UserUtil.user?.uniqueId
                    }
                    else -> 0
                }

                val finalProductName = if (productName != "" && uniqueUserNumber != 0L)
                    productName + " - " + uniqueUserNumber
                else if (productName != "" && uniqueUserNumber == 0L)
                    productName
                else if (productName == "" && uniqueUserNumber != 0L)
                    uniqueUserNumber
                else ""

                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clip: ClipData = ClipData.newPlainText(
                    "Text copied",
                    name + "\n" + finalProductName + "\n" + selectedMasterDevice?.address
                )
                clipboard.setPrimaryClip(clip)
                val textCopiedToClipboard = TextCopiedToClipboardDialog()
                textCopiedToClipboard.show(
                    this@MainActivity.supportFragmentManager,
                    ""
                )
            }
        }

        binding.mainLayout.ivCollectParcel?.setOnClickListener {
            //val devicesWithKeys = MPLDeviceStore.uniqueDevices.values.filter { it.activeKeys.size > 0 && it.isInBleProximity && it.macAddress == selectedMasterDevice?.macAddress }
            setupPickupParcelClickListener()
        }

        binding.mainLayout.ivSendParcel?.setOnClickListener {
            setupSendParcelClickListener()
        }

        binding.mainLayout.ivShareAccess?.setOnClickListener {
            setupShareAccessClickListener()
        }

        binding.mainLayout.ivConfiguration?.setOnClickListener {
            setupSettingsClickListener()
        }

        binding.mainLayout.clCancelPickAtHome.setOnClickListener {
            setupCancelPickAtHomeClickListener(true)
        }

        binding.mainLayout.llChooseCityLocker.setOnClickListener {
            val startIntent = Intent(this@MainActivity, SelectLockerActivity::class.java)
            startActivity(startIntent)
            finish()
        }

        binding.mainLayout.ivGoogleMaps.setOnClickListener {
            val startIntent =
                Intent(this@MainActivity, GoogleMapsLockerLocationsActivity::class.java)
            startActivity(startIntent)
            finish()
        }
    }

    private fun setupToolbarProperties() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if ( binding.drawerLayout != null) {
            val drawerToggle = ActionBarDrawerToggle(
                this@MainActivity, binding.drawerLayout,
                binding.mainLayout.toolbar,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer
            )
            binding.drawerLayout.addDrawerListener(drawerToggle)
            drawerToggle.syncState()
        }

        if (binding.navView != null ) {
            binding.navView!!.setNavigationItemSelectedListener(this)

            val search: MenuItem = binding.navView.menu.findItem(R.id.pahKeysActivity)
            sendParcel =  search.actionView!!.findViewById(R.id.pahKeysActivity)

            val search1: MenuItem = binding.navView.menu.findItem(R.id.listOfDeliveriesActivity)
            pickupParcel =  search1.actionView!!.findViewById(R.id.listOfDeliveriesActivity)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.pickupParcelActivity) {
            setupPickupParcelClickListener()
        } else if (id == R.id.sendParcelActivity) {
            setupSendParcelClickListener()
        } else if (id == R.id.listOfDeliveriesActivity) {
            setupListOfDeliveriesClickListener(false)
        } else if (id == R.id.pahKeysActivity) {
            setupCancelPickAtHomeClickListener(false)
        } else if (id == R.id.shareAccessActivity) {
            setupShareAccessClickListener()
        } else if (id == R.id.settingsActivity) {
            setupSettingsClickListener()
        }
        binding.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupPickupParcelClickListener() {

        val deviceAddressConfirmed =
            selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                ?.firstOrNull()
        if (SettingsHelper.userLastSelectedLocker == "") {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_selected_locker
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        } else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_deliveris_to_locker_possible
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.isUserAssigned == false && selectedMasterDevice?.activeAccessRequest == false) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.app_generic_request_access
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.isUserAssigned == false && selectedMasterDevice?.activeAccessRequest == true) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.admin_approve_request_access
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.hasUserRightsOnSendParcelLocker() == false) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.app_generic_no_access_for_device
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        } else if (selectedMasterDevice?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
                ?.isNotEmpty() ?: false) {
            val startIntent = Intent(this@MainActivity, PickupParcelActivity::class.java)
            startIntent.putExtra("rMacAddress", SettingsHelper.userLastSelectedLocker)
            startActivity(startIntent)
            finish()
        } else {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_deliveries_to_pickup
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
    }

    private fun setupSendParcelClickListener() {

        val deviceAddressConfirmed =
            selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                ?.firstOrNull()
        if (SettingsHelper.userLastSelectedLocker == "") {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_selected_locker
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        } else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_deliveris_to_locker_possible
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.isUserAssigned == false && selectedMasterDevice?.activeAccessRequest == false) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.app_generic_request_access
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.isUserAssigned == false && selectedMasterDevice?.activeAccessRequest == true) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.admin_approve_request_access
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.hasUserRightsOnSendParcelLocker() == false) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.app_generic_no_access_for_device
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if ( selectedMasterDevice?.installationType == InstalationType.LINUX) {
            val startIntent = Intent(this@MainActivity, DisplayQrCodeActivity::class.java)

            startIntent.putExtra("returnToCorrectScreen", 3)
            startIntent.putExtra("rMacAddress", SettingsHelper.userLastSelectedLocker)
            startActivity(startIntent)
            finish()
        }
        else if (SettingsHelper.userLastSelectedLocker != "" && selectedMasterDevice?.hasUserRightsOnSendParcelLocker() ?: false) {
            val startIntent = Intent(this@MainActivity, SelectParcelSizeActivity::class.java)
            startIntent.putExtra("rMacAddress", SettingsHelper.userLastSelectedLocker)
            startActivity(startIntent)
            finish()
        }

    }

    private fun setupShareAccessClickListener() {

        val deviceAddressConfirmed =
            selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                ?.firstOrNull()
        if (SettingsHelper.userLastSelectedLocker == "") {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_selected_locker
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.no_deliveris_to_locker_possible
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.isUserAssigned == false && selectedMasterDevice?.activeAccessRequest == false) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.app_generic_request_access
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.isUserAssigned == false && selectedMasterDevice?.activeAccessRequest == true) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.admin_approve_request_access
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        }
        else if (selectedMasterDevice?.installationType != InstalationType.DEVICE)
        {
        }
        else if (selectedMasterDevice?.hasUserRightsOnSendParcelLocker() == false) {
            val shareAppDialog = NoMasterSelectedDialog(
                R.string.app_generic_no_access_for_device
            )
            shareAppDialog.show(
                this@MainActivity.supportFragmentManager,
                ""
            )
        } else if (SettingsHelper.userLastSelectedLocker != "" && selectedMasterDevice?.hasRightsToShareAccess() ?: false) {
            val startIntent = Intent(this@MainActivity, AccessSharingActivity::class.java)
            startIntent.putExtra("rMacAddress", SettingsHelper.userLastSelectedLocker)
            startIntent.putExtra("nameOfDevice", selectedMasterDevice?.name)
            startActivity(startIntent)
            finish()
        }
    }

    private fun setupSettingsClickListener() {
        val startIntent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(startIntent)
        finish()
    }

    private fun setupCancelPickAtHomeClickListener(filterByMasterMac: Boolean) {
        if (UserUtil.pahKeys.isNotEmpty() && UserUtil.user?.status == "ACTIVE") {
            val startIntent = Intent(this@MainActivity, SendParcelsOverviewActivity::class.java)
            when {
                filterByMasterMac -> startIntent.putExtra("macAddress", selectedMasterDevice?.macAddress)
                else -> startIntent.putExtra("macAddress", "")
            }
            startActivity(startIntent)
            finish()
        }
    }

    private fun setupListOfDeliveriesClickListener(filterByMasterMac: Boolean) {
        if (UserUtil.user?.status == "ACTIVE" && counterPickupDeliveryKeys > 0
        /*&& SettingsHelper.userLastSelectedLocker != "" && selectedMasterDevice?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
            ?.isNotEmpty() == true */) {
            val startIntent =
                Intent(this@MainActivity.applicationContext, ListOfDeliveriesActivity::class.java)
            when {
                filterByMasterMac -> startIntent.putExtra("macAddress", selectedMasterDevice?.macAddress)
                else -> startIntent.putExtra("macAddress", "")
            }
            startActivity(startIntent)
            finish()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finishAndRemoveTask()
        }
    }

    private fun setNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!!.get(key)
                log.info("Key: $key Value: $value")
            }
        }
    }

    override fun onBluetoothStateUpdated(available: Boolean) {
        super.onBluetoothStateUpdated(available)
        bluetoothAvalilable = available
        if (viewLoaded) {
            updateUI()
        }
    }

    override fun onNetworkStateUpdated(available: Boolean) {
        super.onNetworkStateUpdated(available)
        networkAvailable = available
        if (viewLoaded) {
            updateUI()
        }
    }

    override fun onLocationGPSStateUpdated(available: Boolean) {
        super.onLocationGPSStateUpdated(available)
        locationGPSAvalilable = available
        if (viewLoaded) {
            updateUI()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: MPLDevicesUpdatedEvent) {
        refreshBadges()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
        log.info("Received unauthorized event, user will now be log outed")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }

    private fun displayCPLNameAndUsername() {
        if (SettingsHelper.userLastSelectedLocker != "") {

//            val uniqueUserNumber =
//                if (UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L ) "" + UserUtil.user?.uniqueId else ""
//            val productNameUniqueUserNumber: String =
//                selectedMasterDevice?.customerProductName + " - " + uniqueUserNumber

            val productName = when {
                selectedMasterDevice?.customerProductName != "" -> {
                    selectedMasterDevice?.customerProductName
                }
                else -> ""
            }

            val uniqueUserNumber = when {
                UserUtil.user?.uniqueId != null && UserUtil.user?.uniqueId != 0L -> {
                    UserUtil.user?.uniqueId
                }
                else -> 0
            }

            val finalProductName = if (productName != "" && uniqueUserNumber != 0L)
                productName + " - " + uniqueUserNumber
            else if (productName != "" && uniqueUserNumber == 0L)
                productName
            else if (productName == "" && uniqueUserNumber != 0L)
                "" + uniqueUserNumber
            else ""

            if ( binding.mainLayout.tvUniqueUserNumber != null && finalProductName != "") {
                binding.mainLayout.tvUniqueUserNumber.visibility = View.VISIBLE
                binding.mainLayout.tvUniqueUserNumber.setText(finalProductName)
            } else {
                if (binding.mainLayout.tvUniqueUserNumber != null)
                    binding.mainLayout.tvUniqueUserNumber.visibility = View.GONE
            }

            val addressLocker: String = selectedMasterDevice?.address ?: ""
            if (binding.mainLayout.tvAddress != null)
                binding.mainLayout.tvAddress.setText(addressLocker)
        } else {
            val uniqueUserNumber =
                if (UserUtil.user?.uniqueId != null) UserUtil.user?.uniqueId else 0L

            val productName =
                if (MPLDeviceStore.uniqueDevices != null && MPLDeviceStore.uniqueDevices.values.isNotEmpty()) MPLDeviceStore.uniqueDevices.values.first().customerProductName else ""

            val finalProductName = if (productName != "" && uniqueUserNumber != 0L)
                productName + " - " + uniqueUserNumber
            else if (productName != "" && uniqueUserNumber == 0L)
                productName
            else if (productName == "" && uniqueUserNumber != 0L)
                "" + uniqueUserNumber
            else ""

            if (binding.mainLayout.tvUniqueUserNumber != null && finalProductName != "") {
                binding.mainLayout.tvUniqueUserNumber.visibility = View.VISIBLE
                binding.mainLayout.tvUniqueUserNumber.setText(finalProductName)
            } else {
                if (binding.mainLayout.tvUniqueUserNumber != null)
                    binding.mainLayout.tvUniqueUserNumber.visibility = View.GONE
            }
            if (binding.mainLayout.tvAddress != null)
                binding.mainLayout.tvAddress.visibility = View.GONE
        }
    }

    private fun initializeCountDrawer(
        badgeNumberTextView: DrawerBadge,
        number: Int,
        menuItem: MenuItem
    ) {
        if (number > 0) {
            badgeNumberTextView.visibility = View.VISIBLE
            badgeNumberTextView.setGravity(Gravity.CENTER)
            badgeNumberTextView.setTypeface(null, Typeface.BOLD)
            badgeNumberTextView.textSize = 15f
            badgeNumberTextView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            badgeNumberTextView.setText("" + number)
            val spanableString = SpannableString(menuItem.title.toString())
            spanableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorWhite)), 0,
                spanableString.length, 0
            )
            menuItem.setTitle(spanableString)
        } else {
            badgeNumberTextView.visibility = View.GONE
            val spanableString = SpannableString(menuItem.title.toString())
            spanableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryWhite35Percent)), 0,
                spanableString.length, 0
            )
            menuItem.setTitle(spanableString)
        }
    }

    private fun refreshBadges() {

        selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
        if (binding.mainLayout.tvChoosenCityLocker != null)
            binding.mainLayout.tvChoosenCityLocker.text =
                if (SettingsHelper.userLastSelectedLocker != "") selectedMasterDevice?.name else "-"

        displayCPLNameAndUsername()
        displayTelemetryFromSelectedDevice()

        // when we will receive push notification from backend for refreshing "cancel pick at home keys", then I need to use this
        // val pahKeyy = DataCache.getPickAtHomeKeys().toMutableList(), instead of "UserUtil.pahKeys.isNotEmpty()"
        if (UserUtil.pahKeys.isNotEmpty() && UserUtil.pahKeys.filter { it.lockerMasterMac == selectedMasterDevice?.macAddress?.macRealToClean() }.isNotEmpty()
            && UserUtil.user?.status == "ACTIVE" ) {
            if (binding.mainLayout.clCancelPickAtHome != null && binding.mainLayout.tvCancelPHomeKeysNumber != null) {
                binding.mainLayout.clCancelPickAtHome.visibility = View.VISIBLE
                binding.mainLayout.tvCancelPHomeKeysNumber.text = UserUtil.pahKeys.size.toString()

            }
        } else {
            if (binding.mainLayout.clCancelPickAtHome != null)
                binding.mainLayout.clCancelPickAtHome.visibility = View.GONE
        }

        if (UserUtil.pahKeys.isNotEmpty() && UserUtil.user?.status == "ACTIVE") {
            if (binding.navView != null) {
                val pahKeysTitle = binding.navView.menu.findItem(R.id.pahKeysActivity)
                initializeCountDrawer(sendParcel, UserUtil.pahKeys.size, pahKeysTitle)
            }
        }
        else {
            if (binding.navView != null) {
                val pahKeysTitle = binding.navView.menu.findItem(R.id.pahKeysActivity)
                initializeCountDrawer(sendParcel, 0, pahKeysTitle)
            }
        }

        if (binding.mainLayout.ivSendParcel != null && binding.navView != null) {
            val deviceAddressConfirmed =
                selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                    ?.firstOrNull()
            val isPublicLocker =
                if (selectedMasterDevice == null)
                    false
                else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name)
                    false
                else
                    true
            when {
                isPublicLocker && SettingsHelper.userLastSelectedLocker != "" && UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.hasUserRightsOnSendParcelLocker() ?: false
                        && selectedMasterDevice?.isUserAssigned == true -> {
                    binding.mainLayout.ivSendParcel.alpha = 1.0f
                    val sendParcelTitle = binding.navView.menu.findItem(R.id.sendParcelActivity)
                    val spanableString = SpannableString(sendParcelTitle.title.toString())
                    spanableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorWhite)), 0,
                        spanableString.length, 0
                    )
                    sendParcelTitle.setTitle(spanableString)
                }
                else -> {
                    binding.mainLayout.ivSendParcel.alpha = 0.2f
                    val sendParcelTitle = binding.navView.menu.findItem(R.id.sendParcelActivity)
                    val spanableString = SpannableString(sendParcelTitle.title.toString())
                    spanableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryWhite35Percent
                            )
                        ), 0,
                        spanableString.length, 0
                    )
                    sendParcelTitle.setTitle(spanableString)
                }
            }
        }

        if (binding.mainLayout.ivShareAccess != null && binding.navView != null) {
            val deviceAddressConfirmed =
                selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                    ?.firstOrNull()
            val isPublicLocker =
                if (selectedMasterDevice == null)
                    false
                else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name)
                    false
                else
                    true
            when {
                isPublicLocker && UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.hasRightsToShareAccess() ?: false && selectedMasterDevice?.installationType == InstalationType.DEVICE -> {
                    binding.mainLayout.ivShareAccess.alpha = 1.0f
                    val shareAccesslTitle = binding.navView.menu.findItem(R.id.shareAccessActivity)
                    val spanableString = SpannableString(shareAccesslTitle.title.toString())
                    spanableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorWhite)), 0,
                        spanableString.length, 0
                    )
                    shareAccesslTitle.setTitle(spanableString)
                }
                else -> {
                    binding.mainLayout.ivShareAccess.alpha = 0.2f
                    val shareAccesslTitle = binding.navView.menu.findItem(R.id.shareAccessActivity)
                    val spanableString = SpannableString(shareAccesslTitle.title.toString())
                    spanableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryWhite35Percent
                            )
                        ), 0,
                        spanableString.length, 0
                    )
                    shareAccesslTitle.setTitle(spanableString)
                }
            }
        }

        if (binding.mainLayout.ivCollectParcel != null && binding.navView != null) {
            val deviceAddressConfirmed =
                selectedMasterDevice?.requiredAccessRequestTypes?.filter { it.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name }
                    ?.firstOrNull()
            val isPublicLocker =
                if (selectedMasterDevice == null)
                    false
                else if (UserUtil.user?.addressConfirmed == false && deviceAddressConfirmed != null && deviceAddressConfirmed.name == RequiredAccessRequestTypes.ADDRESS_CONFIRMATION.name)
                    false
                else
                    true
            when {
                isPublicLocker && UserUtil.user?.status == "ACTIVE" && selectedMasterDevice?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
                    ?.isNotEmpty() == true -> {
                    binding.mainLayout.ivCollectParcel.alpha = 1.0f
                    val pickupParcelTitle = binding.navView.menu.findItem(R.id.pickupParcelActivity)
                    val spanableString = SpannableString(pickupParcelTitle.title.toString())
                    spanableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorWhite)), 0,
                        spanableString.length, 0
                    )
                    pickupParcelTitle.setTitle(spanableString)
                }
                else -> {
                    binding.mainLayout.ivCollectParcel.alpha = 0.2f
                    val pickupParcelTitle = binding.navView.menu.findItem(R.id.pickupParcelActivity)
                    val spanableString = SpannableString(pickupParcelTitle.title.toString())
                    spanableString.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this,
                                R.color.colorPrimaryWhite35Percent
                            )
                        ), 0,
                        spanableString.length, 0
                    )
                    pickupParcelTitle.setTitle(spanableString)
                }
            }
        }

        counterPickupDeliveryKeys = numberOfDeliveryOrPAFKeys()

        if( UserUtil.user?.status == "ACTIVE" && counterPickupDeliveryKeys > 0 ) {
            if (binding.navView != null) {
                val deliveryKeysTitle = binding.navView.menu.findItem(R.id.listOfDeliveriesActivity)
                initializeCountDrawer(
                    pickupParcel,
                    counterPickupDeliveryKeys,
                    deliveryKeysTitle
                )
            }
        }
        else {
            if (binding.navView != null) {
                        val deliveryKeysTitle =
                            binding.navView.menu.findItem(R.id.listOfDeliveriesActivity)
                        initializeCountDrawer(
                            pickupParcel,
                            0,
                            deliveryKeysTitle
                        )
                    }
        }

        when {
            UserUtil.user?.status == "ACTIVE" && counterPickupDeliveryKeys > 0 && SettingsHelper.userLastSelectedLocker != ""
                    && selectedMasterDevice?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
            ?.isNotEmpty() == true -> {
                if (binding.mainLayout.tvDeliveryKeys != null && binding.navView != null) {
                    binding.mainLayout.tvDeliveryKeys.visibility = View.VISIBLE
                    binding.mainLayout.tvDeliveryKeys.text = selectedMasterDevice?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }!!.size.toString()
                    binding.mainLayout.tvDeliveryKeys.setOnClickListener {
                        setupListOfDeliveriesClickListener(true)
                    }
                }
            }
            else -> {
                if (binding.mainLayout.tvDeliveryKeys != null)
                    binding.mainLayout.tvDeliveryKeys.visibility = View.GONE
            }
        }
    }

    private fun displayTelemetryFromSelectedDevice() {
        if (selectedMasterDevice?.isInBleProximity ?: false) {
            if (binding.mainLayout.clLockerTelemetry != null)
                binding.mainLayout.clLockerTelemetry.visibility = View.VISIBLE
            val temperatureS = selectedMasterDevice?.temperature?.format(2) ?: "-"
            val pressureS = selectedMasterDevice?.pressure?.format(2) ?: "-"
            val humidityS = selectedMasterDevice?.humidity?.format(2) ?: "-"
            if (binding.mainLayout.tvHumidity != null && binding.mainLayout.tvTemperature != null && binding.mainLayout.tvAirPressure != null) {
                binding.mainLayout.tvHumidity.text = "$humidityS  %"
                binding.mainLayout.tvTemperature.text = "$temperatureS C"
                binding.mainLayout.tvAirPressure.text = "$pressureS hPa"
            }
        } else {
            if (binding.mainLayout.clLockerTelemetry != null)
                binding.mainLayout.clLockerTelemetry.visibility = View.GONE
        }
    }

    private fun numberOfDeliveryOrPAFKeys(): Int {

        val devicesWithKeys = MPLDeviceStore.uniqueDevices.values.filter { it.activeKeys.size > 0 }
        var counter = 0
        for (item in devicesWithKeys) {

            if (item.activeKeys.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
                    .isNotEmpty())
                counter += item.activeKeys.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }.size
        }
        return counter
    }

    override fun onResume() {
        super.onResume()

        checkIfHasEmailAndMobilePhoneSupport()

        lifecycleScope.launch {

            log.info("Selected master mac address is: ${SettingsHelper.userLastSelectedLocker}")
            // when we will receive push notification from backend for refreshing "cancel pick at home keys", then I need to use this
            // val pahKeyy = DataCache.getPickAtHomeKeys().toMutableList(), instead of "WSUser.getActivePaHCreatedKeys() ?: mutableListOf() "
            val pahKeyy = WSUser.getActivePaHCreatedKeys() ?: mutableListOf()

            UserUtil.pahKeys = pahKeyy

            withContext(Dispatchers.Main) {
                if (pahKeyy.size > 0 && binding.mainLayout.clCancelPickAtHome != null && binding.mainLayout.tvCancelPHomeKeysNumber != null) {
                    binding.mainLayout.clCancelPickAtHome.visibility = View.VISIBLE
                    binding.mainLayout.tvCancelPHomeKeysNumber.text = pahKeyy.size.toString()
                } else if (binding.mainLayout.clCancelPickAtHome != null) {
                    binding.mainLayout.clCancelPickAtHome.visibility = View.GONE
                }
                if (!App.ref.eventBus.isRegistered(this@MainActivity))
                    App.ref.eventBus.register(this@MainActivity)
                refreshBadges()
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.activeNotifications.size > 0)
            notificationManager.cancelAll()
    }

    private fun checkIfHasEmailAndMobilePhoneSupport() {
        if( binding.mainLayout.ivSupportImage != null ) {
            binding.mainLayout.ivSupportImage.setOnClickListener {
                val supportEmailPhoneDialog = SupportEmailPhoneDialog()
                supportEmailPhoneDialog.show(
                    supportFragmentManager,
                    ""
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        App.ref.eventBus.unregister(this)
    }

}
