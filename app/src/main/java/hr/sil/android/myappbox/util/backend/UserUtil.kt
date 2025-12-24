/* SWISS INNOVATION LAB CONFIDENTIAL
*
* www.swissinnolab.com
* __________________________________________________________________________
*
* [2016] - [2017] Swiss Innovation Lab AG
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

package hr.sil.android.myappbox.util.backend

//import com.esotericsoftware.minlog.Log
import com.google.firebase.messaging.FirebaseMessaging
import hr.sil.android.myappbox.App
import hr.sil.android.rest.core.util.UserHashUtil
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.myappbox.core.util.DeviceInfo
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.preferences.PreferenceStore
import hr.sil.android.myappbox.remote.WSConfig
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.AppUtil
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.awaitForResult
import hr.sil.android.myappbox.util.connectivity.NetworkChecker
import kotlinx.coroutines.delay
import retrofit2.Response

/**
 * @author mfatiga
 */
object UserUtil {
    private val log = logger()
    fun isUserLoggedIn() = (user != null)

    var userGroup: REndUserGroupInfo? = null
        private set

    var userMemberships: List <RGroupInfo>  = listOf()

    var user: REndUserInfo? = null

    var pahKeys: MutableList<RCreatedLockerKey>  = mutableListOf()

    var twoFactoryAuth: RTwoFactoryAuth? = RTwoFactoryAuth()

    var showMailCodeInput: Boolean? = false

    public suspend fun updateUserHash(username: String?, password: String?) {
        if (username != null && password != null && username.isNotEmpty() && password.isNotEmpty()) {
            PreferenceStore.userHash = UserHashUtil.createUserHash(username, password)
        } else {
            PreferenceStore.userHash = ""
        }
        WSConfig.updateAuthorizationKeys()
    }

    suspend fun login(username: String, password: String): Boolean {
        updateUserHash(username, password)
        return login(username)
    }

