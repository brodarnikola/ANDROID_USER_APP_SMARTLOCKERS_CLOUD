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
import hr.sil.android.myappbox.adapters.ShareKeyAdapter
import hr.sil.android.myappbox.data.ShareAccessKey
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.databinding.DialogDeletePickAtFriendKeyBinding
import hr.sil.android.smartlockers.enduser.databinding.DialogSettingsLogoutBinding
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeletePickAFriendKeyDialog constructor(val parcelPickupKeysAdapter: ShareKeyAdapter,
                                             val shareAccessKey: ShareAccessKey,
                                             val rowView: View) : DialogFragment() {

    private lateinit var binding: DialogDeletePickAtFriendKeyBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDeletePickAtFriendKeyBinding.inflate(LayoutInflater.from(context))

        val dialog = activity?.let {
            Dialog(it)
        }

        if(dialog != null) {
            dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(binding.root)

            binding.tvTitle.setText( binding.root.resources!!.getString(R.string.parcel_pickup_delete_key_title,
                shareAccessKey.email) )

            binding.btnConfirm.setOnClickListener {
                parcelPickupKeysAdapter.deletePickAtFriendKey(shareAccessKey, rowView)
                dismiss()
            }

            binding.btnCancel.setOnClickListener {
                dismiss()
            }

        }

        return dialog!!
    }

}