package hr.sil.android.myappbox.view.ui.activities.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.fragment.app.DialogFragment

class NoMasterSelectedDialog constructor(val descriptionText: Int) : DialogFragment() {

//    private lateinit var binding: DialogNoMasterSelectedBinding
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogNoMasterSelectedBinding.inflate(LayoutInflater.from(context))
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
//            binding.tvDescription.setText( resources.getString(descriptionText))
//
//            binding.btnConfirm.setOnClickListener {
//                dismiss()
//            }
//        }
//
//        return dialog!!
//    }

}