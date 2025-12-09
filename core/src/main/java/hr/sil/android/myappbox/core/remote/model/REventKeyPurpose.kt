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

/**
 * @author mfatiga
 */
enum class REventKeyPurpose {
    UNKNOWN,
    ACTIVE_DELIVERY,
    ACTIVE_PAH,
    ACTIVE_PAF,
    PAH_PICKUP_DONE,
    PAF_PICKUP_DONE,
    DELIVERY_DONE,
    DELIVERY_COLLECTED_BY_COURIER,
    DELIVERY_COLLECTED_BY_ADMIN,
    DELIVERY_FAILED_ALL_LOCKER_OCUPIED,
    CANCEL_DELIVERY,
    CANCEL_PAF,
    CANCEL_PAH,
    INVALIDATED_BY_THE_SYSTEM,
    DELIVERY_PICKUP_BY_PAF,
    DELIVERY_INVALIDATED_NEW_PARCEL_DELIVERY,
    PAF_CONSUMED_WITH_DELIVERY
}