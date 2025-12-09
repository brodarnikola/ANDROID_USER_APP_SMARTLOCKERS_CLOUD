package hr.sil.android.myappbox.compose.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import hr.sil.android.myappbox.core.remote.model.RLanguage

@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel,
    navigateUp: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (tvSettingsTitle, tvLanguageSubtitle, languageList, btnApply) = createRefs()

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
            text = stringResource(id = R.string.nav_settings_app_language).uppercase(),
            color = ThmSubTitleTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 22.dp, end = 22.dp)
                .constrainAs(tvLanguageSubtitle) {
                    top.linkTo(tvSettingsTitle.bottom)
                }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(languageList) {
                    top.linkTo(tvLanguageSubtitle.bottom, margin = 10.dp)
                    //bottom.linkTo(btnApply.top, margin = 20.dp)
                }
        ) {
            items(uiState.availableLanguages) { language ->
                LanguageToggleItem(
                    language = language,
                    isSelected = uiState.selectedLanguage?.code == language.code,
                    onSelected = { viewModel.onLanguageSelected(language) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                )
            }
        }

        ButtonWithFont(
            text = stringResource(id = R.string.app_generic_apply).uppercase(),
            onClick = {
                viewModel.saveLanguageSettings(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Language saved successfully",
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
                color = colorResource(id = R.color.colorAccentZwick)
            )
        }
    }

    if (!uiState.isNetworkAvailable) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.8f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.Center
        ) {
            TextViewWithFont(
                text = stringResource(id = R.string.app_generic_no_network),
                color = ThmSubTitleTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 50.dp)
            )
        }
    }
}

@Composable
private fun LanguageToggleItem(
    language: RLanguage,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding( horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextViewWithFont(
            text = language.name ?: "",
            color = ThmDescriptionTextColor,
            fontSize = ThmSubTitleTextSize,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isSelected,
            onCheckedChange = { if (it) onSelected() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.colorAccentZwick),
                checkedTrackColor = colorResource(id = R.color.colorAccentZwick).copy(alpha = 0.3f),
                uncheckedThumbColor = colorResource(id = R.color.colorWhite),
                uncheckedTrackColor = colorResource(id = R.color.colorPrimaryDarkZwick)
            )
        )
    }
}

