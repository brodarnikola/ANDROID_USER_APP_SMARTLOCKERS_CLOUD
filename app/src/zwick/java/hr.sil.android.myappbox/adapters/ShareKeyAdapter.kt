package hr.sil.android.myappbox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import hr.sil.android.myappbox.activities.collect_parcel.ListOfDeliveriesActivity
import hr.sil.android.myappbox.data.DeletePafKey
import hr.sil.android.myappbox.data.ShareAccessKey
import hr.sil.android.myappbox.dialog.DeletePickAFriendKeyDialog
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.util.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast

class ShareKeyAdapter (mplLocker: List<ShareAccessKey>, val listOfDeliveriesActivity: ListOfDeliveriesActivity
) : RecyclerView.Adapter<ShareKeyAdapter.MplItemViewHolder>(), DeletePafKey {

    val log = logger()

    private val devices: MutableList<ShareAccessKey> = mplLocker.toMutableList()

    override fun onBindViewHolder(holder: MplItemViewHolder, position: Int) {
        holder.bindItem(devices[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MplItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_share_key, parent, false)
        return MplItemViewHolder(view)
    }

    override fun getItemCount() = devices.size

    inner class MplItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val ivCancelShareAccess: ImageView = itemView.findViewById(R.id.ivCancelShareAccess)
        val clRemoveKey: ConstraintLayout = itemView.findViewById(R.id.clRemoveKey)

        fun bindItem(shareAccessKey: ShareAccessKey ) {

            tvEmail.setText(shareAccessKey.email)

            clRemoveKey.setOnClickListener {
                val deletePickAtFriendKey =
                    DeletePickAFriendKeyDialog(
                        this@ShareKeyAdapter,
                        shareAccessKey,
                        itemView
                    )
                deletePickAtFriendKey.show(
                    listOfDeliveriesActivity.supportFragmentManager,
                    ""
                )
            }
        }
    }

    override fun deletePickAtFriendKey(shareAccessKey: ShareAccessKey, rowView: View) {
        GlobalScope.launch {
            if (WSUser.deletePaF(shareAccessKey.id)) {
                withContext(Dispatchers.Main) {
                    App.ref.toast(
                        rowView.context.getString(
                            R.string.peripheral_settings_remove_access_success,
                            shareAccessKey.id.toString()
                        )
                    )
                    devices.remove(shareAccessKey)
                    notifyDataSetChanged()
                }
            } else {
                withContext(Dispatchers.Main) {
                    App.ref.toast(
                        rowView.context.getString(
                            R.string.peripheral_settings_remove_access_error,
                            shareAccessKey.id.toString()
                        )
                    )
                }
            }
        }
    }

}