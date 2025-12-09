package hr.sil.android.myappbox.core.model

import com.google.gson.annotations.SerializedName

class RUpdateAdminInfo {

    var name: String = ""
    var password: String? = null

    @SerializedName("language___id")
    var languageId: Int = 0

}