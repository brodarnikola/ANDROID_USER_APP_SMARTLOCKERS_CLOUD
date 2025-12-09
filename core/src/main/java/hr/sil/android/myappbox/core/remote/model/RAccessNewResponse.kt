package hr.sil.android.myappbox.core.remote.model

import com.google.gson.annotations.SerializedName

class RAccessNewResponse {

    var id: Int = 0
    var dateRequested: String= ""

    @SerializedName("master___id")
    var masterId: Int = 0

    @SerializedName("master___mac")
    var masterMac: String = ""

    @SerializedName("master___name")
    var masterName: String = ""

    var status: AccessRequestEnum = AccessRequestEnum.REJECTED

    @SerializedName("group___id")
    var groupId: Int= 0

    @SerializedName("group___owner___id")
    var groupOwnerId: Int= 0

    @SerializedName("group___owner___name")
    var groupOwnerName: String= ""



}