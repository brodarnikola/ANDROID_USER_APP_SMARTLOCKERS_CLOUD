package hr.sil.android.myappbox.data

import android.view.View
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey

interface CancelPickedHomeInterface {

    fun cancelPickedHome(keyObject: RCreatedLockerKey, view: View)
}