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

package hr.sil.android.myappbox.store.model

import android.content.Context
import hr.sil.android.ble.scanner.model.device.BLEDevice
import hr.sil.android.ble.scanner.scan_multi.model.BLEDeviceData
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.BLEAdvMplMaster
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.BLEAdvMplTablet
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.BLEAdvSpl
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.BLEAdvSplPlus
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.MPLDeviceStatus
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.MPLModemStatus
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.ParcelLockerKeyboardType
import hr.sil.android.ble.scanner.scan_multi.properties.base.BLEAdvProperties
import hr.sil.android.myappbox.App

import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.core.ble.comm.MPLUserBLECommunicator
import hr.sil.android.myappbox.core.model.MPLDeviceType
import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.core.remote.model.RequiredAccessRequestTypes
import hr.sil.android.myappbox.core.util.logger
import kotlinx.coroutines.runBlocking

import hr.sil.android.datacache.lerpInDomain

/**
 * @author mfatiga
 */
class MPLDevice private constructor(
    val macAddress: String,

        // type
    val type: MPLDeviceType,
    val installationType: InstalationType?,

        // from remote
    val masterUnitId: Int,
    //val accessType: RMasterUnitAccessType,
    val accessTypes: List<RMasterUnitAccessType>,
    val isSplActivate: Boolean = false,
    val masterUnitType: RMasterUnitType,
    val name: String,
    val address: String,
    val activeKeys: List<RLockerKey>,
    val customerProductName: String,
    val latitude: Double,
    val longitude: Double,

        // from BLE
    val mplMasterDeviceStatus: MPLDeviceStatus,
    val availableLockers: List<RAvailableLockerSize>,
    val masterModemQueueSize: Int,
    val isInBleProximity: Boolean,
    val modemRssi: Int?,
    val humidity: Double?,
    val pressure: Double?,
    val temperature: Double?,

        // combined
    val mplRequestAccessSend: Boolean?,
    var isCollectParcelSplTaken: Boolean?,
    val pinManagementAllowed: Boolean?,
    val keypadType: ParcelLockerKeyboardType,
    val isProductionReady: Boolean? = false,
    val isPublicDevice: Boolean? = false,
    val isUserAssigned: Boolean? = false,
    val activeAccessRequest: Boolean? = false,
    val requiredAccessRequestTypes: List<RequiredAccessRequestTypes> = listOf()
) {

    fun isDeviceAccessible(mplDevice: MPLDevice): Boolean {
        if (isInBleProximity) {
            return mplMasterDeviceStatus == MPLDeviceStatus.REGISTERED && mplDevice.address != ""
        } else {
            return accessTypes.filter {
                it.equals(RMasterUnitAccessType.BY_GROUP_OWNERSHIP) || it.equals(RMasterUnitAccessType.BY_ACTIVE_PAF_KEY)
                        || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_ADMIN) || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_USER)
            }.isNotEmpty()
        }
    }

    fun hasRightsToShareAccess(): Boolean {

        if (accessTypes.filter { it.equals(RMasterUnitAccessType.BY_GROUP_OWNERSHIP) || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_ADMIN) }.isNotEmpty()) {
            return true
        }
        return false
    }

    fun hasUserRightsOnSendParcelLocker(): Boolean {

        if (accessTypes.filter {
                    it.equals(RMasterUnitAccessType.BY_GROUP_OWNERSHIP)
                            || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_ADMIN)
            }.isNotEmpty()) {
            return true
        }
        return false
    }

    fun hasUserRightsOnEditParcelLocker(): Boolean {

        if (accessTypes.filter {
                    it.equals(RMasterUnitAccessType.BY_GROUP_OWNERSHIP) || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_ADMIN)
                }.isNotEmpty()) {
            return true
        }
        return false
    }


    fun hasUserRightsOnLocker(): Boolean {
        if (accessTypes.filter {
                    it.equals(RMasterUnitAccessType.BY_GROUP_OWNERSHIP) || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_ADMIN)
                            || it.equals(RMasterUnitAccessType.BY_GROUP_MEMBERSHIP_AS_USER) || it.equals(RMasterUnitAccessType.BY_ACTIVE_PAF_KEY)
                }.isNotEmpty() ) {
            return true
        }
        return false
    }

    companion object {

        private fun getAvailableLockers(remoteData: MasterUnitWithKeys?,
                                        bleData: BLEDevice<BLEDeviceData>?
        ): List<RAvailableLockerSize> {
            var freeXS = 0
            var freeS = 0
            var freeM = 0
            var freeL = 0
            var freeXL = 0

            if (bleData != null) {
                //prioritize getting available sizes from BLE advertisement
                val bleProps = bleData.data.properties
                if (bleProps is BLEAdvMplMaster) {
                    freeXS = bleProps.slavesFreeXS.value ?: 0
                    freeS = bleProps.slavesFreeS.value ?: 0
                    freeM = bleProps.slavesFreeM.value ?: 0
                    freeL = bleProps.slavesFreeL.value ?: 0
                    freeXL = bleProps.slavesFreeXL.value ?: 0
                } else if (bleProps is BLEAdvMplTablet) {
                    freeXS = bleProps.slavesFreeXS.value ?: 0
                    freeS = bleProps.slavesFreeS.value ?: 0
                    freeM = bleProps.slavesFreeM.value ?: 0
                    freeL = bleProps.slavesFreeL.value ?: 0
                    freeXL = bleProps.slavesFreeXL.value ?: 0
                }
                else if (bleProps is BLEAdvSplPlus) {
                    freeS = bleProps.lockersFreeS.value ?: 0
                    freeL = bleProps.lockersFreeL.value ?: 0
                }
            } else {
                for (availableLockerSize in (remoteData?.availableLockerSizes ?: listOf())) {
                    when (availableLockerSize.size) {
                        RLockerSize.XS -> {
                            freeXS = availableLockerSize.count
                        }
                        RLockerSize.S -> {
                            freeS = availableLockerSize.count
                        }
                        RLockerSize.M -> {
                            freeM = availableLockerSize.count
                        }
                        RLockerSize.L -> {
                            freeL = availableLockerSize.count
                        }
                        RLockerSize.XL -> {
                            freeXL = availableLockerSize.count
                        }
                        else -> {
                        }
                    }
                }
            }

            return listOf(
                    RAvailableLockerSize(RLockerSize.XS, freeXS),
                    RAvailableLockerSize(RLockerSize.S, freeS),
                    RAvailableLockerSize(RLockerSize.M, freeM),
                    RAvailableLockerSize(RLockerSize.L, freeL),
                    RAvailableLockerSize(RLockerSize.XL, freeXL))
        }


        fun create(macAddress: String,
                   remoteData: MasterUnitWithKeys?,
                   bleData: BLEDevice<BLEDeviceData>?,
                   accessRequestMPL: List<RAccessRequest>?): MPLDevice {

            // remote
            val masterUnit = remoteData?.masterUnit
            val activeKeys = remoteData?.activeKeys
            val installationType = remoteData?.masterUnit?.installationType
            val productionReadyUserHasAccess = remoteData?.masterUnit?.productionReady
            val publicDeviceUserHasAccess = remoteData?.masterUnit?.publicDevice

            val splIsActivatedMainServis = remoteData?.masterUnit?.splIsActivated
            val isUserAssigned = remoteData?.masterUnit?.userIsAssigned
            val activeAccessRequest = remoteData?.masterUnit?.activeAccessRequest
            val requiredAccessRequestTypes = remoteData?.masterUnit?.requiredAccessRequestTypes
            val accessTypes = remoteData?.masterUnit?.accessTypes

            // ble
            var mplDeviceType = MPLDeviceType.UNKNOWN
            var mplMasterDeviceStatus = MPLDeviceStatus.UNKNOWN
            var mplMasterModemStatus = MPLModemStatus.UNKNOWN
            var masterModemQueueSize = 0
            var bleMasterUnitType: RMasterUnitType = RMasterUnitType.UNKNOWN
            val bleProps = bleData?.data?.properties
            var batteryVoltage: Double? = null
            val isInBleProximity = bleProps != null
            var modemRssi: Int? = null
            var humidity: Double? = null
            var pressure: Double? = null
            var temperature: Double? = null

            // mpl request access
            val mplRequestAccessSend: Boolean = accessRequestMPL?.isNotEmpty() == true

            // spl collect parcel taken
            val isCollectParcelSplTaken: Boolean = remoteData?.activeKeys?.filter { it -> it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }?.isNotEmpty()
                    ?: true

            logger().info("PIN MANA AA ... is pin allowed ${remoteData}")
            logger().info("PIN MANA BB ... is pin allowed ${remoteData?.masterUnit}")
            logger().info("PIN MANA CC ... is pin allowed ${remoteData?.masterUnit?.allowPinSave}")
            val pinManagementAllowed: Boolean? = remoteData?.masterUnit?.allowPinSave
            var keypadType = ParcelLockerKeyboardType.SPL_PLUS

            when (bleProps) {
                is BLEAdvMplMaster -> {
                    batteryVoltage = getBatteryVoltage(bleProps)
                    mplDeviceType = MPLDeviceType.MASTER
                    mplMasterDeviceStatus = bleProps.deviceStatus.value ?: MPLDeviceStatus.UNKNOWN
                    mplMasterModemStatus = bleProps.modemStatus.value ?: MPLModemStatus.UNKNOWN
                    masterModemQueueSize = bleProps.modemQueue.value ?: 0
                    modemRssi = bleProps.modemRSSI.value
                    temperature = bleProps.temperature.value
                    pressure = bleProps.pressure.value
                    humidity = bleProps.humidity.value
                    bleMasterUnitType = RMasterUnitType.MPL
                }

                is BLEAdvMplTablet -> {
                    mplDeviceType = MPLDeviceType.TABLET
                    mplMasterDeviceStatus = bleProps.deviceStatus.value ?: MPLDeviceStatus.UNKNOWN
                    temperature = bleProps.temperature.value
                    pressure = bleProps.pressure.value
                    humidity = bleProps.humidity.value
                    bleMasterUnitType = RMasterUnitType.MPL
                }


                is BLEAdvSpl -> {
                    batteryVoltage = getBatteryVoltage(bleProps)
                    mplDeviceType = MPLDeviceType.SPL
                    mplMasterDeviceStatus = bleProps.deviceStatus.value ?: MPLDeviceStatus.UNKNOWN
                    mplMasterModemStatus = bleProps.modemStatus.value ?: MPLModemStatus.UNKNOWN
                    masterModemQueueSize = bleProps.modemQueue.value ?: 0
                    modemRssi = bleProps.modemRSSI.value
                    temperature = bleProps.temperature.value
                    pressure = bleProps.pressure.value
                    humidity = bleProps.humidity.value
                    bleMasterUnitType = RMasterUnitType.SPL
                }

                is BLEAdvSplPlus -> {
                    batteryVoltage = getBatteryVoltage(bleProps)
                    mplDeviceType = MPLDeviceType.SPL_PLUS
                    mplMasterDeviceStatus = bleProps.deviceStatus.value ?: MPLDeviceStatus.UNKNOWN
                    mplMasterModemStatus = bleProps.modemStatus.value ?: MPLModemStatus.UNKNOWN
                    masterModemQueueSize = bleProps.modemQueue.value ?: 0
                    modemRssi = bleProps.modemRSSI.value
                    temperature = bleProps.temperature.value
                    pressure = bleProps.pressure.value
                    humidity = bleProps.humidity.value
                    bleMasterUnitType = RMasterUnitType.SPL_PLUS
                    keypadType = bleProps.keyboardType.value ?: ParcelLockerKeyboardType.SPL_PLUS
                }
            }

            return runBlocking {
                val getDevicesInfo = MPLDeviceStore.remoteInfoDevices
                val device = getDevicesInfo.values.find { it.mac == macAddress.macRealToClean() }
                val rName = masterUnit?.name ?: device?.name ?: macAddress
                val rAddress = masterUnit?.address ?: device?.address ?: ""
                val isSplActivate = device?.splIsActivated ?: false
                val isProductionReady = device?.productionReady
                val isPublicDevice = device?.publicDevice

                MPLDevice(
                        macAddress = macAddress,
                        type = mplDeviceType,
                        installationType = installationType,
                        // from remote
                        masterUnitId = masterUnit?.id ?: -1,
                        //accessType = masterUnit?.accessType ?: RMasterUnitAccessType.UNKNOWN,
                        accessTypes = accessTypes ?: listOf(),
                        isSplActivate = if( splIsActivatedMainServis != null ) splIsActivatedMainServis else isSplActivate,
                        masterUnitType = masterUnit?.type ?: bleMasterUnitType,
                        name = rName,
                        address = rAddress,
                        activeKeys = activeKeys ?: listOf(),
                        customerProductName = if( remoteData?.masterUnit?.productName != "" ) remoteData?.masterUnit?.productName ?: "" else remoteData?.masterUnit?.customerProductName ?: "",
                        latitude = remoteData?.masterUnit?.latitude ?: 0.0,
                        longitude = remoteData?.masterUnit?.longitude ?: 0.0,

                        // from BLE
                        mplMasterDeviceStatus = mplMasterDeviceStatus,
                        availableLockers = getAvailableLockers(remoteData, bleData),
                        masterModemQueueSize = masterModemQueueSize,
                        isInBleProximity = isInBleProximity,
                        modemRssi = modemRssi,
                        humidity = humidity,
                        pressure = pressure,
                        temperature = temperature,

                        // combined
                        mplRequestAccessSend = mplRequestAccessSend,
                        isCollectParcelSplTaken = isCollectParcelSplTaken,
                        pinManagementAllowed = pinManagementAllowed,
                        keypadType = keypadType,
                        isProductionReady = if( productionReadyUserHasAccess != null ) productionReadyUserHasAccess else isProductionReady,
                        isPublicDevice = if( publicDeviceUserHasAccess != null ) publicDeviceUserHasAccess else isPublicDevice,
                    isUserAssigned = isUserAssigned,
                    activeAccessRequest = activeAccessRequest,
                    requiredAccessRequestTypes = requiredAccessRequestTypes ?: listOf()
                )
            }
        }

        private fun getBatteryVoltage(bleProps: BLEAdvProperties): Double {
            when (bleProps) {
                is BLEAdvMplMaster -> {
                    val raw = bleProps.batteryRaw.value?.toDouble()?.lerpInDomain(0.0, 255.0, 0.0, 65535.0)
                            ?: 0.0
                    return 0.0005895 * raw - 18.65
                }
                is BLEAdvSpl -> {
                    val raw = bleProps.batteryRaw.value?.toDouble()?.lerpInDomain(0.0, 255.0, 0.0, 65535.0)
                            ?: 0.0
                    return 0.0005895 * raw - 18.65
                }
                else -> return 0.0
            }

        }
    }

    // util
    fun createBLECommunicator(context: Context): MPLUserBLECommunicator {
        return MPLUserBLECommunicator(context, macAddress, App.Companion.ref)
    }

}