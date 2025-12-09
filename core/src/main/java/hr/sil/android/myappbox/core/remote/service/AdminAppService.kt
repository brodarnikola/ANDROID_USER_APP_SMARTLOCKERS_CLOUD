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

package hr.sil.android.myappbox.core.remote.service

import hr.sil.android.myappbox.core.model.RUpdateAdminInfo
import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.rest.core.factory.RestServiceAccessor
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author mfatiga
 */
interface AdminAppService {
    companion object : RestServiceAccessor<AdminAppService>(AdminAppService::class) {
        //auth: Basic
        private const val ENDPOINT_PREFIX = "service/rest/adminApp/"
    }


    @POST(ENDPOINT_PREFIX + "device/register")
    fun registerDevice(@Body deviceInfo: RUserDeviceInfo): Call<RUserDeviceInfo>

    @GET(ENDPOINT_PREFIX + "login")
    fun login(): Call<RAdminUserInfo>

    @GET(ENDPOINT_PREFIX + "account/info")
    fun getAccountInfo(): Call<RAdminUserInfo>

    @GET(ENDPOINT_PREFIX + "getConfigurationData")
    fun getGlobalConfigurationData(): Call<RGlobalConfigurationData>

    @GET(ENDPOINT_PREFIX + "getApiKey/{mac}/{challenge}")
    fun getDeviceApiKey(@Path("mac") cleanDeviceBleMac: String,
                        @Path("challenge") challenge: String): Call<REncryptResponse>

    @POST(ENDPOINT_PREFIX + "{mac}/encrypt")
    fun encrypt(@Path("mac") mac: String,
                @Body encryptRequest: REncryptRequest): Call<REncryptResponse>


    @GET(ENDPOINT_PREFIX + "master/{mac}/lockers")
    fun getLockerForMaster(@Path("mac") cleanDeviceBleMac: String): Call<List<RLockerUnit>>

    @GET(ENDPOINT_PREFIX + "masterUnits")
    fun getMasterUnits(): Call<List<RMasterUnit>>

    @GET(ENDPOINT_PREFIX + "locker/{mac}")
    fun getLockerDetails(@Path("mac") cleanDeviceBleMac: String): Call<RLockerUnit>

    @GET(ENDPOINT_PREFIX + "registeredLockerMacs")
    fun getLockerMacAddresses(): Call<List<String>>

    @POST(ENDPOINT_PREFIX + "master/{mac}/modify")
    fun modifyMaster(@Path("mac") macAddress: String,
                     @Body masterDetails: RMasterUnitRequest): Call<RMasterUnit>


    @GET(ENDPOINT_PREFIX + "masterAccess/requests")
    fun getMasterAccessList(): Call<List<RMasterUnitAccessRequests>>

   @GET(ENDPOINT_PREFIX + "messageLog")
    fun getMessageLog(): Call<List<RMessageLog>>

    @GET(ENDPOINT_PREFIX + "messageLog/delete/{id}")
    fun deleteMessageItem(  @Path("id") id: Int): Call<Void>

    @GET(ENDPOINT_PREFIX + "messageLog/delete")
    fun deleteAllMessages(): Call<Void>


    @GET(ENDPOINT_PREFIX + "masterAccess/grant/{accessRequestId}/{buttonIndex}")
    fun grantAccessToMaster(@Path("accessRequestId") accessRequestId: Int,
                            @Path("buttonIndex") buttonIndex: Int): Call<Void>

    @GET(ENDPOINT_PREFIX + "masterAccess/reject/{accessRequestId}")
    fun rejectAccessToMaster(@Path("accessRequestId") accessRequestId: Int): Call<Void>

    @GET(ENDPOINT_PREFIX + "master/{mac}/assignedGroups")
    fun getAssignedGroupsToEpaper(@Path("mac") mac: String): Call<List<RAssignedGroup>>

    @GET(ENDPOINT_PREFIX + "networkConfigurations")
    fun getNetworkConfigurations(): Call<List<RNetworkConfiguration>>

    @GET(ENDPOINT_PREFIX + "master/{mac}/unassign/{buttonIndex}")
    fun unAssignMasterFromEpaper(@Path("mac") mac: String,
                                 @Path("buttonIndex") buttonIndex: Int): Call<Void>


    @GET(ENDPOINT_PREFIX + "master/{mac}/assignGroup/{groupId}/{buttonIndex}")
    fun assignGroupToEpaper(@Path("mac") mac: String,
                            @Path("buttonIndex") buttonIndex: Int,
                            @Path("groupId") groupId: Int): Call<Void>

    @GET(ENDPOINT_PREFIX + "endUser/recoverPassword/{email}")
    fun requestPasswordRecovery(@Path("email") email: String): Call<Void>

    @POST(ENDPOINT_PREFIX + "endUser/resetPassword")
    fun resetPassword(@Body resetPasswordRequest: RResetPasswordRequest): Call<Void>

    @POST(ENDPOINT_PREFIX + "endUser/updatePassword")
    fun updatePassword(@Body updatePasswordRequest: RUpdatePasswordRequest): Call<Void>

    @POST(ENDPOINT_PREFIX + "account/modify")
    fun updateUserProfile(@Body updateUserProfileRequest: RUpdateAdminInfo): Call<RAdminUserInfo>

}


