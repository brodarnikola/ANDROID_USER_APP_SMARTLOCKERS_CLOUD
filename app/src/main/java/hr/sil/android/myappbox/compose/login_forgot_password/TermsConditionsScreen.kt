package hr.sil.android.myappbox.compose.login_forgot_password

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.SignUpOnboardingSections
import hr.sil.android.myappbox.compose.components.ButtonWithFont
import hr.sil.android.myappbox.compose.components.GradientBackground
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmButtonLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmButtonTextSize
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmLoginButtonTextColor
import hr.sil.android.myappbox.compose.components.ThmMainButtonBackgroundColor
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.components.ThmToolbarBackgroundColor
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.utils.UiEvent

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TermsConditionsScreen(
    modifier: Modifier = Modifier,
    nextScreen: (route: String) -> Unit = {},
    navigateUp: () -> Unit = {}
) {

    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val isButtonEnabled = remember { mutableStateOf(false) }

    // Check if user has scrolled to the end
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        isButtonEnabled.value = scrollState.value >= scrollState.maxValue
    }

    // Main container is ConstraintLayout to replicate XML's behavior
    GradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (appBar, tvSettingsTitle, tvBeforeProceeding, scrollView, llBottomLayout) = createRefs()

            // 1. AppBarLayout (appBarLayout)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(appBar) {
                        top.linkTo(parent.top)
                    }
            ) {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .padding(end = 30.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.logo_header_zwick), // ?attr/thmToolbarHeader
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.align(Alignment.Center) // layout_centerInParent="true"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        //containerColor = ThmLoginBackground,
                        //containerColor = colorResource(R.color.colorPrimary),
                        //titleContentColor = colorResource(R.color.colorWhite),
                        navigationIconContentColor = colorResource(R.color.colorBlack)
                    )
                )
            }

            // 2. Title Text (tvSettingsTitle)
            TextViewWithFont(
                text = stringResource(id = R.string.terms_and_condtions_title).uppercase(), // textAllCaps="true"
                color = ThmTitleTextColor, // ?attr/thmTitleTextColor
                fontSize = ThmTitleTextSize, // ?attr/thmTitleTextSize
                fontWeight = FontWeight.Medium, // ?attr/thmMainFontTypeMedium
                textAlign = TextAlign.Center,
                letterSpacing = ThmTitleLetterSpacing, // ?attr/thmTitleLetterSpacing
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                    .constrainAs(tvSettingsTitle) {
                        top.linkTo(appBar.bottom)
                    }
            )

            // 3. Subtitle Text (tvBeforeProceeding)
            TextViewWithFont(
                text = stringResource(id = R.string.nav_ttc_subtitle),
                color = ThmTitleTextColor, // ?attr/thmTitleTextColor
                fontSize = ThmDescriptionTextSize, // ?attr/thmDescriptionTextSize
                fontWeight = FontWeight.Normal, // ?attr/thmMainFontTypeRegular
                textAlign = TextAlign.Start, // gravity="left"
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 30.dp, end = 30.dp)
                    .constrainAs(tvBeforeProceeding) {
                        top.linkTo(tvSettingsTitle.bottom)
                    }
            )

            // 4. ScrollView with content (scrollView)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .constrainAs(scrollView) {
                        top.linkTo(tvBeforeProceeding.bottom)
                        bottom.linkTo(llBottomLayout.top)
                        height = Dimension.fillToConstraints
                    }
            ) {
                TextViewWithFont(
                    text = stringResource(id = R.string.terms_and_condtions_zwick), // ?attr/thmTermsConditionsDescription
                    color = ThmDescriptionTextColor, // ?attr/thmDescriptionTextColor
                    fontSize = ThmDescriptionTextSize, // ?attr/thmDescriptionTextSize
                    fontWeight = FontWeight.Normal, // ?attr/thmMainFontTypeRegular
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 5. Bottom Layout with Button (llBottomLayout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .constrainAs(llBottomLayout) {
                        bottom.linkTo(parent.bottom)
                    },
                contentAlignment = Alignment.Center // gravity="center"
            ) {
                ButtonWithFont(
                    text = "OK",
                    onClick = {
                        nextScreen(SignUpOnboardingSections.REGISTRATION_SCREEN.route)
//                        viewModel.onEvent(
//                            TermsConditionEvent.OnOkClick(
//                                context = context
//                            )
//                        )
                    },
                    backgroundColor = ThmMainButtonBackgroundColor, // ?attr/thmMainButtonBackgroundColor
                    textColor = ThmLoginButtonTextColor, // ?attr/thmLoginButtonTextColor
                    fontSize = ThmButtonTextSize, // ?attr/thmButtonTextSize
                    fontWeight = FontWeight.Normal, // ?attr/thmMainFontTypeRegular
                    letterSpacing = ThmButtonLetterSpacing, // ?attr/thmButtonLetterSpacing
                    enabled = isButtonEnabled.value,
                    modifier = Modifier
                        .width(200.dp)
                        .wrapContentHeight()
                        .padding(horizontal = 5.dp)
                )
            }
        }
    }
}