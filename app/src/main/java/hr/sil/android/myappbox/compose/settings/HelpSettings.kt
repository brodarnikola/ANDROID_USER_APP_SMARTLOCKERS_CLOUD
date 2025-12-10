package hr.sil.android.myappbox.compose.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.components.TextViewWithFont
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextColor
import hr.sil.android.myappbox.compose.components.ThmDescriptionTextSize
import hr.sil.android.myappbox.compose.components.ThmTitleTextSize
import hr.sil.android.myappbox.compose.theme.Black

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HelpHorizontalPager() {
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val (mainContent, bottomButton) = createRefs()
        HorizontalPager(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    height = Dimension.fillToConstraints
                },
            state = pagerState
        ) { page ->

            val titleRes = when (page) {
                0 -> R.string.collect_parcel_title
                1 -> R.string.sending_parcels_title
                else -> R.string.app_generic_access_sharing
            }

            val descRes = when (page) {
                0 -> R.string.collect_parcel_help_text
                1 -> R.string.send_parcel_help_text
                else -> R.string.sharing_help_text
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //if (page != 3) {
                Spacer(modifier = Modifier.heightIn(min = 5.dp))

                TextViewWithFont(
                    text = stringResource(titleRes).uppercase(),
                    color = ThmDescriptionTextColor,
                    fontSize = ThmTitleTextSize,
                    //fontWeight = FontWeight.Normal,
                    maxLines = 1000,
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.heightIn(min = 16.dp))

                TextViewWithFont(
                    text = stringResource(descRes),
                    color = ThmDescriptionTextColor,
                    fontSize = ThmDescriptionTextSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1000,
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(horizontal = 20.dp)
                )
            }
        }

        Column(
            modifier = Modifier.constrainAs(bottomButton) {
                top.linkTo(mainContent.bottom)
                bottom.linkTo(parent.bottom, margin = 30.dp)
                start.linkTo(parent.start, margin = 24.dp)
                end.linkTo(parent.end, margin = 24.dp)
                width = Dimension.fillToConstraints
                //height = Dimension.wrapContent
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            DotsIndicator(
                totalDots = 3,
                selectedIndex = pagerState.currentPage,
                selectedColor = colorResource(id = R.color.colorPrimary),
                unSelectedColor = colorResource(id = R.color.colorAccentZwick)
            )

            TextViewWithFont(
                text = stringResource(R.string.help_lbl_bottom),
                color = ThmDescriptionTextColor,
                fontSize = ThmDescriptionTextSize,
                //fontWeight = FontWeight.Normal,
                maxLines = 4,
                modifier = Modifier
                    //.fillMaxSize()
                    .padding(horizontal = 10.dp)
            )

            TextViewWithFont(
                text = stringResource(R.string.help_link),
                color = colorResource(id = R.color.colorAccentZwick),
                fontSize = ThmDescriptionTextSize,
                //fontWeight = FontWeight.Normal,
                maxLines = 4,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable(
                        onClick = {
                            val emailIntent = Intent(
                                Intent.ACTION_SENDTO,
                                Uri.parse("mailto:${BuildConfig.APP_BASE_EMAIL}")
                            )
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
                            context.startActivity(Intent.createChooser(emailIntent, ""))
                        }
                    )
            )
        }

    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {

    LazyRow(
        horizontalArrangement = Arrangement.Center,
    ) {
        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            }
        }
    }
}