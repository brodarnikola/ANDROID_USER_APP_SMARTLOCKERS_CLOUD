package hr.sil.android.datacache.updatable

import android.content.Context
import hr.sil.android.datacache.NetworkConnectivity
//import hr.sil.android.util.network.NetworkConnectivity
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * @author mfatiga
 */
internal object NetworkAvailabilityChecker {
    private const val NETWORK_AVAILABILITY_CHECK_PERIOD = 5_000L

    private var lastNetworkAvailabilityCheck = AtomicLong(0L)
    private var lastNetworkAvailability = AtomicBoolean(true)

    fun isNetworkAvailable(context: Context): Boolean {
        // do the check if it has not been done recently (in the last 10 seconds)
        if (System.currentTimeMillis() - lastNetworkAvailabilityCheck.get() >= NETWORK_AVAILABILITY_CHECK_PERIOD) {
            lastNetworkAvailability.set(NetworkConnectivity.isNetworkAvailable(context))
        }

        return lastNetworkAvailability.get()
    }
}