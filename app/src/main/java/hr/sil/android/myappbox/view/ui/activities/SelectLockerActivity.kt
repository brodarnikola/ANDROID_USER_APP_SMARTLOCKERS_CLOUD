package hr.sil.android.myappbox.view.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import hr.sil.android.myappbox.view.ui.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class SelectLockerActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout)
{

//    private val log = logger()
//    var macAddress: String = ""
//
//    private var sendParcelsAdapter: SendParcelsAdapter =
//        SendParcelsAdapter(
//            listOf(),
//            this@SelectLockerActivity
//        )
//
//    var firstMiliSeconds: Long = 0
//    var finalMiliSeconds: Long = 0
//
//    //private val GET_NEW_GPS_USER_LOCATION = 10000L * 60L
//
//    companion object {
//        var devicesWithDetailsResponse = mutableListOf<RAccessDetaislResponse>()
//    }
//
//    private lateinit var binding: ActivitySelectLockerBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySelectLockerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        macAddress = intent.getStringExtra("rMacAddress") ?: ""
//        log.info("Mac address is ${macAddress}")
//        App.ref.selectedMasterMacAddress = SettingsHelper.userLastSelectedLocker
//    }
//
//    private fun renderDeviceItems() {
//
//        // with purpose here is this var, because of available lockers which don't refresh if here is val
//        val lockerList = MPLDeviceStore.uniqueDevices.values.filter {
//            // testUser and ProductionDevice flag
//            val isThisDeviceAvailable = when {
//                UserUtil.user?.testUser ?: false == true -> true
//                else -> {
//                    it.isProductionReady == true
//                }
//            }
//            isThisDeviceAvailable
//        }.toMutableList()
//
//        when {
//            lockerList.size > 0 -> {
//                binding.tvTitle.text = resources.getString(R.string.app_generic_select_locker)
//                binding.rvSelectLocker.visibility = View.VISIBLE
//                binding.llBottom.visibility = View.VISIBLE
//
//                log.info(lockerList.joinToString("-")
//                { "1111 MPL device status: " + it.mplMasterDeviceStatus + " is in BLE proximity: " + it.isInBleProximity + " has user rights on lockers: " + it.hasUserRightsOnLocker() + " masterUnitId is: " + { it.masterUnitId } })
//
//                when {
//                    binding.rvSelectLocker.adapter == null -> {
//
//                        val finalHomeScreenList = getItemsForRecyclerView(lockerList)
//
//                        binding.rvSelectLocker.layoutManager =
//                            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
//
//                        sendParcelsAdapter =
//                            SendParcelsAdapter(
//                                finalHomeScreenList,
//                                this@SelectLockerActivity
//                            )
//                        binding.rvSelectLocker.adapter = sendParcelsAdapter
//
//                        (binding.rvSelectLocker.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
//                            false
//                    }
//                    else -> {
//
//                        val finalHomeScreenList = getItemsForRecyclerView(lockerList)
//
//                        log.info(lockerList.joinToString("-")
//                        { "2222 MPL device status: " + it.mplMasterDeviceStatus + " is in BLE proximity: " + it.isInBleProximity + " has user rights on lockers: " + it.hasUserRightsOnLocker() + " masterUnitId is: " + { it.masterUnitId } })
//                        sendParcelsAdapter.updateDevices(finalHomeScreenList)
//                    }
//                }
//            }
//            else -> {
//                binding.tvTitle.text = resources.getString(R.string.locker_not_in_proximity_label)
//                binding.rvSelectLocker.visibility = View.INVISIBLE
//                binding.llBottom.visibility = View.INVISIBLE
//            }
//        }
//        finalMiliSeconds = System.currentTimeMillis() - firstMiliSeconds
//        log.info("time passed: " + finalMiliSeconds)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: MPLDevicesUpdatedEvent) {
//        renderDeviceItems()
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        lifecycleScope.launchWhenResumed {
//            displayAllDevicesInRecyclerView()
//        }
//    }
//
//    private suspend fun displayAllDevicesInRecyclerView() {
//        devicesWithDetailsResponse = WSUser.getAccessDetails()?.toMutableList() ?: mutableListOf()
//
//        log.info("backend response size is 777: ${devicesWithDetailsResponse.size}")
//        for (device in 0..devicesWithDetailsResponse.size - 1) {
//            log.info(
//                "backend response size is 777: " +
//                        "Index: ${device} data is: Mac: " + devicesWithDetailsResponse[device].mac + " name" + devicesWithDetailsResponse[device].name +
//                        " user assigned" + devicesWithDetailsResponse[device].userIsAssigned + " active request: " + devicesWithDetailsResponse[device].activeAccessRequest + "\n"
//            )
//        }
//
//        log.info("backend response size is 999: ${devicesWithDetailsResponse.filter { it.userIsAssigned }.size}" +
//                " data is: ${
//                    devicesWithDetailsResponse.filter { it.userIsAssigned }.joinToString {
//                        "Mac: " + it.mac + " name" + it.name + " user assigned" + it.userIsAssigned +
//                                " active request: " + it.activeAccessRequest + "\n"
//                    }
//                } ")
//
//        withContext(Dispatchers.Main) {
//
//            binding.progressBarLoadLockers.visibility = View.GONE
//            binding.tvTitle.visibility = View.VISIBLE
//            binding.rvSelectLocker.visibility = View.VISIBLE
//            binding.llBottom.visibility = View.VISIBLE
//
//            // with purpose here is this var, because of available lockers which don't refresh if here is val
//            val lockerList = MPLDeviceStore.uniqueDevices.values.filter {
//                // testUser and ProductionDevice flag
//                val isThisDeviceAvailable = when {
//                    UserUtil.user?.testUser ?: false == true -> true
//                    else -> {
//                        it.isProductionReady == true
//                    }
//                }
//                isThisDeviceAvailable
//            }.toMutableList()
//
//            if (lockerList.isNotEmpty()) {
//
//                val finalHomeScreenList = getItemsForRecyclerView(lockerList)
//
//                log.info(lockerList.joinToString("-")
//                { "3333 MPL device status: " + it.mplMasterDeviceStatus + " is in BLE proximity: " + it.isInBleProximity + " has user rights on lockers: " + it.hasUserRightsOnLocker() + " masterUnitId is: " + { it.masterUnitId.toString() } })
//
//                binding.tvTitle.text = resources.getString(R.string.app_generic_select_locker)
//                binding.rvSelectLocker.visibility = View.VISIBLE
//                binding.llBottom.visibility = View.VISIBLE
//
//                binding.btnConfirm.setOnClickListener {
//                    SettingsHelper.userLastSelectedLocker = App.ref.selectedMasterMacAddress
//                    val startIntent =
//                        Intent(this@SelectLockerActivity, MainActivity::class.java)
//                    startActivity(startIntent)
//                    finish()
//                }
//
//                binding.rvSelectLocker.layoutManager =
//                    LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
//
//                sendParcelsAdapter = SendParcelsAdapter(
//                    finalHomeScreenList,
//                    this@SelectLockerActivity
//                )
//                binding.rvSelectLocker.adapter = sendParcelsAdapter
//
//                (binding.rvSelectLocker.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//            } else {
//                binding.tvTitle.text = resources.getString(R.string.locker_not_in_proximity_label)
//                binding.rvSelectLocker.visibility = View.INVISIBLE
//                binding.llBottom.visibility = View.INVISIBLE
//            }
//            App.ref.eventBus.register(this@SelectLockerActivity)
//        }
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        binding.ivSupportImage.setOnClickListener {
//            val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//            supportEmailPhoneDialog.show(
//                supportFragmentManager,
//                ""
//            )
//        }
//    }
//
//    private fun getItemsForRecyclerView(lockerList: MutableList<MPLDevice>): MutableList<ItemHomeScreen> {
//
//        val finalHomeScreenList = mutableListOf<ItemHomeScreen>()
//
//        val unsortedLockerList = mutableListOf<ItemHomeScreen.Child>()
//
//        for (deviceWithDetails in lockerList) {
//
//            if (deviceWithDetails.isPublicDevice == false && deviceWithDetails.isUserAssigned == false) {
//                continue
//            } else {
//                if (deviceWithDetails.macAddress == "00E06727A724") {
//                    log.info(
//                        "backend response size is 88888: " +
//                                " Mac: " + deviceWithDetails.macAddress + " has user rights: " + deviceWithDetails.isUserAssigned +
//                                " instalation type: " + deviceWithDetails.installationType +
//                                "\n"
//                    )
//                }
//
//                val device = DeviceData()
//                device.macAddress = deviceWithDetails.macAddress
//                device.deviceName = deviceWithDetails.name
//                device.deviceAddress = deviceWithDetails.address
//                device.isInBleProximity = deviceWithDetails.isInBleProximity
//                device.installationType =
//                    deviceWithDetails.installationType ?: InstalationType.UNKNOWN
//                device.bleDeviceType = deviceWithDetails.type
//                device.backendDeviceType = deviceWithDetails.masterUnitType
//                device.isSplActivate = deviceWithDetails.isSplActivate
//
//                device.latitude = deviceWithDetails.latitude
//                device.longitude = deviceWithDetails.longitude
//                device.publicDevice = deviceWithDetails.isPublicDevice ?: false
//                device.isUserAssigned = deviceWithDetails.isUserAssigned ?: false
//                device.activeAccessRequest = deviceWithDetails.activeAccessRequest ?: false
//                device.requiredAccessRequestTypes = deviceWithDetails.requiredAccessRequestTypes
//
//                /*device.isLinuxDeviceInProximity = isLinuxDeviceInsideProximity(
//                    deviceWithDetails.name,
//                    deviceWithDetails.installationType ?: InstalationType.UNKNOWN,
//                    deviceWithDetails.latitude,
//                    deviceWithDetails.longitude
//                )*/
//
//                device.isSelected = App.ref.selectedMasterMacAddress == device.macAddress
//
//                val childHomeScreen = ItemHomeScreen.Child(device)
//                unsortedLockerList.add(childHomeScreen)
//            }
//        }
//
//        for (device in 0..unsortedLockerList.size - 1) {
//            log.info(
//                "backend response size is 222: " +
//                        "Index: ${device} data is: Mac: " + unsortedLockerList[device].mplOrSplDevice.macAddress + " name: " + unsortedLockerList[device].mplOrSplDevice.deviceName +
//                        "\n" + " active access request: ${unsortedLockerList[device].mplOrSplDevice.activeAccessRequest}" +
//                        " is public device: " + unsortedLockerList[device].mplOrSplDevice.publicDevice +
//                        " user assigned: " + unsortedLockerList[device].mplOrSplDevice.isUserAssigned +
//                        " installationType: " + unsortedLockerList[device].mplOrSplDevice.installationType
//                        + " bluebtooth proximity: " + unsortedLockerList[device].mplOrSplDevice.isInBleProximity
//                        + "\n"
//            )
//        }
//
//        log.info("Size of data is unsorted list: ${unsortedLockerList.size}")
//
//        val userHasRightsInProximityList = unsortedLockerList.filter {
//            it.mplOrSplDevice.isUserAssigned && ((it.mplOrSplDevice.installationType == InstalationType.LINUX /*&& it.mplOrSplDevice.isLinuxDeviceInProximity*/) || (it.mplOrSplDevice.isInBleProximity))
//        }
//        val userHasRightsNotInProximityList =
//            unsortedLockerList.filter {
//                (it.mplOrSplDevice.installationType != InstalationType.LINUX && !it.mplOrSplDevice.isInBleProximity) && it.mplOrSplDevice.isUserAssigned
//            }
//
//        for (device in 0..userHasRightsNotInProximityList.size - 1) {
//            log.info(
//                "backend response size is 5550000: " +
//                        "Index: ${device} data is: Mac: " + userHasRightsNotInProximityList[device].mplOrSplDevice.macAddress + " name: " + userHasRightsNotInProximityList[device].mplOrSplDevice.deviceName +
//                        "\n" + " active access request: ${userHasRightsNotInProximityList[device].mplOrSplDevice.activeAccessRequest}" +
//                        " is public device: " + userHasRightsNotInProximityList[device].mplOrSplDevice.publicDevice +
//                        " user assigned: " + userHasRightsNotInProximityList[device].mplOrSplDevice.isUserAssigned
//                        + " installationType: " + userHasRightsNotInProximityList[device].mplOrSplDevice.installationType
//                        + " bluebtooth proximity: " + userHasRightsNotInProximityList[device].mplOrSplDevice.isInBleProximity
//                        + "\n"
//            )
//        }
//
//        val userDisabledRightsInProximityList =
//            unsortedLockerList.filter {
//                val isCorrectDevice =
//                    if ((it.mplOrSplDevice.backendDeviceType == RMasterUnitType.SPL || it.mplOrSplDevice.backendDeviceType == RMasterUnitType.SPL_PLUS) && it.mplOrSplDevice.isSplActivate) {
//                        false
//                    } else true
//                !it.mplOrSplDevice.isUserAssigned && isCorrectDevice
//                // && (it.mplOrSplDevice.isInBleProximity || it.mplOrSplDevice.isLinuxDeviceInProximity)
//            }
//
//        if (userHasRightsInProximityList.isNotEmpty()) {
//            val headerHomeScreen = ItemHomeScreen.Header()
//            headerHomeScreen.headerTitle = getString(R.string.registered_lockers_in_proximity)
//            headerHomeScreen.numberOfItems = userHasRightsInProximityList.size
//            headerHomeScreen.indexOfHeader = 0
//            headerHomeScreen.positionToExpandCollapseInAdapterList = 1
//            finalHomeScreenList.add(headerHomeScreen)
//
//            userHasRightsInProximityList.map {
//                it.mplOrSplDevice.isExpanded = headerHomeScreen.isExpandedList
//                it.mplOrSplDevice.indexOfHeader = headerHomeScreen.indexOfHeader
//                it.mplOrSplDevice
//            }
//
//            finalHomeScreenList.addAll(userHasRightsInProximityList)
//        }
//
//        if (userHasRightsNotInProximityList.isNotEmpty()) {
//            val headerHomeScreen = ItemHomeScreen.Header()
//
//            headerHomeScreen.headerTitle = getString(R.string.registered_lockers_not_in_proximity)
//            headerHomeScreen.numberOfItems = userHasRightsNotInProximityList.size
//            if (finalHomeScreenList.isEmpty()) {
//                headerHomeScreen.positionToExpandCollapseInAdapterList = 1
//                headerHomeScreen.indexOfHeader = 0
//            } else {
//                headerHomeScreen.positionToExpandCollapseInAdapterList =
//                    finalHomeScreenList.size + 1
//                headerHomeScreen.indexOfHeader = 1
//            }
//            finalHomeScreenList.add(headerHomeScreen)
//
//            userHasRightsNotInProximityList.map {
//                it.mplOrSplDevice.isExpanded = headerHomeScreen.isExpandedList
//                it.mplOrSplDevice.indexOfHeader = headerHomeScreen.indexOfHeader
//                it.mplOrSplDevice
//            }
//
//            finalHomeScreenList.addAll(userHasRightsNotInProximityList)
//        }
//
//        if (userDisabledRightsInProximityList.isNotEmpty()) {
//            val headerHomeScreen = ItemHomeScreen.Header()
//            headerHomeScreen.headerTitle = getString(R.string.unregistered_lockers)
//            headerHomeScreen.numberOfItems = userDisabledRightsInProximityList.size
//            if (finalHomeScreenList.isEmpty()) {
//                headerHomeScreen.positionToExpandCollapseInAdapterList = 1
//            } else {
//                headerHomeScreen.positionToExpandCollapseInAdapterList =
//                    finalHomeScreenList.size + 1
//            }
//            val size = finalHomeScreenList.filter { it.getRecyclerviewItemType() == 0 }
//            if (size.size == 0) {
//                headerHomeScreen.indexOfHeader = 0
//            } else if (size.size == 1) {
//                headerHomeScreen.indexOfHeader = 1
//            } else {
//                headerHomeScreen.indexOfHeader = 2
//            }
//            finalHomeScreenList.add(headerHomeScreen)
//
//            userDisabledRightsInProximityList.map {
//                it.mplOrSplDevice.isExpanded = headerHomeScreen.isExpandedList
//                it.mplOrSplDevice.indexOfHeader = headerHomeScreen.indexOfHeader
//                it.mplOrSplDevice
//            }
//
//            finalHomeScreenList.addAll(userDisabledRightsInProximityList)
//        }
//
//        log.info("Size of data is: ${finalHomeScreenList.size}")
//        return finalHomeScreenList
//    }
//
//    // inside 50 meters
//    /*private fun isLinuxDeviceInsideProximity(
//        deviceName: String,
//        installationType: InstalationType,
//        latitude: Double,
//        longitude: Double
//    ): Boolean {
//        log.info("Device name is: ${deviceName}")
//        return if (installationType == InstalationType.LINUX) {
//            val deviceLocation = Location("GPS")
//            deviceLocation.latitude = latitude
//            deviceLocation.longitude = longitude
//            if (App.ref.userLastLocation.lastGoodLocation != null && App.ref.userLastLocation.lastGoodLocation.latitude != 0.0 &&
//                Date().time - App.ref.userLastLocation.lastFetchedGpsLocation.time < GET_NEW_GPS_USER_LOCATION
//            ) {
//                val calculatedDistance =
//                    deviceLocation.distanceTo(App.ref.userLastLocation.lastGoodLocation)
//                log.info("da li ce uci simm AAAAA: ${deviceName}, distance is: ${calculatedDistance}")
//                calculatedDistance < 50f
//            } else if (App.ref.userLastLocation.lastGoodLocation != null && App.ref.userLastLocation.lastGoodLocation?.latitude != 0.0) {
//
//                log.info("da li ce uci simm BBBBB: ${deviceName}")
//                val calculatedDistance = deviceLocation.distanceTo(App.ref.userLastLocation.lastGoodLocation)
//                calculatedDistance < 50f
//            } else false
//        } else
//            false
//    }*/
//
//    override fun onPause() {
//        super.onPause()
//        App.ref.eventBus.unregister(this)
//    }
//


}

