//package hr.sil.android.myappbox.compose.collect_parcel
//
//
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import hr.sil.android.myappbox.R
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.constraintlayout.compose.ConstraintLayout
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.runtime.Composable
//import androidx.lifecycle.viewmodel.compose.viewModel
//import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
//
//@Composable
//fun PickupParcelScreen1(
//    viewModel: PickupParcelViewModel = viewModel(),
//    uiState: PickupParcelUiState,
//    onOpenClick: () -> Unit,
//    onForceOpenClick: () -> Unit,
//    onFinishClick: () -> Unit,
//    onCleaningChecked: () -> Unit,
//    onKeyAction: (RCreatedLockerKey) -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    ConstraintLayout(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        val (
//            title,
//            inProximity,
//            notInProximity
//        ) = createRefs()
//
//        Text(
//            text = stringResource(R.string.nav_pickup_parcel_lock).uppercase(),
//            modifier = Modifier
//                .constrainAs(title) {
//                    top.linkTo(parent.top)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//                .padding(horizontal = 8.dp),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.titleLarge
//        )
//
//        if (uiState.isInProximity) {
//            InProximityContent(
//                modifier = Modifier.constrainAs(inProximity) {
//                    top.linkTo(title.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    bottom.linkTo(parent.bottom)
//                },
//                uiState = uiState,
//                onOpenClick = onOpenClick,
//                onForceOpenClick = onForceOpenClick,
//                onFinishClick = onFinishClick,
//                onCleaningChecked = onCleaningChecked,
//                onKeyAction = onKeyAction
//            )
//        } else {
//            NotInProximityContent(
//                modifier = Modifier.constrainAs(notInProximity) {
//                    top.linkTo(title.bottom)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                    bottom.linkTo(parent.bottom)
//                },
//                deviceName = uiState.lockerName
//            )
//        }
//    }
//}
//
//@Composable
//fun InProximityContent(
//    modifier: Modifier,
//    uiState: PickupParcelUiState,
//    onOpenClick: () -> Unit,
//    onForceOpenClick: () -> Unit,
//    onFinishClick: () -> Unit,
//    onCleaningChecked: () -> Unit,
//    onKeyAction: (RCreatedLockerKey) -> Unit
//) {
//    Column(
//        modifier = modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Box(
//            modifier = Modifier
//                .weight(0.35f)
//                .clickable(enabled = uiState.openEnabled, onClick = onOpenClick),
//            contentAlignment = Alignment.Center
//        ) {
//            Image(
//                painter = painterResource(uiState.circleDrawable),
//                contentDescription = null
//            )
//            Image(
//                painter = painterResource(R.drawable.ic_padlock_top),
//                contentDescription = null,
//                modifier = Modifier.padding(bottom = 30.dp)
//            )
//            Image(
//                painter = painterResource(R.drawable.ic_padlock_base),
//                contentDescription = null
//            )
//        }
//
//        Text(
//            text = uiState.statusText,
//            modifier = Modifier
//                .weight(0.1f)
//                .padding(horizontal = 8.dp),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.bodyLarge
//        )
//
//        LazyColumn(
//            modifier = Modifier.weight(0.47f)
//        ) {
//            items(uiState.keys) { key ->
//                ParcelPickupKeyItem(
//                    key = key,
//                    onAction = { onKeyAction(key) }
//                )
//            }
//        }
//
//        if (uiState.showTelemetry) {
//            TelemetryRow(
//                humidity = uiState.humidity,
//                temperature = uiState.temperature,
//                pressure = uiState.pressure
//            )
//        }
//
//        if (uiState.showForceOpen) {
//            Button(
//                onClick = onForceOpenClick,
//                modifier = Modifier
//                    .padding(top = 20.dp)
//                    .height(48.dp)
//                    .width(250.dp)
//            ) {
//                Text(stringResource(R.string.app_generic_force_open))
//            }
//        }
//
//        if (uiState.showFinish) {
//            Button(
//                onClick = onFinishClick,
//                modifier = Modifier
//                    .padding(bottom = 140.dp)
//                    .height(48.dp)
//                    .width(250.dp)
//            ) {
//                Text(stringResource(R.string.app_generic_confirm))
//            }
//        }
//
//        if (uiState.showCleaning) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.padding(horizontal = 20.dp)
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.ic_needs_cleaning),
//                    contentDescription = null
//                )
//                Checkbox(
//                    checked = uiState.cleaningChecked,
//                    onCheckedChange = { onCleaningChecked() }
//                )
//                Text(stringResource(R.string.locker_needs_cleaning))
//            }
//        }
//
//        if (uiState.cleaningLoading) {
//            CircularProgressIndicator(modifier = Modifier.size(40.dp))
//        }
//    }
//}
//
//@Composable
//fun NotInProximityContent(
//    modifier: Modifier,
//    deviceName: String
//) {
//    Column(
//        modifier = modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(R.drawable.ic_not_in_proximity),
//            contentDescription = null
//        )
//        Text(
//            text = stringResource(R.string.not_in_proximity_first_description),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(20.dp)
//        )
//        Text(
//            text = stringResource(
//                R.string.not_in_proximity_second_description,
//                deviceName
//            ),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(20.dp)
//        )
//    }
//}
//
//@Composable
//fun ParcelPickupKeyItem(
//    key: RCreatedLockerKey,
//    onAction: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = key.displayText,
//            modifier = Modifier.weight(0.85f)
//        )
//        IconButton(
//            onClick = onAction,
//            modifier = Modifier.weight(0.15f)
//        ) {
//            Icon(
//                painter = painterResource(key.actionIcon),
//                contentDescription = null
//            )
//        }
//    }
//}
//
//@Composable
//fun TelemetryRow(
//    humidity: String,
//    temperature: String,
//    pressure: String
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .padding(10.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        TelemetryItem(
//            icon = R.drawable.ic_humidity,
//            value = "$humidity %"
//        )
//        TelemetryItem(
//            icon = R.drawable.ic_temperature,
//            value = "$temperature C"
//        )
//        TelemetryItem(
//            icon = R.drawable.ic_air_pressure,
//            value = "$pressure hPa"
//        )
//    }
//}
//
//@Composable
//fun TelemetryItem(icon: Int, value: String) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Image(
//            painter = painterResource(icon),
//            contentDescription = null
//        )
//        Text(
//            text = value,
//            modifier = Modifier.padding(start = 5.dp)
//        )
//    }
//}
