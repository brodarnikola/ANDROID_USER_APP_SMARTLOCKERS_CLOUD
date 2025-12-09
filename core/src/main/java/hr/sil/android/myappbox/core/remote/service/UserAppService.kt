package hr.sil.android.myappbox.core.remote.service

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


import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.rest.core.factory.RestServiceAccessor
import hr.sil.android.myappbox.smartlockers.enduser.core.remote.model.RInstallationKey
import hr.sil.android.myappbox.core.remote.model.AccessRequestResponse
import hr.sil.android.myappbox.core.remote.model.RNotificationsEventsUser
import retrofit2.Call
import retrofit2.http.*

/**
 * @author mfatiga
 */
interface UserAppService {
    companion object : RestServiceAccessor<UserAppService>(UserAppService::class) {
        //auth: Basic
        private const val ENDPOINT_PREFIX = "app/service/rest/"
    }

    @GET(ENDPOINT_PREFIX + "masterUnits/accessRequests/new")
    fun getActiveAccessRequestsNew(): Call<List<RAccessNewResponse>>

    @GET(ENDPOINT_PREFIX + "masterUnits/withAccessDetails")
    fun getAccessDetails(): Call<List<RAccessDetaislResponse>>

    @POST(ENDPOINT_PREFIX + "smart/action/put")
    fun createPickAtHomeForLinuxDevices(@Body pickAtHomeRequest: RPickAtHomeRequest): Call<RPickAtHomeResponse>

    @PUT(ENDPOINT_PREFIX + "smart/{lockerMac}/delivery/cancel")
    fun cancelPickAtHomeLinuxDevices(@Path("lockerMac") lockerMac: String): Call<RPickupLinuxResponse>

    @POST(ENDPOINT_PREFIX + "smart/action/get")
    fun pickupCollectKeysForLinuxDevices(@Body pickAtHomeRequest: RPickupLinuxRequest): Call<RPickupLinuxResponse>

    @PUT(ENDPOINT_PREFIX + "smart/{lockerMac}/force/open")
    fun forceOpenLinuxDevice(@Path("lockerMac") lockerMac: String): Call<Void>

    @PUT(ENDPOINT_PREFIX + "locker/{lockerMac}/cleaningRequired")
    fun cleaningNeededForLinuxLocker(@Path("lockerMac") lockerMac: String): Call<Void>

    @POST(ENDPOINT_PREFIX + "endUser/updatePassword")
    fun updatePassword(@Body updatePasswordRequest: RUpdatePasswordRequest): Call<Void>

    @PUT(ENDPOINT_PREFIX + "confirmationEmail/resend?headerBasicAuth=headerBasicAuthorizationKey")
    fun resendEmailForConfirmation(@Query("headerBasicAuthorizationKey") headerBasicAuthorizationKey: String): Call<Boolean>

    @GET(ENDPOINT_PREFIX + "endUser/acceptTerms")
    fun acceptedTerms(): Call<Void>

    @POST(ENDPOINT_PREFIX + "endUser/update")
    fun updateUserProfile(@Body updateUserProfileRequest: RUpdateUserProfileRequest): Call<REndUserInfo>

    @POST(ENDPOINT_PREFIX + "cpl/endUser/update")
    fun updateUserProfileCPLBasel(@Body updateUserProfileRequest: RUpdateUserProfileRequestCPLBasel): Call<REndUserInfo>

    @POST(ENDPOINT_PREFIX + "endUser/update")
    fun updateUserProfileInvited(@Body updateUserProfileRequest: RUpdateUserProfileRequestInvited): Call<REndUserInfo>

    @GET(ENDPOINT_PREFIX + "endUser/info")
    fun getUserInfo(): Call<REndUserInfo>

    @POST(ENDPOINT_PREFIX + "vendor/delivery/prepare")
    fun getGroupIdForDeliveryRetail(@Body vendorDeliveryPrepare: RGroupIdDeliveryRetail): Call<Int>

    @GET(ENDPOINT_PREFIX + "masterUnit/{masterId}/generatePin")
    fun getGeneratedPinFromBackendForSendParcel(@Path("masterId") masterId: Int): Call<String>

    @GET(ENDPOINT_PREFIX + "group/pins/{groupId}/{masterId}")
    fun getPinManagementForSendParcel(@Path("groupId") groupId: Int, @Path("masterId") masterId: Int?): Call<List<RPinManagementResponse>>

    @POST(ENDPOINT_PREFIX + "group/pin/create/")
    fun savePinManagementForSendParcel(@Body saveRPinManagement: RPinManagementSavePin ): Call<RPinManagementResponse>

    @GET(ENDPOINT_PREFIX + "group/pin/remove/{groupPinId}")
    fun deletePinForSendParcel(@Path("groupPinId") pinId: Int ): Call<Void>

    @GET(ENDPOINT_PREFIX + "group")
    fun getGroupInfo(): Call<REndUserGroupInfo>

    @GET(ENDPOINT_PREFIX + "groupMembers")
    fun getGroupMembers(): Call<List<REndUserGroupMember>>

