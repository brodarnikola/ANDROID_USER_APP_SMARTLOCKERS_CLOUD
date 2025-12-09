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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.RPinManagement
import hr.sil.android.myappbox.core.remote.model.RPinManagementSavePin
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("ValidFragment")
class PinManagmentDialog constructor(val parcelLocker: MPLDevice?, var parcelLockerSize: String, val activity: AppCompatActivity) : DialogFragment() {

//    val log = logger()
//    private lateinit var binding: DialogPinManagmentBinding
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogPinManagmentBinding.inflate(LayoutInflater.from(context))
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
//            lifecycleScope.launch {
//
//                if (parcelLocker != null && UserUtil.userGroup != null) {
//
//                    var counter = 0
//
//                    val combinedListOfPins: MutableList<RPinManagement> = mutableListOf()
//
//                    val generatedPinFromBackend = WSUser.getGeneratedPinForSendParcel(parcelLocker.masterUnitId)
//                        ?: ""
//                    val generatedPin = RPinManagement()
//                    generatedPin.pin = generatedPinFromBackend
//                    generatedPin.pinGenerated = true
//                    generatedPin.position = counter
//                    generatedPin.pinId = 0
//                    generatedPin.isSelected = true
//                    generatedPin.isExtendedToDelete = false
//                    counter++
//
//                    combinedListOfPins.add(generatedPin)
//
//                    val pinsFromGroup = WSUser.getPinManagementForSendParcel(UserUtil.userGroup?.id!!, parcelLocker.masterUnitId)
//                    if (pinsFromGroup != null) {
//                        for (items in pinsFromGroup) {
//                            val generatedPin = RPinManagement()
//                            generatedPin.pin = items.pin
//                            generatedPin.pinName = items.name
//                            generatedPin.pinGenerated = false
//                            generatedPin.position = counter
//                            generatedPin.pinId = items.id
//                            generatedPin.isSelected = false
//                            generatedPin.isExtendedToDelete = false
//
//                            combinedListOfPins.add(generatedPin)
//                            counter++
//                        }
//                    }
//
//                    withContext(Dispatchers.Main) {
//
//                        binding.progressBar.visibility = View.GONE
//                        binding.recylcerViewPinData.visibility = View.VISIBLE
//
//                        PinManagementAdapter.Companion.setLastSelectedPin(combinedListOfPins.first())
//
//                        binding.recylcerViewPinData.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                        binding.recylcerViewPinData.adapter = PinManagementAdapter(
//                            combinedListOfPins,
//                            parcelLocker.masterUnitId,
//                            activity
//                        )
//
//                        binding.btnPinConfirm.setOnClickListener {
//
//                            log.info("Pin name is: ${PinManagementAdapter.Companion.getPinName()}, pin generated is: ${PinManagementAdapter.Companion.getLastSelectedPin().pinGenerated}")
//                            if (PinManagementAdapter.Companion.getPinName() != "" && PinManagementAdapter.Companion.getLastSelectedPin().pinGenerated) {
//                                lifecycleScope.launch {
//                                    val savePin = RPinManagementSavePin()
//                                    savePin.groupId = UserUtil.userGroup?.id
//                                    savePin.masterId = parcelLocker.masterUnitId
//                                    savePin.pin = PinManagementAdapter.Companion.getLastSelectedPin().pin
//                                    savePin.name = PinManagementAdapter.Companion.getPinName()
//
//                                    WSUser.savePinManagementForSendParcel(savePin)
//                                }
//                            }
//
//                            if (PinManagementAdapter.Companion.getLastSelectedPin() != null) {
//
//                                log.info("rmacAddress is: ${parcelLocker.macAddress}, pin is: ${PinManagementAdapter.Companion.getLastSelectedPin().pin.toInt()}, size ${parcelLockerSize}")
//
//                                val intent = Intent()
//                                val packageName = activity.packageName
//                                val componentName = ComponentName(packageName, packageName + ".aliastStartSendParcelDelivery")
//                                intent.component = componentName
//
//                                intent.putExtra("rMacAddress", parcelLocker.macAddress)
//                                intent.putExtra("pin", PinManagementAdapter.Companion.getLastSelectedPin().pin.toInt())
//                                intent.putExtra("size", parcelLockerSize)
//
//                                startActivity(intent)
//                                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                                activity.finish()
///*
//
//                            val startIntent = Intent(activity, SendParcelDeliveryActivity::class.java)
//                            startIntent.putExtra("rMacAddress", parcelLocker.macAddress)
//                            startIntent.putExtra("pin", PinManagementAdapter.getLastSelectedPin().pin.toInt())
//                            startIntent.putExtra("size", parcelLockerSize)
//                            startActivity(startIntent)
//                            activity.finish()*/
//
//                                dismiss()
//                            }
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