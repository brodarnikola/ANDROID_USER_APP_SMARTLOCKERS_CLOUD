package hr.sil.android.myappbox.compose.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.SettingsRoundedBackground
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
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

@Composable
fun SettingsNotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsNotificationsViewModel,
    navigateUp: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
        //.background(ThmLoginBackground)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (tvSettingsTitle, tvNotificationsSubtitle, clPushNotification, clEmail, btnApply) = createRefs()

            TextViewWithFont(
                text = stringResource(id = R.string.settings_title).uppercase(),
                color = ThmTitleTextColor,
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = ThmTitleLetterSpacing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                    .constrainAs(tvSettingsTitle) {
                        top.linkTo(parent.top)
                    }
            )

            TextViewWithFont(
                text = stringResource(id = R.string.app_generic_notifications).uppercase(),
                color = ThmSubTitleTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 22.dp, end = 22.dp)
                    .constrainAs(tvNotificationsSubtitle) {
                        top.linkTo(tvSettingsTitle.bottom)
                    }
            )

            NotificationToggleItem(
                label = stringResource(id = R.string.app_generic_push_notifications),
                isChecked = uiState.pushNotifications,
                onCheckedChange = { viewModel.onPushNotificationsChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .constrainAs(clPushNotification) {
                        top.linkTo(tvNotificationsSubtitle.bottom, margin = 10.dp)
                    }
            )

            NotificationToggleItem(
                label = stringResource(id = R.string.app_generic_email),
                isChecked = uiState.emailNotifications,
                onCheckedChange = { viewModel.onEmailNotificationsChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .constrainAs(clEmail) {
                        top.linkTo(clPushNotification.bottom, margin = 10.dp)
                    }
            )

            ButtonWithFont(
                text = stringResource(id = R.string.app_generic_apply).uppercase(),
                onClick = {
                    viewModel.saveNotificationSettings(
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Settings saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateUp()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
                    .constrainAs(btnApply) {
                        bottom.linkTo(parent.bottom, margin = 30.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                enabled = uiState.isSaveEnabled && !uiState.isLoading,
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.colorPrimary)
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextViewWithFont(
            text = label,
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.colorAccentZwick),
                checkedTrackColor = colorResource(id = R.color.colorAccentZwick).copy(alpha = 0.3f),
                uncheckedThumbColor = colorResource(id = R.color.colorWhite),
                uncheckedTrackColor = colorResource(id = R.color.colorPrimaryDarkZwick)
            )
        )
    }
}

