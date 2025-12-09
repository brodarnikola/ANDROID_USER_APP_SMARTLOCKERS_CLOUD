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

package hr.sil.android.myappbox.data

import hr.sil.android.smartlockers.enduser.core.remote.model.InstalationType
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerKeyPurpose
import java.util.*

/**
 * @author mfatiga
 */
class LockerKeyWithShareAccess {

    var id: Int = 0
    var createdById: Int? = null

    var timeCreated: String = ""   //: Date = Date()

    var purpose: RLockerKeyPurpose = RLockerKeyPurpose.UNKNOWN

    var createdByName: String? = null

    var lockerSize: String? = null

    var masterName: String? = null

    var masterAddress: String? = null

    // cpl, basel parameters
    var trackingNumber: String? = null
    var tan: Int? = null

    var installationType: InstalationType = InstalationType.UNKNOWN

    var listOfShareAccess: MutableList<ShareAccessKey> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LockerKeyWithShareAccess

        if (id != other.id) return false
        if (timeCreated != other.timeCreated) return false
        if (purpose != other.purpose) return false
        if (createdById != other.createdById) return false
        if (createdByName != other.createdByName) return false
        if (lockerSize != other.lockerSize) return false
        if (masterName != other.masterName) return false
        if (masterAddress != other.masterAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + timeCreated.hashCode()
        result = 31 * result + purpose.hashCode()
        result = 31 * result + (createdById ?: 0)
        result = 31 * result + (createdByName?.hashCode() ?: 0)
        result = 31 * result + (lockerSize?.hashCode() ?: 0)
        result = 31 * result + (masterName?.hashCode() ?: 0)
        result = 31 * result + (masterAddress?.hashCode() ?: 0)
        return result
    }
}
