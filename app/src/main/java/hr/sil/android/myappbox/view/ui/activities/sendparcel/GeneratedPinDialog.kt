package hr.sil.android.myappbox.view.ui.activities.sendparcel

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.store.model.MPLDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("ValidFragment")
class GeneratedPinDialog constructor(parcelLocker: MPLDevice?, var parcelLockerSize: String) : DialogFragment() {

//    private lateinit var binding: DialogGeneratedPinBinding
//    val log = logger()
//    private val finalParcelLocker = parcelLocker
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogGeneratedPinBinding.inflate(LayoutInflater.from(context))
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
//
//            lifecycleScope.launch {
//
//                if (finalParcelLocker != null) {
//
//                    val generatedPin = finalParcelLocker.masterUnitId.let { WSUser.getGeneratedPinFromBackendForSendParcel(it) } ?: ""
//
//                    withContext(Dispatchers.Main) {
//
//                        binding.progressBar?.visibility = View.GONE
//                        binding.btnPinConfirm?.visibility = View.VISIBLE
//
//                        binding.tvPinData?.visibility = View.VISIBLE
//                        binding.tvPinData?.text = "" + generatedPin
//
//                        binding.btnPinConfirm?.setOnClickListener {
//
//                            log.info("rMacAddress is: ${finalParcelLocker.macAddress}, pin generated is: ${generatedPin.toInt()}, size is: ${parcelLockerSize} ")
//
//                            val intent = Intent()
//                            val packageName = activity?.packageName ?: ""
//                            val componentName = ComponentName(packageName, packageName + ".aliastStartSendParcelDelivery")
//                            intent.component = componentName
//
//                            intent.putExtra("rMacAddress", finalParcelLocker.macAddress)
//                            intent.putExtra("pin", generatedPin.toInt())
//                            intent.putExtra("size", parcelLockerSize)
//
//                            startActivity(intent)
//                            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                            activity?.finish()
//
//                            dialog?.dismiss()
//                        }
//
//                        binding.btnCancel.setOnClickListener {
//                            dismiss()
//                        }
//                    }
//                }
//            }
//        }
//
//        return dialog!!
//    }

}