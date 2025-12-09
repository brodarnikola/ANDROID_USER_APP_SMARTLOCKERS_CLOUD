package hr.sil.android.myappbox.compose.settings

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import hr.sil.android.myappbox.core.util.logger
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.uppercase

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    nextScreen: (route: String) -> Unit = {},
    navigateUp: () -> Unit = {}
) {

    //val state = viewModel.state.collectAsStateWithLifecycle().value

    val activity = LocalContext.current as Activity
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        val log = logger()
       // log.info("collecting events: start ${viewModel.uiEvents}")
//        viewModel.uiEvents.collect { event ->
//            log.info("collecting event: ${event}")
//            when (event) {
//                is SettingsScreenUiEvent.NavigateBack -> {
//                    navigateUp()
//                }
//
//                is SettingsScreenUiEvent.NavigateToScreen -> {
//                    nextScreen(event.route)
//                }
//
//                is UiEvent.ShowToast -> {
//                    Toast.makeText(context, event.message, event.toastLength).show()
//                }
//            }
//        }
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

                SettingsNotificationItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {

                    }
//                    onCheckedChange = {
//                        //viewModel.onEvent(SettingsScreenEvent.OnNotificationToggle(it))
//                    }
                )

                SettingsLanguageItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnLanguageClick)
                    }
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

                SettingsMyDetailsItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnMyDetailsClick)
                    }
                )

                SettingsQrCodeItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnQrCodeClick)
                    }
                )

                SettingsChangePasswordItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnChangePasswordClick)
                    }
                )

                SettingsSignOutItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnSignOutClick)
                    }
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

                SettingsHelpItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnHelpClick)
                    }
                )

                SettingsTermsItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnTermsClick)
                    }
                )

                SettingsPrivacyPolicyItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                        //viewModel.onEvent(SettingsScreenEvent.OnPrivacyPolicyClick)
                    }
                )

                SettingsContactItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    onClick = {
                       // viewModel.onEvent(SettingsScreenEvent.OnContactClick)
                    }
                )

                // Version
                TextViewWithFont(
                    text = "GET, FETCH VERSION", //state.currentVersion,
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
fun SettingsNotificationItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    SettingsRoundedBackground(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp) // matches XML top/bottom padding
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_collect_parcel), // your XML left icon
                contentDescription = null,
                modifier = Modifier.weight(1.5f),
                tint = Color.Unspecified
            )

            TextViewWithFont(
                text = stringResource(id = R.string.notifications_title),
                color = ThmDescriptionTextColor,
                fontSize = ThmSubTitleTextSize,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(7f),
                //maxLines = 1
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                modifier = Modifier.weight(1.5f),
                tint = Color.Unspecified
            )
        }
//        Switch(
//            checked = isChecked,
//            onCheckedChange = {
//                isChecked = it
//                onCheckedChange(it)
//            }
//        )
    }
}

@Composable
fun SettingsLanguageItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.nav_settings_language),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsMyDetailsItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.settings_account_details),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsQrCodeItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.user_identification_QR_code),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsChangePasswordItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.nav_settings_change_password),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsSignOutItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.app_generic_sign_out),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsHelpItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.app_generic_help),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsTermsItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.nav_ttc_title),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsPrivacyPolicyItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.settings_privacy_policy),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
fun SettingsContactItem(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextViewWithFont(
            text = stringResource(id = R.string.email_us),
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}