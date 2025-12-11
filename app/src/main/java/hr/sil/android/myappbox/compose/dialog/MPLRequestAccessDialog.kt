package hr.sil.android.myappbox.compose.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSizeInsideDialog
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.view.ui.activities.sendparcel.MplRequestAccessDialog

@Composable
fun MplRequestAccessDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        RoundedDialog {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                val (closeIcon,  subtitleText, confirmButton, spacer) = createRefs()

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

                // Subtitle Text - "Logout Again"
                TextViewWithFont(
                    text = stringResource(R.string.logout_again),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .constrainAs(subtitleText) {
                            top.linkTo(closeIcon.bottom, margin = 10.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )

                // Confirm Button
                ButtonWithFont(
                    onClick = onConfirm,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 10.dp)
                        .constrainAs(confirmButton) {
                            top.linkTo(subtitleText.bottom, margin = 20.dp)
                            start.linkTo(parent.end)
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
            }
        }
    }
}