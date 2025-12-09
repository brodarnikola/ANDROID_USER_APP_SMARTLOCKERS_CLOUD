package hr.sil.android.myappbox.listeners

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.sil.android.myappbox.cache.status.InstallationKeyHandler
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.data.InstallationKey


class InstallListener : BroadcastReceiver() {

    val log = logger()

    override fun onReceive(ctx: Context?, intent: Intent?) {

        val rawReferrerString = intent?.getStringExtra("referrer")

        log.info("Getting ref key from intent $rawReferrerString")
        if (!rawReferrerString.isNullOrBlank()) {
            InstallationKeyHandler.key.put(InstallationKey(rawReferrerString))
        }
    }
}