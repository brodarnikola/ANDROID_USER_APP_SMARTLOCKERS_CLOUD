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
import hr.sil.android.datacache.core.TwoLevelCacheImpl
import hr.sil.android.datacache.state.CacheState
import kotlin.reflect.KClass

/**
 * @author mfatiga
 */
interface TwoLevelCache<K : Any, E : Any> {
    class Builder<K : Any, E : Any>(
            private val eClass: KClass<E>,
            private val instanceKey: E.() -> K) {

        /*
         * TwoLevelCache created instance. Assigned on the first call of the [build] method and
         * returned on all subsequent calls.
         */
        private var instance: TwoLevelCache<K, E>? = null

        // parameters
        private var classGroup = ""
        private var memoryLruMaxSize = 0

        /**
         * Set a [classGroup]. The class group is used when creating multiple caches for a single
         * cache element class ([eClass]).
         */
        fun classGroup(classGroup: String): Builder<K, E> {
            this.classGroup = classGroup
            return this
        }

        /**
         * Set the LRU memory cache max size. When this is set to 0, the memory cache will not be
         * created.
         */
        fun memoryLruMaxSize(maxSize: Int): Builder<K, E> {
            this.memoryLruMaxSize = maxSize
            return this
        }


        /**
         * Build an instance of the [TwoLevelCache]. This method will return the same instance on
         * multiple calls.
         */
        fun build(context: Context): TwoLevelCache<K, E> {
            if (instance == null) {
                instance = TwoLevelCacheImpl(context, memoryLruMaxSize, eClass, classGroup, instanceKey)
            }
            return instance!!
        }
    }

    fun getInstanceKey(element: E): K

    fun get(instanceKey: K): E?
    fun put(element: E)
    fun del(instanceKey: K)

    fun getAll(): Collection<E>
    fun putAll(collection: Collection<E>)
    fun clear()
    fun size(): Int

    fun getCacheState(): CacheState
    fun setCacheState(cacheState: CacheState)
}