package hr.sil.android.myappbox.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import hr.sil.android.myappbox.R

@Composable
fun Modifier.groupNameBackground() = this
    .border(
        width = 2.dp,
        color = colorResource(R.color.colorPrimary),
        shape = RoundedCornerShape(2.dp)
    )
    .background(
        color = colorResource(R.color.transparentColor),
        shape = RoundedCornerShape(2.dp)
    )
    .padding(1.dp)