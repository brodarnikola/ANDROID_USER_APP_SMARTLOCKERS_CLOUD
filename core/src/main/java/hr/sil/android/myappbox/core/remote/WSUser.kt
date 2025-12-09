package hr.sil.android.myappbox.core.remote

import hr.sil.android.myappbox.core.remote.base.WSBase
import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.myappbox.core.remote.service.UserAppService
import hr.sil.android.rest.core.configuration.ServiceConfig
import hr.sil.android.myappbox.core.remote.service.UserPublicService
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.smartlockers.enduser.core.remote.model.RInstallationKey
import hr.sil.android.myappbox.core.remote.model.AccessRequestResponse
import hr.sil.android.myappbox.core.remote.model.RNotificationsEventsUser
import retrofit2.Call
import retrofit2.Response

/**
 * @author mfatiga
 */
object WSUser : WSBase() {

    suspend fun registerDevice(pushToken: String?, metaData: String = ""): Boolean {
        log.info("Push token: ${pushToken}")
        if (pushToken != null) {
            log.info("App key: ${ServiceConfig.cfg.appKey}")
            val request = RUserDeviceInfo().apply {
                this.appKey = ServiceConfig.cfg.appKey
                this.token = pushToken
                this.type = RUserDeviceType.ANDROID
                this.metadata = metaData
            }
            return wrapAwaitIsSuccessful(
                    call = UserPublicService.service.registerDevice(request),
                    methodName = "registerDevice()"
            )
        } else {
            return false
        }
    }

    suspend fun getActiveRequestsNew(): List<RAccessNewResponse>? {
        return wrapAwaitData(
            call = UserAppService.service.getActiveAccessRequestsNew(),
            methodName = "getActiveRequestsNew()"
        )
    }

    suspend fun getAccessDetails(): List<RAccessDetaislResponse>? {
        return wrapAwaitData(
            call = UserAppService.service.getAccessDetails(),
            methodName = "getAccessDetails()"
        )
    }

    suspend fun createPickAtHomeForLinuxDevices(pickAtHomeRequest: RPickAtHomeRequest): RPickAtHomeResponse? {
        return wrapAwaitData(
            call = UserAppService.service.createPickAtHomeForLinuxDevices(pickAtHomeRequest),
            methodName = "createPickAtHomeForLinuxDevices()"
        )
    }

    suspend fun cancelPickAtHomeLinuxDevices(lockerMac: String): RPickupLinuxResponse? {
        return wrapAwaitData(
            call = UserAppService.service.cancelPickAtHomeLinuxDevices(lockerMac),
            methodName = "cancelPickAtHomeLinuxDevices()"
        )
    }

    suspend fun pickupCollectKeysForLinuxDevices(pickAtHomeRequest: RPickupLinuxRequest): RPickupLinuxResponse? {
        return wrapAwaitData(
            call = UserAppService.service.pickupCollectKeysForLinuxDevices(pickAtHomeRequest),
            methodName = "pickupCollectKeysForLinuxDevices()"
        )
    }

