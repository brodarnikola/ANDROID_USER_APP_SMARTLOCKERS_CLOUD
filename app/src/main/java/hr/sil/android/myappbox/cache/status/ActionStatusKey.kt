package hr.sil.android.myappbox.cache.status

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class ActionStatusKey {

    var rMacAddress: String = ""
    var keyId: String = ""
    var timeOfInstance: Date = Date()
    var isScheduleDelete: Boolean = false
    val SCHEDULE_PERIOD: Long = 1000L * 300L


    fun scheduleDelete(instanceKey: String, delayReduction: Long = 0L) {
        if (isScheduleDelete) {
            GlobalScope.launch(Dispatchers.Default) {
                delay(SCHEDULE_PERIOD - delayReduction)
                try {
                    //ActionStatusHandler.actionStatusDb.del(instanceKey)
                } catch (ex: Exception) {

                }
            }
        }
    }

    init {
        if (isScheduleDelete) {
            scheduleDelete(keyId)
        }
    }

}