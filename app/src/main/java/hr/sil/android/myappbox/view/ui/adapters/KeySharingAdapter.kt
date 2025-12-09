//package hr.sil.android.myappbox.view.ui.adapters
//
//
//import android.content.ComponentName
//import android.content.Intent
//import android.graphics.Color
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import hr.sil.android.myappbox.App
//
//import hr.sil.android.myappbox.data.DeleteAccessSharingInterface
//import hr.sil.android.myappbox.R
//import hr.sil.android.myappbox.core.remote.WSUser
//import hr.sil.android.myappbox.core.remote.model.*
//import hr.sil.android.myappbox.core.util.logger
//import hr.sil.android.myappbox.util.backend.UserUtil
//import hr.sil.android.myappbox.view.ui.activities.access_sharing.AccessSharingActivity
//import hr.sil.android.myappbox.view.ui.activities.access_sharing.DeleteAccessSharingUserDialog
//
////import org.jetbrains.anko.*
//import kotlinx.coroutines.*
//import org.jetbrains.anko.toast
//
//
//class KeySharingAdapter(var keys: MutableList<ItemRGroupInfo>, var macAddress: String, var nameOfDevice: String, var accessSharingActivity: AccessSharingActivity) : RecyclerView.Adapter<KeySharingAdapter.ViewHolder>() {
//
//    var counter: Int = 0
//
//    enum class ITEM_TYPES(val typeValue: Int) {
//        ITEM_HEADER(0),
//        ITEM_ADMIN_NAME(1),
//        ITEM_CHILD(2),
//        ITEM_EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERSHIP(3);
//
//        companion object {
//            fun from(findValue: Int): ITEM_TYPES = values().first { it.typeValue == findValue }
//        }
//    }
//
//
//    override fun getItemViewType(position: Int): Int {
//
//        return ITEM_TYPES.from(keys.get(position).getListItemType()).typeValue
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val viewType = keys[position]
//        when (viewType.getListItemType()) {
//            ITEM_TYPES.ITEM_HEADER.typeValue -> {
//
//                holder as HeaderHolder
//                val headerItem = keys[position] as RGroupDisplayMembersHeader
//                holder.bindItem(headerItem)
//            }
//            ITEM_TYPES.ITEM_ADMIN_NAME.typeValue -> {
//
//                val childItem = keys[position] as RGroupDisplayMembersAdmin
//                holder as AdminHolder
//                holder.bindItem(childItem)
//            }
//            ITEM_TYPES.ITEM_CHILD.typeValue -> {
//
//                val childItem = keys[position] as RGroupDisplayMembersChild
//                holder as ChildHolder
//                holder.bindItem(childItem)
//            }
//            ITEM_TYPES.ITEM_EMPTY_OWNER_GROUP_MEMBERS_OR_GROUP_MEMBERSHIP.typeValue -> {
//
//                val childItem = keys[position] as REmptyGroupMembers
//                holder as EmptyGroupMembersHolder
//                holder.bindItem(childItem)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        if (viewType == 0) {
//            val itemViewType = LayoutInflater.from(parent.context)
//                .inflate(R.layout.access_sharing_header, parent, false)
//            return HeaderHolder(itemViewType)
//        } else if (viewType == 1) {
//            val itemViewType = LayoutInflater.from(parent.context)
//                .inflate(R.layout.access_sharing_subheader, parent, false)
//            return AdminHolder(itemViewType)
//        } else if (viewType == 2) {
//            val itemViewType = LayoutInflater.from(parent.context)
//                .inflate(R.layout.access_sharing_child, parent, false)
//            return ChildHolder(itemViewType)
//        } else {
//            val itemViewType = LayoutInflater.from(parent.context)
//                .inflate(R.layout.access_sharing_no_child_items, parent, false)
//            return EmptyGroupMembersHolder(itemViewType)
//        }
//    }
//
//    override fun getItemCount() = keys.size
//
//    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//
//    inner class HeaderHolder(itemView: View) : ViewHolder(itemView) {
//
//        val headerTitle: TextView = itemView.findViewById(R.id.headerTitle)
//
//        fun bindItem(keyObject: RGroupDisplayMembersHeader) {
//            headerTitle.text = keyObject.groupOwnerName
//        }
//    }
//
//    inner class AdminHolder(itemView: View) : ViewHolder(itemView) {
//
//        val subHeaderTitle: TextView = itemView.findViewById(R.id.subHeaderTitle)
//        val addUser: ImageView = itemView.findViewById(R.id.addUser)
//        val adminInGroup: ImageView = itemView.findViewById(R.id.adminInGroup)
//
//        fun bindItem(keyObject: RGroupDisplayMembersAdmin) {
//            subHeaderTitle.text = keyObject.groupOwnerName
//            addUser.setOnClickListener { addNewAccess(keyObject) }
//
//            when (keyObject.role) {
//                "ADMIN" -> {
//                    addUser.visibility = View.VISIBLE
//                    adminInGroup.visibility = View.VISIBLE
//                }
//                "USER" -> {
//                    addUser.visibility = View.GONE
//                    adminInGroup.visibility = View.GONE
//                }
//            }
//
//            if( keyObject.groupOwnerName == UserUtil.userGroup?.name )
//                addUser.visibility = View.VISIBLE
//            else {
//            }
//        }
//
//        val log = logger()
//        private fun addNewAccess(keyObject: RGroupDisplayMembersAdmin) {
//
//            /*val startIntent = Intent(accessSharingActivity, AccessSharingAddUserActivity::class.java)
//            startIntent.putExtra("rMacAddress", macAddress)
//            startIntent.putExtra("nameOfDevice", nameOfDevice)
//
//            accessSharingActivity.startActivity(startIntent)
//            accessSharingActivity.finish()*/
//
//            val intent = Intent()
//            intent.putExtra("rMacAddress", macAddress)
//            intent.putExtra("nameOfDevice", nameOfDevice)
//            intent.putExtra("nameOfGroup", keyObject.groupOwnerName)
//            intent.putExtra("groupId", keyObject.groupId)
//            val packageName = accessSharingActivity.packageName
//            val componentName = ComponentName(packageName, packageName + ".aliasAccessSharingAddUser")
//            intent.component = componentName
//
//            accessSharingActivity.startActivity(intent)
//            accessSharingActivity.finish()
//        }
//    }
//
//
//    inner class ChildHolder(itemView: View) : ViewHolder(itemView), DeleteAccessSharingInterface {
//
//        val mainLayout: ConstraintLayout = itemView.findViewById(R.id.clMainLayout)
//        val userName: TextView = itemView.findViewById(R.id.userName)
//        val userEmail: TextView = itemView.findViewById(R.id.userEmail)
//        val adminInGroup: ImageView = itemView.findViewById(R.id.adminInGroup)
//        val removeFromGroup: ImageView = itemView.findViewById(R.id.removeFromGroup)
//
//        val registeredBC = getColorAttrValue(R.attr.thmASChildOddBC)
//        val registeredButUnavailableBC = getColorAttrValue(R.attr.thmASChildEvenBC)
//
//        private fun getColorAttrValue(attr: Int): Int {
//            val attrArray = intArrayOf(attr)
//            val typedArray = accessSharingActivity.obtainStyledAttributes(attrArray)
//            val result = typedArray.getColor(0, Color.WHITE)
//            typedArray.recycle()
//            return result
//        }
//
//        fun bindItem(keyObject: RGroupDisplayMembersChild) {
//
//            if (counter % 2 == 0)
//                mainLayout.setBackgroundColor(registeredBC)
//            else
//                mainLayout.setBackgroundColor(registeredButUnavailableBC)
//            counter++
//
//            userName.text = keyObject.endUserName
//            userEmail.text = keyObject.endUserEmail
//            when (keyObject.role) {
//                "ADMIN" -> adminInGroup.visibility = View.VISIBLE
//                "USER" -> adminInGroup.visibility = View.INVISIBLE
//            }
//            removeFromGroup.setOnClickListener { deleteAccess(keyObject) }
//        }
//
//        val log = logger()
//        private fun deleteAccess(removeAccess: RGroupDisplayMembersChild) {
//
//            val deleteAccessSharing =
//                DeleteAccessSharingUserDialog(this@ChildHolder, removeAccess, itemView)
//            deleteAccessSharing.show(accessSharingActivity.supportFragmentManager, "")
//        }
//
//        override fun deleteAccessSharing(removeAccess: RGroupDisplayMembersChild, view: View) {
//
//            val userAccess = RUserRemoveAccess().apply {
//                this.groupId = removeAccess.groupId
//                this.endUserId = removeAccess.endUserId
//                this.masterId = removeAccess.master_id
//            }
//
//            GlobalScope.launch {
//                if (WSUser.removeUserAccess(userAccess)) {
//                    log.info("Successfully remove user ${removeAccess.groupOwnerName} from group")
//
//                    deleteItem(removeAccess /*, itemView.context */)
//                            withContext(Dispatchers.Main) {
//
//                                notifyDataSetChanged()
//                                //clickListener(key)
//                                App.Companion.ref.toast(view.context.getString(R.string.toast_access_sharing_remove_success, removeAccess.groupOwnerName))
//                            }
//                } else {
//                    withContext(Dispatchers.Main) {
//                        App.Companion.ref.toast(view.context.getString(R.string.toast_access_sharing_remove_error))
//
//                    }
//                }
//            }
//        }
//    }
//
//    private suspend fun deleteItem(item: RGroupDisplayMembersChild /* , context: Context */) {
//
//        var deletedInOwnerList = false
//
//        for (ownerItems in DataCache.getGroupMembers()) {
//
//            if (ownerItems.endUserId == item.endUserId && ownerItems.groupId == item.groupId && ownerItems.master_id == item.master_id) {
//
//                deletedInOwnerList = true
//                DataCache.deleteOwnerGroupElement(ownerItems.id)
//                break
//            }
//        }
//
//        if (deletedInOwnerList == false) {
//
//            val groupMemberShipId = DataCache.getGroupMembershipById().toMutableList()
//            var isAdminItemDeleted = false
//
//            if (groupMemberShipId.isNotEmpty()) {
//
//                for (items in groupMemberShipId) {
//
//                    val groupDataList: MutableList<RGroupInfo> = DataCache.groupMemberships(items.groupId.toLong()).toMutableList()
//                    Log.d("KeySharingAdapter", "Admin group list size is: " + DataCache.groupMemberships(items.groupId.toLong()).size)
//
//                    if (groupDataList.size > 0) {
//
//                        for (subItems in groupDataList) {
//
//                            if (subItems.endUserId == item.endUserId && subItems.groupId == item.groupId && subItems.master_id == item.master_id) {
//
//                                isAdminItemDeleted = true
//                                DataCache.groupMemberships(items.groupId.toLong(), true)
//                                break
//                            }
//                        }
//                    }
//
//                    if (isAdminItemDeleted) {
//                        break
//                    }
//                }
//            }
//        }
//
//        Log.d("KeySharingAdapter", "Second example Admin group list size is: " + DataCache.getGroupMembers().size)
//
//        keys.remove(item)
//    }
//
//    inner class EmptyGroupMembersHolder(itemView: View) : ViewHolder(itemView) {
//
//        val emptyGroupMembersList: TextView = itemView.findViewById(R.id.emptyGroupMembersList)
//
//        fun bindItem(emptyGroupMembersObject: REmptyGroupMembers) {
//
//            emptyGroupMembersList.text = emptyGroupMembersObject.emptyGroupMembers
//        }
//    }
//
//}