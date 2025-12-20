package hr.sil.android.myappbox.compose.access_sharing

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import hr.sil.android.myappbox.compose.dialog.DeleteAccessShareUserDialog
import hr.sil.android.myappbox.compose.main_activity.MainDestinations
import hr.sil.android.myappbox.core.remote.model.ItemRGroupInfo
import hr.sil.android.myappbox.core.remote.model.REmptyGroupMembers
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersAdmin
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersHeader
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlin.collections.List

import hr.sil.android.myappbox.core.util.logger
@Composable
fun AccessSharingScreen(
    viewModel: AccessSharingViewModel = viewModel(),
    nextScreen: (route: String, nameOfGroup: String, groupId: Int) -> Unit,
    //nextScreen: ( ) -> Unit,
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

    val groupId = rememberSaveable { mutableStateOf(-1) }
    val endUserId = rememberSaveable { mutableStateOf(-1) }
    val masterId = rememberSaveable { mutableStateOf(-1) }
    val displayRemoveUserDialog = rememberSaveable { mutableStateOf(false) }
    if( displayRemoveUserDialog.value ) {
        val errorMessage = stringResource(R.string.toast_access_sharing_remove_error)
        DeleteAccessShareUserDialog(
            onDismiss = { displayRemoveUserDialog.value = false },
            onConfirm = {
                //displayRemoveUserDialog.value = false
                viewModel.deleteAccessSharing(
                    groupId.value,
                    endUserId.value,
                    masterId.value,
                    onSuccess = {
                        displayRemoveUserDialog.value = false
                    },
                    onError = {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
                //onRemoveUser()
            },
            onCancel = { displayRemoveUserDialog.value = false }
        )
    }

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
                text = stringResource(R.string.app_generic_key_sharing).uppercase(),
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
                                    val nameOfGroup = item.groupOwnerName
                                    val groupIdParameter = item.groupId

                                    logger().info("nameOfGroup: $nameOfGroup")
                                    logger().info("groupId: $groupIdParameter")

                                    nextScreen(MainDestinations.ACCESS_SHARING_ADD_USER_SCREEN, nameOfGroup, groupIdParameter)
                                    //onAddUser(item)
                                }
                            )
                        }
                        is RGroupDisplayMembersChild -> {
                            AccessSharingChildItem(
                                child = item,
                                index = index,
                                onRemoveClick = {
                                    groupId.value = item.groupId
                                    endUserId.value = item.endUserId
                                    masterId.value = item.master_id
                                    displayRemoveUserDialog.value = true
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
            text = header.groupOwnerName ,
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
                text = admin.groupOwnerName,
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
                    text = child.endUserName ,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = child.endUserEmail ,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (child.role == "ADMIN") {
                Icon(
                    painter = painterResource(R.drawable.ic_admin),
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
            text = empty.emptyGroupMembers ,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
    }
}