    @GET(ENDPOINT_PREFIX + "groupMemberships")
    fun getGroupMemberships(): Call<List<RGroupInfo>>

    @GET(ENDPOINT_PREFIX + "groupMembers/{id}")
    fun getGroupMembershipsById(@Path("id") id: Long): Call<MutableList<RGroupInfo>>


    @POST(ENDPOINT_PREFIX + "group/rename")
    fun updateUserGroup(@Body updateUserGroupRequest: RUpdateUserGroupRequest): Call<REndUserGroupInfo>

    @GET(ENDPOINT_PREFIX + "masterUnits/withAccessDetails")
    fun getMasterUnits(): Call<List<RMasterUnit>>

    @GET(ENDPOINT_PREFIX + "keys")
    fun getActiveKeys(): Call<List<RLockerKey>>

    @GET(ENDPOINT_PREFIX + "pickAtFriend/activeCreated")
    fun getActivePaFCreatedKeys(): Call<List<RCreatedLockerKey>>

    @GET(ENDPOINT_PREFIX + "pickAtHome/activeCreated")
    fun getActivePaHCreatedKeys(): Call<MutableList<RCreatedLockerKey>>

    @GET(ENDPOINT_PREFIX + "endUser/allkeys/list/{page}/{pageSize}")
    fun getEventsNotificationsUser(@Path("page") page: Int, @Path("pageSize") pageSize: Int): Call<RNotificationsEventsUser>

    @GET(ENDPOINT_PREFIX + "vendor/active/delivery/list")
    fun getVendorDeliveryKeys(): Call<MutableList<RCreatedLockerKey>>

    @GET(ENDPOINT_PREFIX + "device/{masterUnitMac}/user/list")
    fun getEmailListSuggestion(@Path("masterUnitMac") masterUnitMac: String): Call<List<String>>

    @POST(ENDPOINT_PREFIX + "{mac}/encrypt")
    fun encrypt(@Path("mac") mac: String,
                @Body encryptRequest: REncryptRequest): Call<REncryptResponse>

    @GET(ENDPOINT_PREFIX + "masterUnit/{id}/availableSizes")
    fun getAvailableLockerSizes(@Path("id") masterUnitId: Int): Call<List<RAvailableLockerSize>>


    @GET(ENDPOINT_PREFIX + "masterUnits/requestAccess/{mac}")
    fun requestAccess(@Path("mac") mac: String): Call<AccessRequestResponse>

    @GET(ENDPOINT_PREFIX + "masterUnits/accessRequests/new")
    fun getActiveAccessRequests(): Call<List<RAccessRequest>>

    @GET(ENDPOINT_PREFIX + "masterUnit/{mac}")
    fun getLockerInfo(@Path("mac") mac: String): Call<RLockerInfo>


    @POST(ENDPOINT_PREFIX + "masterUnit")
    fun getLockersInfo(@Body userAccess: List<String>): Call<List<RLockerInfo>>


    @POST(ENDPOINT_PREFIX + "group/addUser")
    fun addUserAccess(@Body userAccess: RUserAccess): Call<Void>

    @POST(ENDPOINT_PREFIX + "group/removeUser")
    fun removeUserAccess(@Body userAccess: RUserRemoveAccess): Call<Void>

    @POST(ENDPOINT_PREFIX + "pickAtFriend/create")
    fun createPaF(@Body encryptRequest: RCreatePaf): Call<RInstallationKey>

    @POST(ENDPOINT_PREFIX + "pickAtFriend/cancel")
    fun deletePaF(@Body encryptRequest: RDeletePaf): Call<Void>

    @GET(ENDPOINT_PREFIX + "lockerNeedsCleaning")
    fun sendLockersNeedsCleaningInformation(): Call<Boolean>

    @PUT(ENDPOINT_PREFIX + "endUser/mailCode/confirm")
    fun sendPinRegistrationCode(@Body encryptRequest: RPinRegistrationCodeSend): Call<ResponsePinRegistrationCode>

    @PUT(ENDPOINT_PREFIX + "endUser/mailCode/resend")
    fun sendNewPinRegistrationCode(): Call<Void>

    @GET(ENDPOINT_PREFIX + "endUser/mailCode/info")
    fun getPinRegistrationCodeInfo(): Call<RPinRegistrationCodeInfo>

    @GET(ENDPOINT_PREFIX + "showMailCodeInput")
    fun getShowMailCodeInput(): Call<Boolean>


    @GET("www.google.com")
    fun ping(): Call<Void>

    //SPL
    @GET(ENDPOINT_PREFIX + "spl/{mac}/activate")
    fun activateSpl(@Path("mac") mac: String): Call<Void>

    //SPL
    @GET(ENDPOINT_PREFIX + "spl/{mac}/deactivate")
    fun deactivateSpl(@Path("mac") mac: String): Call<Void>

    //SPL
    @POST(ENDPOINT_PREFIX + "spl/modify")
    fun modifySpl(@Body masterUnit: RMasterUnit): Call<RMasterUnit>



}