//package hr.sil.android.myappbox.view.ui.adapters
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.graphics.drawable.Drawable
//import android.util.Log
//import android.util.TypedValue
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import androidx.core.content.res.getColorOrThrow
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.location.*
//import hr.sil.android.myappbox.App
//import hr.sil.android.myappbox.cache.status.ActionStatusHandler
//import hr.sil.android.myappbox.cache.status.ActionStatusKey
//import hr.sil.android.myappbox.cache.status.ActionStatusType
//import hr.sil.android.myappbox.data.CancelPickedHomeInterface
//import hr.sil.android.myappbox.core.model.MPLDeviceType
//import hr.sil.android.myappbox.core.remote.WSUser
//import hr.sil.android.myappbox.core.remote.model.InstalationType
//import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
//import hr.sil.android.myappbox.core.util.formatFromStringToDate
//import hr.sil.android.myappbox.core.util.formatToViewDateTimeDefaults
//import hr.sil.android.myappbox.core.util.logger
//import hr.sil.android.myappbox.core.util.macCleanToReal
//import hr.sil.android.myappbox.store.MPLDeviceStore
//import hr.sil.android.myappbox.util.backend.UserUtil
//import hr.sil.android.myappbox.view.ui.activities.dialogs.CancelPickAtHomeLinuxDialog
//import hr.sil.android.myappbox.view.ui.activities.sendparcel.CancelPickedHomeDialog
//import hr.sil.android.myappbox.view.ui.activities.sendparcel.SendParcelsOverviewActivity
//import kotlinx.coroutines.*
//import java.text.ParseException
//import java.util.*
//


