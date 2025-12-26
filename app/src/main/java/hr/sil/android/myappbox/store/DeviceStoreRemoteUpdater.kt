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

package hr.sil.android.myappbox.store
 

import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.store.model.MasterUnitWithKeys
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.core.remote.model.RLockerInfo
import hr.sil.android.myappbox.core.remote.model.RLockerKey
import hr.sil.android.myappbox.core.remote.model.RLockerKeyPurpose
import hr.sil.android.myappbox.core.remote.model.RMasterUnit
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.data.DeliveryKey
import hr.sil.android.myappbox.events.NewNotificationEvent
import hr.sil.android.myappbox.util.SettingsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author mfatiga
 */
object DeviceStoreRemoteUpdater {
    private val log = logger()

    private const val UPDATE_PERIOD = 10_000L // 10 seconds
    private const val BACKEND_FETCH_PERIOD = 60_000L // 30 seconds

    private val running = AtomicBoolean(false)
    private val inHandleUpdate = AtomicBoolean(false)

    // Keep the last fetch timestamp
    private var lastBackendFetch = 0L
    private var cachedActiveKeys: List<RLockerKey> = emptyList()
    private var cachedMasterUnits: List<RMasterUnit> = emptyList()

    fun run() {
        if (running.compareAndSet(false, true)) {
            GlobalScope.launch(Dispatchers.Default) {
                while (true) {
                    try {
                        handleUpdate()
                    } catch (ex: Exception) {
                        log.error("Periodic remote-update failed...", ex)
                    }
                    delay(UPDATE_PERIOD)
                }
            }
        }
    }

    suspend fun immediateForceUpdate() {
        if (inHandleUpdate.compareAndSet(false, true)) {
            if (UserUtil.isUserLoggedIn()) {

                log.info("NEW NOTIF 44 Push notification type BBBBBB")
                val cachedActiveKeysForce = WSUser.getActiveKeys() ?: emptyList()
                val cachedMasterUnitsForce = WSUser.getMasterUnits() ?: emptyList()
                MPLDeviceStore.updateFromRemote(
                    cachedMasterUnitsForce.map { masterUnit ->
                        MasterUnitWithKeys(
                            masterUnit = masterUnit,
                            activeKeys = cachedActiveKeysForce.filter { it.lockerMasterId == masterUnit.id },
                            availableLockerSizes = emptyList()
                        )
                    }
                )

                log.info("NEW NOTIF 44 Push notification type EEEEE ${cachedActiveKeysForce.size}")
//                cachedActiveKeysForce.forEach { key ->
//                    log.info("NEW NOTIF 44 Push notification type FFFF ${key.lockerMasterMac}")
//                    log.info("NEW NOTIF 44 Push notification type GGGGG ${SettingsHelper.userLastSelectedLocker}")
//                    if( key.purpose == RLockerKeyPurpose.DELIVERY && key.lockerMasterMac == SettingsHelper.userLastSelectedLocker.macRealToClean() ) {
//                        val deliveryKeys =
//                            DatabaseHandler.deliveryKeyDb.get(SettingsHelper.userLastSelectedLocker)
//                        if (deliveryKeys == null) {
//                            DatabaseHandler.deliveryKeyDb.put(
//                                DeliveryKey(
//                                    SettingsHelper.userLastSelectedLocker,
//                                    listOf(key.id)
//                                )
//                            )
//                        } else {
//                            if (!deliveryKeys.keyIds.contains(key.id)) {
//                                val listOfIds = deliveryKeys.keyIds.plus(key.id)
//                                DatabaseHandler.deliveryKeyDb.put(
//                                    DeliveryKey(
//                                        SettingsHelper.userLastSelectedLocker,
//                                        listOfIds
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }

                App.ref.eventBus.post(NewNotificationEvent(true))
            }
            inHandleUpdate.set(false)
        }
    }

    suspend fun forceUpdate() {
        immediateForceUpdate()
    }

    private suspend fun handleUpdate() {
        if (inHandleUpdate.compareAndSet(false, true)) {
            if (UserUtil.isUserLoggedIn()) {
                doUpdate()
            }
            inHandleUpdate.set(false)
        }
    }

    private suspend fun doUpdate() {
        val now = System.currentTimeMillis()

        val activeKeys = WSUser.getActiveKeys() ?: emptyList()
        // Fetch from backend only every 30 seconds
        if (now - lastBackendFetch >= BACKEND_FETCH_PERIOD) {
            log.info("Fetching master units and keys from backend...")
            //cachedActiveKeys = WSUser.getActiveKeys() ?: emptyList()
            cachedMasterUnits = WSUser.getMasterUnits() ?: emptyList()
            lastBackendFetch = now
        } else {
            log.debug("Using cached master units and keys (no backend call)")
        }

        if (cachedMasterUnits.isEmpty()) {
            log.info("Master unit size from backend is: ${cachedMasterUnits.size}")
        }

        log.info(
            "Master unit size = ${cachedMasterUnits.size}, " +
                    "${cachedMasterUnits.joinToString { it.mac }}"
        )

        MPLDeviceStore.updateFromRemote(
            cachedMasterUnits.map { masterUnit ->
                MasterUnitWithKeys(
                    masterUnit = masterUnit,
                    activeKeys = activeKeys.filter { it.lockerMasterId == masterUnit.id },
                    //activeKeys = cachedActiveKeys.filter { it.lockerMasterId == masterUnit.id },
                    availableLockerSizes = emptyList()
                )
            }
        )
    }
}