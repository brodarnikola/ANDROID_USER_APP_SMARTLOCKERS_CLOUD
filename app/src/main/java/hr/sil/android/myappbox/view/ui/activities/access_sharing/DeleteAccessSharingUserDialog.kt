package hr.sil.android.myappbox.view.ui.activities.access_sharing

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild
//import hr.sil.android.myappbox.view.ui.adapters.KeySharingAdapter

class DeleteAccessSharingUserDialog constructor(
    //val childHolder: KeySharingAdapter.ChildHolder,
                                                val removeAccess: RGroupDisplayMembersChild,
                                                val itemView: View
) : DialogFragment() {

//    private lateinit var binding: DialogDeleteAccessSharingBinding
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogDeleteAccessSharingBinding.inflate(LayoutInflater.from(context))
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
//                childHolder.deleteAccessSharing(removeAccess, itemView)
//                dismiss()
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