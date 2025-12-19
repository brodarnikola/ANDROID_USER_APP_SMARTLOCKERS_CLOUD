package hr.sil.android.myappbox.compose.access_sharing


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.theme.AppTypography
import hr.sil.android.myappbox.core.remote.model.RGroupInfo
import hr.sil.android.myappbox.core.remote.model.RUserAccessRole
import hr.sil.android.myappbox.data.UserGroup
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.listOf
import kotlin.text.isEmpty
import kotlin.text.toRegex
import kotlin.text.uppercase


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AccessSharingAddUserScreen(
    viewModel: AccessSharingAddUserViewModel = viewModel(),
    nameOfGroup: String,
    groupId: Int,
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var emailError by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf(RUserAccessRole.USER) }
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val emailLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val imageEmail = painterResource(R.drawable.ic_email)

    val roles = listOf(
        UserGroup(1, stringResource(R.string.access_sharing_admin_role), RUserAccessRole.ADMIN),
        UserGroup(2, stringResource(R.string.access_sharing_user_role), RUserAccessRole.USER)
    )

    val contactsPermission = rememberPermissionState(Manifest.permission.READ_CONTACTS)

    val emailPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val projection = arrayOf(
                        ContactsContract.CommonDataKinds.Email.ADDRESS
                    )
                    context.contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val emailIndex =
                                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                            if (emailIndex >= 0) {
                                val email = cursor.getString(emailIndex)
                                if (!email.isNullOrBlank()) {
                                    viewModel.onEmailFromContact(email)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var isButtonEnabled by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Title
            TextViewWithFont(
                text = stringResource(R.string.app_generic_key_sharing).uppercase(),
                color = ThmTitleTextColor,
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = ThmTitleLetterSpacing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 15.dp, end = 15.dp)
            )

            // Email Field
            TextField(
                value = uiState.email,
                placeholder = {
                    Text(
                        text = stringResource(R.string.app_generic_email),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = emailLabelStyle.value
                    )
                },
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    textColor = MaterialTheme.colorScheme.onSurface,
//                    focusedBorderColor = colorResource(R.color.colorPrimary),
//                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
//                    cursorColor = colorResource(R.color.colorPrimary),
//                    backgroundColor = Color.Transparent
//                ),
                onValueChange = {
                    viewModel.onEmailChanged(it)
                    emailError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            emailLabelStyle.value = AppTypography.bodySmall
                        } else {
                            emailLabelStyle.value = AppTypography.bodyLarge
                        }
                    },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    Icon(
                        painter = imageEmail,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                },
                isError = emailError != null
            )

            // Error Text
            if (emailError != null) {
                TextViewWithFont(
                    text = emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            // Select from Contacts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 23.dp, end = 10.dp)
                    .padding(top = 10.dp)
                    .clickable {
                        if (contactsPermission.status.isGranted) {
                            val intent = Intent(Intent.ACTION_PICK).apply {
                                type = ContactsContract.CommonDataKinds.Email.CONTENT_TYPE
                            }
                            emailPickerLauncher.launch(intent)
                            //emailPickerLauncher.launch(null)
                        } else {
                            contactsPermission.launchPermissionRequest()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_adressbook),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                TextViewWithFont(
                    text = stringResource(R.string.access_sharing_new_user_selection),
                    color = ThmTitleTextColor,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

//           GroupSelectionDropdown(
//                label = stringResource(R.string.access_sharing_group_selection),
//                groups = uiState.availableGroups,
//                selectedGroup = uiState.selectedGroup,
//                onGroupSelected = { viewModel.onGroupSelected(it) }
//            )
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            RoleSelectionDropdown(
//                label = stringResource(R.string.access_sharing_new_user_access_details),
//                selectedRole = uiState.selectedRole,
//                onRoleSelected = { viewModel.onRoleSelected(it) }
//            )

            // User Role Dropdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .background(
                        color = colorResource(R.color.transparentColor), // MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(top = 1.dp)
            ) {
                TextViewWithFont(
                    text = stringResource(R.string.access_sharing_new_user_access_details),
                    color = ThmTitleTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = roles.first { it.value == selectedRole }.value.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
//                        colors = TextFieldDefaults.textFieldColors(
//                            backgroundColor = Color.Transparent,
//                            textColor = MaterialTheme.colorScheme.onSurface,
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent
//                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            //.padding(horizontal = 50.dp)
                            .background(Color.White)
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = role.value.name,
                                        color = Color.Black
                                    )
                                },
                                onClick = {
                                    selectedRole = role.value
                                    expanded = false
                                    viewModel.onRoleSelected(role.value)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Grant Access Button
            if (!isLoading) {

                val emptyUserText = stringResource(R.string.edit_user_validation_blank_fields_exist)
                val invalidEmail = stringResource(R.string.message_email_invalid)
                ButtonWithFont(
                    text = stringResource(id = R.string.access_sharing_new_user_allow).uppercase(),
                    onClick = {
                        when {
                            uiState.email.isEmpty() -> {
                                emailError = emptyUserText
                            }

                            !".+@.+".toRegex().matches(uiState.email) -> {
                                emailError = invalidEmail
                            }

                            else -> {
                                isLoading = true
                                isButtonEnabled = false

                                viewModel.addUserAccess(  nameOfGroup, groupId, uiState.email, uiState.selectedRole,
                                    onSuccess = {
                                        navigateUp()
                                    }
                                )
                            }
                        }
                    },
                    backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                    textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                    fontSize = ThmButtonTextSize, // ?attr/thmButtonTextSize
                    fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                    letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                    modifier = Modifier
                        .width(250.dp)
                        .height(40.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = isButtonEnabled
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (uiState.showShareAppDialog) {
        ShareAppDialog(
            email = uiState.shareAppEmail,
            onDismiss = { viewModel.dismissShareAppDialog() }
        )
    }
}

@Composable
private fun ShareAppDialog(
    email: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.grant_access_error),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "User not registered",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {

            val appLink = BuildConfig.APP_ANDR_DOWNLOAD_URL
            val iOSLink = BuildConfig.APP_IOS_DOWNLOAD_URL
            val shareBodyText = stringResource(
                R.string.sharing_help_text,
                appLink,
                iOSLink
            )
            val choose = stringResource(R.string.access_sharing_share_choose_sharing)
            TextButton(
                onClick = {

                    val emailIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                        putExtra(Intent.EXTRA_SUBJECT, "Zwickbox App")
                        putExtra(Intent.EXTRA_TEXT, shareBodyText)
                    }

                    context.startActivity(
                        Intent.createChooser(
                            emailIntent,
                            choose
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("Share app")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.app_generic_cancel))
            }
        }
    )
}

@Composable
private fun RoleSelectionDropdown(
    label: String,
    selectedRole: RUserAccessRole,
    onRoleSelected: (RUserAccessRole) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf(
        RUserAccessRole.ADMIN to stringResource(R.string.access_sharing_admin_role),
        RUserAccessRole.USER to stringResource(R.string.access_sharing_user_role)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(colorResource(R.color.colorPrimary))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(colorResource(R.color.colorWhite))
                    .padding(4.dp)
                    .clickable { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = roles.find { it.first == selectedRole }?.second ?: label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(R.color.colorBlack)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = colorResource(R.color.colorBlack)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.Transparent)
                ) {
                    roles.forEach { (role, roleName) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = roleName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onRoleSelected(role)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(3.dp))
}

@Composable
private fun GroupSelectionDropdown(
    label: String,
    groups: List<RGroupInfo>,
    selectedGroup: RGroupInfo?,
    onGroupSelected: (RGroupInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(colorResource(R.color.colorPrimary))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(colorResource(R.color.colorWhite))
                    .padding(4.dp)
                    .clickable { expanded = !expanded }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedGroup?.groupOwnerName ?: label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(
                            if (selectedGroup != null) R.color.colorBlack else R.color.colorDarkGray
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = colorResource(R.color.colorBlack)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.Transparent)
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = group.groupOwnerName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onGroupSelected(group)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(3.dp))
}

