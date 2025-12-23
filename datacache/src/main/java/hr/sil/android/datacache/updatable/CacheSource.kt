/* SWISS INNOVATION LAB CONFIDENTIAL
*
* www.swissinnolab.com
* __________________________________________________________________________
*
* [2016] - [2017] Swiss Innovation Lab AG
* All Rights Reserved.
*
* @author mfatiga
*
* NOTICE:  All information contained herein is, and remains
* the property of Swiss Innovation Lab AG and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Swiss Innovation Lab AG
* and its suppliers and may be covered by E.U. and Foreign Patents,
* patents in process, and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Swiss Innovation Lab AG.
*/

package hr.sil.android.datacache.updatable

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * @author mfatiga
 */
sealed class CacheSource(validTime: Long, validUnit: TimeUnit) {
    val validMillis = validUnit.toMillis(validTime)

    sealed class ForCache<out E>(validTime: Long, validUnit: TimeUnit) : CacheSource(validTime, validUnit) {
        class Suspendable<out E>(
                validTime: Long,
                validUnit: TimeUnit,
                val loader: suspend (lastUpdatedMillis: Long) -> Collection<E>?
        ) : ForCache<E>(validTime, validUnit)

        class Deferrable<out E>(
                validTime: Long,
                validUnit: TimeUnit,
                val loader: (lastUpdatedMillis: Long) -> Deferred<Collection<E>?>?
        ) : ForCache<E>(validTime, validUnit)

        class Blocking<out E>(
                validTime: Long,
                validUnit: TimeUnit,
                val loader: (lastUpdatedMillis: Long) -> Collection<E>?
        ) : ForCache<E>(validTime, validUnit)

        suspend operator fun invoke(lastUpdatedMillis: Long): Collection<E>? = when (this) {
            is Suspendable -> this.loader(lastUpdatedMillis)
            is Deferrable -> this.loader(lastUpdatedMillis)?.await()
            is Blocking -> withContext(Dispatchers.Default) { this@ForCache.loader(lastUpdatedMillis) }
        }
    }

    sealed class ForKey<in K, out E>(validTime: Long, validUnit: TimeUnit) : CacheSource(validTime, validUnit) {
        class Suspendable<in K, out E>(
                validTime: Long,
                validUnit: TimeUnit,
                val loader: suspend (instanceKey: K, lastUpdatedMillis: Long) -> E?
        ) : ForKey<K, E>(validTime, validUnit)

        class Deferrable<in K, out E>(
                validTime: Long,
                validUnit: TimeUnit,
                val loader: (instanceKey: K, lastUpdatedMillis: Long) -> Deferred<E?>?
        ) : ForKey<K, E>(validTime, validUnit)

        class Blocking<in K, out E>(
                validTime: Long,
                validUnit: TimeUnit,
                val loader: (instanceKey: K, lastUpdatedMillis: Long) -> E?
        ) : ForKey<K, E>(validTime, validUnit)

        suspend operator fun invoke(instanceKey: K, lastUpdatedMillis: Long): E? = when (this) {
            is Suspendable -> this.loader(instanceKey, lastUpdatedMillis)
            is Deferrable -> this.loader(instanceKey, lastUpdatedMillis)?.await()
            is Blocking -> withContext(Dispatchers.Default) { this@ForKey.loader(instanceKey, lastUpdatedMillis) }
        }
    }
}