package hr.sil.android.myappbox.data

import android.view.View
import hr.sil.android.myappbox.core.remote.model.RGroupDisplayMembersChild

interface DeleteAccessSharingInterface {

    fun deleteAccessSharing(removeAccess: RGroupDisplayMembersChild, view: View)
}