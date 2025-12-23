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

package hr.sil.android.myappbox.core.ble.comm

import android.content.Context
import hr.sil.android.blecommunicator.impl.characteristics.streaming.StreamingCommand
import hr.sil.android.myappbox.core.ble.comm.model.BLEDoorOpenResult
import hr.sil.android.myappbox.core.ble.comm.model.MPLGenericCommand
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.RLockerSize
import hr.sil.android.myappbox.core.util.BLEScannerStateHolder
import hr.sil.android.myappbox.core.util.macRealToBytes
import hr.sil.android.rest.core.util.toByteArray
//import hr.sil.android.util.general.extensions.hexToByteArray
//import hr.sil.android.util.general.extensions.toByteArray
import kotlinx.coroutines.delay

import hr.sil.android.rest.core.util.hexToByteArray

/**
 * @author mfatiga
 */
class MPLUserBLECommunicator(
        ctx: Context,
        deviceAddress: String,
        bleScannerStateHolder: BLEScannerStateHolder
) : BaseMPLCommunicator(
        ctx,
        deviceAddress,
        bleScannerStateHolder,
        WSUser
) {
    companion object {
        private const val POLL_OPEN_DOOR_STATUS_TIMEOUT = 20_000L
        private const val POLL_OPEN_DOOR_STATUS_PERIOD = 500L
        private val cmdReadOpenDoorResult = StreamingCommand(0x04, 0x00)
        private val cmdParcelPickup = StreamingCommand(0x04, 0x03)
        private val cmdParcelPickupP16 = StreamingCommand(0x04, 0x13)
        private val cmdParcelSendCreate = StreamingCommand(0x04, 0x04)
        private val cmdParcelDeliveryRetailCreate = StreamingCommand(0x04, 0x08)
        private val cmdParcelSendCancel = StreamingCommand(0x04, 0x05)
        private val cmdParcelSendCancelP16 = StreamingCommand(0x04, 0x15)

        private val cmdCancelDeliveryVendor = StreamingCommand(0x04, 0x09)
        private val cmdCancelDeliveryVendorP16 = StreamingCommand(0x04, 0x019)
        private val cmdInvalidateAllKeysOnSPLPLUS = StreamingCommand(0x04, 0xFE)

        private val cmdLockerIsDirty = StreamingCommand(0x04, 0xC0)

        private val MAC_ADDRESS_7_BYTE_LENGTH = 14
        private val MAC_ADDRESS_6_BYTE_LENGTH = 12
        private val MAC_ADDRESS_LAST_BYTE_LENGTH = 2
    }

    // core access
    private suspend fun readOpenDoorStatusCode(): Byte? {
        val resultSize = 1
        val readResult = streaming.readArray(cmdReadOpenDoorResult, resultSize)
        return if (readResult.status && readResult.data.size == resultSize) {
            readResult.data.first()
        } else {
            null
        }
    }

    private suspend fun pollOpenDoorStatus(
            timeout: Long = POLL_OPEN_DOOR_STATUS_TIMEOUT,
            period: Long = POLL_OPEN_DOOR_STATUS_PERIOD
    ): BLEDoorOpenResult.BLESlaveErrorCode? {
        var result: BLEDoorOpenResult.BLESlaveErrorCode? = null
        val start = System.currentTimeMillis()
        while ((System.currentTimeMillis() - start) < timeout) {
            //read status code or break if null
            val statusCode = readOpenDoorStatusCode() ?: break

            //when status code is set (not equal to 0xFF), set and break
            if (statusCode != 0xFF.toByte()) {
                result = BLEDoorOpenResult.BLESlaveErrorCode.parse(statusCode)
                break
            }

            //wait for period between checks
            delay(period)
        }
        return result
    }

    
    suspend fun requestParcelPickup(lockerBLEMac: String, endUserId: Int): BLEDoorOpenResult {

        //result
        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE
        //parse parameters
        var data: ByteArray
        when {
            // this is for mpl with new lockers with p16
            lockerBLEMac.length == MAC_ADDRESS_7_BYTE_LENGTH -> {
                val first6ByteMacAddress = lockerBLEMac.take(MAC_ADDRESS_6_BYTE_LENGTH)
                val lastByteMacAddress = lockerBLEMac.takeLast(MAC_ADDRESS_LAST_BYTE_LENGTH)
                data = first6ByteMacAddress.macRealToBytes().reversedArray() + lastByteMacAddress.hexToByteArray() + endUserId.toByteArray(4)
            }
            // this is for mpl with old lockers
            else -> data = lockerBLEMac.macRealToBytes().reversedArray() + endUserId.toByteArray(4)
        }
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {

            val cmdPickupParcel = when (lockerBLEMac.length) {
                MAC_ADDRESS_7_BYTE_LENGTH -> cmdParcelPickupP16
                else -> cmdParcelPickup
            }

            if (streaming.writeArray(cmdPickupParcel, encrypted).status) {
                streaming.writeEmpty()
                val openDoorStatus = pollOpenDoorStatus()
                if (openDoorStatus != null) {
                    bleSlaveErrorCode = openDoorStatus
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
        }
        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    suspend fun requestParcelSendCreate(lockerSize: RLockerSize, endUserId: Int, pin: Int): BLEDoorOpenResult {
        //result
        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE

        //parse parameters
        val lockerSizeCode = lockerSize.code
        if (lockerSizeCode != null && pin > 0 && pin <= 9999) {
            val pinBytes = pin.toByteArray(2)
            val data = byteArrayOf(lockerSizeCode) + endUserId.toByteArray(4) + pinBytes
            log.info("endUserId to byteArray is: ${endUserId.toByteArray(4)}")

            val encrypted = wrapEncryptData(data)
            if (encrypted != null) {
                if (streaming.writeArray(cmdParcelSendCreate, encrypted).status) {
                    streaming.writeEmpty()
                    val openDoorStatus = pollOpenDoorStatus()
                    if (openDoorStatus != null) {
                        bleSlaveErrorCode = openDoorStatus
                    } else {
                        bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                    }
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.INVALID_PARAMETERS
        }

        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    suspend fun requestParcelSendCreateForCPLBasel(lockerSize: RLockerSize, endUserId: Int, pin: Int, reducedMobility: Byte): BLEDoorOpenResult {
        //result
        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE

        //parse parameters
        val lockerSizeCode = lockerSize.code
        if (lockerSizeCode != null && pin > 0 && pin <= 9999) {
            val pinBytes = pin.toByteArray(2)
            val data = byteArrayOf(lockerSizeCode) + endUserId.toByteArray(4) + pinBytes + byteArrayOf(reducedMobility)

            val encrypted = wrapEncryptData(data)
            if (encrypted != null) {
                if (streaming.writeArray(cmdParcelSendCreate, encrypted).status) {
                    streaming.writeEmpty()
                    val openDoorStatus = pollOpenDoorStatus()
                    if (openDoorStatus != null) {
                        bleSlaveErrorCode = openDoorStatus
                    } else {
                        bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                    }
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.INVALID_PARAMETERS
        }

        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    suspend fun requestParcelSendCreateForRetailLockers(lockerSize: RLockerSize, endUserId: Int, vendorId: Int): BLEDoorOpenResult {
        //result
        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE

        //parse parameters
        val lockerSizeCode = lockerSize.code
        if (lockerSizeCode != null) {
            val data = byteArrayOf(lockerSizeCode) + endUserId.toByteArray(4) + vendorId.toByteArray(4)
            log.info("endUserId to byteArray is: ${endUserId.toByteArray(4)}")

            val encrypted = wrapEncryptData(data)
            if (encrypted != null) {
                if (streaming.writeArray(cmdParcelDeliveryRetailCreate, encrypted).status) {
                    streaming.writeEmpty()
                    val openDoorStatus = pollOpenDoorStatus()
                    if (openDoorStatus != null) {
                        bleSlaveErrorCode = openDoorStatus
                    } else {
                        bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                    }
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.INVALID_PARAMETERS
        }

        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    suspend fun requestParcelSendCreateForTablets(lockerSize: RLockerSize, endUserId: Int, pin: Int, reducedMobility: Byte): BLEDoorOpenResult {
        //result
        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE

        //parse parameters
        val lockerSizeCode = lockerSize.code
        if (lockerSizeCode != null && pin > 0 && pin <= 9999) {
            val pinBytes = pin.toByteArray(2)
            val data = byteArrayOf(lockerSizeCode) + endUserId.toByteArray(4) + pinBytes + byteArrayOf(reducedMobility)

            val encrypted = wrapEncryptData(data)
            if (encrypted != null) {
                if (streaming.writeArray(cmdParcelSendCreate, encrypted).status) {
                    streaming.writeEmpty()
                    val openDoorStatus = pollOpenDoorStatus()
                    if (openDoorStatus != null) {
                        bleSlaveErrorCode = openDoorStatus
                    } else {
                        bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                    }
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.INVALID_PARAMETERS
        }

        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    /*suspend fun lockerIsDirty(slaveMacAddress: String): Boolean {

        val flagToUpdate = 0x02.toByteArray() // this is a flag for tablet to update "Cleaning needed" propertie
        val correctSlaveMacAddress = when {
            // this is for mpl with new lockers with p16
            slaveMacAddress.length == MAC_ADDRESS_7_BYTE_LENGTH -> {
                val first6ByteMacAddress = slaveMacAddress.take(MAC_ADDRESS_6_BYTE_LENGTH)
                val lastByteMacAddress = slaveMacAddress.takeLast(MAC_ADDRESS_LAST_BYTE_LENGTH)
                first6ByteMacAddress.macRealToBytes().reversedArray() + lastByteMacAddress.hexToByteArray()
            }
            // this is for mpl with old lockers
            else -> {
                val oldLockerIndex = 0x00.toByte()
                slaveMacAddress.macRealToBytes().reversedArray() + oldLockerIndex
            }
        }
        val flagCleaningNeeded = 0x02.toByteArray()
        val data = flagToUpdate + correctSlaveMacAddress + flagCleaningNeeded

        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {
            if (streaming.writeArray(cmdLockerIsDirty, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }*/

    suspend fun lockerIsDirty(byteArrayCleaningNeeded: ByteArray): Boolean {

        val encrypted = wrapEncryptData(byteArrayCleaningNeeded)
        if (encrypted != null) {
            if (streaming.writeArray(cmdLockerIsDirty, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }

    
    suspend fun forceOpenDoor(slaveMacAddress: String): Boolean {
        var data: ByteArray
        when {
            // this is for mpl with new lockers with p16
            slaveMacAddress.length == MAC_ADDRESS_7_BYTE_LENGTH -> {
                val first6ByteMacAddress = slaveMacAddress.take(MAC_ADDRESS_6_BYTE_LENGTH)
                val lastByteMacAddress = slaveMacAddress.takeLast(MAC_ADDRESS_LAST_BYTE_LENGTH)
                data = first6ByteMacAddress.macRealToBytes().reversedArray() + lastByteMacAddress.hexToByteArray()
            }
            // this is for mpl with old lockers
            else -> data = slaveMacAddress.macRealToBytes().reversedArray()
        }

        if( slaveMacAddress.length == MAC_ADDRESS_7_BYTE_LENGTH ) {
            return sendGenericCommand(MPLGenericCommand.FORCE_OPEN_DOOR_P16, data)
        }
        else
            return sendGenericCommand(MPLGenericCommand.FORCE_OPEN_DOOR, data)
        /*val params = slaveMacAddress.macRealToBytes().reversedArray()
        return sendGenericCommand(MPLGenericCommand.FORCE_OPEN_DOOR, params)*/
    }

    
    suspend fun requestParcelSendCancel(lockerBLEMac: String, endUserId: Int): BLEDoorOpenResult {
        //result
        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE
        //parse parameters
        var data: ByteArray
        when {
            // this is for mpl with new lockers with p16
            lockerBLEMac.length == MAC_ADDRESS_7_BYTE_LENGTH -> {
                val first6ByteMacAddress = lockerBLEMac.take(MAC_ADDRESS_6_BYTE_LENGTH)
                val lastByteMacAddress = lockerBLEMac.takeLast(MAC_ADDRESS_LAST_BYTE_LENGTH)
                data = first6ByteMacAddress.macRealToBytes().reversedArray() + lastByteMacAddress.hexToByteArray() + endUserId.toByteArray(4)
            }
            // this is for mpl with old lockers
            else -> data = lockerBLEMac.macRealToBytes().reversedArray() + endUserId.toByteArray(4)
        }
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {

            val cmdCancel = when (lockerBLEMac.length) {
                MAC_ADDRESS_7_BYTE_LENGTH -> cmdParcelSendCancelP16
                else -> cmdParcelSendCancel
            }

            if (streaming.writeArray(cmdCancel, encrypted).status) {
                streaming.writeEmpty()
                val openDoorStatus = pollOpenDoorStatus()
                if (openDoorStatus != null) {
                    bleSlaveErrorCode = openDoorStatus
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
        }
        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    
    suspend fun requestCancelDeliveryVendor(lockerBLEMac: String, endUserId: Int): BLEDoorOpenResult {

        var bleDeviceErrorCode: BLEDoorOpenResult.BLEDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.OK
        var bleSlaveErrorCode: BLEDoorOpenResult.BLESlaveErrorCode = BLEDoorOpenResult.BLESlaveErrorCode.NONE

        //parse parameters
        var data: ByteArray
        when {
            // this is for mpl with new lockers with p16
            lockerBLEMac.length == MAC_ADDRESS_7_BYTE_LENGTH -> {
                val first6ByteMacAddress = lockerBLEMac.take(MAC_ADDRESS_6_BYTE_LENGTH)
                val lastByteMacAddress = lockerBLEMac.takeLast(MAC_ADDRESS_LAST_BYTE_LENGTH)
                data = first6ByteMacAddress.macRealToBytes().reversedArray() + lastByteMacAddress.hexToByteArray() + endUserId.toByteArray(4)
            }
            // this is for mpl with old lockers
            else -> data = lockerBLEMac.macRealToBytes().reversedArray() + endUserId.toByteArray(4)
        }

        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {

            var cmdCancel = StreamingCommand(0x00, 0x00)
            when {
                lockerBLEMac.length == MAC_ADDRESS_7_BYTE_LENGTH -> cmdCancel = cmdCancelDeliveryVendorP16
                else -> cmdCancel = cmdCancelDeliveryVendor
            }

            if (streaming.writeArray(cmdCancel, encrypted).status) {
                streaming.writeEmpty()
                val openDoorStatus = pollOpenDoorStatus()
                if (openDoorStatus != null) {
                    bleSlaveErrorCode = openDoorStatus
                } else {
                    bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.READ_RESULT_FAILED
                }
            } else {
                bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.COMMAND_WRITE_FAILED
            }
        } else {
            bleDeviceErrorCode = BLEDoorOpenResult.BLEDeviceErrorCode.ENCRYPTION_FAILED
        }

        val result = BLEDoorOpenResult.create(bleDeviceErrorCode, bleSlaveErrorCode)
        log.info("OpenDoorResult: $result")
        return result
    }

    suspend fun invalidateAllKeysOnMasterUnit (): Boolean {
        val data = byteArrayOf(0x01.toByte())
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {
            if (streaming.writeArray(cmdInvalidateAllKeysOnSPLPLUS, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }




}