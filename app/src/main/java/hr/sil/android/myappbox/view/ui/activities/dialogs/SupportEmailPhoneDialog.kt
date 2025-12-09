package hr.sil.android.myappbox.view.ui.activities.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.BuildConfig


@SuppressLint("ValidFragment")
class SupportEmailPhoneDialog  : DialogFragment() {

//    private lateinit var binding: DialogSupportEmailPhoneBinding
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogSupportEmailPhoneBinding.inflate(LayoutInflater.from(context))
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
//            initializeUi()
//
//            setOnClickListeners()
//        }
//
//        return dialog!!
//    }
//
//    private fun initializeUi() {
//        binding.tvPhoneNumber.text = "" + BuildConfig.MOBILE_PHONE_SUPPORT
//        binding.tvEmailSupport.text = "" + BuildConfig.APP_BASE_EMAIL
//    }
//
//    private fun setOnClickListeners() {
//
//        binding.clPhoneSupport.setOnClickListener {
//            val phone = BuildConfig.MOBILE_PHONE_SUPPORT
//            val intent = Intent(
//                Intent.ACTION_DIAL,
//                Uri.fromParts("tel", phone, null)
//            )
//            startActivity(intent)
//        }
//
//        binding.clEmailSupport.setOnClickListener {
//            val emailIntent =
//                Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: ${BuildConfig.APP_BASE_EMAIL}"))
//            startActivity(Intent.createChooser(emailIntent, ""))
//        }
//
//        binding.btnCancel.setOnClickListener {
//            dismiss()
//        }
//    }

}