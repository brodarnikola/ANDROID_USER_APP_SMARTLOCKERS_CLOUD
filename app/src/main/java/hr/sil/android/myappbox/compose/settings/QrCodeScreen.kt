package hr.sil.android.myappbox.compose.settings

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LocalLifecycleOwner
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.compose.theme.Black
import hr.sil.android.myappbox.compose.theme.White
import hr.sil.android.myappbox.events.QrCodeScannedEvent
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.activities.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlin.text.get

import android.graphics.Color
import hr.sil.android.myappbox.compose.home_screen.HomeScreenUiEvent
import hr.sil.android.myappbox.compose.main_activity.MainDestinations

val GO_TO_HOME_SCREEN = 1
val GO_TO_SETTINGS_SCREEN = 2
val GO_TO_PICKUP_PARCEL_SCREEN = 3

@Composable
fun DisplayQrCodeScreen(
    returnToScreen: Int,
    macAddress: String = "",
    qrCodeData: String?
) {
    var isLoading by remember { mutableStateOf(true) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val title = when (returnToScreen) {
        1 -> stringResource(R.string.collect_QR_code)
        else -> stringResource(R.string.user_identification_QR_code)
    }

    // Generate QR code when screen launches
    LaunchedEffect(qrCodeData) {
        withContext(Dispatchers.Default) {
            qrBitmap = generateQrCodeBitmap(qrCodeData)
        }
        isLoading = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(colorResource(R.color.colorWhite))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            QrCodeContent(
                title = title,
                qrBitmap = qrBitmap
            )
        }
    }
}

@Composable
fun QrCodeContent(
    title: String,
    qrBitmap: Bitmap?
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (titleText, qrImage, bottomSection) = createRefs()

        // Title
        Text(
            text = title.uppercase(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .constrainAs(titleText) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(qrImage.top)
                    height = Dimension.percent(0.1f)
                },
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 0.1.em,
            fontWeight = FontWeight.Medium,
//            style = MaterialTheme.typography.titleLarge.copy(
//                textTransform = TextTransform.Uppercase
//            )
        )

        // QR Code Image
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .constrainAs(qrImage) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottomSection.top)
                        height = Dimension.percent(0.6f)
                    },
                contentScale = ContentScale.Fit
            )
        }

        // Bottom Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .constrainAs(bottomSection) {
                    top.linkTo(qrImage.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.percent(0.3f)
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(R.string.collect_master_QR_code_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

//private fun generateQrCodeBitmap(data: String?): Bitmap? {
//
//    //log.info("Final qrCode in displayQrCodeActivity is: ${UserUtil.user?.identificationQrCode}")
//
//    val bitMatrix: BitMatrix?
//    bitMatrix = try {
//        MultiFormatWriter().encode(
//            UserUtil.user?.identificationQrCode,
//            BarcodeFormat.QR_CODE,
//            350, 350, null
//        )
//    } catch (illegalargumentexception: IllegalArgumentException) {
//        //log.info("exceptions is: ${illegalargumentexception}")
//        null
//    }
//    val bitMatrixWidth = bitMatrix?.width ?: 1
//    val bitMatrixHeight = bitMatrix?.height ?: 1
//    val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
//    for (y in 0 until bitMatrixHeight) {
//        val offset = y * bitMatrixWidth
//        for (x in 0 until bitMatrixWidth) {
//            pixels[offset + x] =
//                if (bitMatrix!!.get(x, y)) Color.BLACK else Color.WHITE
//        }
//    }
//    val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
//    bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight)
//    return bitmap
//    //binding.ivImage.setImageBitmap(bitmap)
//}

// QR Code Generation Function
fun generateQrCodeBitmap(data: String?): Bitmap? {
    if (data.isNullOrEmpty()) return null

    return try {
        val bitMatrix = MultiFormatWriter().encode(
            data,
            BarcodeFormat.QR_CODE,
            350,
            350,
            null
        )

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix!!.get(x, y)) Color.BLACK else Color.WHITE
            }
        }

        Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).apply {
            setPixels(pixels, 0, 350, 0, 0, width, height)
        }
    } catch (e: IllegalArgumentException) {
        Log.e("QRCode", "Error generating QR code: ${e.message}")
        null
    }
}

// Navigation wrapper for Activity-based navigation
@Composable
fun DisplayQrCodeScreenWrapper(
    returnToScreen: Int,
    macAddress: String,
    nextScreen: (route: String) -> Unit
) {
    // Get user QR code data from your UserUtil or ViewModel
    val qrCodeData = remember { UserUtil.user?.identificationQrCode }

    // Handle EventBus events if needed
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val eventBus = App.ref.eventBus

        val qrScannedSubscriber = object {
            @Subscribe(threadMode = ThreadMode.BACKGROUND)
            fun onQrScanned(event: QrCodeScannedEvent) {
                if( returnToScreen == GO_TO_PICKUP_PARCEL_SCREEN ) {
                    nextScreen(MainDestinations.PARCEL_PICKUP)
                }
                else if ( returnToScreen == GO_TO_SETTINGS_SCREEN ) {
                    nextScreen(MainDestinations.SETTINGS)
                }
                else if( returnToScreen == GO_TO_HOME_SCREEN ) {
                    nextScreen(MainDestinations.HOME)
                }
            }

            @Subscribe(threadMode = ThreadMode.MAIN)
            fun onUnauthorized(event: UnauthorizedUserEvent) {
                // Navigate to login
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            }
        }

        eventBus.register(qrScannedSubscriber)

        onDispose {
            eventBus.unregister(qrScannedSubscriber)
        }
    }

    // Handle back navigation
//    BackHandler {
//        onNavigateBack(returnToScreen, macAddress)
//    }

    DisplayQrCodeScreen(
        returnToScreen = returnToScreen,
        macAddress = macAddress,
        qrCodeData = qrCodeData
        //,
        //onBackClick = { onNavigateBack(returnToScreen, macAddress) }
    )
}