package hr.sil.android.myappbox.adapters

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.sil.android.myappbox.activities.ShareAccessKeyActivity
import hr.sil.android.myappbox.activities.collect_parcel.ListOfDeliveriesActivity
import hr.sil.android.myappbox.data.LockerKeyWithShareAccess
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.model.InstalationType
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerKeyPurpose
import hr.sil.android.smartlockers.enduser.core.util.formatFromStringToDate
import hr.sil.android.smartlockers.enduser.core.util.formatToViewDateTimeDefaults
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.util.ListDiffer
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import java.text.ParseException
import java.util.*

class ListOfDeliveriesAdapter (mplLocker: List<LockerKeyWithShareAccess>,
                               val clickListener: (LockerKeyWithShareAccess) -> Unit,
                               val listOfDeliveriesActivity: ListOfDeliveriesActivity,
                               val macAddress: String
) : RecyclerView.Adapter<ListOfDeliveriesAdapter.MplItemViewHolder>() {

    val log = logger()

    private val devices: MutableList<LockerKeyWithShareAccess> = mplLocker.toMutableList()

    override fun onBindViewHolder(holder: MplItemViewHolder, position: Int) {
        holder.bindItem(devices[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MplItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_of_deliveries_parcel, parent, false)
        return MplItemViewHolder(view)
    }

    override fun getItemCount() = devices.size


    fun updateDevices(updatedDevices: List<LockerKeyWithShareAccess>) {

        val listDiff = ListDiffer.getDiff(
            devices,
            updatedDevices,
            { old, new ->
                old.masterName == new.masterName &&
                        old.masterAddress == new.masterAddress &&
                        old.trackingNumber == new.trackingNumber &&
                        old.timeCreated == new.timeCreated &&
                        old.tan == new.tan &&
                        old.lockerSize == new.lockerSize
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

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class MplItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvLockerName)
        val address: TextView = itemView.findViewById(R.id.tvLockerAddress)
        var lockerPictureType: ImageView = itemView.findViewById(R.id.ivLockerPicture)
        var clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
        val tvTrackingNumberValue: TextView = itemView.findViewById(R.id.tvTrackingNumberValue)
        val tvDeliveredValue: TextView = itemView.findViewById(R.id.tvDeliveredValue)
        val tvTANValue: TextView = itemView.findViewById(R.id.tvTANValue)
        val tvLockerSizeValue: TextView = itemView.findViewById(R.id.tvLockerSizeValue)
        val btnShareData: Button = itemView.findViewById(R.id.btnShareData)
        val rvShareAccess: RecyclerView = itemView.findViewById(R.id.rvShareAccess)
        val tvSharedWith: TextView = itemView.findViewById(R.id.tvSharedWith)

        fun bindItem(cached: LockerKeyWithShareAccess, clickListener: (LockerKeyWithShareAccess) -> Unit) {
            val parcelLocker =cached
            if (parcelLocker != null) {

                log.info("Unique id: ${UserUtil.user?.uniqueId}")
                val nameValue = when {
                    parcelLocker.masterName?.isNotEmpty() ?: false && UserUtil.user?.uniqueId != null -> parcelLocker.masterName?.trim() + " - " + UserUtil.user?.uniqueId
                    parcelLocker.masterName?.isNotEmpty() ?: false && UserUtil.user?.uniqueId == null -> parcelLocker.masterName?.trim()
                    else -> "-"
                }
                name.text = nameValue
                address.text = parcelLocker.masterAddress

                val trackingNumber = if( parcelLocker.trackingNumber != null && parcelLocker.trackingNumber != "" ) parcelLocker.trackingNumber else "-"
                val tan = if( parcelLocker.tan != null ) parcelLocker.tan else "-"

                tvTrackingNumberValue.setText( "" + trackingNumber)

                parcelLocker.timeCreated = formatCorrectDate(parcelLocker.timeCreated)
                tvDeliveredValue.setText(parcelLocker.timeCreated)
                tvTANValue.setText("" + tan)
                tvLockerSizeValue.setText(parcelLocker.lockerSize)

                val drawable: Drawable? = with(TypedValue()) {
                    listOfDeliveriesActivity.theme.resolveAttribute(R.attr.thmDeliveryImage, this, true)
                    ContextCompat.getDrawable(listOfDeliveriesActivity, resourceId)
                }

                lockerPictureType.setImageDrawable(drawable)

                if( parcelLocker.installationType == InstalationType.LINUX ) {
                    btnShareData.visibility = View.GONE
                    if( cached.listOfShareAccess.isNotEmpty() ) {
                        tvSharedWith.visibility = View.GONE
                        rvShareAccess.visibility = View.GONE
                        rvShareAccess.apply {
                            layoutManager = LinearLayoutManager(rvShareAccess.context, RecyclerView.VERTICAL, false)
                            adapter = ShareKeyAdapter(cached.listOfShareAccess, listOfDeliveriesActivity)
                        }
                    }
                    else {
                        tvSharedWith.visibility = View.GONE
                        rvShareAccess.visibility = View.GONE
                    }
                }
                else if( parcelLocker.purpose == RLockerKeyPurpose.DELIVERY ) {
                    tvSharedWith.visibility = View.GONE
                    btnShareData.visibility = View.GONE
                    rvShareAccess.visibility = View.VISIBLE
                    rvShareAccess.apply {
                        layoutManager = LinearLayoutManager(rvShareAccess.context, RecyclerView.VERTICAL, false)
                        adapter = ShareKeyAdapter(cached.listOfShareAccess, listOfDeliveriesActivity)
                    }
                }
                else {
                    tvSharedWith.visibility = View.GONE
                    tvSharedWith.setText(itemView.context.getString(
                        R.string.peripheral_settings_grant_access,
                        parcelLocker.createdByName,
                        parcelLocker.lockerSize,
                        parcelLocker.timeCreated
                    ))
                    rvShareAccess.visibility = View.GONE
                    btnShareData.visibility = View.GONE
                }

                btnShareData.setOnClickListener {
                    val startIntent = Intent(listOfDeliveriesActivity, ShareAccessKeyActivity::class.java)
                    startIntent.putExtra("shareAccessKeyId", parcelLocker.id)
                    startIntent.putExtra("macAddress", macAddress)
                    listOfDeliveriesActivity.startActivity(startIntent)
                    listOfDeliveriesActivity.finish()
                }

                itemView.setOnClickListener {
                    clickListener(parcelLocker)
                }
            }
        }

        private fun formatCorrectDate(timeCreated: String): String {
            val fromStringToDate: Date
            var fromDateToString = ""
            try {
                fromStringToDate = timeCreated.formatFromStringToDate()
                fromDateToString = fromStringToDate.formatToViewDateTimeDefaults()
            }
            catch (e: ParseException) {
                e.printStackTrace()
            }
            log.info("Correct date is: ${fromDateToString}")
            return fromDateToString
        }
    }
}