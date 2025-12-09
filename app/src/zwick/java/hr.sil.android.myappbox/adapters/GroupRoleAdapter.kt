package hr.sil.android.myappbox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.data.UserGroup

class GroupRoleAdapter(var groups: List<UserGroup>) : BaseAdapter() {
    override fun getItemId(p0: Int): Long {
        return groups[p0].id
    }

    override fun getItem(p0: Int): Any {
        return groups[p0]
    }

    override fun getCount(): Int {
        return groups.size
    }

    override fun getView(p0: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val itemRowHolder: GroupViewHolder
        val itemView = LayoutInflater.from(viewGroup?.context).inflate(R.layout.access_sharing_group_adapter, viewGroup, false)

        if (convertView == null) {
            view = itemView
            itemRowHolder = GroupViewHolder(view)
            view.tag = itemRowHolder
        } else {
            view = convertView
            itemRowHolder = view.tag as GroupViewHolder
        }

        itemRowHolder.itemGroupName.text = groups[p0].textLabel
        return view
    }

    inner class GroupViewHolder(row: View) {
        val itemGroupName: TextView
        init {
            this.itemGroupName = row.findViewById(R.id.itemGroupName)
        }
    }
}