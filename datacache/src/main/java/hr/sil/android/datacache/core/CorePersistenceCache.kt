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
import hr.sil.android.datacache.CacheDatabase
import hr.sil.android.datacache.state.CacheState
import hr.sil.android.datacache.synchronizedDelegate
import hr.sil.android.datacache.util.CacheKeyConstructor
import hr.sil.android.datacache.util.PersistenceClassTracker
//import hr.sil.android.util.general.delegates.synchronizedDelegate
import kotlin.reflect.KClass

/**
 * @author mfatiga
 */
internal class CorePersistenceCache<in K : Any, E : Any>(
        context: Context,
        private val eClass: KClass<E>,
        classGroup: String,
        private val instanceKey: E.() -> K) {

    private val db by synchronizedDelegate(CacheDatabase.getCacheDatabase(context), this)

    init {
        PersistenceClassTracker.checkClass(db, eClass)
    }


    /* KEY LOGIC */
    private val classGroupSuffix = if (classGroup.isNotBlank()) "_${classGroup.trim()}_" else ""
    private val elementPrefix = CacheKeyConstructor.getDataKeyPrefix(eClass) + classGroupSuffix

    private fun getKey(instanceKey: String): String {
        val cleanInstanceKey = instanceKey.trim().replace(" ", "_")
        return if (cleanInstanceKey.isBlank()) {
            elementPrefix
        } else {
            "${elementPrefix}_$cleanInstanceKey"
        }
    }

    private fun key() = getKey("")
    private fun key(instanceKey: K) = getKey(instanceKey.toString())
    private fun keyForInstance(element: E) = key(instanceKey(element))


    /* CACHE STATE */
    private val cacheStateKey = CacheKeyConstructor.getStateKeyPrefix(eClass)
    private var cacheState: CacheState = loadCacheState()
    private fun loadCacheState(): CacheState {
        return if (db.exists(cacheStateKey)) {
            try {
                db.getObject(cacheStateKey, CacheState::class.java)
            } catch (exc: Exception) {
                db.del(cacheStateKey)
                CacheState()
            }
        } else {
            CacheState()
        }
    }

    fun getCacheState(): CacheState = cacheState

    fun setCacheState(cacheState: CacheState) {
        this.cacheState = cacheState
        db.put(cacheStateKey, cacheState)
    }


    /* CACHE OPERATIONS */
    private fun getAllKeys(): Array<String> = db.findKeys(key()) ?: arrayOf()

    private fun countAllKeys(): Int = db.countKeys(key())

    @Volatile private var sizeDirty = true
    private fun markSizeDirty() {
        synchronized(this) {
            sizeDirty = true
        }
    }

    private var size = 0
    fun size(): Int {
        if (sizeDirty) {
            synchronized(this) {
                if (sizeDirty) {
                    size = countAllKeys()
                    sizeDirty = false
                }
            }
        }
        return size
    }

    fun get(instanceKey: K): E? {
        val key = key(instanceKey)
        return if (db.exists(key)) {
            db.getObject(key, eClass.java)
        } else {
            null
        }
    }

    fun put(element: E) {
        db.put(keyForInstance(element), element)

        markSizeDirty()
    }

    fun del(instanceKey: K) {
        try {
            db.del(key(instanceKey))
        } catch (exc: Exception) {
            //ignore
        }

        markSizeDirty()
    }

    fun getAll(): Collection<E> =
            getAllKeys().map { db.getObject(it, eClass.java) }

    fun putAll(collection: Collection<E>) {
        for (element in collection) {
            db.put(keyForInstance(element), element)
        }

        markSizeDirty()
    }

    fun clear() {
        for (key in getAllKeys()) {
            try {
                db.del(key)
            } catch (exc: Exception) {
                //ignore
            }
        }

        setCacheState(CacheState(0L, 0L))
        markSizeDirty()
    }
    /* ------------------------ */
}