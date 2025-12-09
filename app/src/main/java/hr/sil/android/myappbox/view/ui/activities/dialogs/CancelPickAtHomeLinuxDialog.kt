package hr.sil.android.myappbox.view.ui.activities.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.R

class CancelPickAtHomeLinuxDialog(val pinOrTan: String) : DialogFragment() {

//    private lateinit var binding: DialogCancelPickedHomeBinding
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogCancelPickedHomeBinding.inflate(LayoutInflater.from(context))
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
//            binding.tvDescription.text = resources.getString(R.string.cancel_pick_at_home_description_linux, pinOrTan)
//
//            binding.btnSelect.setOnClickListener {
//                dismiss()
//            }
//        }
//
//        return dialog!!
//    }

}