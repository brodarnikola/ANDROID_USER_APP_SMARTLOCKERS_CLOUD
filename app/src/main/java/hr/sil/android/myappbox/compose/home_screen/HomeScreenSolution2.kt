//package hr.sil.android.myappbox.compose.home_screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//// --- 1. MOCK THEME & RESOURCES (Replace with your actual Theme/R values) ---
//
//object AppColors {
//    val LoginBackground = Color(0xFFF5F5F5)
//    val ToolbarBackground = Color(0xFFF5F5F5) // ?attr/thmLoginBackground
//    val TitleText = Color(0xFF333333)
//    val DescriptionText = Color(0xFF666666)
//    val ButtonText = Color.White
//    val BadgeBackground = Color.Red
//    val BadgeText = Color.White
//    val PrimaryDark = Color(0xFF00574B)
//    val DarkerBlack80 = Color(0xCC000000)
//
//    // Simulating the red background drawable
//    val RedButtonBackground = Color(0xFFD32F2F)
//}
//
//// Simulating your R.string and R.drawable
//object Res {
//    object string {
//        const val no_deliveries = "No deliveries to locker possible"
//        const val enter_pin = "ENTER VERIFICATION PIN"
//        const val selected_locker = "SELECTED LOCKER"
//        const val pickup_parcel = "PICKUP PARCEL"
//        const val send_parcel = "SEND PARCEL"
//        const val key_sharing = "KEY SHARING"
//        const val my_config = "MY CONFIGURATION"
//        const val humidity = "50%" // Example data
//        const val temperature = "22°C"
//        const val pressure = "1013 hPa"
//    }
//    object drawable {
//        // Placeholders - replace with actual resource IDs
//        val ic_map = android.R.drawable.ic_dialog_map
//        val ic_chevron_right = android.R.drawable.ic_media_play // Placeholder
//        val ic_copy_address = android.R.drawable.ic_menu_save // Placeholder
//        val ic_collect_parcel = android.R.drawable.ic_input_add // Placeholder
//        val ic_send_parcel = android.R.drawable.ic_menu_send // Placeholder
//        val ic_share_access = android.R.drawable.ic_menu_share // Placeholder
//        val ic_settings = android.R.drawable.ic_menu_preferences // Placeholder
//        val ic_humidity = android.R.drawable.ic_lock_idle_low_battery // Placeholder
//        val ic_temperature = android.R.drawable.ic_lock_idle_charging // Placeholder
//        val ic_pressure = android.R.drawable.ic_lock_power_off // Placeholder
//    }
//}
//
//// --- 2. MAIN SCREEN COMPOSABLE ---
//
//@Composable
//fun NavHomeScreenSolution2() {
//    // Scaffold provides the basic structure (TopBar, Content, etc.)
//    Scaffold(
//        containerColor = AppColors.LoginBackground,
//        topBar = { AppToolbar() }
//    ) { paddingValues ->
//
//        // The main scrollable content
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//        ) {
//
//            // -- Section 1: Verification Pin (Conditionally visible in logic) --
//            VerificationPinSection()
//
//            // -- Section 2: Welcome & Locker Selection --
//            LockerSelectionSection()
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // -- Section 3: Action Buttons (Grid) --
//            ActionButtonsGrid()
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            // -- Section 4: Telemetry (Bottom) --
//            TelemetrySection()
//
//            Spacer(modifier = Modifier.height(20.dp))
//        }
//    }
//}
//
//// --- 3. SUB-COMPONENTS (Breaking down the XML) ---
//
//@Composable
//fun AppToolbar() {
//    // Simulates the AppBarLayout / Toolbar / RelativeLayout structure
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp) // standard action bar size
//            .background(AppColors.ToolbarBackground)
//            .padding(horizontal = 16.dp)
//    ) {
//        // Center Image (Header)
//        // Image(painter = painterResource(...), modifier = Modifier.align(Alignment.Center))
//        Text(
//            text = "APP HEADER", // Placeholder for ?attr/thmToolbarHeader image
//            modifier = Modifier.align(Alignment.Center),
//            fontWeight = FontWeight.Bold
//        )
//
//        // Support Image (Right)
//        Icon(
//            painter = painterResource(id = android.R.drawable.ic_menu_help), // ?attr/thmToolbarHeaderSupportImage
//            contentDescription = "Support",
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .size(24.dp)
//        )
//    }
//}
//
//@Composable
//fun VerificationPinSection() {
//    // Corresponds to llVerificationPinDisabled
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp, vertical = 10.dp)
//            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)) // @drawable/round_enter_verification_pin
//            .padding(10.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = Res.string.no_deliveries,
//            color = AppColors.DarkerBlack80,
//            fontSize = 16.sp, // ?attr/thmTitleTextSize
//            textAlign = TextAlign.Center,
//            maxLines = 3,
//            overflow = TextOverflow.Ellipsis
//        )
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        Button(
//            onClick = { /* Handle Click */ },
//            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryDark), // background="@drawable/enter_verification_pin"
//            shape = RoundedCornerShape(20.dp), // approximate shape
//            modifier = Modifier.width(230.dp)
//        ) {
//            Text(
//                text = Res.string.enter_pin,
//                color = AppColors.ButtonText,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium
//            )
//        }
//    }
//}
//
//@Composable
//fun LockerSelectionSection() {
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        // "SELECTED LOCKER" Title
//        Text(
//            text = Res.string.selected_locker,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp),
//            color = AppColors.TitleText,
//            fontSize = 16.sp
//        )
//
//        // City Selector Row + Map Icon
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 18.dp, vertical = 10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Dropdown button area (llChooseCityLocker)
//            Row(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(40.dp)
//                    .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp)) // @drawable/rounded_button_primary
//                    .clickable { /* Select City */ }
//                    .padding(horizontal = 10.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Zagreb, Main Station", // Placeholder for tvChoosenCityLocker
//                    modifier = Modifier.weight(1f),
//                    color = Color.Black,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Icon(
//                    painter = painterResource(id = Res.drawable.ic_chevron_right),
//                    contentDescription = null
//                )
//            }
//
//            Spacer(modifier = Modifier.width(10.dp))
//
//            // Map Icon
//            Icon(
//                painter = painterResource(id = Res.drawable.ic_map),
//                contentDescription = "Map",
//                modifier = Modifier
//                    .size(32.dp)
//                    .clickable { /* Open Map */ },
//                tint = AppColors.PrimaryDark
//            )
//        }
//
//        // Address & Copy (clAddressAndUsername)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 18.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = "User: 12345678", // tvUniqueUserNumber
//                    color = AppColors.TitleText,
//                    fontSize = 16.sp
//                )
//                Text(
//                    text = "Street 123, 10000 Zagreb", // tvAddress
//                    color = AppColors.TitleText,
//                    fontSize = 16.sp,
//                    maxLines = 2
//                )
//            }
//
//            IconButton(onClick = { /* Copy Address */ }) {
//                Icon(
//                    painter = painterResource(id = Res.drawable.ic_copy_address),
//                    contentDescription = "Copy"
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ActionButtonsGrid() {
//    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)) {
//
//        // Row 1: Collect & Send
//        Row(modifier = Modifier.fillMaxWidth()) {
//            // Collect Parcel
//            ActionButton(
//                modifier = Modifier.weight(1f),
//                iconRes = Res.drawable.ic_collect_parcel,
//                text = Res.string.pickup_parcel,
//                badgeCount = 0 // set to > 0 to see badge
//            )
//
//            // Send Parcel
//            ActionButton(
//                modifier = Modifier.weight(1f),
//                iconRes = Res.drawable.ic_send_parcel,
//                text = Res.string.send_parcel
//            )
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Row 2: Share & Configuration
//        Row(modifier = Modifier.fillMaxWidth()) {
//            // Share Access
//            ActionButton(
//                modifier = Modifier.weight(1f),
//                iconRes = Res.drawable.ic_share_access,
//                text = Res.string.key_sharing
//            )
//
//            // Configuration
//            ActionButton(
//                modifier = Modifier.weight(1f),
//                iconRes = Res.drawable.ic_settings,
//                text = Res.string.my_config
//            )
//        }
//    }
//}
//
//@Composable
//fun ActionButton(
//    modifier: Modifier = Modifier,
//    iconRes: Int,
//    text: String,
//    badgeCount: Int = 0
//) {
//    Box(
//        modifier = modifier.padding(4.dp),
//        contentAlignment = Alignment.TopCenter
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth().clickable { /* Action */ }
//        ) {
//            // Icon
//            Image(
//                painter = painterResource(id = iconRes),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(60.dp) // approximate size
//                    .alpha(0.6f), // matches android:alpha="0.2" roughly (adjusted for visibility)
//                colorFilter = ColorFilter.tint(AppColors.PrimaryDark)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Text
//            Text(
//                text = text,
//                color = AppColors.DescriptionText,
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center,
//                maxLines = 2
//            )
//        }
//
//        // Badge (Visibility logic included)
//        if (badgeCount > 0) {
//            Box(
//                modifier = Modifier
//                    .size(38.dp)
//                    .offset(x = 20.dp, y = (-5).dp) // Adjust position relative to center
//                    .clip(CircleShape)
//                    .background(AppColors.BadgeBackground),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = badgeCount.toString(),
//                    color = AppColors.BadgeText,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun TelemetrySection() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 40.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        TelemetryItem(Res.drawable.ic_humidity, Res.string.humidity)
//        TelemetryItem(Res.drawable.ic_temperature, Res.string.temperature)
//        TelemetryItem(Res.drawable.ic_pressure, Res.string.pressure)
//    }
//}
//
//@Composable
//fun TelemetryItem(iconRes: Int, text: String) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Image(
//            painter = painterResource(id = iconRes),
//            contentDescription = null,
//            modifier = Modifier.size(24.dp)
//        )
//        Spacer(modifier = Modifier.width(5.dp))
//        Text(
//            text = text,
//            fontSize = 13.sp,
//            fontWeight = FontWeight.Medium,
//            color = AppColors.DescriptionText
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewMainActivity() {
//    NavHomeScreen()
//}