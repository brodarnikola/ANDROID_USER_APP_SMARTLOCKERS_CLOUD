package hr.sil.android.myappbox.compose.send_parcel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmErrorTextColor
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmSubTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.login_forgot_password.ForgotPasswordEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendParcelDeliveryScreen(
    macAddress: String,
    pin: Int,
    size: String,
    onFinish: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: SendParcelDeliveryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(macAddress, pin, size) {
        viewModel.sendParcel(macAddress, pin, size)
    }

    LaunchedEffect(uiState.isUnauthorized) {
        if (uiState.isUnauthorized) {
            onNavigateToLogin()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            HorizontalDivider(
//                color = colorResource(R.color.colorDarkGray),
//                thickness = 1.dp
//            )

            Spacer(modifier = Modifier.height(40.dp))

            TextViewWithFont(
                text = stringResource(R.string.app_generic_send_parcel).uppercase(),
                color = ThmDescriptionTextColor,
                fontSize = ThmTitleTextSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 0.05.em,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(90.dp))

            Image(
                painter = painterResource(R.drawable.ic_parcel_sent),
                contentDescription = "Package Icon",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = colorResource(R.color.colorBlack)
                    )
                }

                uiState.isSuccess -> {

                    TextViewWithFont(
                        text = stringResource(R.string.nav_send_parcel_content_text),
                        color = ThmDescriptionTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.05.em,
                        maxLines = 10,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }

                uiState.hasError -> {

                    TextViewWithFont(
                        text = stringResource(R.string.nav_send_parcel_failed),
                        color = ThmErrorTextColor,
                        fontSize = ThmSubTitleTextSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.05.em,
                        maxLines = 10,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            when {
                uiState.isSuccess -> {

                    ButtonWithFont(
                        text = stringResource(id = R.string.app_generic_finish).uppercase(),
                        onClick = onFinish,
                        backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                        textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                        fontSize = ThmButtonTextSize, // ?attr/thmButtonTextSize
                        fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                        letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                        modifier = Modifier
                            .width(250.dp)
                            .height(40.dp),
                        enabled = true
                    )
                }

                // 8932 PIN
                // 4352 PIN SEND PARCEL DELIVERY PIN ,, SEND PARCEL PIN

                uiState.hasError -> {

                    ButtonWithFont(
                        text = stringResource(id = R.string.nav_send_parcel_error_button).uppercase(),
                        onClick = { viewModel.sendParcel(macAddress, pin, size) },
                        backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                        textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                        fontSize = ThmButtonTextSize, // ?attr/thmButtonTextSize
                        fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                        letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                        modifier = Modifier
                            .width(250.dp)
                            .height(40.dp),
                        enabled = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
