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
import hr.sil.android.myappbox.core.ble.comm.model.MPLGenericCommand
import hr.sil.android.myappbox.core.model.MPLDeviceType
import hr.sil.android.myappbox.core.remote.WSAdmin
import hr.sil.android.myappbox.core.remote.model.RLockerSize
import hr.sil.android.myappbox.core.remote.model.RNetworkConfiguration
import hr.sil.android.myappbox.core.util.BLEScannerStateHolder
import hr.sil.android.myappbox.core.util.macRealToBytes
import hr.sil.android.util.general.extensions.toByteArray
import java.nio.charset.StandardCharsets

/**
 * @author mfatiga
 */
class MPLAdminBLECommunicator(
        ctx: Context,
        deviceAddress: String,
        bleScannerStateHolder: BLEScannerStateHolder
) : BaseMPLCommunicator(
        ctx,
        deviceAddress,
        bleScannerStateHolder,
        WSAdmin
) {
    companion object {
        private val cmdCfgRegisterMaster = StreamingCommand(0x04, 0xF0)
        private val cmdCfgNetApnUrl = StreamingCommand(0x04, 0xF1)
        private val cmdCfgNetApnUser = StreamingCommand(0x04, 0xF2)
        private val cmdCfgNetApnPass = StreamingCommand(0x04, 0xF3)
        private val cmdCfgNetSimPin = StreamingCommand(0x04, 0xF4)
        private val cmdCfgNetBackendUrl = StreamingCommand(0x04, 0xF5)
        private val cmdCfgNetBackendApiKey = StreamingCommand(0x04, 0xF6)
        private val cmdCfgNetEnableRadioAccessTech = StreamingCommand(0x04, 0xF7)

        private val cmdRegisterSlave = StreamingCommand(0x04, 0x01)
        private val cmdDeregisterSlave = StreamingCommand(0x04, 0x02)
    }

    // core access
    private suspend fun writeEncryptData(cmd: StreamingCommand, data: ByteArray): Boolean {
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {
            if (streaming.writeArray(cmd, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }

    private suspend fun writeNetApnUrl(apnUrl: String): Boolean {
        return writeEncryptData(cmdCfgNetApnUrl, apnUrl.toByteArray(StandardCharsets.US_ASCII))
    }

    private suspend fun writeNetApnUser(apnUser: String): Boolean {
        return writeEncryptData(cmdCfgNetApnUser, apnUser.toByteArray(StandardCharsets.US_ASCII))
    }

    private suspend fun writeNetApnPass(apnPass: String): Boolean {
        return writeEncryptData(cmdCfgNetApnPass, apnPass.toByteArray(StandardCharsets.US_ASCII))
    }

    private suspend fun writeNetSimPin(simPin: String): Boolean {
        return writeEncryptData(cmdCfgNetSimPin, simPin.toByteArray(StandardCharsets.US_ASCII))
    }

    private suspend fun writeNetBackendUrl(backendUrl: String): Boolean {
        return writeEncryptData(cmdCfgNetBackendUrl, backendUrl.toByteArray(StandardCharsets.US_ASCII))
    }

    private suspend fun writeNetRadioAccessTechnology(enableHttps: Int): Boolean {
        return writeEncryptData(cmdCfgNetEnableRadioAccessTech, (enableHttps).toByteArray(1))
    }

    private suspend fun writeGlobalConfiguration(networkConfiguration: RNetworkConfiguration, type: MPLDeviceType): Boolean {
        val configuration = WSAdmin.getGlobalConfigurationData() ?: return false

        val backendBaseUrl = if (type == MPLDeviceType.TABLET) configuration.backendBaseUrl else configuration.backendBaseCoapUrl
        val backendRadioAccessTechnology = configuration.backendRadioAccessTechnology

        if (backendBaseUrl != null) {
            log.info("Backend base url: {$backendBaseUrl}")
            if (!writeNetBackendUrl(backendBaseUrl)) return false
        }

        if (backendRadioAccessTechnology != null) if (!writeNetRadioAccessTechnology(networkConfiguration.modemRadioAccess.type)) return false

        return true
    }

    private suspend fun writeNetBackendApiKey(): Boolean {
        val challenge = readChallenge()
        val encrypted = if (challenge != null) WSAdmin.getDeviceApiKey(challenge, deviceAddress) else null
        return if (encrypted != null) {
            if (streaming.writeArray(cmdCfgNetBackendApiKey, encrypted).status) {
                streaming.writeEmpty()
                true
            } else false
        } else {
            false
        }
    }

    private suspend fun writeMasterRegistration(customerId: Int): Boolean {
        val data = customerId.toByteArray(4)
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {
            if (streaming.writeArray(cmdCfgRegisterMaster, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }

    suspend fun writeNetworkConfiguration(networkConfiguration: RNetworkConfiguration, simPin: String?): Boolean {
        val apnUrl = networkConfiguration.apnUrl
        val apnUser = networkConfiguration.apnUser
        val apnPass = networkConfiguration.apnPass

        if (apnUrl != null) if (!writeNetApnUrl(apnUrl)) return false
        if (apnUser != null) if (!writeNetApnUser(apnUser)) return false
        if (apnPass != null) if (!writeNetApnPass(apnPass)) return false
        if (simPin != null) if (!writeNetSimPin(simPin)) return false
        log.info("Network configuration ${networkConfiguration.modemRadioAccess.type}")
        writeNetRadioAccessTechnology(networkConfiguration.modemRadioAccess.type)

        return true
    }

    suspend fun registerMaster(customerId: Int, networkConfiguration: RNetworkConfiguration, simPin: String?, type: MPLDeviceType): Boolean {
        //reset registration
        if (!writeMasterRegistration(0)) return false

        //write network configuration
        if (!writeNetworkConfiguration(networkConfiguration, simPin)) return false

        //write global configuration
        if (!writeGlobalConfiguration(networkConfiguration, type)) return false

        //write api-key
        if (!writeNetBackendApiKey()) return false

        //run registration
        if (!writeMasterRegistration(customerId)) return false

        //success
        return true
    }

    suspend fun registerSlave(slaveMacAddress: String, size: RLockerSize): Boolean {
        val sizeCode = size.code ?: return false
        val data = slaveMacAddress.macRealToBytes().reversedArray() + byteArrayOf(sizeCode)
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {
            if (streaming.writeArray(cmdRegisterSlave, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }

    suspend fun deregisterSlave(slaveMacAddress: String): Boolean {
        val data = slaveMacAddress.macRealToBytes().reversedArray()
        val encrypted = wrapEncryptData(data)
        if (encrypted != null) {
            if (streaming.writeArray(cmdDeregisterSlave, encrypted).status) {
                streaming.writeEmpty()
                return true
            }
        }
        return false
    }

    suspend fun updateEpaper(forceDownload: Boolean): Boolean {
        val forceDownloadByte = if (forceDownload) 0x01.toByte() else 0x00.toByte()
        return sendGenericCommand(MPLGenericCommand.UPDATE_EPAPER, byteArrayOf(forceDownloadByte))
    }

    suspend fun forceOpenDoor(slaveMacAddress: String): Boolean {
        val params = slaveMacAddress.macRealToBytes().reversedArray()
        return sendGenericCommand(MPLGenericCommand.FORCE_OPEN_DOOR, params)
    }
}
