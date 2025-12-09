package hr.sil.android.myappbox.smartlockers.enduser.core.remote.model


class RInstallationKey {
    var status: InstallationStatus = InstallationStatus.INVITATION_REQUIRED
    var invitationCode: String = ""

    enum class InstallationStatus{
        INVITATION_REQUIRED, CREATED
    }
}