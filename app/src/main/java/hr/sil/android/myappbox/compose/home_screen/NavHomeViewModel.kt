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

class NavHomeViewModel : BaseViewModel<NavHomeUiState, HomeScreenEvent>() {

    val log = logger()

    private val _uiState = MutableStateFlow(NavHomeUiState())
    val uiState: StateFlow<NavHomeUiState> = _uiState.asStateFlow()

    init {
        App.ref.eventBus.register(this)
        loadUserInfo()
    }

    private fun loadUserInfo() {
        _uiState.value = _uiState.value.copy(
            userName = UserUtil.user?.name ?: "",
            address = UserUtil.user?.address ?: ""
        )
    }

    fun loadDevices(context: Context) {
        viewModelScope.launch {
            val items = getItemsForRecyclerView(context)
            _uiState.value = _uiState.value.copy(devices = items)
        }
    }

    private fun getItemsForRecyclerView(context: Context?): List<ItemHomeScreen> {
        val items = mutableListOf<ItemHomeScreen>()

//        val (splList, mplList) = MPLDeviceStore.uniqueDevices.values
//            .filter {
//                val isThisDeviceAvailable = when {
//                    UserUtil.user?.testUser == true -> true
//                    else -> it.isProductionReady == true
//                }
//                isThisDeviceAvailable
//            }



        return items
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: MPLDevicesUpdatedEvent) {
        loadDevices(context = App.ref.applicationContext)
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

//                viewModelScope.launch {
//                    _state.update { it.copy(loading = true) }
//                    login(email = event.email, password = event.password, context = event.context)
//                    _state.update { it.copy(loading = false) }
//                }
        }
    }

    fun getEmailError(email: String, context: Context): String {
        var emailError = ""
        if (email.isBlank()) {
            emailError = context.getString(R.string.forgot_password_error)
        } else if (!email.isEmailValid()) {
            emailError = context.getString(R.string.pickup_parcel_email_error)
        }

        return emailError
    }
}

data class NavHomeUiState(
    val loading: Boolean = false,

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
    data class NavigateToNextScreen(val route: String) : HomeScreenUiEvent()

    object NavigateBack : HomeScreenUiEvent()
}