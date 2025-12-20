package hr.sil.android.myappbox.compose.collect_parcel

import androidx.lifecycle.ViewModel
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.SettingsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PickupParcelUiState(
    val loading: Boolean = true,
    val isInProximity: Boolean = false,
    val lockerName: String = "",
    val error: String? = null
)

class PickupParcelViewModel1( ) : ViewModel() {

    private val _uiState = MutableStateFlow(PickupParcelUiState())
    val uiState: StateFlow<PickupParcelUiState> = _uiState.asStateFlow()

    private var device: MPLDevice? = null

    init {
        refreshDevice()
    }

    fun refreshDevice() {
        device = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
        _uiState.update {
            it.copy(
                loading = false,
                isInProximity = device?.isInBleProximity == true,
                lockerName = device?.name.orEmpty()
            )
        }
    }
}