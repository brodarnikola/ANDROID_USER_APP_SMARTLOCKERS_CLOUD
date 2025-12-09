package hr.sil.android.myappbox.core.remote.model

import com.google.gson.annotations.SerializedName
import java.util.*

class RPinRegistrationCodeInfo {

    var id: Int = 0
    var dateRequested: Date = Date()

    @SerializedName("endUser___id")
    var endUserId: Int = 0

    @SerializedName("endUser___name")
    var endUserName: String = ""


    @SerializedName("status")
    var status: PinRegistrationCodeStatus = PinRegistrationCodeStatus.PENDING

    @SerializedName("timeSent")
    var timeSent: Date = Date()

    @SerializedName("timeResent")
    var timeResent: Date = Date()

    @SerializedName("timeResendRequested")
    var timeResendRequested: Date = Date()


}