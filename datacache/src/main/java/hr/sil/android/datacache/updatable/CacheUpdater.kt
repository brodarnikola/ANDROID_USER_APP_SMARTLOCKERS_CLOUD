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

import android.content.Context
import android.util.Log
import hr.sil.android.datacache.MathUtil
import hr.sil.android.datacache.TwoLevelCache
import hr.sil.android.datacache.format
import hr.sil.android.datacache.state.CacheState
//import hr.sil.android.util.general.extensions.format
//import hr.sil.android.util.general.util.MathUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

/**
 * @author mfatiga
 */
internal class CacheUpdater<in K : Any, E : Any>(
        private val context: Context?,
        private val cache: TwoLevelCache<K, E>,
        private val sourceFull: CacheSource.ForCache<E>? = null,
        private val sourcePartial: CacheSource.ForCache<E>? = null,
        private val sourceForKey: CacheSource.ForKey<K, E>? = null) {

    companion object {
        private const val TAG = "CacheUpdater"
        var DEBUG_MODE = false
    }

    private fun debug(msg: String, err: Throwable? = null) {
        if (DEBUG_MODE) {
            if (err == null) Log.d(TAG, msg)
            else Log.e(TAG, msg, err)
        }
    }

    /**
     * full update is due if the full source has been defined and a full update has not yet been
     * done or it has not been done in the last [sourceFull.validMillis] milliseconds
     */
    private fun isFullUpdateDue(): Boolean {
        val fullUpdateTimestamp = cache.getCacheState().fullUpdateTimestamp
        return sourceFull != null && (fullUpdateTimestamp <= 0L || (System.currentTimeMillis() - fullUpdateTimestamp) >= sourceFull.validMillis)
    }

    /**
     * partial update is due if the partial source has been defined and a partial update has not
     * been done in the last [sourcePartial.validMillis] milliseconds
     */
    private fun isPartialUpdateDue(): Boolean {
        val partialUpdateTimestamp = cache.getCacheState().partialUpdateTimestamp
        return sourcePartial != null && ((System.currentTimeMillis() - partialUpdateTimestamp) >= sourcePartial.validMillis)
    }

    private fun getSingleElementLastUpdateTimestamp(instanceKey: K): Long {
        val fullUpdateTimestamp = cache.getCacheState().fullUpdateTimestamp
        return (keyUpdateTimestamps[instanceKey] ?: 0L).coerceAtLeast(fullUpdateTimestamp)
    }

    /**
     * single element update is due if the single element source has been defined and a single
     * element update has not been done in the last [sourceForKey.validMillis] milliseconds
     */
    private fun isSingleElementUpdateDue(keyUpdateTimestamp: Long): Boolean =
            sourceForKey != null && ((System.currentTimeMillis() - keyUpdateTimestamp) >= sourceForKey.validMillis)

    private fun isSingleElementFirstUpdate(instanceKey: K): Boolean {
        val lastUpdateTimestamp = getSingleElementLastUpdateTimestamp(instanceKey)
        return lastUpdateTimestamp <= 0L
    }

    private val lastCacheUpdateTimestamp = AtomicLong(0)

    private suspend fun internalCacheUpdate(forceUpdate: Boolean): Boolean {
        var result = false

        val fullUpdateTimestamp = cache.getCacheState().fullUpdateTimestamp
        val partialUpdateTimestamp = cache.getCacheState().partialUpdateTimestamp

        val isFullDue = isFullUpdateDue()
        val isPartialDue = isPartialUpdateDue()

        var doFullUpdate = false
        var doPartialUpdate = false

        if (forceUpdate) {
            // when force update is requested, do a full update if it's due, otherwise do a partial
            // update if the partial source is defined or do a full update if the full source has
            // been defined
            if (isFullDue) doFullUpdate = true
            else {
                if (sourcePartial != null) doPartialUpdate = true
                else if (sourceFull != null) doFullUpdate = true
            }
        } else {
            // when a normal update is requested, check:
            //  - if it's time for a full update do the full update
            //  - if it's not time for a full update and it's time for a partial update
            //    do a partial update
            //  - if it's not time for a full or partial update, don't do an update
            if (isFullDue) doFullUpdate = true
            else if (isPartialDue) doPartialUpdate = true
        }

        when {
            doFullUpdate -> {
                debug("FULL update started")
                val data = try {
                    // source is not null in this case
                    sourceFull!!.invoke(fullUpdateTimestamp)
                } catch (exc: Exception) {
                    debug("FULL update ERROR during source call!", exc)
                    null
                }
                if (data != null) {
                    cache.clear()
                    cache.putAll(data)

                    //update cache state
                    val now = System.currentTimeMillis()
                    cache.setCacheState(CacheState(now, now))
                    lastCacheUpdateTimestamp.set(now)

                    result = true
                    debug("FULL update done!")
                } else {
                    debug("FULL update skipped, data is null!")
                }
            }

            doPartialUpdate -> {
                debug("PARTIAL update started")
                val data = try {
                    // source is not null in this case
                    sourcePartial!!.invoke(partialUpdateTimestamp)
                } catch (exc: Exception) {
                    debug("PARTIAL update ERROR during source call!", exc)
                    null
                }
                if (data != null) {
                    cache.putAll(data)

                    //update cache state
                    val now = System.currentTimeMillis()
                    cache.setCacheState(cache.getCacheState().copy(partialUpdateTimestamp = now))
                    lastCacheUpdateTimestamp.set(now)

                    // Update key timestamps if [sourceForKey] is defined.
                    // There is no need to do this on full update because the key update will
                    // assume that the full update is the key update timestamp if the full update
                    // has been done after a key or partial update.
                    if (sourceForKey != null) {
                        for (element in data) {
                            keyUpdateTimestamps[cache.getInstanceKey(element)] = now
                        }
                    }

                    result = true
                    debug("PARTIAL update done!")
                } else {
                    debug("PARTIAL update skipped, data is null!")
                }
            }

            else -> {
            }
        }

        return result
    }

    private val keyUpdateTimestamps = mutableMapOf<K, Long>()
    private suspend fun internalKeyUpdate(instanceKey: K, forceUpdate: Boolean): Boolean {
        // skip key update checks if no key sources are defined
        if (sourceForKey == null) return false

        val keyUpdateTimestamp = getSingleElementLastUpdateTimestamp(instanceKey)
        val isKeyUpdateDue = forceUpdate || isSingleElementUpdateDue(keyUpdateTimestamp)

        var result = false
        if (isKeyUpdateDue) {
            debug("KEY [$instanceKey] update started")
            val data = try {
                // source is not null in this case
                sourceForKey.invoke(instanceKey, keyUpdateTimestamp)
            } catch (exc: Exception) {
                debug("FULL update ERROR during source call!", exc)
                null
            }
            if (data != null) {
                cache.put(data)

                //update key timestamp
                keyUpdateTimestamps[instanceKey] = System.currentTimeMillis()

                result = true
                debug("KEY [$instanceKey] update done!")
            } else {
                debug("KEY [$instanceKey] update skipped, data is null!")
            }
        }

        return result
    }

    // update coroutine context
    private val updaterCoroutineContext by lazy { Executors.newSingleThreadExecutor().asCoroutineDispatcher() }

    private data class UpdateRequest<out K>(val awaitUpdate: Boolean, val forceUpdate: Boolean, val response: CompletableDeferred<Deferred<Boolean?>>, val instanceKey: K? = null)

    private val updater by lazy {
        GlobalScope.actor<UpdateRequest<K>>(updaterCoroutineContext) {
            //wait for first cache update (full or partial)
            debug("[updater] - calling initial update")
            var activeUpdateDeferred: Deferred<Boolean?> = async(updaterCoroutineContext) { internalCacheUpdate(cache.getCacheState().fullUpdateTimestamp <= 0L) }
            activeUpdateDeferred.await()
            debug("[updater] - initial update complete")

            //handle subsequent update requests
            debug("[updater] - waiting for request...")
            for (request in channel) {
                debug("[updater] - got request (awaitUpdate=${request.awaitUpdate}; forceUpdate=${request.forceUpdate})")
                if (!activeUpdateDeferred.isActive) {
                    activeUpdateDeferred = if (request.instanceKey == null) {
                        debug("[updater] - no update currently in progress, calling CACHE update")
                        async(updaterCoroutineContext) { internalCacheUpdate(request.forceUpdate) }
                    } else {
                        debug("[updater] - no update currently in progress, calling KEY update")
                        async(updaterCoroutineContext) { internalKeyUpdate(request.instanceKey, request.forceUpdate) }
                    }
                }

                if (request.awaitUpdate) {
                    debug("[updater] - need to wait for update, responding with active update deferred")
                    request.response.complete(activeUpdateDeferred)
                } else {
                    debug("[updater] - don't need to wait for update, responding with completed deferred")
                    request.response.complete(CompletableDeferred(value = null))
                }
            }
            debug("[updater] - no more requests!")
        }
    }

    private val cacheUpdateCheckPeriod by lazy {
        val a = sourceFull?.validMillis ?: 0L
        val b = sourcePartial?.validMillis ?: a
        val aRounded = (a / 1000L) * 1000L
        val bRounded = (b / 1000L) * 1000L

        val result = (MathUtil.greatestCommonDenominator(aRounded, bRounded) - 1000L).coerceAtLeast(1000L)
        debug("UPDATE check period set to ${(result / 1000.0).format(2)} seconds.")
        result
    }

    private fun isNetworkAvailable(): Boolean {
        //if context is null, that means that network checking has not been enabled
        return if (context == null) true
        else NetworkAvailabilityChecker.isNetworkAvailable(context)
    }

    /**
     * Will update the cache from the given sources if the requested update validity time has passed.
     *
     * If an update has been done recently and [forceUpdate] is set to false, this method will skip
     * sending an update request and immediately return null.
     *
     * The "recently" check period is defined as a greatest common denominator of partial and full
     * source validity periods floor rounded to nearest 1 second, then subtracted by 1 second, and
     * then coerced to at least 1 second.
     *
     * If [forceUpdate] is set to true or if the cache was not updated using the [sourceFull] source
     * at least once, the method will force an update.
     *
     * If [awaitUpdate] is set to true this method will suspend until the update process is done.
     *
     * This method returns a deferred immediately or when the update process completes, depending
     * on the conditions described above.
     */
    internal suspend fun update(awaitUpdate: Boolean, forceUpdate: Boolean): Boolean? {
        debug("cache.update() - START")

        // skip cache update checks if no cache sources are defined
        if (sourcePartial == null && sourceFull == null) return null
        debug("cache.update() - at least one source is defined")

        // skip cache update checks if the last update has been done recently and this request is
        // not an awaiting request
        val lastUpdateWasRecentlyDone = (System.currentTimeMillis() - lastCacheUpdateTimestamp.get()) < cacheUpdateCheckPeriod
        if (!forceUpdate && lastUpdateWasRecentlyDone) return null
        debug("cache.update() - update not recently done or update forced")

        // skip cache update checks if no update is due and this request is not an awaiting request
        if (!forceUpdate && !isFullUpdateDue() && !isPartialUpdateDue()) return null
        debug("cache.update() - at least one source update is due")

        // skip cache update checks if the cache source is using the network and no network is available
        if (!isNetworkAvailable()) return null
        debug("cache.update() - network is available")

        //build request
        val response = CompletableDeferred<Deferred<Boolean?>>()
        val request = UpdateRequest<K>(awaitUpdate, forceUpdate, response)
        debug("cache.update() - request constructed")

        //send request
        updater.send(request)
        debug("cache.update() - request sent")

        //await response
        val deferred = response.await()
        debug("cache.update() - response complete")

        //await result
        val result = deferred.await()
        debug("cache.update() - END - result complete")

        return result
    }

    /**
     * Will check and try to do a cache update and if the cache update does not do an update, it will
     * request a single element update using the provided [instanceKey] if the [sourceForKey] is
     * defined.
     */
    internal suspend fun update(instanceKey: K, awaitUpdate: Boolean, forceUpdate: Boolean): Boolean? {
        debug("key.update() - START")

        // skip cache update checks if the cache source is using the network and no network is available
        if (!isNetworkAvailable()) return null
        debug("key.update() - network is available")

        // check cache update
        val cacheUpdateResult = update(awaitUpdate = awaitUpdate, forceUpdate = false)
        debug("key.update() - cache.update() returned $cacheUpdateResult")

        // update single key only if source for key has been defined and the cache update
        // did not do an update
        if (sourceForKey != null && cacheUpdateResult != true) {
            debug("key.update() - key source is defined and cache update did not do an update")

            //build request - force await update if this is the first single element update
            val response = CompletableDeferred<Deferred<Boolean?>>()
            val request = UpdateRequest(
                    awaitUpdate = awaitUpdate,
                    forceUpdate = forceUpdate || isSingleElementFirstUpdate(instanceKey),
                    response = response,
                    instanceKey = instanceKey)
            debug("key.update() - request constructed")

            //send request
            updater.send(request)
            debug("key.update() - request sent")

            //await response
            val deferred = response.await()
            debug("key.update() - response complete")

            //await result
            val result = deferred.await()
            debug("key.update() - END - result complete")

            return result
        } else {
            debug("key.update() - END - key source is not defined or cache.update() did an update")
            return cacheUpdateResult
        }
    }
}