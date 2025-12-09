package hr.sil.android.myappbox.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import hr.sil.android.myappbox.activities.collect_parcel.PickupParcelActivity
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.cache.DataCache
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.DialogPickAtFriendKeyBinding
import hr.sil.android.smartlockers.enduser.databinding.DialogSettingsLogoutBinding
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.access_sharing.ShareAppDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast

class PickAFriendKeyDialog constructor(val pickAtFriendKeyId: Int,
                                       val pickupParcelActivity: PickupParcelActivity
) : DialogFragment() {

    val log = logger()

    private lateinit var binding: DialogPickAtFriendKeyBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPickAtFriendKeyBinding.inflate(LayoutInflater.from(context))

        val dialog = activity?.let {
            Dialog(it)
        }

        if(dialog != null) {
            dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(binding.root)

            binding.btnConfirm.setOnClickListener {

                val mailAddress = binding.etEmail.text.toString()
                if( validate(binding.tilEmail, binding.etEmail)) {
                    lifecycleScope.launch(Dispatchers.Default) {
                        if (!isUserMemberOfGroup(mailAddress)) {
                            log.info("PaF share $pickAtFriendKeyId - $mailAddress")
                            log.info("P@h created $pickAtFriendKeyId mail: $mailAddress")
                            val returnedData = WSUser.createPaF(pickAtFriendKeyId, mailAddress)
                            log.info("Invitation key = ${returnedData?.invitationCode}")
                            if (returnedData?.invitationCode.isNullOrEmpty()) {
                                pickupParcelActivity.setupAdapterForKeys()
                                withContext(Dispatchers.Main) {
                                    val message = pickupParcelActivity.resources?.getString(R.string.app_generic_success).toString()
                                    App.ref.toast(message)
                                    dismiss()
                                    //updateKeys()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    val shareAppDialog = ShareAppDialog(pickupParcelActivity, mailAddress)
                                    shareAppDialog.show( pickupParcelActivity.supportFragmentManager, "" )
                                    dismiss()
                                }
                            }

                        } else {
                            withContext(Dispatchers.Main) {
                                App.ref.toast(R.string.grant_access_error_exists)
                            }
                        }
                    }
                }
            }

            binding.btnCancel.setOnClickListener {
                dismiss()
            }
        }

        return dialog!!
    }

    suspend private fun isUserMemberOfGroup(email: String): Boolean {
        val groups = DataCache.getGroupMembers()
        val groupMemberships = groups.filter { it.email == email }
        return groupMemberships.isNotEmpty()
    }

    private fun validate(tilEmail: TextInputLayout, etEmail: EditText): Boolean {
        if (!validateEmail(tilEmail, etEmail)) return false

        return true
    }

    protected fun validateEmail(emailInputLayout: TextInputLayout?, emailParam: EditText): Boolean {
        return validateEditText(emailInputLayout, emailParam) { email ->
            when {
                email.isBlank() -> BaseActivity.ValidationResult.INVALID_EMAIL_BLANK
                !(".+@.+".toRegex().matches(email)) -> BaseActivity.ValidationResult.INVALID_EMAIL
                else -> BaseActivity.ValidationResult.VALID
            }
        }
    }

    protected fun validateSetError(emailInputLayout: TextInputLayout?, result: BaseActivity.ValidationResult): BaseActivity.ValidationResult {
        val errorText = if (!result.isValid()) result.getText( this@PickAFriendKeyDialog.context ?: App.ref ) else null
        emailInputLayout?.error = errorText
        return result
    }

    fun validateEditText(emailInputLayout: TextInputLayout?, editText: EditText, validate: (value: String) -> BaseActivity.ValidationResult): Boolean {
        val result = validate(editText.text.toString())
        validateSetError(emailInputLayout, result)

        return result.isValid()
    }

}