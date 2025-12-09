package hr.sil.android.myappbox.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hr.sil.android.myappbox.activities.SelectLockerActivity
import hr.sil.android.myappbox.data.DeviceData
import hr.sil.android.myappbox.data.ItemHomeScreen
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.cache.DataCache
import hr.sil.android.smartlockers.enduser.core.model.MPLDeviceType
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.InstalationType
import hr.sil.android.smartlockers.enduser.core.remote.model.RMasterUnitType
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.core.util.macRealToClean
import hr.sil.android.smartlockers.enduser.store.DeviceStoreRemoteUpdater
import hr.sil.android.smartlockers.enduser.util.ListDiffer
import hr.sil.android.smartlockers.enduser.view.ui.activities.sendparcel.MplRequestAccessDialog
import hr.sil.smartlockers.smartlockers.enduser.core.remote.model.AccessRequestResponse
import hr.sil.smartlockers.smartlockers.enduser.core.remote.model.AccessRequestResponseEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendParcelsAdapter(
    mplLocker: List<ItemHomeScreen>,
    val sendParcelsListActivity: SelectLockerActivity
) : RecyclerView.Adapter<SendParcelsAdapter.ViewHolder>() {

    val log = logger()

    private val devices: MutableList<ItemHomeScreen> = mplLocker.toMutableList()

    enum class ITEM_TYPES(val typeValue: Int) {
        ITEM_HEADER_HOME_SCREEN(0),
        ITEM_CHILD_HOME_SCREEN(1);

        companion object {
            fun from(findViewByIdValue: Int): ITEM_TYPES = values().first { it.typeValue == findViewByIdValue }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return ITEM_TYPES.from(devices.get(position).getRecyclerviewItemType()).typeValue
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = devices[position]
        when (viewType.getRecyclerviewItemType()) {
            ITEM_TYPES.ITEM_HEADER_HOME_SCREEN.typeValue -> {

                holder as HeaderHolder
                val headerItem = devices[position] as ItemHomeScreen.Header
                holder.bindItem(headerItem)
            }
            ITEM_TYPES.ITEM_CHILD_HOME_SCREEN.typeValue -> {

                holder as MplItemViewHolder
                val childItem = devices[position] as ItemHomeScreen.Child
                holder.bindItem(childItem, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0) {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_home_screen_header, parent, false)
            return HeaderHolder(itemView)
        } else {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.locker_item_details, parent, false)
            return MplItemViewHolder(itemView)
        }
    }

    override fun getItemCount() = devices.size

    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun updateDevices(updatedDevices: MutableList<ItemHomeScreen>) {

        val listDiff = ListDiffer.getDiff(
            devices,
            updatedDevices,
            { old, new ->
                if (old is ItemHomeScreen.Child && new is ItemHomeScreen.Child) {
                    old.mplOrSplDevice.macAddress == new.mplOrSplDevice.macAddress &&
                            old.mplOrSplDevice.deviceName == new.mplOrSplDevice.deviceName &&
                            old.mplOrSplDevice.deviceAddress == new.mplOrSplDevice.deviceAddress &&
                            old.mplOrSplDevice.isInBleProximity == new.mplOrSplDevice.isInBleProximity &&
                            old.mplOrSplDevice.publicDevice == new.mplOrSplDevice.publicDevice &&
                            old.mplOrSplDevice.isSplActivate == new.mplOrSplDevice.isSplActivate &&
                            old.mplOrSplDevice.publicDevice == new.mplOrSplDevice.publicDevice &&
                            old.mplOrSplDevice.isUserAssigned == new.mplOrSplDevice.isUserAssigned &&
                            old.mplOrSplDevice.activeAccessRequest == new.mplOrSplDevice.activeAccessRequest &&
                            old.mplOrSplDevice.requiredAccessRequestTypes == new.mplOrSplDevice.requiredAccessRequestTypes &&
                            old.mplOrSplDevice.isSelected == new.mplOrSplDevice.isSelected
                } else if (old is ItemHomeScreen.Header && new is ItemHomeScreen.Header) {
                    old.headerTitle == new.headerTitle
                } else {
                    false
                }
            })

        for (diff in listDiff) {
            when (diff) {
                is ListDiffer.DiffInserted -> {
                    devices.addAll(diff.elements)
                    log.info("notifyItemRangeInserted")
                    notifyItemRangeInserted(diff.position, diff.elements.size)
                }
                is ListDiffer.DiffRemoved -> {
                    //remove devices
                    for (i in (devices.size - 1) downTo diff.position) {
                        devices.removeAt(i)
                    }
                    log.info("notifyItemRangeRemoved")
                    notifyItemRangeRemoved(diff.position, diff.count)
                }
                is ListDiffer.DiffChanged -> {
                    devices[diff.position] = diff.newElement
                    log.info("notifyItemChanged")
                    notifyItemChanged(diff.position)
                }
            }
        }
    }

    inner class HeaderHolder(itemView: View) : ViewHolder(itemView) {

        val headerTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val ivArrowUpAndDown: ImageView = itemView.findViewById(R.id.ivArrowUpAndDown)
        val clArrowUpAndDown: ConstraintLayout = itemView.findViewById(R.id.clArrowUpAndDown)

        fun bindItem(keyObject: ItemHomeScreen.Header) {
            headerTitle.text = keyObject.headerTitle

            if( keyObject.isExpandedList )
                ivArrowUpAndDown.setImageDrawable(ContextCompat.getDrawable(sendParcelsListActivity.baseContext, R.drawable.arrow_up))
            else
                ivArrowUpAndDown.setImageDrawable(ContextCompat.getDrawable(sendParcelsListActivity.baseContext, R.drawable.arrow_down))

            clArrowUpAndDown.setOnClickListener {
                keyObject.isExpandedList = !keyObject.isExpandedList
                if( keyObject.isExpandedList )
                    ivArrowUpAndDown.setImageDrawable(ContextCompat.getDrawable(sendParcelsListActivity.baseContext, R.drawable.arrow_up))
                else
                    ivArrowUpAndDown.setImageDrawable(ContextCompat.getDrawable(sendParcelsListActivity.baseContext, R.drawable.arrow_down))

                for (previousSelectedIndex in (0 until devices.size)) {
                    if( devices[previousSelectedIndex].getRecyclerviewItemType() == 1  ) {
                        val previousSelectedLocker = devices[previousSelectedIndex].getRecvyclerviewItemTypeData() as DeviceData
                        if( previousSelectedLocker.indexOfHeader == keyObject.indexOfHeader ) {
                            previousSelectedLocker.isExpanded = keyObject.isExpandedList
                        }
                    }
                }

                notifyItemRangeChanged(keyObject.positionToExpandCollapseInAdapterList, keyObject.numberOfItems)
            }
        }
    }

    inner class MplItemViewHolder(itemView: View) : ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvLockerName)
        val address: TextView = itemView.findViewById(R.id.tvLockerAddress)
        var lockerPictureType: ImageView = itemView.findViewById(R.id.ivLockerPicture)
        var clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
        var tvLockerActivateRequestAccess: TextView =
            itemView.findViewById(R.id.tvLockerActivateRequestAccess)

        private val requestAccessSelected =
            getDrawableAttrValue(R.attr.thmMPLRequestAccessAlreadySend )
        private val notSelectedRequestAccesss =
            getDrawableAttrValue(R.attr.thmMPLRequestAccess)

        //?attr/
        private fun getDrawableAttrValue(attr: Int): Drawable? {
            val attrArray = intArrayOf(attr)
            val typedArray = sendParcelsListActivity.obtainStyledAttributes(attrArray)
            val result = try {
                typedArray.getDrawable(0)
            } catch (exc: Exception) {
                null
            }
            typedArray.recycle()
            return result
        }

        private fun findViewByIdNewSelectedLocker(currentLockerData: ItemHomeScreen.Child) {

            for (previousSelectedIndex in (0 until devices.size)) {
                if( devices[previousSelectedIndex].getRecyclerviewItemType() == 1  ) {
                    val previousSelectedLocker = devices[previousSelectedIndex].getRecvyclerviewItemTypeData() as DeviceData
                    if( previousSelectedLocker.isSelected ) {
                        log.info("Da li ce tu uci  BEDIII: ${previousSelectedLocker.macAddress}")
                        previousSelectedLocker.isSelected = false
                        notifyItemChanged( previousSelectedIndex, previousSelectedLocker)
                        break
                    }
                }
            }

            currentLockerData.mplOrSplDevice.isSelected = true

            App.ref.selectedMasterMacAddress = currentLockerData.mplOrSplDevice.macAddress

            // this is with animation
            notifyItemChanged(adapterPosition)
            setLastSelectedLocker(currentLockerData.mplOrSplDevice)
        }

        fun bindItem(currentItem: ItemHomeScreen.Child, position: Int) {
            val parcelLocker =
                currentItem.mplOrSplDevice //MPLDeviceStore.uniqueDevices[currentItem.mplOrSplDevice?.macAddress]
            if (parcelLocker != null) {

                clMain.setVisibility(if (parcelLocker.isExpanded ) View.VISIBLE else View.GONE)

                name.text =
                    if (parcelLocker.deviceName.isEmpty()) parcelLocker.macAddress else parcelLocker.deviceName
                address.text = parcelLocker.deviceAddress

                clMain.setOnClickListener {
                    log.info("Mac address inside selected city parcel locker is: ${parcelLocker.macAddress}")
                    findViewByIdNewSelectedLocker(currentItem)
                }

                if ( parcelLocker.isUserAssigned && (( parcelLocker.installationType == InstalationType.LINUX /*&& parcelLocker.isLinuxDeviceInProximity*/ ) || (parcelLocker.isInBleProximity)) ) {

                    tvLockerActivateRequestAccess.visibility = View.GONE

                    if( parcelLocker.isSelected ) {
                        clMain.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.square_button_primary)
                        lockerPictureType.setImageDrawable(ContextCompat.getDrawable( itemView.context, R.drawable.ic_locker_green_inverted))
                        name.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorWhite))
                        address.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorWhite))
                    }
                    else {
                        clMain.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.rounded_button_dark_blue)
                        lockerPictureType.setImageDrawable(ContextCompat.getDrawable( itemView.context, R.drawable.ic_locker_green))
                        name.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorBlackText))
                        address.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorBlackText))
                    }
                } else if ( ( !parcelLocker.isInBleProximity /*|| !parcelLocker.isLinuxDeviceInProximity*/ ) && parcelLocker.isUserAssigned ) {

                    log.info("da li ce uci sim 111222")
                    tvLockerActivateRequestAccess.visibility = View.GONE
                    if( parcelLocker.isSelected ) {
                        clMain.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.square_button_primary)
                        lockerPictureType.setImageDrawable(ContextCompat.getDrawable( itemView.context, R.drawable.ic_locker_yellow_inverted))
                        name.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorWhite))
                        address.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorWhite))
                    }
                    else {
                        clMain.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.rounded_button_dark_blue)
                        lockerPictureType.setImageDrawable(ContextCompat.getDrawable( itemView.context, R.drawable.ic_locker_yellow))
                        name.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorBlackText))
                        address.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorBlackText))
                    }
                }
                else if ( ( /*parcelLocker.isLinuxDeviceInProximity ||*/ parcelLocker.isInBleProximity ) || !parcelLocker.isUserAssigned ) {

                    tvLockerActivateRequestAccess.visibility = View.VISIBLE
                    if( parcelLocker.isSelected ) {
                        clMain.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.square_button_primary)
                        lockerPictureType.setImageDrawable(ContextCompat.getDrawable( itemView.context, R.drawable.ic_locker_grey_inverted))
                        name.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorWhite))
                        address.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorWhite))
                        tvLockerActivateRequestAccess.background = requestAccessSelected
                        tvLockerActivateRequestAccess.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorWhite))
                    }
                    else {
                        clMain.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.rounded_button_dark_blue)
                        lockerPictureType.setImageDrawable(ContextCompat.getDrawable( itemView.context, R.drawable.ic_locker_grey))
                        name.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorBlackText))
                        address.setTextColor( ContextCompat.getColor( itemView.context, R.color.colorBlackText))
                        tvLockerActivateRequestAccess.background = requestAccessSelected
                        tvLockerActivateRequestAccess.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorBlackText))
                    }

                    if (parcelLocker.backendDeviceType == RMasterUnitType.MPL || parcelLocker.bleDeviceType == MPLDeviceType.MASTER
                        || parcelLocker.installationType == InstalationType.TABLET || parcelLocker.installationType == InstalationType.LINUX )  {
                        if ( !parcelLocker.activeAccessRequest ) {
                            tvLockerActivateRequestAccess.visibility = View.VISIBLE
                            tvLockerActivateRequestAccess.text =
                                sendParcelsListActivity.getString(R.string.locker_details_registration_btn)

                            tvLockerActivateRequestAccess.setOnClickListener {
                                GlobalScope.launch {
                                    val success: AccessRequestResponse
                                    log.info("Address for access ${parcelLocker.macAddress.macRealToClean()}")
                                    success =
                                        WSUser.requestMPlAccess(parcelLocker.macAddress.macRealToClean()) ?: AccessRequestResponse()
                                    log.info("Access request response is: ${success.result}")
                                    DataCache.getActiveRequestAccessMpl(true)
                                    withContext(Dispatchers.Main) {
                                        if (success.result == AccessRequestResponseEnum.PENDING) {
                                            val mplRequestAccessDialog = MplRequestAccessDialog()
                                            mplRequestAccessDialog.show(sendParcelsListActivity.supportFragmentManager, "")
                                            tvLockerActivateRequestAccess.visibility = View.VISIBLE
                                            tvLockerActivateRequestAccess.setText(R.string.locker_request_send)
                                            parcelLocker.activeAccessRequest = true
                                            notifyItemChanged(position)
                                        }
                                        else if (success.result == AccessRequestResponseEnum.GRANTED) {
                                            Toast.makeText(itemView.context, R.string.app_generic_success, Toast.LENGTH_SHORT).show()
                                            parcelLocker.isUserAssigned = true
                                            notifyItemChanged(position)
                                        }
                                        else {
                                            Toast.makeText(itemView.context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        } else if( parcelLocker.activeAccessRequest ) {
                            tvLockerActivateRequestAccess.visibility = View.VISIBLE
                            tvLockerActivateRequestAccess.setOnClickListener {

                            }
                            tvLockerActivateRequestAccess.text =
                                sendParcelsListActivity.getString(R.string.locker_request_send)
                        }
                        else {
                            tvLockerActivateRequestAccess.visibility = View.GONE
                        }
                    } else {
                        if (!parcelLocker.isSplActivate ) {
                            tvLockerActivateRequestAccess.visibility = View.VISIBLE
                            tvLockerActivateRequestAccess.text =
                                sendParcelsListActivity.getString(R.string.locker_details_activate_btn)

                            tvLockerActivateRequestAccess.setOnClickListener {
                                GlobalScope.launch {
                                    val success: Boolean
                                    log.info("Address for access ${parcelLocker.macAddress.macRealToClean()}")
                                    success =
                                        WSUser.activateSPL(parcelLocker.macAddress.macRealToClean())
                                    withContext(Dispatchers.Main) {
                                        if (success) {
                                            //AppUtil.refreshCache()
                                            DeviceStoreRemoteUpdater.forceUpdate()
                                            Toast.makeText(
                                                itemView.context,
                                                R.string.app_generic_success,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                itemView.context,
                                                R.string.something_went_wrong,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        } else {
                            tvLockerActivateRequestAccess.visibility = View.VISIBLE
                            tvLockerActivateRequestAccess.text =
                                sendParcelsListActivity.getString(R.string.locker_no_access)
                        }
                    }
                }

            }
        }
    }

    companion object {

        private var lastSelectedLocker = DeviceData()

        fun getLastSelectedLocker(): DeviceData {
            return lastSelectedLocker
        }

        fun setLastSelectedLocker(mLastSelectedLocker: DeviceData) {
            lastSelectedLocker = mLastSelectedLocker
        }
    }

}