package hr.sil.android.myappbox.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.compose.main_activity.MainActivity
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.events.QrCodeScannedEvent
import hr.sil.android.myappbox.util.AppUtil
import hr.sil.android.myappbox.util.NotificationHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class MPLFireBaseMessagingService : FirebaseMessagingService() {
    val log = logger()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        log.info("From: " + remoteMessage.from!!)
        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            log.info("Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage.data)
            }

        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            log.info("Message Notification Body: " + remoteMessage.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String) {
        log.info("Refreshed token: $token")
        GlobalScope.launch(Dispatchers.Default){
            if (!sendRegistrationToServer(token)) {
                log.error("Error in registration to server please check your internet connection")
            }

        }
    }



    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
//        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//        val myJob = dispatcher.newJobBuilder()
//                .setService(NotificationJobService::class.java)
//                .setTag("my-job-tag")
//                .build()
//        dispatcher.schedule(myJob)
        // [END dispatch_job]
    }

    private fun handleNow(result: Map<String, String>) {
        val type = result["type"]?:""
        log.info("Did new notification arrived?: ${result["subject"]} .. body: ${result["body"]}")
        if(type=="DEFAULT"){
            NotificationHelper.createNotification(result["subject"], result["body"], MainActivity::class.java)
            GlobalScope.launch {
                AppUtil.refreshCache()
                UserUtil.pahKeys = WSUser.getActivePaHCreatedKeys() ?: mutableListOf() //DataCache.getPickAtHomeKeys(true).toMutableList()
                // when we will receive push notification from backend for refreshing "cancel pick at home keys", then I need to use this
                //DataCache.getPickAtHomeKeys(true)
            }
            log.info("Short task when notification is opened is done. $type " )
        }
        else if( type == "SILENT" ) {
            val masterMac = result["masterMac"]
            if (masterMac != null) {

                log.info("Silent push masterMac has arrived ${masterMac} ")
                GlobalScope.launch {
                    //DataCache.preloadKeysCache()
                    WSUser.getActiveKeys() ?: listOf()
                    withContext(Dispatchers.Main) {
                        App.Companion.ref.eventBus.post(QrCodeScannedEvent(true))
                        withContext(Dispatchers.IO) {
                            log.info("Silent push masterMac has.. Will it enter here, just to refresh data ${masterMac} ")
                            WSUser.getMasterUnits()
                            //DataCache.preloadMasterUnitsCache()
                        }
                    }
                }
            }
        }

    }

    companion object {
        suspend fun sendRegistrationToServer(token: String): Boolean {
            return WSUser.registerDevice(token)
        }
    }
}