    suspend fun register(name: String, address: String, phoneNumber: String, email: String, password: String, groupName: String): Boolean {

        WSUser.registerDevice(fcmTokenRequest(), DeviceInfo.getJsonInstance())

        val langList = WSUser.getLanguages()
        val language = langList?.find { it.code == "EN" }
        var user: REndUserInfo? = null
        try {
            var key = ""
            //val installationKeys = InstallationKeyHandler.key.getAll()
            //if (!installationKeys.isNullOrEmpty()) key = installationKeys.first().key
            log.info("Getting ref key from registration $key")
            //log.info("Size from installation keys ${installationKeys.size}")
            if (language != null) {
                user = WSUser.registerEndUser(name, address, phoneNumber, email, password, language, groupName, key)
                updateUserHash(user?.email, password)
                //InstallationKeyHandler.key.clear()
                return user != null && login(user.email, password)
            } else {
                log.error("No language found -> Language list size = ${langList?.size}")
            }

            return false
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun checkInsertedAddress(name: String, lastName: String, street: String, houseNumber: String, postcode: String, town: String): RAddressCheckResponse {
        val isInsertedAddressCorret = WSUser.checkInsertedAddress(name, lastName, street, houseNumber, postcode, town) ?: RAddressCheckResponse.NOT_AVAILABLE

        if (isInsertedAddressCorret == RAddressCheckResponse.INVALID) {
            log.error("Error, wrong address ( town, street, houseNumber or postcode) inserted.")
        } else if(isInsertedAddressCorret == RAddressCheckResponse.NOT_AVAILABLE) {
            log.error("Error, not available at the moment.")
        }
        else {
            log.info("Address is valid.")
        }
        return isInsertedAddressCorret
    }

    suspend fun resendEmailForConfirmation(email: String, password: String): Boolean {
        updateUserHash(email, password)
        delay(1000)
        val emailExist = WSUser.resendEmailForConfirmation(PreferenceStore.userHash ?: "")

        if (!emailExist) {
            log.error("Error, email confirmation did not sended once more")
            updateUserHash(null, null)
            return false
        } else {
            updateUserHash(null, null)
            return true
        }
    }

    suspend fun registerCPLUser(name: String, lastName: String, email: String, password: String, phoneNumber: String, street: String, houseNumber: String, postCode: String, town: String, reducedMobility: Boolean, languageId: Int): Response<REndUserInfo>? {

        WSUser.registerDevice(fcmTokenRequest(), DeviceInfo.getJsonInstance())

        val langList = WSUser.getLanguages()
        val language = langList?.find { it.code == "EN" }
        var user: Response<REndUserInfo>? = null
        try {
            var key = ""

            log.info("Getting ref key from registration $key")
            if (language != null) {
                user = WSUser.registerEndUserCPL(name, lastName, email, password, phoneNumber, street, houseNumber, postCode, town, reducedMobility, languageId, key)
                updateUserHash(null, null)
                return user
            } else {
                log.error("No language found -> Language list size = ${langList?.size}")
            }
            return user
        } catch (e: Exception) {
            return user
        }
    }

    suspend fun loginCheckUserStatus(username: String, password: String): UserStatus {

        WSUser.registerDevice(fcmTokenRequest(), DeviceInfo.getJsonInstance())
        updateUserHash(username, password)
        twoFactoryAuth = WSUser.getTwoFactoryAuthForRegisterinCPLUsers()
        log.info("Two factory authentification is enabled: ${twoFactoryAuth?.twoFactorAuth}")
        delay(1000)
        if (!PreferenceStore.userHash.isNullOrBlank()) {
            val responseUser = WSUser.getUserInfo()
            val group = WSUser.getUserGroupInfo()
            //userMemberships = WSUser.getGroupMemberships() ?: listOf()

            if (responseUser != null && ( responseUser.status == UserStatus.ACTIVE.toString() || responseUser.status == UserStatus.PENDING_VERIFICATION.toString() ) ) {

                if( App.Companion.ref.resources.getBoolean(R.bool.has_email_verification) && responseUser.emailCode != null  ) {

                    user = responseUser
                    updateUserHash(null, null)
                    return UserStatus.ACTIVE
                }
                else {
                    user = responseUser

                    if (group != null) {
                        userGroup = group
                    }
                    //invalidate caches on login
                    AppUtil.refreshCache()

                    log.info("User is logged in updating device and token...")

                    val languagesList = WSUser.getLanguages()
                    val languageData = languagesList?.find { it.id == responseUser.languageId }

                    if (languageData != null) {
                        SettingsHelper.languageName = languageData.code
                    }
                    SettingsHelper.pushEnabled = responseUser.isNotifyPush
                    SettingsHelper.emailEnabled = responseUser.isNotifyEmail
                    SettingsHelper.usernameLogin = username

                    val result =
                        WSUser.registerDevice(fcmTokenRequest(), DeviceInfo.getJsonInstance())

                    return UserStatus.ACTIVE
                }
            } else if (responseUser != null && responseUser.status == UserStatus.INVITED.toString()) {
                user = responseUser
                if (group != null) {
                    userGroup = group
                }
                return UserStatus.INVITED
            } else {
                updateUserHash(null, null)
                user = null
                return UserStatus.NOT_LOGGED_IN
            }
        } else {
            updateUserHash(null, null)
            user = null
            return UserStatus.NOT_LOGGED_IN
        }
    }

    suspend fun login(username: String): Boolean {
        twoFactoryAuth = WSUser.getTwoFactoryAuthForRegisterinCPLUsers()
        log.info("Two factory authentification is enabled: ${twoFactoryAuth?.twoFactorAuth}")
        return if (!PreferenceStore.userHash.isNullOrBlank()) {
            val responseUser = WSUser.getUserInfo()
            val group = WSUser.getUserGroupInfo()
            //userMemberships = WSUser.getGroupMemberships()?: listOf()

            if (responseUser != null) {

                user = responseUser

                SettingsHelper.pushEnabled = responseUser.isNotifyPush
                SettingsHelper.emailEnabled = responseUser.isNotifyEmail
                SettingsHelper.usernameLogin = username

                if (group != null) {
                    userGroup = group
                }
                //invalidate caches on login
                AppUtil.refreshCache()

                val languagesList = WSUser.getLanguages()
                val languageData = languagesList?.find { it.id == responseUser.languageId }
                if (languageData != null) {
                    SettingsHelper.languageName = languageData.code
                }

                log.info("User is logged in updating device and token...")

                val result = WSUser.registerDevice(fcmTokenRequest(), DeviceInfo.getJsonInstance())
                if( !App.Companion.ref.resources.getBoolean(R.bool.scan_advertise_only_tablet) ) {
                    userMemberships = WSUser.getGroupMemberships()?.toList() ?: listOf()

                    val ownerGroupList: Collection<REndUserGroupMember> =
                        WSUser.getGroupMembers() ?: mutableListOf()
                    val adminOwnerShipGroup: Collection<RGroupInfo> =
                        WSUser.getGroupMemberships()?: mutableListOf()
                    val adminGroup: MutableList<RGroupInfo> = mutableListOf()

                    for (items in adminOwnerShipGroup) {
                        val listUsersFromGroup: Collection<RGroupInfo> = WSUser.getGroupMembershipsById(items.groupId.toLong()) ?: mutableListOf()
                            // DataCache.groupMemberships(items.groupId.toLong(), false)
                        adminGroup.addAll(listUsersFromGroup)
                    }

                    println( "Owner group size is: " + ownerGroupList.size)
                    println(  "Data group size is: " + adminGroup.size)
                }

                return result

            } else {
                updateUserHash(null, null)
                user = null
                false
            }
        } else {
            updateUserHash(null, null)
            user = null
            false
        }
    }

    private suspend fun fcmTokenRequest(): String? {

        val task = FirebaseMessaging.getInstance().token.awaitForResult()
        if (!task.isSuccessful) {
            log.info("getInstanceId failed", task.exception)
        }
        // Get new Instance ID token
        val token = task.result
        val msg = "22 Token: $token"
        // Log and toast
        log.info(msg)

        val subscribeTask = FirebaseMessaging.getInstance().subscribeToTopic("news").awaitForResult()
        var subscribeMessage = "Subscribed"
        if (!subscribeTask.isSuccessful) {
            subscribeMessage = "Subscription failed"
        }
        log.info(subscribeMessage)
        return token
    }

    suspend fun logout() {
        log.info("Logging out, clearing data cache...")
        updateUserHash(null, null)
        SettingsHelper.userLastSelectedLocker = ""
        user = null
        //DataCache.clearCaches()
        MPLDeviceStore.clear()
    }

    //ping
    suspend fun ping(notifyNetworkChecker: Boolean = true): Boolean {
        val result = try {
            return WSUser.ping()
        } catch (e: Exception) {
            false
        }
        if (notifyNetworkChecker) {
            NetworkChecker.notifyInternetConnection(result)
        }
        return result
    }

    suspend fun passwordRecovery(email: String): Boolean {
        return WSUser.requestPasswordRecovery(email)
    }

    suspend fun passwordReset(email: String, passwordCode: String, password: String): Boolean {
        return WSUser.resetPassword(email, passwordCode, password)
    }

    suspend fun groupUpdate(name: String): Boolean {
        val userInfo = WSUser.updateUserGroupName(name)

        if (userInfo == null) {
            log.error("Error while updating the user")
            return false
        } else {
            userGroup = userInfo
            return true
        }
    }

    suspend fun passwordUpdate(newPassword: String, oldPassword: String): Boolean {
        val isPasswordUpdated = WSUser.updatePassword(oldPassword, newPassword)

        if (!isPasswordUpdated) {
            log.error("Error while updating the user password")
            return false
        } else {
            return true
        }
    }

    suspend fun userUpdate(name: String, address: String, phone: String, language: RLanguage, pushNotification: Boolean, emailNotification: Boolean, groupName: String): Boolean {
        println("$name $phone $address   $pushNotification $emailNotification ${language.code} ${language.id} ${language.name}")
        val userInfo = WSUser.updateUserProfile(name = name, telephone = phone, address = address, language = language, isPushNotified = pushNotification, isEmailNotified = emailNotification, groupName = groupName)

        if (userInfo == null) {
            log.error("Error while updating the user")
            return false
        } else {
            user = userInfo
            return true
        }
    }

    suspend fun userUpdateCPLBasel(telephone: String, reducedMobility: Boolean): Boolean {
        println("$telephone $reducedMobility ")
        val userInfo = WSUser.updateUserProfileCPLBasel(telephone = telephone, reducedMobility = reducedMobility)

        if (userInfo == null) {
            log.error("Error while updating the user")
            return false
        } else {
            user = userInfo
            return true
        }
    }

    suspend fun acceptedTerms() {
        WSUser.acceptedTerms()
        log.info("we are done")
        log.info("we are done")
        log.info("we are done")
    }

    suspend fun userUpdateInvited(name: String, address: String, phone: String, languageId: Int, pushNotification: Boolean, emailNotification: Boolean, groupName: String, emailValue: String, passwordValue: String ): Boolean {

        log.info("$name $phone $address   $pushNotification $emailNotification ${languageId} ")
        val userInfo = WSUser.updateUserProfileInvited(name = name, address = address, telephone = phone, languageId = languageId,
            isPushNotified = pushNotification, isEmailNotified = emailNotification, groupName = groupName, password = passwordValue)

        if (userInfo == null) {
            updateUserHash(null, null)
            log.error("Error while updating the user")
            return false
        } else {
            //invalidate caches on login
            updateUserHash(emailValue, passwordValue)
            SettingsHelper.pushEnabled = pushNotification
            SettingsHelper.emailEnabled = emailNotification
            SettingsHelper.usernameLogin = emailValue
            AppUtil.refreshCache()
            user = userInfo

            val result = WSUser.registerDevice(fcmTokenRequest(), DeviceInfo.getJsonInstance())
            return true
        }
    }

}