/* SWISS INNOVATION LAB CONFIDENTIAL
*
* www.swissinnolab.com
* __________________________________________________________________________
*
* [2016] - [2018] Swiss Innovation Lab AG
* All Rights Reserved.
*
* @author mfatiga
*
* NOTICE:  All information contained herein is, and remains
* the property of Swiss Innovation Lab AG and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Swiss Innovation Lab AG
* and its suppliers and may be covered by E.U. and Foreign Patents,
* patents in process, and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Swiss Innovation Lab AG.
*/

package hr.sil.android.myappbox

import android.app.Application
import android.content.Context
import android.content.res.Configuration
//import com.esotericsoftware.minlog.Log
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.messaging.FirebaseMessaging
import hr.sil.android.ble.scanner.BLEDeviceScanner
import hr.sil.android.ble.scanner.exception.BLEScanException
import hr.sil.android.ble.scanner.scan_multi.BLEGenericDeviceDataFactory
import hr.sil.android.ble.scanner.scan_multi.model.BLEDeviceType
import hr.sil.android.myappbox.cache.status.ActionStatusHandler
import hr.sil.android.myappbox.data.UserLastLocationGps
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.RLanguage
import hr.sil.android.myappbox.core.remote.model.RPinManagement
import hr.sil.android.myappbox.core.util.BLEScannerStateHolder
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.fcm.MPLFireBaseMessagingService
import hr.sil.android.myappbox.remote.WSConfig
import hr.sil.android.myappbox.store.DeviceStoreRemoteUpdater
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.awaitForResult
import hr.sil.android.myappbox.util.backend.UserUtil
//import hr.sil.android.util.bluetooth.BluetoothAdapterMonitor
//import hr.sil.android.util.general.delegates.synchronizedDelegate
//import hr.sil.android.util.general.extensions.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import java.util.*

import hr.sil.android.datacache.synchronizedDelegate
import hr.sil.android.datacache.format
import hr.sil.android.datacache.BluetoothAdapterMonitor

/**
 * @author mfatiga
 */
class App : Application(), BLEScannerStateHolder {
    private val log = logger()

    var pinManagementSelectedItem = RPinManagement()
    var pinManagementName = ""

    companion object {
        @JvmStatic
        lateinit var ref: App
    }

    init {
        ref = this
    }

    var languageCode: RLanguage = RLanguage()


