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

package hr.sil.android.datacache.core

import android.content.Context
import hr.sil.android.datacache.TwoLevelCache
import hr.sil.android.datacache.state.CacheState
import kotlin.reflect.KClass

/**
 * @author mfatiga
 */
internal class TwoLevelCacheImpl<K : Any, E : Any> internal constructor(
        context: Context,
        memoryLruMaxSize: Int,
        eClass: KClass<E>,
        classGroup: String,
        private val instanceKey: E.() -> K) : TwoLevelCache<K, E> {

    private val persistence: CorePersistenceCache<K, E> = CorePersistenceCache(context, eClass, classGroup, instanceKey)

    private val memory: CoreMemoryLruCache<K, E>? = if (memoryLruMaxSize > 0) {
        CoreMemoryLruCache(memoryLruMaxSize.coerceAtLeast(1), instanceKey)
    } else {
        null
    }

    override fun getInstanceKey(element: E): K = instanceKey.invoke(element)

    override fun get(instanceKey: K): E? {
        synchronized(this) {
            val inMemory = memory?.get(instanceKey)
            return if (inMemory != null) {
                inMemory
            } else {
                val inPersistence = persistence.get(instanceKey)
                if (inPersistence != null) {
                    memory?.put(inPersistence)
                }
                inPersistence
            }
        }
    }

    override fun put(element: E) {
        synchronized(this) {
            memory?.put(element)
            persistence.put(element)
        }
    }

    override fun del(instanceKey: K) {
        synchronized(this) {
            memory?.del(instanceKey)
            persistence.del(instanceKey)
        }
    }

    override fun getAll(): Collection<E> {
        synchronized(this) {
            return if (memory != null && memory.size() == persistence.size()) {
                val res = memory.getAll()
                res
            } else {
                val inPersistence = persistence.getAll()
                if (memory != null && memory.maxSize() >= persistence.size()) {
                    memory.putAll(inPersistence)
                }
                inPersistence
            }
        }
    }

    override fun putAll(collection: Collection<E>) {
        if (collection.isNotEmpty()) {
            synchronized(this) {
                memory?.clear()
                persistence.putAll(collection)
            }
        }
    }

    override fun clear() {
        synchronized(this) {
            memory?.clear()
            persistence.clear()
        }
    }

    override fun size(): Int = persistence.size()

    override fun getCacheState(): CacheState =
            persistence.getCacheState()

    override fun setCacheState(cacheState: CacheState) {
        persistence.setCacheState(cacheState)
    }
}