package hr.sil.android.myappbox.compose.access_sharing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.model.ItemRGroupInfo
import hr.sil.android.myappbox.core.remote.model.REmptyGroupMembers
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersAdmin
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersHeader
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlin.collections.List

@Composable
fun AccessSharingScreen(
    viewModel: AccessSharingViewModel = viewModel(),
    //macAddress: String,
    //isLoading: Boolean,
    //onAddUser: (RGroupDisplayMembersAdmin) -> Unit,
    //onRemoveUser: (RGroupDisplayMembersChild) -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val accessSharingMyGroup = stringResource(R.string.access_sharing_my_group)
    val accessSharingOtherGroup = stringResource(R.string.access_sharing_other_group)
    val shareAccessGroupMembersEmpty = stringResource(R.string.share_access_group_members_empty)
    val shareAccessGroupMembershipEmpty = stringResource(R.string.share_access_group_membership_empty)

    LaunchedEffect(Unit) {
        viewModel.loadData(accessSharingMyGroup, accessSharingOtherGroup, shareAccessGroupMembersEmpty, shareAccessGroupMembershipEmpty)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Title
            Text(
                text = stringResource(R.string.app_generic_key_sharing),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                letterSpacing = 0.1.em,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                //style = MaterialTheme.typography.titleLarge.copy(
                //    textTransform = TextTransform.Uppercase
                //)
            )

            // List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 10.dp)
            ) {
                itemsIndexed(uiState.listAccessSharing) { index, item ->
                    when (item) {
                        is RGroupDisplayMembersHeader -> {
                            AccessSharingHeaderItem(header = item)
                        }
                        is RGroupDisplayMembersAdmin -> {
                            AccessSharingSubHeaderItem(
                                admin = item,
                                onAddClick = {
                                    //onAddUser(item)
                                }
                            )
                        }
                        is RGroupDisplayMembersChild -> {
                            AccessSharingChildItem(
                                child = item,
                                index = index,
                                onRemoveClick = {
                                    //onRemoveUser(item)
                                }
                            )
                        }
                        is REmptyGroupMembers -> {
                            AccessSharingEmptyItem(empty = item)
                        }
                    }
                }
            }
        }

        // Progress Bar
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AccessSharingHeaderItem(header: RGroupDisplayMembersHeader) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = header.groupOwnerName ?: "",
            modifier = Modifier.fillMaxWidth(),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AccessSharingSubHeaderItem(
    admin: RGroupDisplayMembersAdmin,
    onAddClick: () -> Unit
) {
    val showAddButton = admin.role == "ADMIN" ||
            admin.groupOwnerName == UserUtil.userGroup?.name
    val showAdminIcon = admin.role == "ADMIN"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        color = colorResource(R.color.accessSharingItemBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = admin.groupOwnerName ?: "",
                modifier = Modifier.weight(8f),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = colorResource(R.color.colorWhite),
                fontWeight = FontWeight.Medium
            )

            if (showAdminIcon) {
                Icon(
                    painter = painterResource(R.drawable.ic_admin_white),
                    contentDescription = "Admin",
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    tint = Color.Unspecified
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (showAddButton) {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_group),
                        contentDescription = "Add User",
                        modifier = Modifier.height(20.dp),
                        tint = Color.Unspecified
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AccessSharingChildItem(
    child: RGroupDisplayMembersChild,
    index: Int,
    onRemoveClick: () -> Unit
) {
    val backgroundColor = if (index % 2 == 0) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(8f)
            ) {
                Text(
                    text = child.endUserName ?: "",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = child.endUserEmail ?: "",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (child.role == "ADMIN") {
                Icon(
                    painter = painterResource(R.drawable.ic_admin_white),
                    contentDescription = "Admin",
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    tint = Color.Unspecified
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = "Remove User",
                    modifier = Modifier.height(20.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun AccessSharingEmptyItem(empty: REmptyGroupMembers) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp)
    ) {
        Text(
            text = empty.emptyGroupMembers ?: "",
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
    }
}

//class AccessSharingActivity : ComponentActivity() {
//
//    val log = logger()
//
//    private var groupItems by mutableStateOf<List<ItemRGroupInfo>>(emptyList())
//    private var isLoading by mutableStateOf(true)
//    private var showDeleteDialog by mutableStateOf(false)
//    private var userToDelete by mutableStateOf<RGroupDisplayMembersChild?>(null)
//
//    private val macAddress by lazy { intent.getStringExtra("rMacAddress") ?: "" }
//    private val macRealToClean by lazy {
//        intent.getStringExtra("rMacAddress")?.macRealToClean() ?: ""
//    }
//    private val nameOfDevice by lazy { intent.getStringExtra("nameOfDevice") ?: "" }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            MaterialTheme {
//                Box {
//                    AccessSharingScreen(
//                        groupItems = groupItems,
//                        macAddress = macAddress,
//                        nameOfDevice = nameOfDevice,
//                        isLoading = isLoading,
//                        onAddUser = { admin ->
//                            handleAddUser(admin)
//                        },
//                        onRemoveUser = { child ->
//                            userToDelete = child
//                            showDeleteDialog = true
//                        }
//                    )
//
//                    if (showDeleteDialog && userToDelete != null) {
//                        DeleteAccessSharingUserDialog(
//                            user = userToDelete!!,
//                            onConfirm = {
//                                lifecycleScope.launch {
//                                    deleteAccessSharing(userToDelete!!)
//                                }
//                                showDeleteDialog = false
//                            },
//                            onDismiss = {
//                                showDeleteDialog = false
//                                userToDelete = null
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        loadData()
//    }
//
//    private fun loadData() {
//        lifecycleScope.launch {
//            isLoading = true
//
//            val userDevice = MPLDeviceStore.uniqueDevices.values
//                .firstOrNull { it.macAddress == macAddress }
//
//            val ownerGroupData = DataCache.getGroupMembers(true).toMutableList()
//            val finalMembersArray = mutableListOf<ItemRGroupInfo>()
//
//            if (userDevice != null) {
//                if (userDevice.masterUnitType == RMasterUnitType.MPL) {
//                    displayMplDevices(ownerGroupData, finalMembersArray)
//                } else {
//                    displaySplDevices(ownerGroupData, finalMembersArray)
//                }
//            }
//
//            withContext(Dispatchers.Main) {
//                groupItems = finalMembersArray
//                isLoading = false
//            }
//        }
//    }

//    private suspend fun displayMplDevices(
//        ownerResult: MutableList<REndUserGroupMember>,
//        finalMembersArray: MutableList<ItemRGroupInfo>
//    ) {
//        var oneOwnerUserFound = false
//
//        if (ownerResult.isNotEmpty()) {
//            val convertOwnerData = mutableListOf<RGroupDisplayMembersChild>()
//
//            for (items in ownerResult) {
//                if (items.master_mac == macRealToClean &&
//                    items.endUserId != UserUtil.user?.id) {
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
//                    groupOwnerName = stringResource(R.string.access_sharing_my_group)
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
//                emptyGroupMembersForThisMPLorSPLDevice(finalMembersArray)
//            }
//        } else {
//            emptyGroupMembersForThisMPLorSPLDevice(finalMembersArray)
//        }
//
//        val dataGroupMemberShip = DataCache.getGroupMembershipById(true).toMutableList()
//
//        if (dataGroupMemberShip.isNotEmpty()) {
//            var addOnlyOneTimeHeader = 0
//            val rGroupInfo = RGroupDisplayMembersHeader().apply {
//                groupOwnerName = stringResource(R.string.access_sharing_other_group)
//            }
//
//            for (items in dataGroupMemberShip) {
//                if (items.role == "USER" && items.master_mac == macRealToClean) {
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
//                } else if (items.master_mac == macRealToClean) {
//                    processGroupMembership(items, ownerResult, finalMembersArray,
//                        rGroupInfo, addOnlyOneTimeHeader)
//                }
//            }
//
//            if (addOnlyOneTimeHeader == 0) {
//                emptyGroupMembershipForThisMPLorSPL_device(finalMembersArray, rGroupInfo)
//            }
//        } else {
//            emptyGroupMembershipForThisMPLorSPL_device(finalMembersArray, null)
//        }
//    }
//
//    private suspend fun displaySplDevices(
//        ownerResult: MutableList<REndUserGroupMember>,
//        finalMembersArray: MutableList<ItemRGroupInfo>
//    ) {
//        // Similar implementation to displayMplDevices with SPL-specific logic
//        displayMplDevices(ownerResult, finalMembersArray)
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
//
//    private fun emptyGroupMembersForThisMPLorSPLDevice(
//        finalMembersArray: MutableList<ItemRGroupInfo>
//    ) {
//        val rGroupInfo = RGroupDisplayMembersHeader().apply {
//            groupOwnerName = getString(R.string.access_sharing_my_group)
//        }
//
//        val rGroupName = RGroupDisplayMembersAdmin().apply {
//            groupOwnerName = UserUtil.userGroup?.name.toString()
//            role = "USER"
//        }
//
//        val rEmptyGroupMembers = REmptyGroupMembers().apply {
//            emptyGroupMembers = getString(R.string.share_access_group_members_empty)
//        }
//
//        finalMembersArray.add(rGroupInfo)
//        finalMembersArray.add(rGroupName)
//        finalMembersArray.add(rEmptyGroupMembers)
//    }
//
//    private fun emptyGroupMembershipForThisMPLorSPL_device(
//        finalMembersArray: MutableList<ItemRGroupInfo>,
//        rGroupInfo: RGroupDisplayMembersHeader?
//    ) {
//        val groupInfo = rGroupInfo ?: RGroupDisplayMembersHeader().apply {
//            groupOwnerName = getString(R.string.access_sharing_other_group)
//        }
//
//        val nameOfAdminGroup = RGroupDisplayMembersAdmin().apply {
//            groupOwnerName = ""
//            role = "USER"
//        }
//
//        val rEmptyGroupMembers = REmptyGroupMembers().apply {
//            emptyGroupMembers = getString(R.string.share_access_group_membership_empty)
//        }
//
//        finalMembersArray.add(groupInfo)
//        finalMembersArray.add(nameOfAdminGroup)
//        finalMembersArray.add(rEmptyGroupMembers)
//    }
//
//    private fun handleAddUser(admin: RGroupDisplayMembersAdmin) {
//        val intent = Intent().apply {
//            putExtra("rMacAddress", macAddress)
//            putExtra("nameOfDevice", nameOfDevice)
//            putExtra("nameOfGroup", admin.groupOwnerName)
//            putExtra("groupId", admin.groupId)
//            component = ComponentName(
//                packageName,
//                "$packageName.aliasAccessSharingAddUser"
//            )
//        }
//        startActivity(intent)
//        finish()
//    }

    private suspend fun deleteAccessSharing(removeAccess: RGroupDisplayMembersChild) {
//        val userAccess = RUserRemoveAccess().apply {
//            groupId = removeAccess.groupId
//            endUserId = removeAccess.endUserId
//            masterId = removeAccess.master_id
//        }
//
//        if (WSUser.removeUserAccess(userAccess)) {
//            log.info("Successfully removed user ${removeAccess.groupOwnerName} from group")
//
//            deleteItem(removeAccess)
//
//            withContext(Dispatchers.Main) {
//                loadData()
//                Toast.makeText(
//                    this@AccessSharingActivity,
//                    getString(R.string.toast_access_sharing_remove_success,
//                        removeAccess.groupOwnerName),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } else {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(
//                    this@AccessSharingActivity,
//                    getString(R.string.toast_access_sharing_remove_error),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
    }

//    private suspend fun deleteItem(item: RGroupDisplayMembersChild) {
//        var deletedInOwnerList = false
//
//        for (ownerItems in DataCache.getGroupMembers()) {
//            if (ownerItems.endUserId == item.endUserId &&
//                ownerItems.groupId == item.groupId &&
//                ownerItems.master_id == item.master_id) {
//                deletedInOwnerList = true
//                DataCache.deleteOwnerGroupElement(ownerItems.id)
//                break
//            }
//        }
//
//        if (!deletedInOwnerList) {
//            val groupMemberShipId = DataCache.getGroupMembershipById().toMutableList()
//
//            for (items in groupMemberShipId) {
//                val groupDataList = DataCache.groupMemberships(items.groupId.toLong())
//                    .toMutableList()
//
//                for (subItems in groupDataList) {
//                    if (subItems.endUserId == item.endUserId &&
//                        subItems.groupId == item.groupId &&
//                        subItems.master_id == item.master_id) {
//                        DataCache.groupMemberships(items.groupId.toLong(), true)
//                        break
//                    }
//                }
//            }
//        }
//    }
//
//@Composable
//fun DeleteAccessSharingUserDialog(
//    user: RGroupDisplayMembersChild,
//    onConfirm: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = {
//            Text(
//                text = stringResource(R.string.delete_access_sharing_title),
//                fontWeight = FontWeight.Bold
//            )
//        },
//        text = {
//            Text(
//                text = stringResource(
//                    R.string.delete_access_sharing_message,
//                    user.endUserName ?: ""
//                )
//            )
//        },
//        confirmButton = {
//            Button(
//                onClick = onConfirm,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.error
//                )
//            ) {
//                Text(stringResource(R.string.app_generic_confirm))
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text(stringResource(R.string.app_generic_cancel))
//            }
//        }
//    )
//}