package hr.sil.android.myappbox.compose.settings

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.SettingsRoundedBackground
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmToolbarBackgroundColor
import hr.sil.android.myappbox.compose.dialog.LogoutDialog
import hr.sil.android.myappbox.compose.main_activity.MainDestinations
import hr.sil.android.myappbox.core.util.logger
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.uppercase


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.sil.android.myappbox.compose.SignUpOnboardingSections
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.ProgressIndicatorSize
import hr.sil.android.myappbox.compose.components.RotatingRingIndicator
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginBackground
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmToolbarBackgroundColor
import hr.sil.android.myappbox.compose.main_activity.MainActivity
import hr.sil.android.myappbox.compose.theme.AppTypography
import hr.sil.android.myappbox.compose.theme.Black
import hr.sil.android.myappbox.compose.theme.DarkModeTransparent
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.utils.UiEvent
import hr.sil.android.myappbox.view.ui.activities.TCInvitedUserActivity
import androidx.compose.material3.MaterialTheme as Material3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.theme.Black

@Composable
fun ChangePasswordScreen(
    //onChangePassword: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }
    var showPasswords by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val currentPasswordLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val newPasswordLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }
    val retypePasswordLabelStyle = remember { mutableStateOf(AppTypography.bodyLarge) }

    val imagePassword = painterResource(R.drawable.ic_password_zwick)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val (title, subtitle, currentPasswordField, currentPasswordErrorText,
            newPasswordField, newPasswordErrorText, retypePasswordField,
            confirmPasswordErrorText, showPasswordText, progressBar, applyButton) = createRefs()

        // Settings Account Title
        Text(
            text = stringResource(R.string.settings_account_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 56.dp)
                },
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            //color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.1.em,
            fontWeight = FontWeight.Medium,
//            style = MaterialTheme.typography.titleLarge.copy(
//                textTransform = TextTransform.Uppercase
//            )
        )

        // Change Password Subtitle
        Text(
            text = stringResource(R.string.nav_settings_change_password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .constrainAs(subtitle) {
                    top.linkTo(title.bottom, margin = 20.dp)
                },
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            //color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
//            style = MaterialTheme.typography.titleMedium.copy(
//                textTransform = TextTransform.Uppercase
//            )
        )

        // Current Password Field
        TextField(
            value = currentPassword,
            placeholder = {
                Text(
                    text = stringResource(R.string.current_password),
                    //color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = currentPasswordLabelStyle.value
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                //textColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = colorResource(R.color.colorPrimary),
                //unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = colorResource(R.color.colorPrimary),
                backgroundColor = Color.Transparent
            ),
            onValueChange = {
                currentPassword = it
                currentPasswordError = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        currentPasswordLabelStyle.value = AppTypography.bodySmall
                    } else {
                        currentPasswordLabelStyle.value = AppTypography.bodyLarge
                    }
                }
                .constrainAs(currentPasswordField) {
                    top.linkTo(subtitle.bottom, margin = 20.dp)
                },
            maxLines = 1,
            singleLine = true,
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                Icon(
                    painter = imagePassword,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
            },
            isError = currentPasswordError != null
        )

        // Current Password Error Text
        if (currentPasswordError != null) {
            Text(
                text = currentPasswordError ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .constrainAs(currentPasswordErrorText) {
                        top.linkTo(currentPasswordField.bottom)
                    },
                fontSize = 16.sp,
                //color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Normal
            )
        }

        // New Password Field
        TextField(
            value = newPassword,
            placeholder = {
                Text(
                    text = stringResource(R.string.new_password),
                    //color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = newPasswordLabelStyle.value
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                //textColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = colorResource(R.color.colorPrimary),
                //unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = colorResource(R.color.colorPrimary),
                backgroundColor = Color.Transparent
            ),
            onValueChange = {
                newPassword = it
                newPasswordError = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        newPasswordLabelStyle.value = AppTypography.bodySmall
                    } else {
                        newPasswordLabelStyle.value = AppTypography.bodyLarge
                    }
                }
                .constrainAs(newPasswordField) {
                    top.linkTo(
                        if (currentPasswordError != null) currentPasswordErrorText.bottom
                        else currentPasswordField.bottom,
                        margin = 10.dp
                    )
                },
            maxLines = 1,
            singleLine = true,
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                Icon(
                    painter = imagePassword,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
            },
            isError = newPasswordError != null
        )

        // New Password Error Text
        if (newPasswordError != null) {
            Text(
                text = newPasswordError ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .constrainAs(newPasswordErrorText) {
                        top.linkTo(newPasswordField.bottom)
                    },
                fontSize = 16.sp,
                //color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Normal
            )
        }

        // Retype Password Field
        TextField(
            value = retypePassword,
            placeholder = {
                Text(
                    text = stringResource(R.string.retype_password),
                    //color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = retypePasswordLabelStyle.value
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                //textColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = colorResource(R.color.colorPrimary),
                //unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = colorResource(R.color.colorPrimary),
                backgroundColor = Color.Transparent
            ),
            onValueChange = {
                retypePassword = it
                confirmPasswordError = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        retypePasswordLabelStyle.value = AppTypography.bodySmall
                    } else {
                        retypePasswordLabelStyle.value = AppTypography.bodyLarge
                    }
                }
                .constrainAs(retypePasswordField) {
                    top.linkTo(
                        if (newPasswordError != null) newPasswordErrorText.bottom
                        else newPasswordField.bottom,
                        margin = 10.dp
                    )
                },
            maxLines = 1,
            singleLine = true,
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                Icon(
                    painter = imagePassword,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
            },
            isError = confirmPasswordError != null
        )

        // Confirm Password Error Text
        if (confirmPasswordError != null) {
            Text(
                text = confirmPasswordError ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .constrainAs(confirmPasswordErrorText) {
                        top.linkTo(retypePasswordField.bottom, margin = 5.dp)
                    },
                fontSize = 16.sp,
                //color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Normal
            )
        }

        // Show Passwords Text
        Text(
            text = stringResource(R.string.intro_register_show_password),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPasswords = !showPasswords }
                .constrainAs(showPasswordText) {
                    top.linkTo(retypePasswordField.bottom, margin = 20.dp)
                },
            textAlign = TextAlign.Center,
            //color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )

        // Progress Bar
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .constrainAs(progressBar) {
                        top.linkTo(showPasswordText.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                //color = MaterialTheme.colorScheme.primary
            )
        }

        // Apply Button
        Button(
            onClick = {
                //onChangePassword(currentPassword, newPassword, retypePassword)
            },
            modifier = Modifier
                .width(250.dp)
                .height(40.dp)
                .constrainAs(applyButton) {
                    top.linkTo(
                        if (isLoading) progressBar.bottom else showPasswordText.bottom,
                        margin = 50.dp
                    )
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    verticalBias = 0.3f
                },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.primary
//            )
        ) {
            Text(
                text = stringResource(R.string.app_generic_apply),
                letterSpacing = 0.1.em,
                //color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}