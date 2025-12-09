package hr.sil.android.myappbox.view.ui.activities.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.activities.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("ValidFragment")
class SettingsLogoutDialog  : DialogFragment() {

//    private lateinit var binding: DialogSettingsLogoutBinding
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogSettingsLogoutBinding.inflate(LayoutInflater.from(context))
//
//        val dialog = activity?.let {
//            Dialog(it)
//        }
//
//        if(dialog != null) {
//            dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
//            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCanceledOnTouchOutside(false)
//            dialog.setContentView(binding.root)
//
//            binding.btnSelect.setOnClickListener {
//
//                lifecycleScope.launch {
//                    UserUtil.logout()
//                    withContext(Dispatchers.Main) {
//                        val intent = Intent(context, LoginActivity::class.java)
//                        startActivity(intent)
//                        this@SettingsLogoutDialog.activity?.finish()
//                        dismiss()
//                    }
//                }
//            }
//
//            binding.btnCancel.setOnClickListener {
//                dismiss()
//            }
//
//            binding.ivX.setOnClickListener {
//                dismiss()
//            }
//        }
//
//        return dialog!!
//    }

}