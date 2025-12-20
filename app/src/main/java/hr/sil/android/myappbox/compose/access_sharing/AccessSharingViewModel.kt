package hr.sil.android.myappbox.compose.access_sharing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.ItemRGroupInfo
import hr.sil.android.myappbox.core.remote.model.REmptyGroupMembers
import hr.sil.android.myappbox.core.remote.model.REndUserGroupMember
import hr.sil.android.myappbox.core.remote.model.REndUserInfo
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersAdmin
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersHeader
import hr.sil.android.myappbox.core.remote.model.RGroupInfo
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccessSharingUiState(
    val listAccessSharing: List<ItemRGroupInfo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successKeyDelete: Boolean = false
)

private val ROLE_USER = "USER"
//
val EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP = ""

class AccessSharingViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(AccessSharingUiState())
    val uiState: StateFlow<AccessSharingUiState> = _uiState.asStateFlow()

    init {
        //loadData(context)
    }


    fun loadData(
        accessSharingMyGroup: String,
        accessSharingOtherGroup: String,
        shareAccessGroupMembersEmpty: String,
        shareAccessGroupMembershipEmpty: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val finalMembersArray = mutableListOf<ItemRGroupInfo>()

            val userDevice =
                MPLDeviceStore.uniqueDevices.values.first { it.macAddress == SettingsHelper.userLastSelectedLocker }

            val ownerGroupData: MutableList<REndUserGroupMember> =  WSUser.getGroupMembers()?.toMutableList() ?: mutableListOf() // DataCache.getGroupMembers(true).toMutableList()

            addOwnerGroupAndAdminGroup(ownerGroupData, userDevice, finalMembersArray, accessSharingMyGroup, accessSharingOtherGroup, shareAccessGroupMembersEmpty, shareAccessGroupMembershipEmpty)

//            val adminOwnerShipGroup: Collection<RGroupInfo> = WSUser.getGroupMemberships()?: mutableListOf() // DataCache.getGroupMembershipById()
//            val adminDataList: MutableList<RGroupInfo> = mutableListOf()
//
//            for (items in adminOwnerShipGroup) {
//                val usersFromGroup: Collection<RGroupInfo> =  WSUser.getGroupMembershipsById(items.groupId.toLong()) ?: mutableListOf() // DataCache.groupMemberships(items.groupId.toLong())
//                adminDataList.addAll(usersFromGroup)
//            }


//            val userDevice = MPLDeviceStore.uniqueDevices.values
//                .firstOrNull { it.macAddress == SettingsHelper.userLastSelectedLocker }
//
//            val ownerGroupData = WSUser.getGroupMembers()?.toMutableList() ?: mutableListOf()

//            if (userDevice != null) {
//                if (userDevice.masterUnitType == RMasterUnitType.MPL) {
//                    displayMplDevices(ownerGroupData, finalMembersArray, accessSharingMyGroup, accessSharingOtherGroup, shareAccessGroupMembersEmpty, shareAccessGroupMembershipEmpty)
//                } else {
//                    displaySplDevices(ownerGroupData, finalMembersArray, accessSharingMyGroup, accessSharingOtherGroup, shareAccessGroupMembersEmpty, shareAccessGroupMembershipEmpty)
//                }
//            }

            _uiState.update {
                it.copy(
                    listAccessSharing = finalMembersArray,
                    isLoading = false
                )
            }

//            withContext(Dispatchers.Main) {
//                groupItems = finalMembersArray
//                isLoading = false
//            }
        }
    }

    private suspend fun addOwnerGroupAndAdminGroup(
        ownerGroupData: MutableList<REndUserGroupMember>,
        userDevice: MPLDevice?,
        finalMembersArray: MutableList<ItemRGroupInfo>,
        accessSharingMyGroup: String,
        accessSharingOtherGroup: String,
        shareAccessGroupMembersEmpty: String,
        shareAccessGroupMembershipEmpty: String
    ) {

        if (userDevice?.masterUnitType == RMasterUnitType.MPL)
            displayMplDevices(ownerGroupData, finalMembersArray, accessSharingMyGroup, accessSharingOtherGroup, shareAccessGroupMembersEmpty, shareAccessGroupMembershipEmpty)
        else
            dislaySplDevices(ownerGroupData, finalMembersArray, accessSharingMyGroup, accessSharingOtherGroup, shareAccessGroupMembersEmpty, shareAccessGroupMembershipEmpty)
    }

    private suspend fun dislaySplDevices(
        ownerResult: MutableList<REndUserGroupMember>,
        finalMembersArray: MutableList<ItemRGroupInfo>,
        accessSharingMyGroup: String,
        accessSharingOtherGroup: String,
        shareAccessGroupMembersEmpty: String,
        shareAccessGroupMembershipEmpty: String
    ) {

        var oneOwnerUserFound = false
        // First I'm adding all data from owner list to finalMembersArray
        if (ownerResult.isNotEmpty()) {

            val convertOwnerData: MutableList<RGroupDisplayMembersChild> = mutableListOf()

            val userData: REndUserInfo? = UserUtil.user

            for (items in ownerResult) {

                // I'm owner in this group, OWNER CASE
                if (items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() && items.groupOwnerId == userData?.id) {

                    oneOwnerUserFound = true

                    val ownerDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
                    ownerDataObject.groupId = items.groupId
                    ownerDataObject.endUserEmail = items.email
                    ownerDataObject.endUserName = items.name
                    ownerDataObject.role = items.role
                    ownerDataObject.endUserId = items.endUserId
                    ownerDataObject.master_id = items.master_id

                    convertOwnerData.add(ownerDataObject)
                }
            }

            if (oneOwnerUserFound) {

                val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
                rGroupInfo.groupOwnerName = accessSharingMyGroup

                val rGroupName: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
                rGroupName.groupOwnerName = UserUtil.userGroup?.name.toString()
                rGroupName.groupId = UserUtil.userGroup?.id ?: -1

                finalMembersArray.add(rGroupInfo)
                finalMembersArray.add(rGroupName)
                finalMembersArray.addAll(convertOwnerData)
            } else {
                emptyGroupMembersForThisMPLorSPLDevice(finalMembersArray, accessSharingMyGroup, shareAccessGroupMembersEmpty)
            }
        } else {
            emptyGroupMembersForThisMPLorSPLDevice(finalMembersArray, accessSharingMyGroup, shareAccessGroupMembersEmpty)
        }

        val dataGroupMemberShip = WSUser.getGroupMemberships()?: mutableListOf() // DataCache.getGroupMembershipById(true).toMutableList()

        if (dataGroupMemberShip.isNotEmpty()) {

            var oneAdminUserFound: Boolean = false
            var addOnlyOneTimeHeader: Int = 0

            val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
            rGroupInfo.groupOwnerName = accessSharingOtherGroup // this.getString(R.string.access_sharing_other_group)

            for (items in dataGroupMemberShip) {

                if (items.role == ROLE_USER && items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean()) {

                    if (addOnlyOneTimeHeader == 0) {

                        finalMembersArray.add(rGroupInfo)
                        addOnlyOneTimeHeader = 1
                    }
                    val nameOfAdminGroup = RGroupDisplayMembersAdmin()
                    nameOfAdminGroup.groupOwnerName = items.groupName
                    nameOfAdminGroup.role = items.role

                    finalMembersArray.add(nameOfAdminGroup)
                } else if (items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean()) {
                    val groupDataList: Collection<RGroupInfo> = WSUser.getGroupMembershipsById(items.groupId.toLong()) ?: mutableListOf() // DataCache.groupMemberships(items.groupId.toLong())

                    if (groupDataList.size > 0) {

                        val groupMembersData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
                        val nameOfAdminGroup: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
                        nameOfAdminGroup.groupOwnerName = items.groupName
                        nameOfAdminGroup.role = items.role

                        for (subItems in groupDataList) {

                            if (ownerResult.isNotEmpty()) {
                                val firstMember = ownerResult[0]
                                firstMember.let {
                                    if (subItems.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() && subItems.endUserId != firstMember.groupOwnerId) {
                                        oneAdminUserFound = true
                                        val groupDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
                                        groupDataObject.groupId = subItems.groupId
                                        groupDataObject.groupOwnerEmail = subItems.groupOwnerEmail
                                        groupDataObject.endUserName = subItems.endUserName
                                        groupDataObject.endUserEmail = subItems.endUserEmail
                                        groupDataObject.role = subItems.role
                                        groupDataObject.endUserId = subItems.endUserId
                                        groupDataObject.master_id = subItems.master_id
                                        groupMembersData.add(groupDataObject)
                                    } else if (subItems.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean()) {

                                        oneAdminUserFound = true

                                    } else {

                                    }
                                }
                            } else {
                                if (subItems.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() && UserUtil.user?.id != subItems.endUserId) {
                                    oneAdminUserFound = true
                                    val groupDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
                                    groupDataObject.groupId = subItems.groupId
                                    groupDataObject.groupOwnerEmail = subItems.groupOwnerEmail
                                    groupDataObject.endUserName = subItems.endUserName
                                    groupDataObject.endUserEmail = subItems.endUserEmail
                                    groupDataObject.role = subItems.role
                                    groupDataObject.endUserId = subItems.endUserId
                                    groupDataObject.master_id = subItems.master_id
                                    groupMembersData.add(groupDataObject)
                                }
                            }
                        }


                        if (addOnlyOneTimeHeader == 0 && oneAdminUserFound) {
                            finalMembersArray.add(rGroupInfo)
                            addOnlyOneTimeHeader = 1
                        }

                        if (oneAdminUserFound) {
                            finalMembersArray.add(nameOfAdminGroup)
                            if (groupMembersData.isNotEmpty())
                                finalMembersArray.addAll(groupMembersData)
                            oneAdminUserFound = false

                        }
                    }
                }
            }
            if (addOnlyOneTimeHeader == 0 ) {

                val nameOfAdminGroup = RGroupDisplayMembersAdmin()
                nameOfAdminGroup.groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
                nameOfAdminGroup.role = "USER"

                val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
                rEmptyGroupMembers.emptyGroupMembers = shareAccessGroupMembershipEmpty // getString(R.string.share_access_group_membership_empty)

                finalMembersArray.add(rGroupInfo)
                finalMembersArray.add(nameOfAdminGroup)
                finalMembersArray.add(rEmptyGroupMembers)
            }
        }
        else {
            emptyGroupMembershipForThisMPLorSPL_device(finalMembersArray, accessSharingOtherGroup, shareAccessGroupMembershipEmpty)
        }
    }

    private suspend fun displayMplDevices(
        ownerResult: MutableList<REndUserGroupMember>,
        finalMembersArray: MutableList<ItemRGroupInfo>,
        accessSharingMyGroup: String,
        accessSharingOtherGroup: String,
        shareAccessGroupMembersEmpty: String,
        shareAccessGroupMembershipEmpty: String
    ) {


        var oneOwnerUserFound: Boolean = false
        // First I'm adding all data from owner list to finalMembersArray
        if (ownerResult.isNotEmpty()) {

            val convertOwnerData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
            //UserUtil.user?.id != subItems.endUserId

            for (items in ownerResult) {

                if (items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() && items.endUserId != UserUtil.user?.id) {

                    oneOwnerUserFound = true

                    val ownerDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
                    ownerDataObject.groupId = items.groupId
                    ownerDataObject.endUserEmail = items.email
                    ownerDataObject.endUserName = items.name
                    ownerDataObject.role = items.role
                    ownerDataObject.endUserId = items.endUserId
                    ownerDataObject.master_id = items.master_id

                    convertOwnerData.add(ownerDataObject)
                }
            }

            if (oneOwnerUserFound == true) {

                val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
                rGroupInfo.groupOwnerName = accessSharingMyGroup //this.getString(R.string.access_sharing_my_group)

                val rGroupName: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
                rGroupName.groupOwnerName = UserUtil.userGroup?.name.toString()
                rGroupName.groupId =  UserUtil.userGroup?.id ?: 0

                finalMembersArray.add(rGroupInfo)
                finalMembersArray.add(rGroupName)
                finalMembersArray.addAll(convertOwnerData)
            } else {
                emptyGroupMembersForThisMPLorSPLDevice(
                    finalMembersArray,
                    accessSharingMyGroup,
                    shareAccessGroupMembersEmpty
                )
            }
        } else {
            emptyGroupMembersForThisMPLorSPLDevice(
                finalMembersArray,
                accessSharingMyGroup,
                shareAccessGroupMembersEmpty
            )
        }

        val dataGroupMemberShip =  WSUser.getGroupMemberships()?: mutableListOf() // DataCache.getGroupMembershipById(true).toMutableList()

        if (dataGroupMemberShip.isNotEmpty()) {

            var oneAdminUserFound: Boolean = false
            var addOnlyOneTimeHeader: Int = 0

            val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
            rGroupInfo.groupOwnerName = accessSharingOtherGroup // this.getString(R.string.access_sharing_other_group)

            for (items in dataGroupMemberShip) {

                if (items.role == ROLE_USER && items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean()) {

                    if (addOnlyOneTimeHeader == 0) {

                        finalMembersArray.add(rGroupInfo)
                        addOnlyOneTimeHeader = 1
                    }
                    val nameOfAdminGroup = RGroupDisplayMembersAdmin()
                    nameOfAdminGroup.groupOwnerName = items.groupName
                    nameOfAdminGroup.role = items.role
                    nameOfAdminGroup.groupId = items.groupId

                    finalMembersArray.add(nameOfAdminGroup)
                }
                else if( items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() ) {
                    val groupDataList: Collection<RGroupInfo> = WSUser.getGroupMembershipsById(items.groupId.toLong()) ?: mutableListOf() // DataCache.groupMemberships(items.groupId.toLong())
                    if (groupDataList.size > 0) {
                        val groupMembersData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
                        val nameOfAdminGroup = RGroupDisplayMembersAdmin()
                        nameOfAdminGroup.groupOwnerName = items.groupName
                        nameOfAdminGroup.role = items.role
                        nameOfAdminGroup.groupId = items.groupId

                        for (subItems in groupDataList) {

                            if (ownerResult.isNotEmpty()) {
                                val firstMember = ownerResult[0]
                                firstMember.let {
                                    if (subItems.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() ) {
                                        oneAdminUserFound = true
                                        if( subItems.endUserId != firstMember.groupOwnerId ) {
                                            val groupDataObject: RGroupDisplayMembersChild =
                                                RGroupDisplayMembersChild()
                                            groupDataObject.groupId = subItems.groupId
                                            groupDataObject.groupOwnerEmail =
                                                subItems.groupOwnerEmail
                                            groupDataObject.endUserName = subItems.endUserName
                                            groupDataObject.endUserEmail = subItems.endUserEmail
                                            groupDataObject.role = subItems.role
                                            groupDataObject.endUserId = subItems.endUserId
                                            groupDataObject.master_id = subItems.master_id
                                            groupMembersData.add(groupDataObject)
                                        }
                                    }
                                }
                            } else {
                                if (subItems.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() ) {
                                    oneAdminUserFound = true
                                    if( subItems.endUserId != UserUtil.user?.id ) {
                                        val groupDataObject: RGroupDisplayMembersChild =
                                            RGroupDisplayMembersChild()
                                        groupDataObject.groupId = subItems.groupId
                                        groupDataObject.groupOwnerEmail = subItems.groupOwnerEmail
                                        groupDataObject.endUserName = subItems.endUserName
                                        groupDataObject.endUserEmail = subItems.endUserEmail
                                        groupDataObject.role = subItems.role
                                        groupDataObject.endUserId = subItems.endUserId
                                        groupDataObject.master_id = subItems.master_id
                                        groupMembersData.add(groupDataObject)
                                    }
                                }
                            }
                        }


                        if (addOnlyOneTimeHeader == 0 && oneAdminUserFound) {

                            finalMembersArray.add(rGroupInfo)
                            addOnlyOneTimeHeader = 1
                        }

                        if (oneAdminUserFound) {

                            finalMembersArray.add(nameOfAdminGroup)
                            finalMembersArray.addAll(groupMembersData)
                            oneAdminUserFound = false

                        }
                    }
                }
            }

            if (addOnlyOneTimeHeader == 0 ) {

                val nameOfAdminGroup = RGroupDisplayMembersAdmin()
                nameOfAdminGroup.groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
                nameOfAdminGroup.role = "USER"

                val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
                rEmptyGroupMembers.emptyGroupMembers = shareAccessGroupMembershipEmpty // getString(R.string.share_access_group_membership_empty)

                finalMembersArray.add(rGroupInfo)
                finalMembersArray.add(nameOfAdminGroup)
                finalMembersArray.add(rEmptyGroupMembers)
            }
        }
        else {
            emptyGroupMembershipForThisMPLorSPL_device(
                finalMembersArray,
                accessSharingOtherGroup,
                shareAccessGroupMembershipEmpty
            )
        }
    }

    private fun emptyGroupMembersForThisMPLorSPLDevice(
        finalMembersArray: MutableList<ItemRGroupInfo>,
        accessSharingMyGroup: String,
        shareAccessGroupMembersEmpty: String
    ) {

        val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
        rGroupInfo.groupOwnerName = accessSharingMyGroup //this.getString(R.string.access_sharing_my_group)

        val rGroupName: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
        rGroupName.groupOwnerName = UserUtil.userGroup?.name.toString()
        rGroupName.groupId = UserUtil.userGroup?.id ?: -1
        rGroupName.role = "USER"

        val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
        rEmptyGroupMembers.emptyGroupMembers = shareAccessGroupMembersEmpty //getString(R.string.share_access_group_members_empty)

        finalMembersArray.add(rGroupInfo)
        finalMembersArray.add(rGroupName)
        finalMembersArray.add(rEmptyGroupMembers)
    }

    private fun emptyGroupMembershipForThisMPLorSPL_device(
        finalMembersArray: MutableList<ItemRGroupInfo>,
        accessSharingOtherGroup: String,
        shareAccessGroupMembershipEmpty: String
    ) {

        val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
        rGroupInfo.groupOwnerName = accessSharingOtherGroup //this.getString(R.string.access_sharing_other_group)

        val nameOfAdminGroup = RGroupDisplayMembersAdmin()
        nameOfAdminGroup.groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
        nameOfAdminGroup.role = "USER"

        val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
        rEmptyGroupMembers.emptyGroupMembers = shareAccessGroupMembershipEmpty // getString(R.string.share_access_group_membership_empty)

        finalMembersArray.add(rGroupInfo)
        finalMembersArray.add(nameOfAdminGroup)
        finalMembersArray.add(rEmptyGroupMembers)
    }






