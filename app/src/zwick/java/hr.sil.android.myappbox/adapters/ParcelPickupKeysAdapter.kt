package hr.sil.android.myappbox.adapters

import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hr.sil.android.myappbox.activities.collect_parcel.PickupParcelActivity
import hr.sil.android.myappbox.dialog.DeletePickAFriendKeyInsidePickupActivityDialog
import hr.sil.android.myappbox.dialog.PickAFriendKeyDialog
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.InstalationType
import hr.sil.android.smartlockers.enduser.core.remote.model.RCreatedLockerKey
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerKeyPurpose
import hr.sil.android.smartlockers.enduser.core.remote.model.RMasterUnitType
import hr.sil.android.smartlockers.enduser.core.util.formatFromStringToDate
import hr.sil.android.smartlockers.enduser.core.util.formatToViewDateTimeDefaults
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.store.MPLDeviceStore
import hr.sil.android.smartlockers.enduser.view.ui.activities.DisplayQrCodeActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.collectparcel.PickupParcelInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import java.text.ParseException
import java.util.*

class ParcelPickupKeysAdapter(
    val updateKeys: () -> Unit,
    val type: RMasterUnitType,
    val instalationType: InstalationType,
    val macAddress: String,
    val activity: PickupParcelActivity
) : RecyclerView.Adapter<ParcelPickupKeysAdapter.NotesViewHolder>(), PickupParcelInterface {

    val log = logger()

    val keys = mutableListOf<RCreatedLockerKey>()

    var counter: Int = 0

    fun update(newKeys: List<RCreatedLockerKey>) {
        keys.clear()
        keys.addAll(newKeys)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        p1: Int
    ): ParcelPickupKeysAdapter.NotesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.parcel_pickup_keys, parent, false)
        return NotesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return keys.size
    }

    fun removeItem(index: Int) {
        keys.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, keys.size)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindItem(keys[position], position)
    }

    override fun deletePickAtFriendKey(position: Int, keyId: Int, rowView: View) {
        GlobalScope.launch {
            if (WSUser.deletePaF(keyId)) {
                withContext(Dispatchers.Main) {
                    App.ref.toast(
                        rowView.context.getString(
                            R.string.peripheral_settings_remove_access_success,
                            keyId.toString()
                        )
                    )
                    removeItem(position)
                }
            } else {
                withContext(Dispatchers.Main) {
                    App.ref.toast(
                        rowView.context.getString(
                            R.string.peripheral_settings_remove_access_error,
                            keyId.toString()
                        )
                    )
                }
            }
        }
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val log = logger()
        val name: TextView = itemView.findViewById(R.id.keySharingItemDetails)
        val buttonShareDeleteKey: ImageButton = itemView.findViewById(R.id.buttonShareDeleteKey)
        val mainLayout: ConstraintLayout = itemView.findViewById(R.id.clMainLayout)
        val cancelAccess = with(TypedValue()) {
            activity.theme.resolveAttribute(R.attr.thmCPDeleteFriendKey, this, true)
            ContextCompat.getDrawable(activity, resourceId)
        }
        val shareAccess = with(TypedValue()) {
            activity.theme.resolveAttribute(R.attr.thmCPShareFriendKey, this, true)
            ContextCompat.getDrawable(activity, resourceId)
        }

        val qrCode = with(TypedValue()) {
            activity.theme.resolveAttribute(R.attr.thmSmallQrCodeImage, this, true)
            ContextCompat.getDrawable(activity, resourceId)
        }

        val oddLayoutBC = getColorAttrValue(R.attr.thmCPShareKeyOddBC)
        val evenLayoutBC = getColorAttrValue(R.attr.thmCPShareKeyEvenBC)

        private fun getColorAttrValue(attr: Int): Int {
            val attrArray = intArrayOf(attr)
            val typedArray = activity.obtainStyledAttributes(attrArray)
            val result = typedArray.getColor(0, Color.WHITE)
            typedArray.recycle()
            return result
        }

        fun bindItem(parcelLockerKey: RCreatedLockerKey, position: Int) {

            if (counter % 2 == 0)
                mainLayout.setBackgroundColor(oddLayoutBC)
            else
                mainLayout.setBackgroundColor(evenLayoutBC)
            counter++

            name.text = ""
            when (parcelLockerKey.purpose) {
                RLockerKeyPurpose.DELIVERY -> {
                    val createdForName =
                        parcelLockerKey.masterName + " " + parcelLockerKey.masterAddress

                    parcelLockerKey.timeCreated = formatCorrectDate(parcelLockerKey.timeCreated)
                    if (type == RMasterUnitType.SPL)
                        name.text = itemView.context.getString(
                            R.string.peripheral_settings_share_access_spl,
                            parcelLockerKey.timeCreated
                        )
                    else
                        name.text = itemView.context.getString(
                            R.string.peripheral_settings_share_access,
                            parcelLockerKey.lockerSize,
                            parcelLockerKey.timeCreated
                        )
                    buttonShareDeleteKey.visibility = View.VISIBLE
                    if( instalationType != InstalationType.LINUX ) {
                        buttonShareDeleteKey.setImageDrawable(shareAccess)
                        buttonShareDeleteKey.setOnClickListener {
                            val pickAtAFriendKeyDialog = PickAFriendKeyDialog(
                                parcelLockerKey.id,
                                activity
                            )
                            pickAtAFriendKeyDialog.show(activity.supportFragmentManager, "")
                        }
                    }
                    else {
                        log.info("qrCode is: ${parcelLockerKey.qrCode}")
                        buttonShareDeleteKey.setImageDrawable(qrCode)
                        buttonShareDeleteKey.setOnClickListener {
                            val intentQrCodeImage = Intent(activity, DisplayQrCodeActivity::class.java)
                            intentQrCodeImage.putExtra("returnToCorrectScreen", 1)
                            intentQrCodeImage.putExtra("rMacAddress", macAddress)
                            activity.startActivity(intentQrCodeImage)
                            activity.finish()
                        }
                    }
                }
                RLockerKeyPurpose.PAF -> {
                    log.info("Created P@F for ${parcelLockerKey.createdForId}")
                    val mplName =
                        MPLDeviceStore.uniqueDevices[parcelLockerKey.getMasterBLEMacAddress()]?.name

                    parcelLockerKey.baseTimeCreated = formatCorrectDate(parcelLockerKey.baseTimeCreated)
                    if (parcelLockerKey.createdForId != null) {
                        if (type == RMasterUnitType.MPL) {
                            name.text = itemView.context.getString(
                                R.string.peripheral_settings_remove_access,
                                parcelLockerKey.createdForEndUserName,
                                parcelLockerKey.lockerSize,
                                parcelLockerKey.baseTimeCreated
                            )
                        } else {
                            name.text = itemView.context.getString(
                                R.string.peripheral_settings_remove_access_spl,
                                parcelLockerKey.createdForEndUserName,
                                parcelLockerKey.baseTimeCreated
                            )
                        }
                        if( instalationType != InstalationType.LINUX ) {
                            buttonShareDeleteKey.visibility = View.VISIBLE
                            buttonShareDeleteKey.setImageDrawable(cancelAccess)
                            buttonShareDeleteKey.setOnClickListener {
                                val deletePickAtFriendKey =
                                    DeletePickAFriendKeyInsidePickupActivityDialog(
                                        this@ParcelPickupKeysAdapter,
                                        position,
                                        parcelLockerKey.createdForEndUserEmail ?: "",
                                        parcelLockerKey.id,
                                        itemView
                                    )
                                deletePickAtFriendKey.show(
                                    activity.supportFragmentManager,
                                    ""
                                )
                            }
                        }
                        else
                            buttonShareDeleteKey.visibility = View.INVISIBLE
                    } else {
                        if (type == RMasterUnitType.MPL) {
                            name.text = itemView.context.getString(
                                R.string.peripheral_settings_grant_access,
                                parcelLockerKey.createdByName,
                                parcelLockerKey.lockerSize,
                                parcelLockerKey.baseTimeCreated
                            )
                        } else {
                            name.text = itemView.context.getString(
                                R.string.peripheral_settings_grant_access_spl,
                                parcelLockerKey.createdByName,
                                parcelLockerKey.baseTimeCreated
                            )
                        }
                        buttonShareDeleteKey.visibility = View.GONE
                    }
                }
                RLockerKeyPurpose.UNKNOWN -> {

                }
                else -> {}
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