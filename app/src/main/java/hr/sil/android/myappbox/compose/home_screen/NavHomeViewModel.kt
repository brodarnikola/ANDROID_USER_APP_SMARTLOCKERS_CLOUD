package hr.sil.android.myappbox.compose.home_screen

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.events.MPLDevicesUpdatedEvent
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.utils.BaseViewModel
import hr.sil.android.myappbox.utils.UiEvent
import hr.sil.android.myappbox.utils.isEmailValid
//import hr.sil.android.schlauebox.cache.DataCache
//import hr.sil.android.schlauebox.cache.status.InstallationKeyHandler
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper

class NavHomeViewModel : BaseViewModel<NavHomeUiState, HomeScreenEvent>() {

    val log = logger()

    private val _uiState = MutableStateFlow(NavHomeUiState())
    val uiState: StateFlow<NavHomeUiState> = _uiState.asStateFlow()

    init {
        App.ref.eventBus.register(this)
        loadUserInfo()

        val selectedMasterDevice = MPLDeviceStore.uniqueDevices[SettingsHelper.userLastSelectedLocker]
        val displayNameOrAddress = selectedMasterDevice?.name?.ifEmpty { selectedMasterDevice.address } 
        _uiState.value = _uiState.value.copy(selectedLocker = displayNameOrAddress ?: "")
    }

    private fun loadUserInfo() {
        _uiState.value = _uiState.value.copy(
            userName = UserUtil.user?.name ?: "",
            address = UserUtil.user?.address ?: ""
        )
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnauthorizedUser(event: UnauthorizedUserEvent) {
        _uiState.value = _uiState.value.copy(isUnauthorized = true)
    }
    
    override fun initialState(): NavHomeUiState {
        return NavHomeUiState()
    }

    override fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnForgotPasswordRequest -> {

                }
        }
    }

}

data class NavHomeUiState(
    val loading: Boolean = false,

    val selectedLocker: String = "",

    val userName: String = "",
    val address: String = "",
    val devices: List<ItemHomeScreen> = emptyList(),
    val isUnauthorized: Boolean = false
)

sealed class HomeScreenEvent {
    data class OnForgotPasswordRequest(
        val email: String,
        val context: Context,
        val activity: Activity
    ) : HomeScreenEvent()
}

sealed class HomeScreenUiEvent : UiEvent {

    object NavigateBack : HomeScreenUiEvent()
}