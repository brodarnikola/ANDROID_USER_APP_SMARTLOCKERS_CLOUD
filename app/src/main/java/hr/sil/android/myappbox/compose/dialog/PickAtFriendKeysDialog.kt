package hr.sil.android.myappbox.compose.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.cache.status.ActionStatusHandler.log
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.RoundedDialog
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSizeInsideDialog
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmEdittextColor
import hr.sil.android.myappbox.compose.components.ThmEdittextHintColor
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.theme.AppTypography
import hr.sil.android.myappbox.compose.theme.DarkModeTransparent
import hr.sil.android.myappbox.view.ui.activities.sendparcel.CancelPickedHomeDialog

@Composable
fun PickAtFriendKeysDialog(
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (email: String) -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }

    var errorMessageEmail by remember {
        mutableStateOf<String?>(null)
    }

    val isError = rememberSaveable { mutableStateOf(false) }
    val errorText = rememberSaveable { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        RoundedDialog {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {

                val (
                    titleText,
                    emailField,
                    cancelButton,
                    confirmButton,
                    spacer
                ) = createRefs()

                // ===== TITLE =====
                TextViewWithFont(
                    text = stringResource(R.string.access_sharing_pop_up_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .constrainAs(titleText) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = ThmDescriptionTextColor,
                    fontWeight = FontWeight.Normal
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                        .constrainAs(emailField) {
                            top.linkTo(titleText.bottom)
                        }
                ) {
                    // TextInputLayout + EditText
                    TextField(
                        value = email,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.app_generic_email),
                                color = ThmEdittextHintColor, // ?attr/thmEdittextHintColor
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = ThmEdittextColor, // ?attr/thmEdittextColor
                            focusedBorderColor = colorResource(R.color.colorPrimary),
                            //unfocusedBorderColor = Mater.colorScheme.outline,
                            cursorColor = colorResource(R.color.colorPrimary),
                            backgroundColor = DarkModeTransparent
                        ),
                        onValueChange = {
                            email = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "emailTextFieldPasswordRecoveryScreen"
                            },
//                            .onFocusChanged {
//                                if (it.isFocused) {
//                                    emailLabelStyle.value = AppTypography.bodySmall
//                                } else {
//                                    emailLabelStyle.value = AppTypography.bodyLarge
//                                }
//                            },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            androidx.compose.material.Icon(
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

                    // Display error message if exists
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
                }

                // ===== EMAIL INPUT =====
//                TextField(
//                    value = email,
//                    onValueChange = {
//                        email = it
//                    },
//                    onValueChange = {
//                        email = it
//                        if( email.contains("@")  ) {
//                            errorText.value = "Email in wrong format"
//                            isError.value = true
//                        }
//                        else {
//                            isError.value = false
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 10.dp)
//                        .constrainAs(emailField) {
//                            top.linkTo(titleText.bottom)
//                            start.linkTo(parent.start)
//                            end.linkTo(parent.end)
//                        },
//                    placeholder = {
//                        TextViewWithFont(
//                            text = stringResource(R.string.app_generic_email),
//                            color = ThmEdittextHintColor
//                        )
//                    },
//                    isError = isError,
//                    singleLine = true,
//                    textStyle = TextStyle(
//                        color = ThmEdittextColor,
//                        fontSize = 16.sp
//                    )
//                )
//
//                // Optional error text (TextInputLayout replacement)
//                if (isError.value && errorText.value != "") {
//                    TextViewWithFont(
//                        text = errorText.value,
//                        color = ThmErrorTextColor,
//                        fontSize = 12.sp,
//                        modifier = Modifier
//                            .padding(top = 4.dp)
//                            .constrainAs(spacer) {
//                                top.linkTo(emailField.bottom)
//                                start.linkTo(parent.start)
//                            }
//                    )
//                }

                // ===== CANCEL BUTTON =====
                ButtonWithFont(
                    onClick = onCancel,
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 20.dp)
                        .constrainAs(cancelButton) {
                            top.linkTo(emailField.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(confirmButton.start)
                        },
                    text = stringResource(R.string.app_generic_cancel),
                    backgroundColor = ThmMainButtonBackgroundColor,
                    textColor = ThmLoginButtonTextColor,
                    fontSize = ThmButtonTextSizeInsideDialog,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = ThmButtonLetterSpacing,
                    enabled = true
                )

                // ===== CONFIRM BUTTON =====
                ButtonWithFont(
                    onClick = {
                        log.info("onConfirm email 22: ${email}")
                        onConfirm(email)
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 20.dp)
                        .constrainAs(confirmButton) {
                            top.linkTo(emailField.bottom)
                            start.linkTo(cancelButton.end)
                            end.linkTo(parent.end)
                        },
                    text = stringResource(R.string.app_generic_confirm),
                    backgroundColor = ThmMainButtonBackgroundColor,
                    textColor = ThmLoginButtonTextColor,
                    fontSize = ThmButtonTextSizeInsideDialog,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = ThmButtonLetterSpacing,
                    enabled = true
                )

                // ===== BOTTOM SPACER =====
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                        .constrainAs(spacer) {
                            top.linkTo(confirmButton.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                // Horizontal chain (same as XML)
                createHorizontalChain(
                    cancelButton,
                    confirmButton,
                    chainStyle = ChainStyle.Spread
                )
            }
        }
    }
}