//    private suspend fun displaySplDevices(
//        ownerResult: MutableList<REndUserGroupMember>,
//        finalMembersArray: MutableList<ItemRGroupInfo>,
//        accessSharingMyGroup: String,
//        accessSharingOtherGroup: String,
//        shareAccessGroupMembersEmpty: String,
//        shareAccessGroupMembershipEmpty: String
//    ) {
//        // Similar implementation to displayMplDevices with SPL-specific logic
//        displayMplDevices(
//            ownerResult,
//            finalMembersArray,
//            accessSharingMyGroup,
//            accessSharingOtherGroup,
//            shareAccessGroupMembersEmpty,
//            shareAccessGroupMembershipEmpty
//        )
//    }
//
//    private suspend fun displayMplDevices(
//        ownerResult: MutableList<REndUserGroupMember>,
//        finalMembersArray: MutableList<ItemRGroupInfo>,
//        accessSharingMyGroup: String,
//        accessSharingOtherGroup: String,
//        shareAccessGroupMembersEmpty: String,
//        shareAccessGroupMembershipEmpty: String
//    ) {
//        var oneOwnerUserFound = false
//
//        if (ownerResult.isNotEmpty()) {
//            val convertOwnerData = mutableListOf<RGroupDisplayMembersChild>()
//
//            for (items in ownerResult) {
//                if (items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean() &&
//                    items.endUserId != UserUtil.user?.id
//                ) {
//                    oneOwnerUserFound = true
//
//                    val ownerDataObject = RGroupDisplayMembersChild().apply {
//                        groupId = items.groupId
//                        endUserEmail = items.email
//                        endUserName = items.name
//                        role = items.role
//                        endUserId = items.endUserId
//                        master_id = items.master_id
//                    }
//                    convertOwnerData.add(ownerDataObject)
//                }
//            }
//
//            if (oneOwnerUserFound) {
//                val rGroupInfo = RGroupDisplayMembersHeader().apply {
//                    groupOwnerName = accessSharingMyGroup// stringResource(R.string.access_sharing_my_group)
//                }
//
//                val rGroupName = RGroupDisplayMembersAdmin().apply {
//                    groupOwnerName = UserUtil.userGroup?.name.toString()
//                    groupId = UserUtil.userGroup?.id ?: 0
//                }
//
//                finalMembersArray.add(rGroupInfo)
//                finalMembersArray.add(rGroupName)
//                finalMembersArray.addAll(convertOwnerData)
//            } else {
//                emptyGroupMembersForThisMPLorSPLDevice(finalMembersArray, accessSharingMyGroup, shareAccessGroupMembersEmpty)
//            }
//        } else {
//            emptyGroupMembersForThisMPLorSPLDevice(finalMembersArray, accessSharingMyGroup, shareAccessGroupMembersEmpty)
//        }
//
//        val dataGroupMemberShip = WSUser.getGroupMemberships()?: mutableListOf() // DataCache.getGroupMembershipById(true).toMutableList()
//
//        if (dataGroupMemberShip.isNotEmpty()) {
//            var addOnlyOneTimeHeader = 0
//            val rGroupInfo = RGroupDisplayMembersHeader().apply {
//                groupOwnerName = accessSharingOtherGroup // stringResource(R.string.access_sharing_other_group)
//            }
//
//            for (items in dataGroupMemberShip) {
//                if (items.role == "USER" && items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean()) {
//                    if (addOnlyOneTimeHeader == 0) {
//                        finalMembersArray.add(rGroupInfo)
//                        addOnlyOneTimeHeader = 1
//                    }
//
//                    val nameOfAdminGroup = RGroupDisplayMembersAdmin().apply {
//                        groupOwnerName = items.groupName
//                        role = items.role
//                        groupId = items.groupId
//                    }
//                    finalMembersArray.add(nameOfAdminGroup)
//                } else if (items.master_mac == SettingsHelper.userLastSelectedLocker.macRealToClean()) {
//                    processGroupMembership(
//                        items, ownerResult, finalMembersArray,
//                        rGroupInfo, addOnlyOneTimeHeader
//                    )
//                }
//            }
//
//            if (addOnlyOneTimeHeader == 0) {
//                emptyGroupMembershipForThisMPLorSPL_device(finalMembersArray, rGroupInfo, accessSharingOtherGroup, shareAccessGroupMembershipEmpty)
//            }
//        } else {
//            emptyGroupMembershipForThisMPLorSPL_device(finalMembersArray, null, accessSharingOtherGroup, shareAccessGroupMembershipEmpty)
//        }
//    }
//
//    private fun emptyGroupMembersForThisMPLorSPLDevice(
//        finalMembersArray: MutableList<ItemRGroupInfo>,
//        accessSharingMyGroup: String,
//        shareAccessGroupMembersEmpty: String
//    ) {
//        val rGroupInfo = RGroupDisplayMembersHeader().apply {
//            groupOwnerName = accessSharingMyGroup // getString(R.string.access_sharing_my_group)
//        }
//
//        val rGroupName = RGroupDisplayMembersAdmin().apply {
//            groupOwnerName = UserUtil.userGroup?.name.toString()
//            groupId = UserUtil.userGroup?.id ?: -1
//            role = "USER"
//        }
//
//        val rEmptyGroupMembers = REmptyGroupMembers().apply {
//            emptyGroupMembers = shareAccessGroupMembersEmpty //getString(R.string.share_access_group_members_empty)
//        }
//
//        finalMembersArray.add(rGroupInfo)
//        finalMembersArray.add(rGroupName)
//        finalMembersArray.add(rEmptyGroupMembers)
//    }
//
//    private fun emptyGroupMembershipForThisMPLorSPL_device(
//        finalMembersArray: MutableList<ItemRGroupInfo>,
//        rGroupInfo: RGroupDisplayMembersHeader?,
//        accessSharingOtherGroup: String,
//        shareAccessGroupMembershipEmpty: String
//    ) {
//        val groupInfo = rGroupInfo ?: RGroupDisplayMembersHeader().apply {
//            groupOwnerName = accessSharingOtherGroup // getString(R.string.access_sharing_other_group)
//        }
//
//        val nameOfAdminGroup = RGroupDisplayMembersAdmin().apply {
//            groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
//            role = "USER"
//        }
//
//        val rEmptyGroupMembers = REmptyGroupMembers().apply {
//            emptyGroupMembers = shareAccessGroupMembershipEmpty //getString(R.string.share_access_group_membership_empty)
//        }
//
//        finalMembersArray.add(groupInfo)
//        finalMembersArray.add(nameOfAdminGroup)
//        finalMembersArray.add(rEmptyGroupMembers)
//    }
//
//    private fun processGroupMembership(
//        items: RGroupInfo,
//        ownerResult: MutableList<REndUserGroupMember>,
//        finalMembersArray: MutableList<ItemRGroupInfo>,
//        rGroupInfo: RGroupDisplayMembersHeader,
//        addOnlyOneTimeHeader: Int
//    ) {
//        // Process group membership logic
//    }




}