    override fun attachBaseContext(base: Context) {
        println("Attaching base context in APP!!")

        SettingsHelper.init(base)
        super.attachBaseContext(SettingsHelper.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        SettingsHelper.setLocale(this)
    }

    //bluetooth adapters monitor
    val btMonitor: BluetoothAdapterMonitor by lazy { BluetoothAdapterMonitor.create(this) }

    //event bus initialization
    val eventBus: EventBus by lazy {
        EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build()
    }

    @Volatile
    private var debugMode: Boolean = false

    private fun setDebugMode(enabled: Boolean) {
        log.info("Setting DEBUG_MODE to $enabled")
        debugMode = enabled
        //deviceScanner.DEBUG_MODE = enabled
    }

    //device scanner
    var permissionCheckDone by synchronizedDelegate(false, this)
    private var errorLastShownAt = 0L

    private val deviceScanner by lazy {
        BLEDeviceScanner.create(
            GlobalScope,
            this,
            {
                if (permissionCheckDone) {
                    log.error("Error during BLE scan!", it)
                }

                var showError = true
                if (!permissionCheckDone && (it.errorCode == BLEScanException.ErrorCode.SCAN_FAILED_BLUETOOTH_DISABLED
                            || it.errorCode == BLEScanException.ErrorCode.SCAN_FAILED_LOCATION_PERMISSION_MISSING)) {
                    showError = false
                }

                if (showError) {
//                    runOnUiThread {
//                        val now = System.currentTimeMillis()
//                        if (now - errorLastShownAt >= 10000L) {
//                            ref.toast("Error: ${it.errorCode}")
//                            errorLastShownAt = now
//                        }
//                    }
                }
            },
            BLEGenericDeviceDataFactory()
        )
    }

    var selectedMasterMacAddress: String = ""

    var isFirstStart: Boolean = false

    var userLastLocation: UserLastLocationGps = UserLastLocationGps()

    private lateinit var stethoClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        stethoClient = OkHttpClient.Builder().addInterceptor(StethoInterceptor()).build()

        log.info("Starting...")
        ActionStatusHandler.checkClasses(this)
        isFirstStart = handleFirstStartup()

        log.info("Checking cache for external cache class modifications...")
        //DataCache.checkClasses(this)

        log.info("Initializing web services...")
        WSConfig.initialize(this.applicationContext)

        ActionStatusHandler.run()

        if( this@App.resources.getBoolean(R.bool.scan_all_devices_mpl_spl_plus_tablet) ) {
            deviceScanner.setAdvertisementFilters(BLEDeviceType.MPL_MASTER.filters() + BLEDeviceType.MPL_SLAVE.filters() + BLEDeviceType.MPL_SLAVE_P16.filters() + BLEDeviceType.SPL.filters() + BLEDeviceType.SPL_PLUS.filters() + BLEDeviceType.MPL_TABLET.filters() )
        }
        else if( this@App.resources.getBoolean(R.bool.scan_advertise_only_tablet) ) {
            deviceScanner.setAdvertisementFilters( BLEDeviceType.MPL_SLAVE.filters() + BLEDeviceType.MPL_SLAVE_P16.filters() + BLEDeviceType.MPL_TABLET.filters() )
        }
        else if( this@App.resources.getBoolean(R.bool.scan_advertise_only_mpl) ) {
            deviceScanner.setAdvertisementFilters( BLEDeviceType.MPL_SLAVE.filters() + BLEDeviceType.MPL_SLAVE_P16.filters() + BLEDeviceType.MPL_MASTER.filters() )
        }
        deviceScanner.addDeviceEventListener { events ->
            MPLDeviceStore.updateFromBLE(deviceScanner.devices.values.filter { !it.data.manufacturer.isSILBootloader() }.toList())

            if (debugMode) {
                log.info("BLE device events: ${events.joinToString(", ") {
                    val device = it.bleDevice.deviceAddress
                    val deviceType = it.bleDevice.data.deviceType
                    val eventType = it.eventType.toString()
                    val time = Date(it.bleDevice.lastPacketTimestampMillis).format("HH:mm:ss")

                    "$device [$deviceType]->[$eventType]@$time"
                }}")
            }
        }

        //start periodic remote-data updater
        DeviceStoreRemoteUpdater.run()

        //enable or disable debug mode
        setDebugMode(true)

        log.info("Starting BLE scan...")
        startScanner()

        GlobalScope.launch {
            if( resources.getBoolean(R.bool.scan_advertise_only_tablet)  ) {
                UserUtil.twoFactoryAuth = WSUser.getTwoFactoryAuthForRegisterinCPLUsers()
                UserUtil.showMailCodeInput = WSUser.showMailCodeInput()
                log.info("Two factory authentification is enabled: ${UserUtil.twoFactoryAuth?.twoFactorAuth}, is email code enabled: ${UserUtil.showMailCodeInput}")
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            //delay(3000)
            try {
                val task = FirebaseMessaging.getInstance().token.awaitForResult()
                if (!task.isSuccessful) {
                    log.info("getInstanceId failed", task.exception)
                }
                // Get new Instance ID token
                val token = task.result
                if (token != null) {
                    log.info("FCM token: $token")
                    MPLFireBaseMessagingService.sendRegistrationToServer(token)
                } else {
                    log.error("Error while fetching the FCM token!")
                }
            } catch (e: Exception) {
                log.error("Failed to get FCM token: ${e.message}")
                // Don't crash - just log and continue
            }
        }


        WSUser.registerOnUnauthorizedListener {
            //App.ref.toast("Unauthorized event")
            log.info("Starting to executing user logout")
            GlobalScope.launch {
                UserUtil.logout()
                ref.eventBus.post(UnauthorizedUserEvent(true))
            }
        }
    }

    private fun handleFirstStartup(): Boolean {
        val firstRun =  SettingsHelper.firstRun
        return if ( firstRun ) {
            log.info("First time app startup...")
            true
        } else {
            false
        }
    }

    override fun startScanner() {
        deviceScanner.start()
    }

    override suspend fun stopScannerAsync(forceDeviceLost: Boolean) {
        deviceScanner.stop(forceDeviceLost)
    }

    override fun isScannerStarted() =  deviceScanner.isStarted()

    override fun onTerminate() {
        GlobalScope.launch(Dispatchers.Main) {
            deviceScanner.stop(false)
            deviceScanner.destroy()
        }
        super.onTerminate()
    }
}