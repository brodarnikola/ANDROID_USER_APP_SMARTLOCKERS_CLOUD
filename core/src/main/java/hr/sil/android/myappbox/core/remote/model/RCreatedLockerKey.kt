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

package hr.sil.android.myappbox.core.remote.model

import com.google.gson.annotations.SerializedName
import hr.sil.android.myappbox.core.util.macCleanToReal

/**
 * @author mfatiga
 */
class RCreatedLockerKey {
    var id: Int = 0
    var timeCreated: String = "" //Date = Date()

    var isDeleting: Boolean = false

    @SerializedName("locker___id")
    var lockerId: Int = 0

    @SerializedName("locker___mac")
    var lockerMac: String = ""

    var tan: String = ""
    var pin: String = ""

    @SerializedName("locker___master___id")
    var lockerMasterId: Int = 0

    @SerializedName("locker___master___mac")
    var lockerMasterMac: String = ""

    var purpose: RLockerKeyPurpose = RLockerKeyPurpose.UNKNOWN

    @SerializedName("createdBy___id")
    var createdById: Int? = null

    @SerializedName("createdBy___name")
    var createdByName: String? = null

    @SerializedName("createdForEndUser___id")
    var createdForId: Int? = null

    @SerializedName("createdForEndUser___name")
    var createdForEndUserName: String? = null

    @SerializedName("createdForEndUser___email")
    var createdForEndUserEmail: String? = null

    @SerializedName("basedOn___id")
    var baseId: Int? = null

    @SerializedName("basedOn___timeCreated")
    var baseTimeCreated = "" //: Date? = Date()

    @SerializedName("basedOn___createdForGroup___id")
    var baseGroupId: Int? = null

    @SerializedName("basedOn___purpose")
    var basePurpose: String? = null

    @SerializedName("locker___size")
    var lockerSize: String? = null

    @SerializedName("locker___master___name")
    var masterName: String? = null

    @SerializedName("locker___master___address")
    var masterAddress: String? = null


    @SerializedName("locker___master___installationType")
    var keyInstallationtype: InstalationType = InstalationType.LINUX


    var isInBleProximityOrLinuxDevice: Boolean = false
    var isLinuxKeyDevice: InstalationType = InstalationType.DEVICE

    var deviceLatitude: Double = 0.0
    var deviceLongitude: Double = 0.0

    var qrCode: String = ""

    fun getLockerBLEMacAddress(): String = lockerMac.macCleanToReal()

    fun getMasterBLEMacAddress(): String = lockerMasterMac.macCleanToReal()
}

// In a separate file (e.g., RCreatedLockerKeyExtensions.kt) or inside a companion object
fun RCreatedLockerKey.clone(newIsDeleting: Boolean? = null): RCreatedLockerKey {
    val newKey = RCreatedLockerKey().apply {
        // Copy all properties manually
        this.id = this@clone.id
        this.timeCreated = this@clone.timeCreated
        this.isDeleting = newIsDeleting ?: this@clone.isDeleting // Use newIsDeleting if provided, otherwise copy existing
        this.lockerId = this@clone.lockerId
        this.lockerMac = this@clone.lockerMac
        this.tan = this@clone.tan
        this.pin = this@clone.pin
        this.lockerMasterId = this@clone.lockerMasterId
        this.lockerMasterMac = this@clone.lockerMasterMac
        this.purpose = this@clone.purpose
        this.createdById = this@clone.createdById
        this.createdByName = this@clone.createdByName
        this.createdForId = this@clone.createdForId
        this.createdForEndUserName = this@clone.createdForEndUserName
        this.createdForEndUserEmail = this@clone.createdForEndUserEmail
        this.baseId = this@clone.baseId
        this.baseTimeCreated = this@clone.baseTimeCreated
        this.baseGroupId = this@clone.baseGroupId
        this.basePurpose = this@clone.basePurpose
        this.lockerSize = this@clone.lockerSize
        this.masterName = this@clone.masterName
        this.masterAddress = this@clone.masterAddress
        this.keyInstallationtype = this@clone.keyInstallationtype
        this.isInBleProximityOrLinuxDevice = this@clone.isInBleProximityOrLinuxDevice
        this.isLinuxKeyDevice = this@clone.isLinuxKeyDevice
        this.deviceLatitude = this@clone.deviceLatitude
        this.deviceLongitude = this@clone.deviceLongitude
        this.qrCode = this@clone.qrCode
    }
    return newKey
}
