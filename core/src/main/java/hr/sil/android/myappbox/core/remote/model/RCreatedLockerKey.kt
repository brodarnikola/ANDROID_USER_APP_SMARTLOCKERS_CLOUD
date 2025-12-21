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
        this.id = if (this@clone.id != null) this@clone.id else -1
        this.timeCreated = if (this@clone.timeCreated != null) this@clone.timeCreated else ""
        this.isDeleting = newIsDeleting ?: this@clone.isDeleting // Use newIsDeleting if provided, otherwise copy existing
        this.lockerId = if (this@clone.lockerId != null) this@clone.lockerId else -1
        this.lockerMac = if (this@clone.lockerMac != null)  this@clone.lockerMac else ""
        this.tan = if (this@clone.tan != null) this@clone.tan else ""
        this.pin =  if (this@clone.pin != null) this@clone.pin else ""
        this.lockerMasterId = if (this@clone.lockerMasterId != null)  this@clone.lockerMasterId else -1
        this.lockerMasterMac = if (this@clone.lockerMasterMac != null) this@clone.lockerMasterMac else ""
        this.purpose = if (this@clone.purpose != null) this@clone.purpose else RLockerKeyPurpose.UNKNOWN
        this.createdById = if (this@clone.createdById != null) this@clone.createdById else -1
        this.createdByName = if (this@clone.createdByName != null)  this@clone.createdByName else ""
        this.createdForId = if (this@clone.createdForId != null)  this@clone.createdForId else -1
        this.createdForEndUserName = if (this@clone.createdForEndUserName != null)  this@clone.createdForEndUserName else ""
        this.createdForEndUserEmail = if (this@clone.createdForEndUserEmail != null)  this@clone.createdForEndUserEmail else ""
        this.baseId = if (this@clone.baseId != null) this@clone.baseId else -1
        this.baseTimeCreated = if (this@clone.baseTimeCreated != null) this@clone.baseTimeCreated else ""
        this.baseGroupId = if (this@clone.baseGroupId != null)  this@clone.baseGroupId else -1
        this.basePurpose = if (this@clone.basePurpose != null)  this@clone.basePurpose else ""
        this.lockerSize = if (this@clone.lockerSize != null)  this@clone.lockerSize else ""
        this.masterName = if (this@clone.masterName != null)  this@clone.masterName else ""
        this.masterAddress = if (this@clone.masterAddress != null)  this@clone.masterAddress else ""
        this.keyInstallationtype = if (this@clone.keyInstallationtype != null) this@clone.keyInstallationtype else InstalationType.LINUX
        this.isInBleProximityOrLinuxDevice = if (this@clone.isInBleProximityOrLinuxDevice != null) this@clone.isInBleProximityOrLinuxDevice else false
        this.isLinuxKeyDevice = if (this@clone.isLinuxKeyDevice != null) this@clone.isLinuxKeyDevice else InstalationType.DEVICE
        this.deviceLatitude = if (this@clone.deviceLatitude != null) this@clone.deviceLatitude else 0.0
        this.deviceLongitude = if (this@clone.deviceLongitude != null) this@clone.deviceLongitude else 0.0
        this.qrCode = if (this@clone.qrCode != null) this@clone.qrCode else ""
    }
    return newKey
}
