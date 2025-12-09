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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.DialogDeactivateSplBinding
import hr.sil.android.smartlockers.enduser.databinding.DialogDisableRequestNewVerificationPinBinding
import hr.sil.android.smartlockers.enduser.databinding.DialogSettingsLogoutBinding
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DisableRequestNewPinVerificationDialog constructor(val lastDateAndTimeOfSendingVerificationPin: String) : DialogFragment() {

    private val log = logger()

    private lateinit var binding: DialogDisableRequestNewVerificationPinBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDisableRequestNewVerificationPinBinding.inflate(LayoutInflater.from(context))

        val dialog = activity?.let {
            Dialog(it)
        }

        if(dialog != null) {
            dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(binding.root)

            binding.tvDateAndTime.text = binding.root.resources!!.getString(R.string.last_date_and_time_of_sended_pin, lastDateAndTimeOfSendingVerificationPin)

            binding.btnConfirm.setOnClickListener {
                dismiss()
            }
        }

        return dialog!!
    }

}