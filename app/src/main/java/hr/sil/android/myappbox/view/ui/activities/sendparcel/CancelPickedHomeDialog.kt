package hr.sil.android.myappbox.view.ui.activities.sendparcel

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey

class CancelPickedHomeDialog constructor(
    //val notesViewHolder: SendParcelsSharingAdapter.NotesViewHolder,
                                         val keyObject: RCreatedLockerKey,
                                         val itemView: View) : DialogFragment()
{

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
//            binding.btnSelect.setOnClickListener {
//                notesViewHolder.cancelPickedHome(keyObject, itemView)
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