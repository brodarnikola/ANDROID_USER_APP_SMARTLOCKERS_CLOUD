package hr.sil.android.myappbox.view.ui.activities.access_sharing


import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope 

import hr.sil.android.myappbox.core.remote.model.*
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean 
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.LoginActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AccessSharingActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout)
{

//    val log = logger()
//
//    private lateinit var binding: ActivityAccessSharingBinding
//
//    var members = mutableListOf<REndUserGroupMember>()
//    private val macAddress by lazy { intent.getStringExtra("rMacAddress") ?: "" }
//    private val macRealToClean by lazy {
//        intent.getStringExtra("rMacAddress")?.macRealToClean() ?: ""
//    }
//    private val nameOfDevice by lazy { intent.getStringExtra("nameOfDevice") ?: "" }
//
//    var finalMembersArray: MutableList<ItemRGroupInfo> = mutableListOf()
//
//    var internetConnection: Boolean = true
//
//    private val ROLE_USER = "USER"
//
//    val EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP = ""
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAccessSharingBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        binding.progressBarAccessSharing.visibility = View.VISIBLE
//        binding.rvAccessSharing.visibility = View.GONE
//        lifecycleScope.launch {
//
//            val userDevice = MPLDeviceStore.uniqueDevices.values.filter { it.macAddress == macAddress }.first()
//
//            val ownerGroupData: MutableList<REndUserGroupMember> = DataCache.getGroupMembers(true).toMutableList()
//
//            if (internetConnection)
//                addOwnerGroupAndAdminGroupToRecylerView(ownerGroupData, userDevice)
//
//            val adminOwnerShipGroup: Collection<RGroupInfo> = DataCache.getGroupMembershipById()
//            val adminDataList: MutableList<RGroupInfo> = mutableListOf()
//
//            for (items in adminOwnerShipGroup) {
//                val usersFromGroup: Collection<RGroupInfo> = DataCache.groupMemberships(items.groupId.toLong())
//                adminDataList.addAll(usersFromGroup)
//            }
//
//            log.info("Members: ${members.size}")
//            log.info("Members: ${finalMembersArray.size}")
//            withContext(Dispatchers.Main) {
//
//                binding.progressBarAccessSharing.visibility = View.GONE
//                binding.rvAccessSharing.visibility = View.VISIBLE
//                binding.rvAccessSharing.adapter = KeySharingAdapter(
//                    finalMembersArray,
//                    macAddress,
//                    nameOfDevice,
//                    this@AccessSharingActivity
//                )
//                binding.rvAccessSharing.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
//            }
//        }
//
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        if( resources.getBoolean(R.bool.has_mobile_and_email_support) ) {
//            binding.ivSupportImage.visibility = View.VISIBLE
//            binding.ivSupportImage.setOnClickListener {
//                val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//                supportEmailPhoneDialog.show(
//                    supportFragmentManager,
//                    ""
//                )
//            }
//        }
//        else
//            binding.ivSupportImage.visibility = View.GONE
//    }
//
//    private suspend fun addOwnerGroupAndAdminGroupToRecylerView(ownerGroupData: MutableList<REndUserGroupMember>, userDevice: MPLDevice?) {
//
//        if (userDevice?.masterUnitType == RMasterUnitType.MPL)
//            displayMplDevices(ownerGroupData)
//        else
//            dislaySplDevices(ownerGroupData)
//    }
//
//    private suspend fun dislaySplDevices(ownerResult: MutableList<REndUserGroupMember>) {
//
//        var oneOwnerUserFound = false
//        // First I'm adding all data from owner list to finalMembersArray
//        if (ownerResult.isNotEmpty()) {
//
//            val convertOwnerData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
//
//            val userData: REndUserInfo? = UserUtil.user
//
//            for (items in ownerResult) {
//
//                // I'm owner in this group, OWNER CASE
//                if (items.master_mac == macRealToClean && items.groupOwnerId == userData?.id) {
//
//                    oneOwnerUserFound = true
//
//                    val ownerDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
//                    ownerDataObject.groupId = items.groupId
//                    ownerDataObject.endUserEmail = items.email
//                    ownerDataObject.endUserName = items.name
//                    ownerDataObject.role = items.role
//                    ownerDataObject.endUserId = items.endUserId
//                    ownerDataObject.master_id = items.master_id
//
//                    convertOwnerData.add(ownerDataObject)
//                }
//            }
//
//            if (oneOwnerUserFound == true) {
//
//                val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
//                rGroupInfo.groupOwnerName = this.getString(R.string.access_sharing_my_group)
//
//                val rGroupName: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
//                rGroupName.groupOwnerName = UserUtil.userGroup?.name.toString()
//
//                finalMembersArray.add(rGroupInfo)
//                finalMembersArray.add(rGroupName)
//                finalMembersArray.addAll(convertOwnerData)
//            } else {
//                emptyGroupMembersForThisMPLorSPLDevice()
//            }
//        } else {
//            emptyGroupMembersForThisMPLorSPLDevice()
//        }
//
//        val dataGroupMemberShip = DataCache.getGroupMembershipById(true).toMutableList()
//
//        if (dataGroupMemberShip.isNotEmpty()) {
//
//            var oneAdminUserFound: Boolean = false
//            var addOnlyOneTimeHeader: Int = 0
//
//            val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
//            rGroupInfo.groupOwnerName = this.getString(R.string.access_sharing_other_group)
//
//            for (items in dataGroupMemberShip) {
//
//                if (items.role == ROLE_USER && items.master_mac == macRealToClean) {
//
//                    if (addOnlyOneTimeHeader == 0) {
//
//                        finalMembersArray.add(rGroupInfo)
//                        addOnlyOneTimeHeader = 1
//                    }
//                    val nameOfAdminGroup = RGroupDisplayMembersAdmin()
//                    nameOfAdminGroup.groupOwnerName = items.groupName
//                    nameOfAdminGroup.role = items.role
//
//                    finalMembersArray.add(nameOfAdminGroup)
//                } else if (items.master_mac == macRealToClean) {
//                    val groupDataList: Collection<RGroupInfo> = DataCache.groupMemberships(items.groupId.toLong())
//
//                    if (groupDataList.size > 0) {
//
//                        val groupMembersData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
//                        val nameOfAdminGroup: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
//                        nameOfAdminGroup.groupOwnerName = items.groupName
//                        nameOfAdminGroup.role = items.role
//
//                        for (subItems in groupDataList) {
//
//                            if (ownerResult.isNotEmpty()) {
//                                val firstMember = ownerResult[0]
//                                firstMember.let {
//                                    if (subItems.master_mac == macRealToClean && subItems.endUserId != firstMember.groupOwnerId) {
//                                        oneAdminUserFound = true
//                                        val groupDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
//                                        groupDataObject.groupId = subItems.groupId
//                                        groupDataObject.groupOwnerEmail = subItems.groupOwnerEmail
//                                        groupDataObject.endUserName = subItems.endUserName
//                                        groupDataObject.endUserEmail = subItems.endUserEmail
//                                        groupDataObject.role = subItems.role
//                                        groupDataObject.endUserId = subItems.endUserId
//                                        groupDataObject.master_id = subItems.master_id
//                                        groupMembersData.add(groupDataObject)
//                                    } else if (subItems.master_mac == macRealToClean) {
//
//                                        oneAdminUserFound = true
//
//                                    } else {
//
//                                    }
//                                }
//                            } else {
//                                if (subItems.master_mac == macRealToClean && UserUtil.user?.id != subItems.endUserId) {
//                                    oneAdminUserFound = true
//                                    val groupDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
//                                    groupDataObject.groupId = subItems.groupId
//                                    groupDataObject.groupOwnerEmail = subItems.groupOwnerEmail
//                                    groupDataObject.endUserName = subItems.endUserName
//                                    groupDataObject.endUserEmail = subItems.endUserEmail
//                                    groupDataObject.role = subItems.role
//                                    groupDataObject.endUserId = subItems.endUserId
//                                    groupDataObject.master_id = subItems.master_id
//                                    groupMembersData.add(groupDataObject)
//                                }
//                            }
//                        }
//
//
//                        if (addOnlyOneTimeHeader == 0 && oneAdminUserFound) {
//                            finalMembersArray.add(rGroupInfo)
//                            addOnlyOneTimeHeader = 1
//                        }
//
//                        if (oneAdminUserFound) {
//                            finalMembersArray.add(nameOfAdminGroup)
//                            if (groupMembersData.size > 0)
//                                finalMembersArray.addAll(groupMembersData)
//                            oneAdminUserFound = false
//
//                        }
//                    }
//                }
//            }
//            if (addOnlyOneTimeHeader == 0 ) {
//
//                val nameOfAdminGroup = RGroupDisplayMembersAdmin()
//                nameOfAdminGroup.groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
//                nameOfAdminGroup.role = "USER"
//
//                val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
//                rEmptyGroupMembers.emptyGroupMembers = getString(R.string.share_access_group_membership_empty)
//
//                finalMembersArray.add(rGroupInfo)
//                finalMembersArray.add(nameOfAdminGroup)
//                finalMembersArray.add(rEmptyGroupMembers)
//            }
//        }
//        else {
//            emptyGroupMembershipForThisMPLorSPL_device()
//        }
//    }
//
//    private suspend fun displayMplDevices(ownerResult: MutableList<REndUserGroupMember>) {
//
//
//        var oneOwnerUserFound: Boolean = false
//        // First I'm adding all data from owner list to finalMembersArray
//        if (ownerResult.isNotEmpty()) {
//
//            val convertOwnerData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
//            //UserUtil.user?.id != subItems.endUserId
//
//            for (items in ownerResult) {
//
//                if (items.master_mac == macRealToClean && items.endUserId != UserUtil.user?.id) {
//
//                    oneOwnerUserFound = true
//
//                    val ownerDataObject: RGroupDisplayMembersChild = RGroupDisplayMembersChild()
//                    ownerDataObject.groupId = items.groupId
//                    ownerDataObject.endUserEmail = items.email
//                    ownerDataObject.endUserName = items.name
//                    ownerDataObject.role = items.role
//                    ownerDataObject.endUserId = items.endUserId
//                    ownerDataObject.master_id = items.master_id
//
//                    convertOwnerData.add(ownerDataObject)
//                }
//            }
//
//            if (oneOwnerUserFound == true) {
//
//                val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
//                rGroupInfo.groupOwnerName = this.getString(R.string.access_sharing_my_group)
//
//                val rGroupName: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
//                rGroupName.groupOwnerName = UserUtil.userGroup?.name.toString()
//                rGroupName.groupId =  UserUtil.userGroup?.id ?: 0
//
//                finalMembersArray.add(rGroupInfo)
//                finalMembersArray.add(rGroupName)
//                finalMembersArray.addAll(convertOwnerData)
//            } else {
//                emptyGroupMembersForThisMPLorSPLDevice()
//            }
//        } else {
//            emptyGroupMembersForThisMPLorSPLDevice()
//        }
//
//        val dataGroupMemberShip = DataCache.getGroupMembershipById(true).toMutableList()
//
//        if (dataGroupMemberShip.isNotEmpty()) {
//
//            var oneAdminUserFound: Boolean = false
//            var addOnlyOneTimeHeader: Int = 0
//
//            val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
//            rGroupInfo.groupOwnerName = this.getString(R.string.access_sharing_other_group)
//
//            for (items in dataGroupMemberShip) {
//
//                if (items.role == ROLE_USER && items.master_mac == macRealToClean) {
//
//                    if (addOnlyOneTimeHeader == 0) {
//
//                        finalMembersArray.add(rGroupInfo)
//                        addOnlyOneTimeHeader = 1
//                    }
//                    val nameOfAdminGroup = RGroupDisplayMembersAdmin()
//                    nameOfAdminGroup.groupOwnerName = items.groupName
//                    nameOfAdminGroup.role = items.role
//                    nameOfAdminGroup.groupId = items.groupId
//
//                    finalMembersArray.add(nameOfAdminGroup)
//                }
//                else if( items.master_mac == macRealToClean ) {
//                    val groupDataList: Collection<RGroupInfo> = DataCache.groupMemberships(items.groupId.toLong())
//                    if (groupDataList.size > 0) {
//                        val groupMembersData: MutableList<RGroupDisplayMembersChild> = mutableListOf()
//                        val nameOfAdminGroup = RGroupDisplayMembersAdmin()
//                        nameOfAdminGroup.groupOwnerName = items.groupName
//                        nameOfAdminGroup.role = items.role
//                        nameOfAdminGroup.groupId = items.groupId
//
//                        for (subItems in groupDataList) {
//
//                            if (ownerResult.isNotEmpty()) {
//                                val firstMember = ownerResult[0]
//                                firstMember.let {
//                                    if (subItems.master_mac == macRealToClean ) {
//                                        oneAdminUserFound = true
//                                        if( subItems.endUserId != firstMember.groupOwnerId ) {
//                                            val groupDataObject: RGroupDisplayMembersChild =
//                                                RGroupDisplayMembersChild()
//                                            groupDataObject.groupId = subItems.groupId
//                                            groupDataObject.groupOwnerEmail =
//                                                subItems.groupOwnerEmail
//                                            groupDataObject.endUserName = subItems.endUserName
//                                            groupDataObject.endUserEmail = subItems.endUserEmail
//                                            groupDataObject.role = subItems.role
//                                            groupDataObject.endUserId = subItems.endUserId
//                                            groupDataObject.master_id = subItems.master_id
//                                            groupMembersData.add(groupDataObject)
//                                        }
//                                    }
//                                }
//                            } else {
//                                if (subItems.master_mac == macRealToClean ) {
//                                    oneAdminUserFound = true
//                                    if( subItems.endUserId != UserUtil.user?.id ) {
//                                        val groupDataObject: RGroupDisplayMembersChild =
//                                            RGroupDisplayMembersChild()
//                                        groupDataObject.groupId = subItems.groupId
//                                        groupDataObject.groupOwnerEmail = subItems.groupOwnerEmail
//                                        groupDataObject.endUserName = subItems.endUserName
//                                        groupDataObject.endUserEmail = subItems.endUserEmail
//                                        groupDataObject.role = subItems.role
//                                        groupDataObject.endUserId = subItems.endUserId
//                                        groupDataObject.master_id = subItems.master_id
//                                        groupMembersData.add(groupDataObject)
//                                    }
//                                }
//                            }
//                        }
//
//
//                        if (addOnlyOneTimeHeader == 0 && oneAdminUserFound) {
//
//                            finalMembersArray.add(rGroupInfo)
//                            addOnlyOneTimeHeader = 1
//                        }
//
//                        if (oneAdminUserFound) {
//
//                            finalMembersArray.add(nameOfAdminGroup)
//                            finalMembersArray.addAll(groupMembersData)
//                            oneAdminUserFound = false
//
//                        }
//                    }
//                }
//            }
//
//            if (addOnlyOneTimeHeader == 0 ) {
//
//                val nameOfAdminGroup = RGroupDisplayMembersAdmin()
//                nameOfAdminGroup.groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
//                nameOfAdminGroup.role = "USER"
//
//                val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
//                rEmptyGroupMembers.emptyGroupMembers = getString(R.string.share_access_group_membership_empty)
//
//                finalMembersArray.add(rGroupInfo)
//                finalMembersArray.add(nameOfAdminGroup)
//                finalMembersArray.add(rEmptyGroupMembers)
//            }
//        }
//        else {
//            emptyGroupMembershipForThisMPLorSPL_device()
//        }
//    }
//
//    private fun emptyGroupMembersForThisMPLorSPLDevice() {
//
//        val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
//        rGroupInfo.groupOwnerName = this.getString(R.string.access_sharing_my_group)
//
//        val rGroupName: RGroupDisplayMembersAdmin = RGroupDisplayMembersAdmin()
//        rGroupName.groupOwnerName = UserUtil.userGroup?.name.toString()
//        rGroupName.role = "USER"
//
//        val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
//        rEmptyGroupMembers.emptyGroupMembers = getString(R.string.share_access_group_members_empty)
//
//        finalMembersArray.add(rGroupInfo)
//        finalMembersArray.add(rGroupName)
//        finalMembersArray.add(rEmptyGroupMembers)
//    }
//
//    private fun emptyGroupMembershipForThisMPLorSPL_device() {
//
//        val rGroupInfo: RGroupDisplayMembersHeader = RGroupDisplayMembersHeader()
//        rGroupInfo.groupOwnerName = this.getString(R.string.access_sharing_other_group)
//
//        val nameOfAdminGroup = RGroupDisplayMembersAdmin()
//        nameOfAdminGroup.groupOwnerName = EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERHSIP
//        nameOfAdminGroup.role = "USER"
//
//        val rEmptyGroupMembers: REmptyGroupMembers = REmptyGroupMembers()
//        rEmptyGroupMembers.emptyGroupMembers = getString(R.string.share_access_group_membership_empty)
//
//        finalMembersArray.add(rGroupInfo)
//        finalMembersArray.add(nameOfAdminGroup)
//        finalMembersArray.add(rEmptyGroupMembers)
//    }
//
//    override fun onNetworkStateUpdated(available: Boolean) {
//        super.onNetworkStateUpdated(available)
//        networkAvailable = available
//        internetConnection = available
//        updateUI()
//    }
//
//    override fun onBluetoothStateUpdated(available: Boolean) {
//        super.onBluetoothStateUpdated(available)
//        bluetoothAvalilable = available
//        updateUI()
//    }
//
//    override fun onLocationGPSStateUpdated(available: Boolean) {
//        super.onLocationGPSStateUpdated(available)
//        locationGPSAvalilable = available
//        updateUI()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        finalMembersArray.clear()
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
//        log.info("Received unauthorized event, user will now be log outed")
//        val intent = Intent( this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                /*val intent = Intent(baseContext, AccessSharingListActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                finish()*/
//
//                val intent = Intent()
//                val packageName = this@AccessSharingActivity.packageName
//                val componentName = ComponentName(packageName, packageName + ".aliasFinishAccessSharing")
//                intent.component = componentName
//
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                finish()
//
//                return true
//            }
//
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onBackPressed() {
//        /*val intent = Intent(baseContext, AccessSharingListActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()*/
//
//        val intent = Intent()
//        val packageName = this@AccessSharingActivity.packageName
//        val componentName = ComponentName(packageName, packageName + ".aliasFinishAccessSharing")
//        intent.component = componentName
//
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }

}
