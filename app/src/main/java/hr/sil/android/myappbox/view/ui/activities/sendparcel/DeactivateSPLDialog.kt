package hr.sil.android.myappbox.view.ui.activities.sendparcel


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.core.util.logger


class DeactivateSPLDialog  constructor( val editSplActivity: EditSplActivity ) : DialogFragment() {

//    val log = logger()
//    private lateinit var binding: DialogDeactivateSplBinding
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogDeactivateSplBinding.inflate(LayoutInflater.from(context))
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
//            val macAddress = arguments?.getString("rMacAddress") ?: ""
//            log.info("macc address in deactivate SPL dialog is: ${macAddress}" )
//
//            binding.btnSelect.setOnClickListener {
//                dismiss()
//                editSplActivity.deactivateSPL()
//            }
//
//            binding.btnCancel.setOnClickListener {
//                dismiss()
//            }
//        }
//
//        return dialog!!
//    }

}