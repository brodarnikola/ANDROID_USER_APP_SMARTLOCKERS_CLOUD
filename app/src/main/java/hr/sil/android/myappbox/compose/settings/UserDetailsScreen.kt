package hr.sil.android.myappbox.compose.settings



import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.cache.status.ActionStatusHandler.log
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.theme.AppTypography
import hr.sil.android.myappbox.core.remote.model.RLanguage
import hr.sil.android.myappbox.util.SettingsHelper

import hr.sil.android.myappbox.util.backend.UserUtil

@Composable
fun UserDetailsSettingsScreen(
    viewModel: UserDetailsViewModel,
    navigateUp: () -> Unit
) {

    val context = LocalContext.current

    var name by remember { mutableStateOf(UserUtil.user?.name ?: "") }
    var groupNameFirstRow by remember { mutableStateOf(UserUtil.user?.group___name?.take(15) ?: "") }
    var groupNameSecondRow by remember { mutableStateOf(UserUtil.user?.group___name?.drop(15)?.take(15) ?: "") }
    var email by remember { mutableStateOf(UserUtil.user?.email ?: "") }
    var address by remember { mutableStateOf(UserUtil.user?.address ?: "") }
    var phone by remember { mutableStateOf(UserUtil.user?.telephone ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var groupNameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    val nameLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val groupNameFirstLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val groupNameSecondLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val emailLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val addressLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val phoneLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }

    val imageName = painterResource(R.drawable.ic_register_name)
    val imageEmail = painterResource(R.drawable.ic_email)
    val imageAddress = painterResource(R.drawable.ic_register_address)
    val imagePhone = painterResource(R.drawable.ic_register_phone)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(bottom = 10.dp)
        ) {

            TextViewWithFont(
                text = stringResource(R.string.settings_account_title).uppercase(),
                color = ThmDescriptionTextColor,
                fontSize = ThmTitleTextSize,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.em,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )

            TextViewWithFont(
                text = stringResource(R.string.settings_account_details).uppercase(),
                color = ThmDescriptionTextColor,
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.1.em,
                maxLines = 3,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Name Field
            TextField(
                value = name,
                placeholder = {
                    Text(
                        text = stringResource(R.string.app_generic_name),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = nameLabelStyle.value
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = colorResource(R.color.colorPrimary),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = colorResource(R.color.colorPrimary),
                    backgroundColor = Color.Transparent
                ),
                onValueChange = {
                    name = it
                    nameError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            nameLabelStyle.value = AppTypography.bodySmall
                        } else {
                            nameLabelStyle.value = AppTypography.bodyLarge
                        }
                    },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    Icon(
                        painter = imageName,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                },
                isError = nameError != null
            )

            if (nameError != null) {
                Text(
                    text = nameError ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Group Name Title
            TextViewWithFont(
                text = stringResource(R.string.registration_group_name).uppercase(),
                color = ThmDescriptionTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.1.em,
                maxLines = 3,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .padding(top = 20.dp)
            )

            // Group Name Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)
                ) {
                    // First Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = groupNameFirstRow,
                            onValueChange = {
                                if (it.length <= 15) {
                                    groupNameFirstRow = it
                                    groupNameError = null
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        groupNameFirstLabelStyle.value = AppTypography.bodySmall
                                    } else {
                                        groupNameFirstLabelStyle.value = AppTypography.bodyLarge
                                    }
                                },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = colorResource(R.color.colorPrimary),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                cursorColor = colorResource(R.color.colorPrimary),
                                backgroundColor = Color.Transparent
                            ),
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        TextViewWithFont(
                            text = "${groupNameFirstRow.length}/15".uppercase(),
                            color = ThmDescriptionTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.1.em,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Second Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = groupNameSecondRow,
                            onValueChange = {
                                if (it.length <= 15) {
                                    groupNameSecondRow = it
                                    groupNameError = null
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        groupNameSecondLabelStyle.value = AppTypography.bodySmall
                                    } else {
                                        groupNameSecondLabelStyle.value = AppTypography.bodyLarge
                                    }
                                },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = colorResource(R.color.colorPrimary),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                cursorColor = colorResource(R.color.colorPrimary),
                                backgroundColor = Color.Transparent
                            ),
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        TextViewWithFont(
                            text = "${groupNameSecondRow.length}/15".uppercase(),
                            color = ThmDescriptionTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.1.em,
                            maxLines = 1
                        )
                    }
                }
            }

            if (groupNameError != null) {
                Text(
                    text = groupNameError ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Email Field (Disabled)
            TextField(
                value = email,
                placeholder = {
                    Text(
                        text = stringResource(R.string.edit_user_notification_setting_email),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = emailLabelStyle.value
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = colorResource(R.color.colorPrimary),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = colorResource(R.color.colorPrimary),
                    backgroundColor = Color.Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .alpha(0.6f),
                enabled = false,
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    Icon(
                        painter = imageEmail,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Address Field
            TextField(
                value = address,
                placeholder = {
                    Text(
                        text = stringResource(R.string.app_generic_address),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = addressLabelStyle.value
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = colorResource(R.color.colorPrimary),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = colorResource(R.color.colorPrimary),
                    backgroundColor = Color.Transparent
                ),
                onValueChange = {
                    address = it
                    addressError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            addressLabelStyle.value = AppTypography.bodySmall
                        } else {
                            addressLabelStyle.value = AppTypography.bodyLarge
                        }
                    },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    Icon(
                        painter = imageAddress,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                },
                isError = addressError != null
            )

            if (addressError != null) {
                Text(
                    text = addressError ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Phone Field
            TextField(
                value = phone,
                placeholder = {
                    Text(
                        text = stringResource(R.string.app_generic_phone),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = phoneLabelStyle.value
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = colorResource(R.color.colorPrimary),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = colorResource(R.color.colorPrimary),
                    backgroundColor = Color.Transparent
                ),
                onValueChange = { phone = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            phoneLabelStyle.value = AppTypography.bodySmall
                        } else {
                            phoneLabelStyle.value = AppTypography.bodyLarge
                        }
                    },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    Icon(
                        painter = imagePhone,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ButtonWithFont(
                text = stringResource(id = R.string.app_generic_apply).uppercase(),
                onClick = {
                    viewModel.updateUserDetails(
                        name = name,
                        groupName = groupNameFirstRow + groupNameSecondRow,
                        address = address,
                        phone = phone,
                        isNotifyPush = UserUtil.user?.isNotifyPush ?: true,
                        isNotifyEmail = UserUtil.user?.isNotifyEmail ?: true,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                R.string.app_generic_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateUp()
                        },
                        onError = {
                            Toast.makeText(context, R.string.error_while_saving_user, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                fontSize = ThmButtonTextSize, // ?attr/thmButtonTextSize
                fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                modifier = Modifier
                    .width(250.dp)
                    .height(40.dp)
                    .align(Alignment.CenterHorizontally) ,
                enabled = true
            )

            Spacer(modifier = Modifier.height(2.dp))
        }

    }
}