package hr.sil.android.myappbox.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import hr.sil.android.myappbox.R

@Composable
fun SettingsRoundedBackground(
    modifier: Modifier = Modifier,
    radius: Dp = 5.dp,
    backgroundColor: Color = colorResource(id = R.color.colorPrimary),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(radius)
            )
    ) {
        content()
    }
}