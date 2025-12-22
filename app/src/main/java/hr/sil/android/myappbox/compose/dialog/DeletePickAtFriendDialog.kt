package hr.sil.android.myappbox.compose.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor

@Composable
fun DeletePickAtFriendDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    shareAccessEmail: String
) {
    Dialog(onDismissRequest = onDismiss) {
        RoundedDialog {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                val ( titleText, subtitleText, cancelButton, confirmButton, spacer) = createRefs()

                // Title Text - "Logout Question"
                TextViewWithFont(
                    text = stringResource(R.string.parcel_pickup_delete_key_title, shareAccessEmail),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(titleText) {
                            top.linkTo(parent.top, margin = 15.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )

                // Subtitle Text - "Logout Again"
                TextViewWithFont(
                    text = stringResource(R.string.pickup_parcel_delete_content),
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
                    maxLines = 3,
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