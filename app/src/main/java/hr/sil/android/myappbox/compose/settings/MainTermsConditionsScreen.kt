package hr.sil.android.myappbox.compose.settings

// Assuming your package structure
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleLetterSpacing
import hr.sil.android.myappbox.compose.components.ThmTitleTextColor
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize

@Composable
fun MainTermsConditionsScreen() {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
        // .background(ThmLoginBackground) // XML: ?attr/thmLoginBackground
    ) {
        val (titleRef, scrollContentRef) = createRefs()

        // 2. Title Text (tvSettingsTitle)
        TextViewWithFont(
            text = stringResource(id = R.string.terms_and_condtions_title).uppercase(),
            color = ThmTitleTextColor,
            fontSize = ThmTitleTextSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            letterSpacing = ThmTitleLetterSpacing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                .constrainAs(titleRef) {
                    top.linkTo(parent.top)
                }
        )

        // 3. ScrollView with Content
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 10.dp) // XML margin top/bottom 10dp
                .constrainAs(scrollContentRef) {
                    top.linkTo(titleRef.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints // Matches XML 0dp height behavior
                }
        ) {
            // Content Body (tvSettings)
            TextViewWithFont(
                text = stringResource(R.string.terms_and_condtions_zwick),
                color = ThmDescriptionTextColor,
                fontSize = ThmDescriptionTextSize,
                fontWeight = FontWeight.Normal,
                maxLines = 1000,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
        }
    }
}