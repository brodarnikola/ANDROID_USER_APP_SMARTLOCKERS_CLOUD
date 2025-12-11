package hr.sil.android.myappbox.compose.dialog

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.RoundedDialog
import hr.sil.android.myappbox.compose.components.SettingsRoundedBackground
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmButtonTextSizeInsideDialog
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginBackground
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.login_forgot_password.ForgotPasswordEvent
import hr.sil.android.myappbox.core.remote.model.RLanguage

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        RoundedDialog {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                val (closeIcon, titleText, subtitleText, cancelButton, confirmButton, spacer) = createRefs()

                Box(
                    modifier = Modifier 
                        .size(30.dp)
                        .constrainAs(closeIcon) {
                            top.linkTo(parent.top, margin = 15.dp)
                            end.linkTo(parent.end, margin = 15.dp)
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.btn_x),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            //.padding(top = 15.dp, end = 15.dp)
                            .size(15.dp)
                            .clickable(
                                onClick = onDismiss
                            ),
                        tint = colorResource(R.color.colorDarkGray)
                    )
                }

                // Title Text - "Logout Question"
                TextViewWithFont(
                    text = stringResource(R.string.logout_question),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(titleText) {
                            top.linkTo(closeIcon.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )

                // Subtitle Text - "Logout Again"
                TextViewWithFont(
                    text = stringResource(R.string.logout_again),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .constrainAs(subtitleText) {
                            top.linkTo(titleText.bottom, margin = 10.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )

                // Cancel Button
                ButtonWithFont(
                    onClick = onCancel,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(end = 10.dp)
                        .constrainAs(cancelButton) {
                            top.linkTo(subtitleText.bottom, margin = 20.dp)
                            start.linkTo(parent.start)
                            end.linkTo(confirmButton.start)
                        },
                    text = stringResource(id = R.string.app_generic_cancel).uppercase(),
                    backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                    textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                    fontSize = ThmButtonTextSizeInsideDialog, // ?attr/thmButtonTextSize
                    fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                    letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                    enabled = true,
                    width = 120.dp
                )

                // Confirm Button
                ButtonWithFont(
                    onClick = onConfirm,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 10.dp)
                        .constrainAs(confirmButton) {
                            top.linkTo(subtitleText.bottom, margin = 20.dp)
                            start.linkTo(cancelButton.end)
                            end.linkTo(parent.end)
                        },
                    text = stringResource(id = R.string.app_generic_confirm).uppercase(),
                    backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                    textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                    fontSize = ThmButtonTextSizeInsideDialog, // ?attr/thmButtonTextSize
                    fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                    letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                    enabled = true,
                    width = 120.dp
                )

                // Bottom Spacer
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .constrainAs(spacer) {
                            top.linkTo(confirmButton.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                // Create horizontal chain for buttons
                createHorizontalChain(
                    cancelButton,
                    confirmButton,
                    chainStyle = ChainStyle.Spread
                )
            }
        }
    }
}


@Composable
fun LogoutDialog1(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    RoundedDialog(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            //.background(backgroundColor, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp)
    ) {

        // X Button aligned to top-right
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.btn_x),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 15.dp, end = 15.dp)
                    .size(15.dp)
            )
        }

        // "logout_question"
        TextViewWithFont(
            text = stringResource(id = R.string.logout_question),
            color = ThmDescriptionTextColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp),
            textAlign = TextAlign.Center
        )

        // "logout_again"
        TextViewWithFont(
            text = stringResource(id = R.string.logout_again),
            color = ThmDescriptionTextColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
            textAlign = TextAlign.Center
        )

        // Buttons row (Cancel | Confirm)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ButtonWithFont(
                text = stringResource(id = R.string.app_generic_cancel).uppercase(),
                onClick = {
                    onCancel
                },
                backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                fontSize = ThmButtonTextSizeInsideDialog, // ?attr/thmButtonTextSize
                fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                modifier = Modifier
                    .width(250.dp)
                    .height(40.dp),
                enabled = true
            )

            ButtonWithFont(
                text = stringResource(id = R.string.app_generic_confirm).uppercase(),
                onClick = {
                    onConfirm
                },
                backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                fontSize = ThmButtonTextSizeInsideDialog, // ?attr/thmButtonTextSize
                fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                modifier = Modifier
                    .width(250.dp)
                    .height(40.dp),
                enabled = true
            )
        }

        // Bottom spacing
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
    }
}