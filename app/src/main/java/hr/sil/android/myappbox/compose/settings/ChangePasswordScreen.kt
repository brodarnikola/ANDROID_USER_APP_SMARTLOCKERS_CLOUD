package hr.sil.android.myappbox.compose.settings


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.R
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

@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    navigateUp: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    val password = rememberSaveable {
        mutableStateOf("")
    }
    val newPassword = rememberSaveable {
        mutableStateOf("")
    }
    val repeatPassword = rememberSaveable {
        mutableStateOf("")
    }

    val context = LocalContext.current

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
            //.systemBarsPadding()
    ) {
        val (title, subtitle, currentPasswordField, currentPasswordErrorText,
            newPasswordField, newPasswordErrorText, retypePasswordField,
            confirmPasswordErrorText, showPasswordText, progressBar, applyButton) = createRefs()

        TextViewWithFont(
            text = stringResource(R.string.settings_account_title).uppercase(),
            color = ThmDescriptionTextColor,
            fontSize = ThmTitleTextSize,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.em,
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 10.dp)
                }
        )

        TextViewWithFont(
            text = stringResource(R.string.nav_settings_change_password).uppercase(),
            color = ThmDescriptionTextColor,
            fontSize = ThmDescriptionTextSize,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            maxLines = 3,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .constrainAs(subtitle) {
                    top.linkTo(title.bottom, margin = 20.dp)
                }
        )

        // Current Password Field
        TextField(
            value = password.value,
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
                password.value = it
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
                color = colorResource(R.color.colorRedBadgeNumber),
                fontWeight = FontWeight.Normal
            )
        }

        // New Password Field
        TextField(
            value = newPassword.value,
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
                newPassword.value = it
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
                color = colorResource(R.color.colorRedBadgeNumber),
                fontWeight = FontWeight.Normal
            )
        }

        // Retype Password Field
        TextField(
            value = repeatPassword.value,
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
                repeatPassword.value = it
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
                color = colorResource(R.color.colorRedBadgeNumber),
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
                    top.linkTo(retypePasswordField.bottom, margin = 30.dp)
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

        val toShortPassword = stringResource(R.string.edit_user_validation_password_min_6_characters)
        ButtonWithFont(
            text = stringResource(id = R.string.app_generic_apply).uppercase(),
            onClick = {
                if( password.value.isBlank() || newPassword.value.isBlank() ) {
                    currentPasswordError = "Password can not be empty"
                    newPasswordError = "Password can not be empty"
                }
                else if( password.value.length < 6 || newPassword.value.length < 6 ) {
                    currentPasswordError = toShortPassword
                    newPasswordError = toShortPassword
                }
                else if( newPassword.value != repeatPassword.value ) {
                    newPasswordError = "Password needs to be the same"
                    confirmPasswordError = "Password needs to be the same"
                }
                else {
                    viewModel.saveNewPassword(
                        oldPassword = password.value,
                        newPassword = newPassword.value,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                R.string.nav_settings_password_update_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateUp()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
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
            enabled = true
        )
    }
}