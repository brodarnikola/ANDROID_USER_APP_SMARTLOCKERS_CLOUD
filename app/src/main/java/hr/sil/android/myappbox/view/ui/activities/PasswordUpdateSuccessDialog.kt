package hr.sil.android.myappbox.view.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.R

class PasswordUpdateSuccessDialog constructor(val passwordUpdateActivity: PasswordUpdateActivity) : DialogFragment() {
//
//    private lateinit var binding: DialogPasswordUpdateSuccessBinding
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        val view = inflater.inflate(R.layout.dialog_password_update_success, container, false)
//
//        binding = DialogPasswordUpdateSuccessBinding.inflate(inflater)
//        if (dialog != null && dialog?.window != null) {
//            dialog?.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
//            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
//            dialog?.setCanceledOnTouchOutside(false)
//        }
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.btnUserSettings.setOnClickListener {
//            val startIntent = Intent(this@PasswordUpdateSuccessDialog.requireContext(), LoginActivity::class.java)
//            startActivity(startIntent)
//            dismiss()
//            passwordUpdateActivity.finish()
//        }
//
//    }
}