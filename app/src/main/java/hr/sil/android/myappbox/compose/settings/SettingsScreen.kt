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
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.SignUpOnboardingActivity
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
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.uppercase

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    nextScreen: (route: String) -> Unit = {},
    nextScreenQrCode: (route: String, returnToScreen: Int, macAddress: String) -> Unit
) {

    //val state = viewModel.state.collectAsStateWithLifecycle().value

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    var appVersion by remember {
        mutableStateOf("")
    }

    var displayLogoutDialog by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    if (displayLogoutDialog) {
        LogoutDialog(
            onCancel = {
                displayLogoutDialog = false
            },
            onDismiss = {
                displayLogoutDialog = false
            },
            onConfirm = {
//                viewModel.logout(
//                    onSuccess = {
//                        displayLogoutDialog = false
//                        val intent = Intent(context, SignUpOnboardingActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                        context.startActivity(intent)
//                        activity.finish()
//                    },
//                    onError = {
//                        displayLogoutDialog = false
//                    }
//                )

//                displayLogoutDialog = false
//                scope.launch {
//                    withContext(Dispatchers.IO) {
//                        UserUtil.logout()
//                    }
//                    val intent = Intent(context, LoginActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    context.startActivity(intent)
//                    activity.finish()
//                }

                displayLogoutDialog = false
                CoroutineScope(Dispatchers.IO).launch {
                    UserUtil.logout()
                    withContext(Dispatchers.Main) {
                        val intent = Intent(context, SignUpOnboardingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        activity.finish()
                    }
                }
            }
        )
    }

    LaunchedEffect(key1 = Unit) {
        val log = logger()

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            appVersion = "Version: ${packageInfo.versionName}"
            log.info("collecting events: appVersion 11 $appVersion")
        } catch (e: PackageManager.NameNotFoundException) {
            appVersion = "Version: Unknown"
            log.info("collecting events: appVersion 22 $appVersion")
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (tvSettingsTitle, scrollContent) = createRefs()

        // 2. Title Text (tvSettingsTitle)
        TextViewWithFont(
            text = stringResource(id = R.string.app_generic_my_configuration).uppercase(),
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

        // 3. ScrollView with content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 10.dp)
                .constrainAs(scrollContent) {
                    top.linkTo(tvSettingsTitle.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
        ) {
            // SETTINGS Section
            TextViewWithFont(
                text = stringResource(id = R.string.nav_settings_title).uppercase(),
                color = ThmSubTitleTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 20.dp, end = 20.dp)
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_NOFITICATIONS)
                },
                startIcon = R.drawable.ic_notifications,
                text = R.string.notifications_title,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_LANGUAGE)
                },
                startIcon = R.drawable.ic_language,
                text = R.string.nav_settings_language,
                endIcon = R.drawable.ic_chevron_right_black
            )

            // ACCOUNT Section
            TextViewWithFont(
                text = stringResource(id = R.string.settings_account_title).uppercase(),
                color = ThmSubTitleTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_MY_DETAILS)
                    //viewModel.onEvent(SettingsScreenEvent.OnLanguageClick)
                },
                startIcon = R.drawable.ic_settings_account,
                text = R.string.settings_account_details,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    //goToDeviceDetails(MainDestinations.DEVICE_DETAILS, deviceId, nameOfDevice)
                    nextScreenQrCode(MainDestinations.SETTINGS_QR_CODE, 2, "")
                },
                startIcon = R.drawable.qr_code,
                text = R.string.user_identification_QR_code,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_CHANGE_PASSWORD)
                    //viewModel.onEvent(SettingsScreenEvent.OnLanguageClick)
                },
                startIcon = R.drawable.ic_password_zwick,
                text = R.string.nav_settings_change_password,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    displayLogoutDialog = true
                },
                startIcon = R.drawable.ic_settings_sign_out,
                text = R.string.app_generic_sign_out,
                endIcon = R.drawable.ic_chevron_right_black
            )

            // SUPPORT Section
            TextViewWithFont(
                text = stringResource(id = R.string.settings_support).uppercase(),
                color = ThmSubTitleTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_HELP)
                },
                startIcon = R.drawable.ic_help,
                text = R.string.app_generic_help,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_TERMS_AND_CONDITIONS)
                },
                startIcon = R.drawable.ic_terms,
                text = R.string.nav_ttc_title,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    nextScreen(MainDestinations.SETTINGS_PRIVACY_POLICY)
                },
                startIcon = R.drawable.ic_privacy,
                text = R.string.settings_privacy_policy,
                endIcon = R.drawable.ic_chevron_right_black
            )

            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                onClick = {
                    val emailIntent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("mailto:${BuildConfig.APP_BASE_EMAIL}")
                    )
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
                    context.startActivity(Intent.createChooser(emailIntent, ""))
                },
                startIcon = R.drawable.ic_email,
                text = R.string.email_us,
                endIcon = R.drawable.ic_chevron_right_black,
                hasEndIcon = false
            )

            // Version
            TextViewWithFont(
                text = appVersion, //state.currentVersion,
                color = ThmDescriptionTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp)
            )
        }
    }
}

// Individual Settings Items as Composables
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    startIcon: Int,
    text: Int,
    endIcon: Int,
    hasEndIcon: Boolean = true
) {
    SettingsRoundedBackground(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
        //.padding(top = 5.dp) // matches XML top/bottom padding
    ) {
        val paddingStart = if (hasEndIcon) 10.dp else 5.dp
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(end = 5.dp, start = paddingStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                painter = painterResource(id = startIcon), // your XML left icon
                contentDescription = null,
                modifier = Modifier.weight(1.5f),
                tint = Color.Unspecified
            )

            TextViewWithFont(
                text = stringResource(id = text),
                color = ThmDescriptionTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Light,
                modifier = Modifier.weight(7f),
                maxLines = 1
            )

            if (hasEndIcon)
                Icon(
                    painter = painterResource(id = endIcon),
                    contentDescription = null,
                    modifier = Modifier.weight(1.5f),
                    tint = Color.Unspecified
                )
        }
    }
}