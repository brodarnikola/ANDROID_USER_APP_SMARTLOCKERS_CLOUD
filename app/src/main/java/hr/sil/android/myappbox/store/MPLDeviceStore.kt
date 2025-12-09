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

import hr.sil.android.ble.scanner.model.device.BLEDevice
import hr.sil.android.ble.scanner.scan_multi.model.BLEDeviceData
import hr.sil.android.myappbox.App

import hr.sil.android.myappbox.events.MPLDevicesUpdatedEvent
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.store.model.MasterUnitWithKeys
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RLockerInfo
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macCleanToReal
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.util.general.delegates.synchronizedDelegate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author mfatiga
 */
object MPLDeviceStore {
    private var mDevices by synchronizedDelegate(mapOf<String, MPLDevice>())
    var remoteInfoDevices = mutableMapOf<String, RLockerInfo>()

    private var remoteInfoKeys = mutableListOf<String>()
    val uniqueDevices: Map<String, MPLDevice>
        get() = mDevices.toMap()

    private var bleData by synchronizedDelegate(mapOf<String, BLEDevice<BLEDeviceData>>())
    fun updateFromBLE(bleDevices: List<BLEDevice<BLEDeviceData>>) {

        bleData = bleDevices.associateBy { it.deviceAddress.uppercase() }
        mergeData()
        notifyEvents(bleDevices.map { it.deviceAddress.uppercase() })
    }

    val log = logger()
    private var remoteData by synchronizedDelegate(mapOf<String, MasterUnitWithKeys>())

    suspend fun updateFromRemote(remoteDevices: Collection<MasterUnitWithKeys>) {
        remoteInfoKeys = bleData.map { it.key.macRealToClean() }.toMutableList()
        val linuxPublicDevices = remoteDevices.filter { it.masterUnit.installationType == InstalationType.LINUX } .map { it.masterUnit.mac.macRealToClean() }.toMutableList()
        log.info("Linux public devices are: ${linuxPublicDevices.joinToString { it + ", \n" }}")
        remoteInfoKeys.addAll( linuxPublicDevices )
        if (remoteInfoKeys.isNotEmpty()) {
            val list = WSUser.getDevicesInfo(remoteInfoKeys)
            log.info("Fetched list size${list?.size}.. ${list?.joinToString(",") { it.mac.plus(it.productionReady) } }")
            remoteInfoDevices = list?.associateBy { it.mac.macCleanToReal() }?.toMutableMap()
                ?: mutableMapOf()
        }

        log.info("RemoteInfoDevices size${remoteDevices.size}")
        remoteData = remoteDevices.associateBy { it.masterUnit.mac.macCleanToReal() }
        mergeData()
        notifyEvents(remoteDevices.map { it.masterUnit.mac.toUpperCase() })
    }

    private fun mergeData() {
        val allKeys = (remoteData.keys + bleData.keys).distinct()
        GlobalScope.launch {
            val allActiveRequestAccessMPL = WSUser.getActiveRequests()?.groupBy {
                it.masterMac.macCleanToReal()
            }

            val devices = allKeys
                .associate {
                    it to MPLDevice.Companion.create(
                        it,
                        remoteData[it],
                        bleData[it],
                        allActiveRequestAccessMPL?.get(it)
                    )
                }
                .toList()

//                .sortedBy { it.second.mplMasterDeviceStatus == MPLDeviceStatus.REGISTERED && it.second.isInBleProximity && it.second.masterUnitId != -1  }
//                .sortedBy { it.second.mplMasterDeviceStatus == MPLDeviceStatus.UNREGISTERED && it.second.isInBleProximity }
//                .sortedBy { it.second.mplMasterDeviceStatus == MPLDeviceStatus.REGISTERED && !it.second.isInBleProximity }
//                .sortedBy { it.second.mplMasterDeviceStatus == MPLDeviceStatus.REGISTERED && it.second.isInBleProximity && it.second.masterUnitId == -1 }
//                .toMap().toMutableMap()
                .sortedBy { it.second.isInBleProximity && it.second.masterUnitId != -1  }
                .sortedBy { it.second.isInBleProximity && it.second.masterUnitId == -1 }
                .sortedBy { !it.second.isInBleProximity && it.second.masterUnitId != -1  }
                .toMap().toMutableMap()

            mDevices = devices.filter {
                it.value.isDeviceAccessible(it.value)
            }
        }
    }

    private fun notifyEvents(macList: List<String>) {
        App.Companion.ref.eventBus.post(MPLDevicesUpdatedEvent(macList))
    }

    fun clear() {
        remoteData = mapOf()
        bleData = mapOf()
        mDevices = mapOf()
    }
}