    suspend fun forceOpenLinuxDevice(lockerMac: String): Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.forceOpenLinuxDevice(lockerMac),
            methodName = "forceOpenLinuxDevice()"
        )
    }

    suspend fun cleaningNeededForLinuxLocker(lockerMac: String): Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.cleaningNeededForLinuxLocker(lockerMac),
            methodName = "cleaningNeededForLinuxLocker()"
        )
    }

    suspend fun getGeneratedPinFromBackendForSendParcel(masterId: Int): String? {
        return wrapAwaitData(
                call = UserAppService.service.getGeneratedPinFromBackendForSendParcel(masterId),
                methodName = "getGeneratedPinFromBackendForSendParcel()"

        )
    }

    suspend fun getLanguages(): List<RLanguage>? {
        return wrapAwaitData(
                call = UserPublicService.service.getLanguages(),
                methodName = "getLanguages()",
                defaultNullValue = listOf()
        )
    }

    suspend fun getDeviceInfo(macAddress: String): RLockerInfo? {
        return wrapAwaitData(
                call = UserAppService.service.getLockerInfo(macAddress.macRealToClean()),
                methodName = "getDeviceInfo()"
        )
    }

    suspend fun getDevicesInfo(macAddress: List<String>): List<RLockerInfo>? {
        log.info("Addresses for info: " + macAddress.joinToString(",") { it })
        return wrapAwaitData(
                call = UserAppService.service.getLockersInfo(macAddress),
                methodName = "getDevicesInfo()"
        )
    }


    suspend fun registerEndUser(
            name: String,
            address: String,
            telephone: String,
            email: String,
            password: String,
            language: RLanguage,
            groupName: String,
            installationKey: String): REndUserInfo? {

        val request = REndUserRegisterRequest().apply {
            this.name = name
            this.address = address
            this.telephone = telephone
            this.email = email
            this.password = password
            this.languageId = language.id
            this.hasAcceptedTerms = true
            this.groupName = groupName
            this.inviteCode= installationKey
        }
        return wrapAwaitData(
                call = UserPublicService.service.registerEndUser(request),
                methodName = "registerEndUser()"
        )
    }

    suspend fun registerEndUserCPL(
        name: String,
        lastName: String,
        email: String,
        password: String,
        telephone: String,
        street: String,
        houseNumber: String,
        postCode: String,
        town: String,
        reducedMobility: Boolean,
        language: Int,
        installationKey: String ): Response<REndUserInfo>? {

        val request = REndUserRegisterRequestCPL().apply {
            this.name = name
            this.lastName = lastName
            this.email = email
            this.password = password
            this.phone = telephone
            this.street = street
            this.houseNumber = houseNumber
            this.postcode = postCode
            this.town = town
            this.reducedMobility = reducedMobility
            this.languageId = language
            //this.inviteCode= installationKey
        }
        return wrapAwaitDataAndHttpStatus(
            call = UserPublicService.service.registerEndUserCPL(request),
            methodName = "registerEndUser()"
        )
    }

    suspend fun requestPasswordRecovery(email: String): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserPublicService.service.requestPasswordRecovery(email),
                methodName = "requestPasswordRecovery()"
        )
    }

    suspend fun resetPassword(email: String, passwordCode: String, password: String): Boolean {
        val request = RResetPasswordRequest().apply {
            this.email = email
            this.passwordCode = passwordCode
            this.password = password
        }
        return wrapAwaitIsSuccessful(
                call = UserPublicService.service.resetPassword(request),
                methodName = "resetPassword()"
        )
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String): Boolean {
        val request = RUpdatePasswordRequest().apply {
            this.oldPassword = oldPassword
            this.newPassword = newPassword
        }
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.updatePassword(request),
                methodName = "updatePassword()"
        )
    }

    suspend fun checkInsertedAddress(name: String, lastName: String, street: String, houseNumber: String, postcode: String, town: String): RAddressCheckResponse? {
        val request = RCheckInsertedAddress().apply {
            this.name = name
            this.lastName = lastName
            this.street = street
            this.houseNumber = houseNumber
            this.postcode = postcode
            this.town = town
        }
        return wrapAwaitData(
            call = UserPublicService.service.checkInsertedAddress(request),
            methodName = "checkInsertedAddress()"
        )
    }

    suspend fun resendEmailForConfirmation(userHashKey: String): Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.resendEmailForConfirmation(userHashKey),
            methodName = "resendEmailForConfirmation()"
        )
    }

    suspend fun updateUserGroupName(name: String): REndUserGroupInfo? {

        val request = RUpdateUserGroupRequest().apply {
            this.name = name
        }
        return wrapAwaitData(
                call = UserAppService.service.updateUserGroup(request),
                methodName = "updateUserGroupName()"
        )
    }

    suspend fun acceptedTerms(): Boolean {

        return wrapAwaitIsSuccessful(
            call = UserAppService.service.acceptedTerms(),
            methodName = "acceptedTerms()"
        )
    }

    suspend fun updateUserProfile(
            name: String,
            address: String,
            telephone: String,
            language: RLanguage,
            isPushNotified: Boolean,
            isEmailNotified: Boolean,
            groupName: String

    ): REndUserInfo? {
        val request = RUpdateUserProfileRequest().apply {
            this.name = name
            this.address = address
            this.telephone = telephone
            this.languageId = language.id
            this.isNotifyPush = isPushNotified
            this.isNotifyEmail = isEmailNotified
            this.groupName = groupName
        }
        return wrapAwaitData(
                call = UserAppService.service.updateUserProfile(request),
                methodName = "updateUserProfile()"
        )
    }

    suspend fun updateUserProfileCPLBasel(
        telephone: String,
        reducedMobility: Boolean

    ): REndUserInfo? {
        val request = RUpdateUserProfileRequestCPLBasel().apply {
            this.telephone = telephone
            this.reducedMobility = reducedMobility
        }
        return wrapAwaitData(
            call = UserAppService.service.updateUserProfileCPLBasel(request),
            methodName = "updateUserProfile()"
        )
    }

    suspend fun updateUserProfileInvited(
        name: String,
        address: String,
        telephone: String,
        languageId: Int,
        isPushNotified: Boolean,
        isEmailNotified: Boolean,
        groupName: String,
        password: String

    ): REndUserInfo? {
        val request = RUpdateUserProfileRequestInvited().apply {
            this.name = name
            this.address = address
            this.telephone = telephone
            this.languageId = languageId
            this.isNotifyPush = isPushNotified
            this.isNotifyEmail = isEmailNotified
            this.groupName = groupName
            this.password = password
        }
        return wrapAwaitData(
            call = UserAppService.service.updateUserProfileInvited(request),
            methodName = "updateUserProfileInvited()"
        )
    }

    suspend fun getUserInfo(): REndUserInfo? {
        return wrapAwaitData(
                call = UserAppService.service.getUserInfo(),
                methodName = "getUserInfo()"
        )
    }

    suspend fun getUserGroupInfo(): REndUserGroupInfo? {
        return wrapAwaitData(
                call = UserAppService.service.getGroupInfo(),
                methodName = "getUserGroupInfo()"
        )
    }

    suspend fun getMasterUnits(): List<RMasterUnit>? {
        return wrapAwaitData(
                call = UserAppService.service.getMasterUnits(),
                methodName = "getMasterUnits()",
                defaultNullValue = listOf()
        )
    }

    suspend fun getGroupMembers(): List<REndUserGroupMember>? {
        return wrapAwaitData(
                call = UserAppService.service.getGroupMembers(),
                methodName = "getGroupMembers()",
                defaultNullValue = listOf<REndUserGroupMember>()
        )
    }

    suspend fun getGroupMemberships(): List<RGroupInfo>? {
        return wrapAwaitData(
                call = UserAppService.service.getGroupMemberships(),
                methodName = "getGroupMemberships()",
                defaultNullValue = listOf<RGroupInfo>()
        )
    }


    suspend fun getGroupMembershipsById(groupId: Long): MutableList<RGroupInfo>? {
        return wrapAwaitData(
                call = UserAppService.service.getGroupMembershipsById(groupId),
                methodName = "getGroupMembershipsById()",
                defaultNullValue = mutableListOf<RGroupInfo>()
        )
    }

    suspend fun getActiveKeys(): List<RLockerKey>? {
        return wrapAwaitData(
                call = UserAppService.service.getActiveKeys(),
                methodName = "getActiveKeys()"
        )
    }

    suspend fun getActivePaFCreatedKeys(): List<RCreatedLockerKey>? {
        return wrapAwaitData(
                call = UserAppService.service.getActivePaFCreatedKeys(),
                methodName = "getActivePaFCreatedKeys()"
        )
    }

    suspend fun getActivePaHCreatedKeys(): MutableList<RCreatedLockerKey>? {
        return wrapAwaitData(
                call = UserAppService.service.getActivePaHCreatedKeys(),
                methodName = "getActivePaHCreatedKeys()"
        )
    }

    suspend fun getNotificationsEventsList(page: Int, pageSize: Int): RNotificationsEventsUser? {
        return wrapAwaitData(
            call = UserAppService.service.getEventsNotificationsUser(page, pageSize),
            methodName = "getNotificationsEventsUserList()"
        )
    }

    suspend fun sendLockersNeedsCleaningInformation(): Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.sendLockersNeedsCleaningInformation(),
            methodName = "sendLockersNeedsCleaningInformation()"
        )
    }

    suspend fun getVendorDeliveryKeys(): MutableList<RCreatedLockerKey>? {
        return wrapAwaitData(
            call = UserAppService.service.getVendorDeliveryKeys(),
            methodName = "getVendorDeliveryKeys()"
        )
    }


    suspend fun getEmailListSuggestion(masterMacAddress: String): Response<List<String>>? {
        return wrapAwaitDataAndHttpStatus(
            call = UserAppService.service.getEmailListSuggestion(masterMacAddress),
            methodName = "getEmailListSuggestion()"
        )
    }

    suspend fun getAvailableLockerSizes(masterUnitId: Int): List<RAvailableLockerSize>? {
        return wrapAwaitData(
                call = UserAppService.service.getAvailableLockerSizes(masterUnitId),
                methodName = "getAvailableLockerSizes()",
                defaultNullValue = listOf()
        )
    }

    suspend fun requestMPlAccess(macAddress: String): AccessRequestResponse? {
        return wrapAwaitData(
                call = UserAppService.service.requestAccess(macAddress),
                methodName = "requestAccess()"
        )
    }

    suspend fun getActiveRequests(): List<RAccessRequest>? {
        return wrapAwaitData(
                call = UserAppService.service.getActiveAccessRequests(),
                methodName = "getActiveRequestsNew()"
        )
    }

    suspend fun getGroupIdForDeliveryRetail(mailAddress: String, name: String, languageId: Int): Int? {
        val vendorDeliveryPrepare = RGroupIdDeliveryRetail().apply {
            this.email = mailAddress
            this.name = name
            this.inviteNotifLanguageId = languageId
        }
        return wrapAwaitData(
            call = UserAppService.service.getGroupIdForDeliveryRetail(vendorDeliveryPrepare),
            methodName = "getGroupIdForDeliveryRetail()"
        )
    }

    suspend fun getGeneratedPinForSendParcel(masterId: Int): String? {
        return wrapAwaitData(
            call = UserAppService.service.getGeneratedPinFromBackendForSendParcel(masterId),
            methodName = "getGeneratedPinForSendParcel()"
        )
    }

    suspend fun getPinManagementForSendParcel(groupId: Int, masterId: Int?): List<RPinManagementResponse>? {
        return wrapAwaitData(
            call = UserAppService.service.getPinManagementForSendParcel(groupId, masterId),
            methodName = "getPinManagementForSendParcel()"
        )
    }

    suspend fun savePinManagementForSendParcel(savePin: RPinManagementSavePin): RPinManagementResponse? {
        return wrapAwaitData(
            call = UserAppService.service.savePinManagementForSendParcel(savePin),
            methodName = "savePinManagementForSendParcel()"
        )
    }

    suspend fun deletePinForSendParcel(pinId: Int): Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.deletePinForSendParcel(pinId),
            methodName = "savePinManagementForSendParcel()"
        )
    }

    suspend fun activateSPL(macAddress: String): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.activateSpl(macAddress),
                methodName = "activateSPL()"
        )
    }

    suspend fun deactivateSPL(macAddress: String): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.deactivateSpl(macAddress),
                methodName = "deactivateSPL()"
        )
    }


    suspend fun addUserAccess(userAccess: RUserAccess): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.addUserAccess(userAccess),
                methodName = "addUserAccess()"
        )
    }

    suspend fun removeUserAccess(userAccess: RUserRemoveAccess): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.removeUserAccess(userAccess),
                methodName = "addUserAccess()"
        )
    }

    suspend fun createPaF(keyId: Int, email: String) : RInstallationKey? {
        val pafKey = RCreatePaf().apply {
            this.keyId = keyId
            this.email = email
        }
        return wrapAwaitData(
                call = UserAppService.service.createPaF(pafKey),
                methodName = "createPaF()"
        )

    }

    suspend fun deletePaF(keyId: Int): Boolean {
        val pafKey = RDeletePaf().apply {
            this.keyId = keyId
        }
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.deletePaF(pafKey),
                methodName = "deletePaF()"
        )

    }

    suspend fun modifyMasterUnit(unit: RMasterUnit): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.modifySpl(masterUnit = unit),
                methodName = "modifyMasterUnit()"
        )

    }

    suspend fun sendPinRegistrationCode( pinRegistrationCode : String ) : ResponsePinRegistrationCode? {
        val pin = RPinRegistrationCodeSend().apply {
            this.code = pinRegistrationCode
        }
        return wrapAwaitData(
            call = UserAppService.service.sendPinRegistrationCode(pin),
            methodName = "sendPinRegistrationCode()"
        )
    }

    suspend fun sendNewPinRegistrationCode( ) : Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.sendNewPinRegistrationCode(),
            methodName = "sendNewPinRegistrationCode()"
        )
    }

    suspend fun getPinRegistrationCodeInfo( ): Response<RPinRegistrationCodeInfo>? {
        return wrapAwaitDataAndHttpStatus(
            call = UserAppService.service.getPinRegistrationCodeInfo(),
            methodName = "getPinRegistrationCodeInfo()"
        )
    }

    suspend fun getTwoFactoryAuthForRegisterinCPLUsers( ) : RTwoFactoryAuth? {
        return wrapAwaitData(
            call = UserPublicService.service.getTwoFactoryAuth(),
            methodName = "getTwoFactoryAuthForRegisterinCPLUsers()"
        )
    }

    suspend fun showMailCodeInput( ) : Boolean {
        return wrapAwaitIsSuccessful(
            call = UserAppService.service.getShowMailCodeInput(),
            methodName = "showMailCodeInput()"
        )
    }

    suspend fun ping(): Boolean {
        return wrapAwaitIsSuccessful(
                call = UserAppService.service.ping(),
                methodName = "serverPing()"
        )
    }


    override fun callEncryptService(mac: String, request: REncryptRequest): Call<REncryptResponse> {
        return UserAppService.service.encrypt(mac, request)
    }
}