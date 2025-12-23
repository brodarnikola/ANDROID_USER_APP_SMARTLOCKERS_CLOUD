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

package hr.sil.android.myappbox.core.remote

import android.util.Base64
import hr.sil.android.myappbox.core.model.RUpdateAdminInfo
import hr.sil.android.myappbox.core.remote.base.WSBase
import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.myappbox.core.remote.service.AdminAppService
import hr.sil.android.myappbox.core.remote.service.WebAppService
import hr.sil.android.rest.core.configuration.ServiceConfig
import hr.sil.android.myappbox.core.util.macRealToClean
//import hr.sil.android.util.general.extensions.toHexString
import retrofit2.Call

import hr.sil.android.rest.core.util.toHexString

/**
 * @author mfatiga
 */
object WSAdmin : WSBase() {


    suspend fun registerDevice(pushToken: String?, metaData: String = ""): Boolean {
        if (pushToken != null) {
            log.info("AppKey: " + ServiceConfig.cfg.appKey)
            val request = RUserDeviceInfo().apply {
                this.appKey = ServiceConfig.cfg.appKey
                this.token = pushToken
                this.type = RUserDeviceType.ANDROID
                this.metadata = metaData
            }
            return WSAdmin.wrapAwaitIsSuccessful(
                    call = AdminAppService.service.registerDevice(request),
                    methodName = "registerDevice()"
            )
        } else {
            return false
        }
    }

    suspend fun login(): RAdminUserInfo? {
        return wrapAwaitData(
                call = AdminAppService.service.login(),
                methodName = "login()"
        )
    }

    suspend fun getAccountInfo(): RAdminUserInfo? {
        return wrapAwaitData(
                call = AdminAppService.service.getAccountInfo(),
                methodName = "getAccountInfo()"
        )
    }

    suspend fun modifyMasterUnit(mac: String, request: RMasterUnitRequest): RMasterUnit? {
        return wrapAwaitData(
                call = AdminAppService.service.modifyMaster(mac, request),
                methodName = "modifyMaster()"
        )
    }


    suspend fun getGlobalConfigurationData(): RGlobalConfigurationData? {
        return wrapAwaitData(
                call = AdminAppService.service.getGlobalConfigurationData(),
                methodName = "getGlobalConfigurationData()"
        )
    }

    
    suspend fun getDeviceApiKey(challenge: ByteArray, masterBleMacAddress: String): ByteArray? {
        val result = wrapAwaitData(
                call = AdminAppService.service.getDeviceApiKey(
                        masterBleMacAddress.macRealToClean(),
                        challenge.toHexString()),
                methodName = "getDeviceApiKey()"
        )

        val b64 = result?.data
        return if (b64 != null) {
            Base64.decode(b64, Base64.DEFAULT)
        } else null
    }

    suspend fun getLockers(masterBleMacAddress: String): List<RLockerUnit>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getLockerForMaster(masterBleMacAddress.macRealToClean()),
                methodName = "getLockerForMaster()"
        )
    }

    suspend fun getLockerDetails(lockerUnitMac: String): RLockerUnit? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getLockerDetails(lockerUnitMac.macRealToClean()),
                methodName = "getLockerDetails()"
        )
    }

    suspend fun getLockerMacAddresses(): List<String>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getLockerMacAddresses(),
                methodName = "getLockerMacAddresses()"
        )
    }

    suspend fun getMasterUnits(): List<RMasterUnit>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getMasterUnits(),
                methodName = "getMasterUnits()",
                defaultNullValue = listOf()
        )
    }

    override fun callEncryptService(mac: String, request: REncryptRequest): Call<REncryptResponse> {
        return AdminAppService.service.encrypt(mac, request)
    }

    suspend fun getLanguages(): RWebLanguage? {
        return WSAdmin.wrapAwaitData(
                call = WebAppService.service.getLanguages(RPagination()),
                methodName = "getLanguages()"
        )
    }

    suspend fun getMasterAccessRequests(): List<RMasterUnitAccessRequests>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getMasterAccessList(),
                methodName = "getMasterAccessList()"
        )
    }


    suspend fun getAssignedGroupsToEpaper(mac: String): List<RAssignedGroup>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getAssignedGroupsToEpaper(mac.macRealToClean()),
                methodName = "getAvailableEPaperPlaces()"
        )
    }

    suspend fun grantAccessToMaster(accessRequestId: Int, index: Int): Boolean {
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.grantAccessToMaster(accessRequestId, index),
                methodName = "grantAccessToMaster()"
        )
    }

    suspend fun assignGroupToEpaper(mac: String, index: Int, groupId: Int): Boolean {
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.assignGroupToEpaper(mac, index, groupId),
                methodName = "assignGroupToEpaper()"
        )
    }

    suspend fun unAssignMasterFromEpaper(mac: String, index: Int): Boolean {
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.unAssignMasterFromEpaper(mac, index),
                methodName = "unAssignMasterFromEpaper()"
        )
    }

    suspend fun rejectAccessToMaster(accessRequestId: Int): Boolean {
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.rejectAccessToMaster(accessRequestId),
                methodName = "rejectAccessToMaster()"
        )
    }

    suspend fun getNetworkConfigurations(): List<RNetworkConfiguration>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getNetworkConfigurations(),
                methodName = "getNetworkConfigurations()"
        )
    }


    suspend fun updateUserProfile(
            user: RUpdateAdminInfo
           ): RAdminUserInfo? {


        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.updateUserProfile(user),
                methodName = "updateUserProfile()"
        )
    }
    suspend fun requestPasswordRecovery(email: String): Boolean {
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.requestPasswordRecovery(email),
                methodName = "requestPasswordRecovery()"
        )
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String): Boolean {
        val request = RUpdatePasswordRequest().apply {
            this.oldPassword = oldPassword
            this.newPassword = newPassword
        }
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.updatePassword(request),
                methodName = "updatePassword()"
        )
    }

    suspend fun resetPassword(email: String, passwordCode: String, password: String): Boolean {
        val request = RResetPasswordRequest().apply {
            this.email = email
            this.passwordCode = passwordCode
            this.password = password
        }
        return WSAdmin.wrapAwaitIsSuccessful(
                call = AdminAppService.service.resetPassword(request),
                methodName = "resetPassword()"
        )
    }


    suspend fun getMessageLog(): List<RMessageLog>? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.getMessageLog(),
                methodName = "getMessageLog()"
        )
    }

    suspend fun deleteMessageItem(itemId: Int): Void? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.deleteMessageItem(itemId),
                methodName = "v()"
        )
    }

    suspend fun deleteAll(): Void? {
        return WSAdmin.wrapAwaitData(
                call = AdminAppService.service.deleteAllMessages(),
                methodName = "deleteAllMessages()"
        )
    }

}