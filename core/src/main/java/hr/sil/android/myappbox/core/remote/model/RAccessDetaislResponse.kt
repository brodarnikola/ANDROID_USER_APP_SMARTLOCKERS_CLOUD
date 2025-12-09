package hr.sil.android.myappbox.core.remote.model


class RAccessDetaislResponse {

    var id: Int = 0

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    var splIsActivated: Boolean = true
    var type: RMasterUnitType = RMasterUnitType.UNKNOWN

    var activeAccessRequest: Boolean = false

    var address: String = ""

    var mac: String = ""
    var name: String = ""

    var userIsAssigned: Boolean = false

    var publicDevice: Boolean = false

    var requiredAccessRequestTypes: List<RequiredAccessRequestTypes> = listOf()

    var installationType: InstalationType = InstalationType.LINUX

}