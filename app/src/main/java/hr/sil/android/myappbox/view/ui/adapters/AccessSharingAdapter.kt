//package hr.sil.android.myappbox.view.ui.adapters
//
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.drawable.Drawable
//import android.util.TypedValue
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.MPLDeviceStatus
//import hr.sil.android.myappbox.R
//import hr.sil.android.myappbox.core.model.MPLDeviceType
//import hr.sil.android.myappbox.core.remote.model.InstalationType
//import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
//import hr.sil.android.myappbox.core.util.logger
//import hr.sil.android.myappbox.store.model.MPLDevice
//import hr.sil.android.myappbox.util.ListDiffer
//import hr.sil.android.myappbox.view.ui.activities.access_sharing.AccessSharingListActivity
//import hr.sil.android.myappbox.view.ui.activities.sendparcel.EditSplActivity
//
//class AccessSharingAdapter(
//    mplLocker: List<MPLDevice>,
//    val clickListener: (MPLDevice) -> Unit,
//    val accessSharingListActivity: AccessSharingListActivity
//) : RecyclerView.Adapter<AccessSharingAdapter.MplItemViewHolder>() {
//
//    val log = logger()
//
//    private val devices: MutableList<MPLDevice> = mplLocker.toMutableList()
//
//    var counter: Int = 0
//
//    override fun onBindViewHolder(holder: MplItemViewHolder, position: Int) {
//        holder.bindItem(devices[position], clickListener)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MplItemViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.locker_item_details_access_sharing, parent, false)
//        return MplItemViewHolder(view)
//    }
//
//    override fun getItemCount() = devices.size
//
//    fun updateDevices(updatedDevices: List<MPLDevice>) {
//
//        val listDiff = ListDiffer.getDiff(
//            devices,
//            updatedDevices,
//            { old, new ->
//                old.macAddress == new.macAddress &&
//                        old.type == new.type &&
//                        old.masterUnitId == new.masterUnitId &&
//                        //old.accessType == new.accessType &&
//                        old.accessTypes == new.accessTypes &&
//                        old.isSplActivate == new.isSplActivate &&
//                        old.masterUnitType == new.masterUnitType &&
//                        old.name == new.name &&
//                        old.address == new.address &&
//                        old.activeKeys.toTypedArray().contentEquals(new.activeKeys.toTypedArray()) &&
//                        old.availableLockers.size == new.availableLockers.size &&
//                        old.isInBleProximity == new.isInBleProximity &&
//                        old.modemRssi == new.modemRssi &&
//                        old.humidity == new.humidity &&
//                        old.temperature == new.temperature &&
//                        old.pressure == new.pressure &&
//                        old.mplRequestAccessSend == new.mplRequestAccessSend &&
//                        old.isCollectParcelSplTaken == new.isCollectParcelSplTaken
//            })
//
//        for (diff in listDiff) {
//            when (diff) {
//                is ListDiffer.DiffInserted -> {
//                    devices.addAll(diff.elements)
//                    log.info("notifyItemRangeInserted")
//                    notifyItemRangeInserted(diff.position, diff.elements.size)
//                }
//                is ListDiffer.DiffRemoved -> {
//                    //remove devices
//                    for (i in (devices.size - 1) downTo diff.position) {
//                        devices.removeAt(i)
//                    }
//                    log.info("notifyItemRangeRemoved")
//                    notifyItemRangeRemoved(diff.position, diff.count)
//                }
//                is ListDiffer.DiffChanged -> {
//                    devices[diff.position] = diff.newElement
//                    log.info("notifyItemChanged")
//                    notifyItemChanged(diff.position)
//                }
//            }
//        }
//    }
//
//
//    inner class MplItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val name: TextView = itemView.findViewById(R.id.tvLockerName)
//        val address: TextView = itemView.findViewById(R.id.tvNotificationType)
//        var lockerPictureType: ImageView = itemView.findViewById(R.id.ivNotificationPicture)
//        var clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
//        var ivEditLocker: ImageView = itemView.findViewById(R.id.ivEditLocker)
//
//        val oddBC = getColorAttrValue(R.attr.thmSPRegisteredOddBackgroundColor)
//        val evenBC = getColorAttrValue(R.attr.thmSPRegisteredEvenBackgroundColor)
//
//        private fun getColorAttrValue(attr: Int): Int {
//            val attrArray = intArrayOf(attr)
//            val typedArray = accessSharingListActivity.obtainStyledAttributes(attrArray)
//            val result = typedArray.getColor(0, Color.WHITE)
//            typedArray.recycle()
//            return result
//        }
//
//        fun bindItem(cachedDevice: MPLDevice, clickListener: (MPLDevice) -> Unit) {
//            val parcelLocker = cachedDevice
//
//            if (counter % 2 == 0)
//                clMain.setBackgroundColor(oddBC) //= R.color.colorPrimary40Percent
//            else
//                clMain.setBackgroundColor(evenBC) //= R.color.colorPrimary80Percent
//            counter++
//
//            if (parcelLocker.masterUnitType == RMasterUnitType.SPL && parcelLocker.isInBleProximity && parcelLocker.hasUserRightsOnLocker()
//                && parcelLocker.mplMasterDeviceStatus == MPLDeviceStatus.REGISTERED
//            ) {
//
//                ivEditLocker.visibility = View.VISIBLE
//
//                val drawable = with(TypedValue()) {
//                    accessSharingListActivity.theme.resolveAttribute(R.attr.thmEditSplImage, this, true)
//                    ContextCompat.getDrawable(accessSharingListActivity, resourceId)
//                }
//                ivEditLocker.setImageDrawable(drawable)
//
//                ivEditLocker.setOnClickListener {
//
//                    val startIntent = Intent(accessSharingListActivity, EditSplActivity::class.java)
//                    startIntent.putExtra("rMacAddress", cachedDevice.macAddress)
//                    accessSharingListActivity.startActivity(startIntent)
//                    accessSharingListActivity.finish()
//                }
//            } else {
//                ivEditLocker.visibility = View.INVISIBLE
//            }
//
//            name.text =
//                if (parcelLocker.name.isEmpty()) parcelLocker.macAddress else parcelLocker.name
//            address.text = parcelLocker.address
//
//            val drawable: Drawable?
//            when {
//                (parcelLocker.masterUnitType == RMasterUnitType.MPL && parcelLocker.installationType == InstalationType.DEVICE ) || parcelLocker.type == MPLDeviceType.MASTER -> {
//                    drawable = with(TypedValue()) {
//                        accessSharingListActivity.theme.resolveAttribute(R.attr.thmMplImage, this, true)
//                        ContextCompat.getDrawable(accessSharingListActivity, resourceId)
//                    }
//                }
//                (parcelLocker.masterUnitType == RMasterUnitType.MPL && parcelLocker.installationType == InstalationType.TABLET ) || parcelLocker.type == MPLDeviceType.TABLET -> {
//                    drawable = with(TypedValue()) {
//                        accessSharingListActivity.theme.resolveAttribute(R.attr.thmTabletImage, this, true)
//                        ContextCompat.getDrawable(accessSharingListActivity, resourceId)
//                    }
//                }
//                else -> {
//                    drawable = with(TypedValue()) {
//                        accessSharingListActivity.theme.resolveAttribute(R.attr.thmSplImage, this, true)
//                        ContextCompat.getDrawable(accessSharingListActivity, resourceId)
//                    }
//                }
//            }
//            lockerPictureType.setImageDrawable(drawable)
//
//            itemView.setOnClickListener {
//                clickListener(parcelLocker)
//            }
//        }
//
//    }
//}