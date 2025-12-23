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

package hr.sil.android.datacache

import android.content.Context
import hr.sil.android.datacache.updatable.CacheSource
import hr.sil.android.datacache.updatable.CacheUpdater

/**
 * @author mfatiga
 */
class AutoCache<in K : Any, E : Any> private constructor(
        context: Context?,
        private val cache: TwoLevelCache<K, E>,
        sourceFull: CacheSource.ForCache<E>?,
        sourcePartial: CacheSource.ForCache<E>?,
        sourceForKey: CacheSource.ForKey<K, E>?) {

    companion object {
        var DEBUG_MODE: Boolean
            get() = CacheUpdater.DEBUG_MODE
            set(value) {
                CacheUpdater.DEBUG_MODE = value
            }
    }

    class Builder<K : Any, E : Any>(private val cache: TwoLevelCache<K, E>) {
        /*
         * AutoCache created instance. Assigned on the first call of the [build] method and
         * returned on all subsequent calls.
         */
        private var instance: AutoCache<K, E>? = null

        private var sourceFull: CacheSource.ForCache<E>? = null
        fun setFullSource(source: CacheSource.ForCache<E>): Builder<K, E> {
            this.sourceFull = source
            return this
        }

        private var sourcePartial: CacheSource.ForCache<E>? = null
        fun setPartialSource(source: CacheSource.ForCache<E>): Builder<K, E> {
            this.sourcePartial = source
            return this
        }

        private var sourceForKey: CacheSource.ForKey<K, E>? = null
        fun setSingleElementSource(source: CacheSource.ForKey<K, E>): Builder<K, E> {
            this.sourceForKey = source
            return this
        }

        private var context: Context? = null
        fun enableNetworkChecking(context: Context): Builder<K, E> {
            this.context = context
            return this
        }

        /**
         * Build an instance of the [TwoLevelCache]. This method will return the same instance on
         * multiple calls.
         */
        fun build(): AutoCache<K, E> {
            if (instance == null) {
                instance = AutoCache(context, cache, sourceFull, sourcePartial, sourceForKey)
            }
            return instance!!
        }
    }

    private val updater = CacheUpdater(context, cache, sourceFull, sourcePartial, sourceForKey)

    suspend fun get(instanceKey: K, awaitUpdate: Boolean = false): E? {
        updater.update(instanceKey = instanceKey, awaitUpdate = awaitUpdate, forceUpdate = awaitUpdate)
        return cache.get(instanceKey)
    }

    fun put(element: E) = cache.put(element)

    fun del(instanceKey: K) = cache.del(instanceKey)

    suspend fun getAll(awaitUpdate: Boolean = false): Collection<E> {
        updater.update(awaitUpdate = awaitUpdate, forceUpdate = awaitUpdate)
        return cache.getAll()
    }

    fun putAll(collection: Collection<E>) = cache.putAll(collection)

    fun clear() = cache.clear()
}