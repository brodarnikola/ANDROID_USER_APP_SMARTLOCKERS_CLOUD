package hr.sil.android.myappbox.compose.collect_parcel

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmEdittextColor
import hr.sil.android.myappbox.compose.components.ThmEdittextHintColor
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.theme.AppTypography
import hr.sil.android.myappbox.compose.theme.DarkModeTransparent
import hr.sil.android.myappbox.utils.isEmailValid
import androidx.compose.material3.MaterialTheme as Material3

@Composable
fun ShareAccessKeyScreen(
    shareAccessKeyId: Int,
    macAddress: String,
    viewModel: ShareAccessKeyViewModel = viewModel(),
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val showToastMessage = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }

    val emailLabelStyle = remember {
        mutableStateOf(AppTypography.labelLarge)
    }

    var errorMessageEmail by remember {
        mutableStateOf<String?>(null)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (showToastMessage.value) {
        Toast.makeText(context, toastMessage.value, Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TextViewWithFont(
                text = stringResource(R.string.share_parcel_acess).uppercase(),
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Medium,
                color = ThmTitleTextColor,
                letterSpacing = ThmTitleLetterSpacing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 15.dp, end = 15.dp),
                textAlign = TextAlign.Center,
                //uppercase = true
            )

            TextViewWithFont(
                text = stringResource(R.string.enter_email_to_share_access),
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = ThmDescriptionTextColor,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 20.dp, end = 20.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(20.dp))


            TextField(
                value = email,
                placeholder = {
                    Text(
                        text = stringResource(R.string.app_generic_email),
                        color = ThmEdittextHintColor, // ?attr/thmEdittextHintColor
                        style = emailLabelStyle.value
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = ThmEdittextColor, // ?attr/thmEdittextColor
                    focusedBorderColor = colorResource(R.color.colorPrimary),
                    unfocusedBorderColor = Material3.colorScheme.outline,
                    cursorColor = colorResource(R.color.colorPrimary),
                    backgroundColor = DarkModeTransparent
                ),
                onValueChange = {
                    email = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .semantics {
                        contentDescription = "emailTextFieldPasswordRecoveryScreen"
                    }
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
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_email),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .width(25.dp)
                    )
                },
                isError = errorMessageEmail != null
            )

            if (errorMessageEmail != null) {
                Text(
                    text = errorMessageEmail ?: "",
                    color = ThmErrorTextColor, // ?attr/thmErrorTextColor
                    fontSize = 14.sp,
                    style = AppTypography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                )
            }


            TextViewWithFont(
                text = stringResource(R.string.person_to_collect_parcel),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = ThmDescriptionTextColor,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 25.dp, end = 25.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 80.dp),
                    // color = ThmSplashProgressBarColor
                )
            } else {
                val error1 = stringResource(R.string.app_generic_error)
                val error2 = stringResource(R.string.grant_access_error_exists)
                val success = stringResource(R.string.app_generic_success)
                ButtonWithFont(
                    text = stringResource(R.string.app_generic_confirm).uppercase(),
                    fontSize = ThmButtonTextSize,
                    fontWeight = FontWeight.Medium,
                    backgroundColor = ThmMainButtonBackgroundColor,
                    textColor = ThmLoginButtonTextColor,
                    letterSpacing = ThmButtonLetterSpacing,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 80.dp)
                        .width(250.dp)
                        .height(40.dp),
                    onClick = {
                        showToastMessage.value = false
                        val emailValidation = viewModel.getEmailError(email, context)
                        if (emailValidation.isNotBlank()) {
                            errorMessageEmail = emailValidation.ifBlank { "" }
                        } else {
                            viewModel.addShareAccessKey(
                                email = email,
                                shareAccessKeyId = shareAccessKeyId,
                                onSuccess = {
                                    showToastMessage.value = true
                                    toastMessage.value = success
                                    navigateUp()
                                },
                                onError = { it ->
                                    showToastMessage.value = true
                                    if (it == R.string.app_generic_error)
                                        toastMessage.value = error1
                                    else
                                        toastMessage.value = error2
                                }
                            )
                        }
                    },
                    enabled = true,
                )
            }
        }
    }
}
