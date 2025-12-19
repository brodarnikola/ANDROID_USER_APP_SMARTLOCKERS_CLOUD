package hr.sil.android.myappbox.compose.access_sharing

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.ItemRGroupInfo
import hr.sil.android.myappbox.core.remote.model.RAdminGroup
import hr.sil.android.myappbox.core.remote.model.REmptyGroupMembers
import hr.sil.android.myappbox.core.remote.model.REndUserGroupMember
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersAdmin
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersHeader
import hr.sil.android.myappbox.core.remote.model.RGroupInfo
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.remote.model.RUserAccess
import hr.sil.android.myappbox.core.remote.model.RUserAccessRole
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.text.toLong


data class AccessSharingAddUserUiState(
    val email: String = "",
    val selectedGroup: RGroupInfo? = null,
    val selectedRole: RUserAccessRole = RUserAccessRole.USER,
    val availableGroups: List<RGroupInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isUnauthorized: Boolean = false,
    val emailError: String? = null,
    val showShareAppDialog: Boolean = false,
    val shareAppEmail: String = ""
)

class AccessSharingAddUserViewModel : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow(AccessSharingAddUserUiState())
    val uiState: StateFlow<AccessSharingAddUserUiState> = _uiState.asStateFlow()

    init {
        App.ref.eventBus.register(this)
    }

    override fun onCleared() {
        App.ref.eventBus.unregister(this)
        super.onCleared()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnauthorizedEvent(event: UnauthorizedUserEvent) {
        log.info("Received unauthorized event, user will now be logged out")
        _uiState.update { it.copy(isUnauthorized = true) }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                emailError = null
            )
        }
    }


    fun onRoleSelected(role: RUserAccessRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun onEmailFromContact(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun dismissShareAppDialog() {
        _uiState.update {
            it.copy(
                showShareAppDialog = false,
                shareAppEmail = ""
            )
        }
    }

    fun addUserAccess(
        nameOfGroup: String, groupId: Int,
        email: String, selectedItem: RUserAccessRole,
        onSuccess: () -> Unit
    ) {
        val currentState = _uiState.value

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email cannot be blank") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {

            val correctMasterId =
                MPLDeviceStore.uniqueDevices.values.first { it.macAddress == SettingsHelper.userLastSelectedLocker }.masterUnitId
            val deviceName =
                MPLDeviceStore.uniqueDevices.values.first { it.macAddress == SettingsHelper.userLastSelectedLocker }.name

            val userAccess = RUserAccess().apply {
                this.groupId = groupId
                this.groupUserEmail = email
                this.role = selectedItem.name
                this.masterId = correctMasterId
            }

            log.info("userAccess: $userAccess")
            log.info("nameOfGroup 22: $nameOfGroup")
            log.info("groupId 22: $groupId")
            log.info("email: $email")
            log.info("selectedItem: ${selectedItem.name}")
            log.info("correctMasterId: $correctMasterId")
            log.info("deviceName: $deviceName")

            if (userAccess.groupUserEmail.isNotEmpty() && (".+@.+".toRegex().matches(email))) {
                if (UserUtil.user?.email == userAccess.groupUserEmail && UserUtil.userGroup?.id == userAccess.groupId) {
                    //App.ref.toast("You can not add your self to your group")
                } else {

                    if (WSUser.addUserAccess(userAccess)) {

                        // add new user to admin data cache
                        if (UserUtil.userGroup?.name != nameOfGroup) {

                            userAccess.groupId?.toLong()
                                ?.let { it ->
                                    val adminGroup =
                                        WSUser.getGroupMembershipsById(groupId.toLong())
                                            ?: mutableListOf()
                                    //DataCache.groupMemberships(it, true)
                                }
                        }
                        // add new user to owner data cache
                        else {
                            WSUser.getGroupMembers() ?: listOf<REndUserGroupMember>()
                            // DataCache.getGroupMembers()
                        }

                        onSuccess()

//                        withContext(Dispatchers.Main) {
//                            log.info("Successfully add user ${userAccess.groupUserEmail} to group")
//                            App.ref.toast(this@AccessSharingAddUserActivity.getString(R.string.app_generic_success))
//                            val startIntent = Intent(
//                                this@AccessSharingAddUserActivity,
//                                AccessSharingActivity::class.java
//                            )
//                            startIntent.putExtra("rMacAddress", masterMacAddress)
//                            startIntent.putExtra("nameOfDevice", nameOfDevice)
//                            startActivity(startIntent)
//                            finish()
//                        }
                    } else {

                        withContext(Dispatchers.Main) {

//                            val shareAppDialog = ShareAppDialog(
//                                this@AccessSharingAddUserActivity,
//                                userAccess.groupUserEmail
//                            )
//                            shareAppDialog.show(
//                                this@AccessSharingAddUserActivity.supportFragmentManager,
//                                ""
//                            )
//                            binding.progressBar.visibility = View.GONE
//                            binding.btnGrantAccess.visibility = View.VISIBLE
                        }
                    }
                }
            } else if (userAccess.groupUserEmail.isEmpty()) {
                //binding.wrongEmail.visibility = View.VISIBLE
                // binding.wrongEmail.text = getString(R.string.edit_user_validation_blank_fields_exist)
                //validateNewEmail(false, emailEditText)
            } else {
                //binding.wrongEmail.visibility = View.VISIBLE
                //binding.wrongEmail.text = getString(R.string.message_email_invalid)
            }


        }
    }

    fun addUserAccess1(macAddress: String, onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email cannot be blank") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val correctMasterId = MPLDeviceStore.uniqueDevices[macAddress]?.masterUnitId
                    ?: 0 //as? Int ?: return@launch
                val selectedGroup = currentState.selectedGroup ?: return@launch

                val userAccess = RUserAccess().apply {
                    this.groupId = selectedGroup.groupId
                    this.groupUserEmail = currentState.email
                    this.role = currentState.selectedRole.name
                    this.masterId = correctMasterId
                }

                val result = WSUser.addUserAccess(userAccess)

                if (result) {
                    val userGroup = UserUtil.userGroup
                    if (userGroup?.name != selectedGroup.groupOwnerName) {
                        selectedGroup.groupId.toLong().let {
                            WSUser.getGroupMembershipsById(it)
                        }
                    } else {
                        WSUser.getGroupMembers()
                    }

                    log.info("Successfully added user ${userAccess.groupUserEmail} to group")
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showShareAppDialog = true,
                            shareAppEmail = currentState.email
                        )
                    }
                }
            } catch (e: Exception) {
                log.error("Error adding user access", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        emailError = "Failed to add user"
                    )
                }
            }
        }
    }


}