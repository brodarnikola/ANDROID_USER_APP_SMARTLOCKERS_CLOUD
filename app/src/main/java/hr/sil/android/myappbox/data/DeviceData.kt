package hr.sil.android.myappbox.data

import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.MPLDeviceStatus
import hr.sil.android.myappbox.core.model.MPLDeviceType
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.remote.model.RequiredAccessRequestTypes


class DeviceData (var macAddress: String = "", var deviceName: String = "",
                  var deviceAddress: String = "", var isSelected: Boolean = false,
                  var isInBleProximity: Boolean = false,
                  var isExpanded: Boolean = false, var indexOfHeader: Int = 0,
                  var installationType: InstalationType = InstalationType.UNKNOWN, var publicDevice: Boolean = false,
                  var bleDeviceType: MPLDeviceType = MPLDeviceType.UNKNOWN, var backendDeviceType: RMasterUnitType = RMasterUnitType.UNKNOWN,
                  var isSplActivate: Boolean = false,
                  /*var isLinuxDeviceInProximity: Boolean = true,*/ var isUserAssigned: Boolean = false, var activeAccessRequest: Boolean = false,
                  var requiredAccessRequestTypes: List<RequiredAccessRequestTypes> = listOf(), var latitude: Double = 0.0, var longitude: Double = 0.0
)