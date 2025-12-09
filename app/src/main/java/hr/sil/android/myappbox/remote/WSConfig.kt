package hr.sil.android.myappbox.remote

import android.content.Context
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.rest.core.configuration.ServiceConfig
import hr.sil.android.rest.core.configuration.parameters.model.Authorization
import hr.sil.android.myappbox.core.remote.service.UserAppService
import hr.sil.android.myappbox.core.remote.service.UserPublicService
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.preferences.PreferenceStore

/**
 * @author mfatiga
 */
object WSConfig {
    private val log = logger()

    fun initialize(applicationContext: Context) {
        log.info("Initializing web service configuration...")
        ServiceConfig.initialize(applicationContext)
        log.info("Web service configuration initialized, APP_KEY: ${ServiceConfig.cfg.appKey}")

        log.info("Configuring WSUser clients...")
        UserAppService.config.setBaseURL(BuildConfig.API_BASE_URL, BuildConfig.API_CONTEXT)
        UserPublicService.config.setBaseURL(BuildConfig.API_BASE_URL, BuildConfig.API_CONTEXT)

        updateAuthorizationKeys()
    }

    fun updateAuthorizationKeys() {
        log.info("Updating authorization keys...")
        val authKey = PreferenceStore.userHash ?: ""
        UserAppService.config.setAuthorization(Authorization.Basic(authKey))
        log.info("API Key: $authKey")
    }
}