//class SendParcelsSharingAdapter(
//    private var keys: MutableList<RCreatedLockerKey>,
//    val sendParcelsOverviewActivity: SendParcelsOverviewActivity
//) : RecyclerView.Adapter<SendParcelsSharingAdapter.NotesViewHolder>() {
//
//    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
//        holder.bindItem(keys[position])
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
//
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.cancel_picked_home_keys, parent, false)
//        return NotesViewHolder(view)
//    }
//
//    override fun getItemCount() = keys.size
//
//    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
//        CancelPickedHomeInterface {
//
//        val log = logger()
//        private val clMainLayout: ConstraintLayout = itemView.findViewById(R.id.clMainLayout)
//        private val tvLockerName: TextView = itemView.findViewById(R.id.tvLockerName)
//        private val tvLockerAddress: TextView = itemView.findViewById(R.id.tvLockerAddress)
//        private val tvLockerPin: TextView = itemView.findViewById(R.id.tvLockerPin)
//        private val tvLockerDateCreated: TextView = itemView.findViewById(R.id.tvLockerDateCreated)
//        private val tvLockerSize: TextView = itemView.findViewById(R.id.tvLockerSize)
//        private val delete: Button = itemView.findViewById(R.id.btnCancel)
//        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressCancelKey)
//
//        private val rlRightLayout: RelativeLayout = itemView.findViewById(R.id.rlRightLayout)
//
//        private val ivLockerPicture: ImageView = itemView.findViewById(R.id.ivLockerPicture)
//        private val btnShareData: Button = itemView.findViewById(R.id.btnShareData)
//
//        private val registeredBC = getColorAttrValue(R.attr.thmSPRegisteredOddBackgroundColor)
//        private val notInProximityBackgroundColor =
//            getColorAttrValue(R.attr.thmSPRegisteredEvenBackgroundColor)
//
//        private val registeredDrawable =
//            getDrawableAttrValue(R.attr.thmSPRegisteredOddBackgroundColor)
//        private val notInProximityDrawable =
//            getDrawableAttrValue(R.attr.thmSPRegisteredEvenBackgroundColor)
//
//        private fun getColorAttrValue(attr: Int): Int? {
//            val attrArray = intArrayOf(attr)
//            val typedArray = sendParcelsOverviewActivity.obtainStyledAttributes(attrArray)
//            val result = try {
//                typedArray.getColorOrThrow(0)
//            } catch (exc: Exception) {
//                null
//            }
//            typedArray.recycle()
//            return result
//        }
//
//        private fun getDrawableAttrValue(attr: Int): Drawable? {
//            val attrArray = intArrayOf(attr)
//            val typedArray = sendParcelsOverviewActivity.obtainStyledAttributes(attrArray)
//            val result = try {
//                typedArray.getDrawable(0)
//            } catch (exc: Exception) {
//                null
//            }
//            typedArray.recycle()
//            return result
//        }
//
//        fun bindItem(keyObject: RCreatedLockerKey) {
//
//            if (registeredBC != null)
//                clMainLayout.setBackgroundColor(registeredBC)
//
//            val drawable: Drawable? = with(TypedValue()) {
//                sendParcelsOverviewActivity.theme.resolveAttribute(R.attr.thmDeliveryImage, this, true)
//                ContextCompat.getDrawable(sendParcelsOverviewActivity, resourceId)
//            }
//            ivLockerPicture.setImageDrawable(drawable)
//
//            if (keyObject.masterName != null)
//                tvLockerName.text = keyObject.masterName
//            else
//                tvLockerName.text = "-"
//
//            if (keyObject.masterAddress != null)
//                tvLockerAddress.text = keyObject.masterAddress
//            else
//                tvLockerAddress.text = "-"
//
//            if (keyObject.pin != null) {
//                tvLockerPin.visibility = View.VISIBLE
//                tvLockerPin.text =
//                    itemView.context.getString(R.string.app_generic_parcel_pin, keyObject.pin)
//            }
//            else if( keyObject.tan != null ) {
//                tvLockerPin.visibility = View.VISIBLE
//                tvLockerPin.text = itemView.context.getString(R.string.app_generic_parcel_pin, keyObject.tan)
//            }
//            else {
//                tvLockerPin.visibility = View.GONE
//            }
//
//            val locker = MPLDeviceStore.uniqueDevices[keyObject.getMasterBLEMacAddress()]
//            keyObject.timeCreated = formatCorrectDate(keyObject.timeCreated)
//            when {
//                locker?.type == MPLDeviceType.SPL -> {
//                    tvLockerSize.text = itemView.context.getString(
//                        R.string.app_generic_time_created,
//                        keyObject.timeCreated
//                    )
//                    tvLockerDateCreated.visibility = View.GONE
//                }
//                else -> {
//                    tvLockerSize.text =
//                        itemView.context.getString(R.string.app_generic_size, keyObject.lockerSize)
//                    tvLockerDateCreated.visibility = View.VISIBLE
//                    tvLockerDateCreated.text = itemView.context.getString(
//                        R.string.app_generic_time_created,
//                        keyObject.timeCreated
//                    )
//                }
//            }
//
//            btnShareData.setOnClickListener {
//                val shareBodyText = when {
//                    keyObject.pin != null -> {
//                        itemView.context.getString(R.string.share_pin_device_name, keyObject.masterName) + "\n"+
//                                itemView.context.getString(R.string.share_pin_device_address, keyObject.masterAddress) +  "\n" +
//                                itemView.context.getString(R.string.share_pin_device_locker_size, keyObject.lockerSize) +  "\n" +
//                                itemView.context.getString(R.string.share_pin_device_pin, keyObject.pin ) + "\n" +
//                                itemView.context.getString(R.string.app_generic_date_created, keyObject.timeCreated)
//
//                    }
//                    keyObject.tan != null -> {
//                        itemView.context.getString(R.string.share_pin_device_name, keyObject.masterName) + "\n"+
//                                itemView.context.getString(R.string.share_pin_device_address, keyObject.masterAddress) +  "\n" +
//                                itemView.context.getString(R.string.share_pin_device_locker_size, keyObject.lockerSize) +  "\n" +
//                                itemView.context.getString(R.string.share_pin_device_pin, keyObject.tan ) + "\n" +
//                                itemView.context.getString(R.string.app_generic_date_created, keyObject.timeCreated)
//                    }
//                    else -> {
//                        itemView.context.getString(R.string.share_pin_device_name, keyObject.masterName) + "\n"+
//                                itemView.context.getString(R.string.share_pin_device_address, keyObject.masterAddress) +  "\n" +
//                                itemView.context.getString(R.string.share_pin_device_locker_size, keyObject.lockerSize) + "\n" +
//                                itemView.context.getString(R.string.app_generic_date_created, keyObject.timeCreated)
//                    }
//                }
//
//                val emailIntent = Intent(Intent.ACTION_SEND)
//                emailIntent.setType("text/plain")
//                emailIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
//                sendParcelsOverviewActivity.startActivity(Intent.createChooser(emailIntent,  sendParcelsOverviewActivity.baseContext.getString(R.string.access_sharing_share_choose_sharing)))
//            }
//
//            delete.visibility = View.VISIBLE
//            when {
//                keyObject.isInBleProximityOrLinuxDevice -> {
//                    delete.alpha = 1.0f
//                    delete.isEnabled = true
//                    progressBar.visibility = View.INVISIBLE
//                    delete.setOnClickListener {
//                        if( keyObject.isLinuxKeyDevice == InstalationType.LINUX ) {
//
//                            val pinOrTan = if(keyObject.pin != null) keyObject.pin else if(keyObject.tan != null) keyObject.tan else ""
//
//                            val cancelPickedHomeLinuxDialog =
//                                CancelPickAtHomeLinuxDialog(pinOrTan)
//                            cancelPickedHomeLinuxDialog.show(
//                                sendParcelsOverviewActivity.supportFragmentManager,
//                                ""
//                            )
//                        }
//                        else {
//                            val cancelPickedHomeDialog =
//                                CancelPickedHomeDialog(this@NotesViewHolder, keyObject, itemView)
//                            cancelPickedHomeDialog.show(
//                                sendParcelsOverviewActivity.supportFragmentManager,
//                                ""
//                            )
//                        }
//                    }
//                }
//                else -> {
//                    progressBar.visibility = View.INVISIBLE
//                    delete.alpha = 0.4f
//                    delete.isEnabled = false
//                }
//            }
//        }
//
//        private fun formatCorrectDate(timeCreated: String): String {
//            val fromStringToDate: Date
//            var fromDateToString = ""
//            try {
//                fromStringToDate = timeCreated.formatFromStringToDate()
//                fromDateToString = fromStringToDate.formatToViewDateTimeDefaults()
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//            log.info("Correct date is: ${fromDateToString}")
//            return fromDateToString
//        }
//
//        override fun cancelPickedHome(keyObject: RCreatedLockerKey, view: View) {
//            progressBar.visibility = View.VISIBLE
//            delete.visibility = View.INVISIBLE
//
//            cancelOtherDevicesPickAtHomeKey(keyObject, view)
//        }
//
//        private fun cancelOtherDevicesPickAtHomeKey(keyObject: RCreatedLockerKey, view: View) {
//            GlobalScope.launch {
//                log.info("SPl unit mac = ${keyObject.lockerMasterMac.macCleanToReal()}")
//                val communicator =
//                    MPLDeviceStore.uniqueDevices[keyObject.lockerMasterMac.macCleanToReal()]?.createBLECommunicator(
//                        view.context
//                    )
//                val userId = UserUtil.user?.id ?: 0
//                if (communicator != null && communicator.connect() && userId != 0) {
//                    log.info("Connected to ${keyObject.lockerMasterMac} - deleting ${keyObject.lockerMac}")
//                    val response = communicator.requestParcelSendCancel(keyObject.lockerMac, userId)
//                    if (response.isSuccessful) {
//                        val action = ActionStatusKey().apply {
//                            keyId =
//                                keyObject.lockerId.toString() + ActionStatusType.PAH_ACCESS_CANCEL
//                        }
//                        ActionStatusHandler.actionStatusDb.put(action)
//                        deleteItem(keyObject /*, itemView.context */)
//                        withContext(Dispatchers.Main) {
//                            log.error("Success delete ${keyObject.lockerId}")
//                            //delete.visibility = View.INVISIBLE
//                            //progressBar.visibility = View.GONE
//                            notifyDataSetChanged()
//
//                        }
//                    } else {
//                        log.error("Error while deleting the key ${response.bleDeviceErrorCode} - ${response.bleSlaveErrorCode}")
//                        withContext(Dispatchers.Main) {
//                            App.Companion.ref.toast(
//                                view.context.getString(
//                                    R.string.sent_parcel_error_delete,
//                                    keyObject.lockerId.toString()
//                                )
//                            )
//                            delete.visibility = View.VISIBLE
//                            progressBar.visibility = View.GONE
//                        }
//
//                    }
//                    communicator.disconnect()
//                } else {
//                    log.error("Error while connecting the main unit ${keyObject.lockerMac}")
//                    withContext(Dispatchers.Main) {
//                        App.Companion.ref.toast(
//                            view.context.getString(
//                                R.string.sent_parcel_error_delete,
//                                keyObject.lockerId.toString()
//                            )
//                        )
//                        delete.visibility = View.VISIBLE
//                        progressBar.visibility = View.GONE
//                    }
//                }
//            }
//        }
//
//        @SuppressLint("MissingPermission")
//        private fun cancelLinuxPickAtHomeKey(keyObject: RCreatedLockerKey, view: View) {
//            GlobalScope.launch {
//                cancelPickAtHomeForLinuxDevices(keyObject)
//            }
//        }
//
//        private suspend fun cancelPickAtHomeForLinuxDevices(keyObject: RCreatedLockerKey) {
//            val backendResponse = WSUser.cancelPickAtHomeLinuxDevices(keyObject.lockerMac)
//            if( backendResponse?.success ?: false )
//                deleteItem(keyObject)
//            withContext(Dispatchers.Main) {
//                if( backendResponse?.success ?: false ) {
//                    notifyDataSetChanged()
//                    App.Companion.ref.toast(itemView.context.getString(R.string.app_generic_success))
//                }
//                else
//                    App.Companion.ref.toast("${backendResponse?.errorType}")
//            }
//        }
//    }
//
//    private fun deleteItem(item: RCreatedLockerKey /* , context: Context */) {
//
//        Log.d("SendParcelSharingAdapt", "Second example Admin group list size is: " + keys.size)
//
//        val key =
//            keys.filter { it.id == item.id && it.lockerMasterMac == item.lockerMasterMac && it.lockerMac == item.lockerMac }
//                .firstOrNull()
//        if (key != null)
//            keys.remove(key)
//
//        Log.d("SendParcelSharingAdapt", "Second example Admin group list size is: " + keys.size)
//    }
//
//}