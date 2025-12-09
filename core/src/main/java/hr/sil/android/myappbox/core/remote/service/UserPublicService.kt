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
import retrofit2.Call
import retrofit2.http.*

/**
 * @author mfatiga
 */
interface UserPublicService {
    companion object : RestServiceAccessor<UserPublicService>(UserPublicService::class) {
        //auth: Basic
        private const val ENDPOINT_PREFIX = "app/service/rest/"
    }

    @POST(ENDPOINT_PREFIX + "device/register")
    fun registerDevice(@Body deviceInfo: RUserDeviceInfo): Call<RUserDeviceInfo>

    @GET(ENDPOINT_PREFIX + "languages")
    fun getLanguages(): Call<List<RLanguage>>

    @POST(ENDPOINT_PREFIX + "endUser/register")
    fun registerEndUser(@Body endUserInfo: REndUserRegisterRequest): Call<REndUserInfo>

    @POST(ENDPOINT_PREFIX + "cpl/endUser/register")
    fun registerEndUserCPL(@Body endUserInfo: REndUserRegisterRequestCPL): Call<REndUserInfo>

    @GET(ENDPOINT_PREFIX + "endUser/recoverPassword/{email}")
    fun requestPasswordRecovery(@Path("email") email: String): Call<Void>

    @POST(ENDPOINT_PREFIX + "endUser/resetPassword")
    fun resetPassword(@Body resetPasswordRequest: RResetPasswordRequest): Call<Void>

    @GET(ENDPOINT_PREFIX + "twoFactorAuthentication")
    fun getTwoFactoryAuth(): Call<RTwoFactoryAuth>

    @PUT(ENDPOINT_PREFIX + "endUser/address/check")
    fun checkInsertedAddress(@Body updatePasswordRequest: RCheckInsertedAddress): Call<RAddressCheckResponse>





}