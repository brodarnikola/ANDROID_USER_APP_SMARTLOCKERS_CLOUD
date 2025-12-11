package hr.sil.android.myappbox.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.strokeBackground(
    backgroundColor: Color = Color.Transparent,
    strokeColor: Color,
    strokeWidth: Dp = 1.5.dp,
    cornerRadius: Dp = 5.dp
): Modifier = this.then(
    Modifier
        .border(
            width = strokeWidth,
            color = strokeColor,
            shape = RoundedCornerShape(cornerRadius)
        )
        .background(
            color = backgroundColor,
            shape = RoundedCornerShape(cornerRadius)
        )
)