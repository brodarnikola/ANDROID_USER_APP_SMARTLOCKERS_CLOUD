package hr.sil.android.datacache.db

import android.content.Context
import com.esotericsoftware.kryo.Kryo
import com.snappydb.DB
import com.snappydb.DBFactory
import com.snappydb.KeyIterator
import com.snappydb.SnappydbException
import java.io.Serializable

/**
 * @author mfatiga
 */
internal object DBMultiFactory {
    private const val DEFAULT_DB_NAME = "_db_"

    private val databases = mutableMapOf<String, DBSync>()

    private var dbOpened = false
    private lateinit var db: DB
    internal fun open(context: Context, name: String = DEFAULT_DB_NAME): DBSync {
        synchronized(this) {
            if (!dbOpened) {
                databases.clear()

                db = DBFactory.open(context)
                dbOpened = true
            }

            return if (databases.containsKey(name)) {
                databases[name]!!
            } else {
                val dbSync = DBSync(name)
                databases[name] = dbSync
                dbSync
            }
        }
    }

    private fun key(dbName: String, key: String?): String? {
        val result = if (key != null) dbName + "_" + key else null
//        Log.i("DBMultiFactory", "key() = $result")
        return result
    }


    //********* MULTI DB IMPLEMENTATION ***********//

    //***********************
    //*      DB MANAGEMENT
    //***********************

    @Throws(SnappydbException::class)
    fun close(name: String) {
        synchronized(this) {
            databases.remove(name)
            if (databases.isEmpty()) {
                db.close()
                dbOpened = false
            }
        }
    }

    @Throws(SnappydbException::class)
    fun destroy(name: String) {
        synchronized(this) {
            databases.remove(name)
            if (databases.isEmpty()) {
                db.destroy()
                dbOpened = false
            }
        }
    }

    @Throws(SnappydbException::class)
    fun isOpen(name: String): Boolean {
        synchronized(this) {
            return db.isOpen && databases.containsKey(name)
        }
    }

    //***********************
    //*      CREATE
    //***********************
    @Throws(SnappydbException::class)
    fun put(name: String, key: String?, data: ByteArray?) {
        synchronized(this) {
            db.put(key(name, key), data)
        }
    }

    @Throws(SnappydbException::class)
    fun put(name: String, key: String?, value: String?) {
        synchronized(this) {
            db.put(key(name, key), value)
        }
    }

    @Throws(SnappydbException::class)
    fun put(name: String, key: String?, value: Serializable?) {
        synchronized(this) {
            db.put(key(name, key), value)
        }
    }

    @Throws(SnappydbException::class)
    fun put(name: String, key: String?, value: Array<out Serializable>?) {
        synchronized(this) {
            db.put(key(name, key), value)
        }
    }

    @Throws(SnappydbException::class)
    fun put(name: String, key: String?, `object`: Any?) {
        synchronized(this) {
            db.put(key(name, key), `object`)
        }
    }

    @Throws(SnappydbException::class)
    fun put(name: String, key: String?, `object`: Array<out Any>?) {
        synchronized(this) {
            db.put(key(name, key), `object`)
        }
    }

    @Throws(SnappydbException::class)
    fun putInt(name: String, key: String?, `val`: Int) {
        synchronized(this) {
            db.putInt(key(name, key), `val`)
        }
    }

    @Throws(SnappydbException::class)
    fun putShort(name: String, key: String?, `val`: Short) {
        synchronized(this) {
            db.putShort(key(name, key), `val`)
        }
    }

    @Throws(SnappydbException::class)
    fun putBoolean(name: String, key: String?, `val`: Boolean) {
        synchronized(this) {
            db.putBoolean(key(name, key), `val`)
        }
    }

    @Throws(SnappydbException::class)
    fun putDouble(name: String, key: String?, `val`: Double) {
        synchronized(this) {
            db.putDouble(key(name, key), `val`)
        }
    }

    @Throws(SnappydbException::class)
    fun putFloat(name: String, key: String?, `val`: Float) {
        synchronized(this) {
            db.putFloat(key(name, key), `val`)
        }
    }

    @Throws(SnappydbException::class)
    fun putLong(name: String, key: String?, `val`: Long) {
        synchronized(this) {
            db.putLong(key(name, key), `val`)
        }
    }

    //***********************
    //*      DELETE
    //***********************
    @Throws(SnappydbException::class)
    fun del(name: String, key: String?) {
        synchronized(this) {
            db.del(key(name, key))
        }
    }

    //***********************
    //*      RETRIEVE
    //***********************
    @Throws(SnappydbException::class)
    fun get(name: String, key: String?): String {
        synchronized(this) {
            return db.get(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun getBytes(name: String, key: String?): ByteArray {
        synchronized(this) {
            return db.getBytes(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun <T : Serializable?> get(name: String, key: String?, className: Class<T>?): T {
        synchronized(this) {
            return db.get(key(name, key), className)
        }
    }

    @Throws(SnappydbException::class)
    fun <T : Any?> getObject(name: String, key: String?, className: Class<T>?): T {
        synchronized(this) {
            return db.getObject(key(name, key), className)
        }
    }

    @Throws(SnappydbException::class)
    fun <T : Serializable?> getArray(name: String, key: String?, className: Class<T>?): Array<out T> {
        synchronized(this) {
            return db.getArray(key(name, key), className)
        }
    }

    @Throws(SnappydbException::class)
    fun <T : Any?> getObjectArray(name: String, key: String?, className: Class<T>?): Array<out T> {
        synchronized(this) {
            return db.getObjectArray(key(name, key), className)
        }
    }

    @Throws(SnappydbException::class)
    fun getShort(name: String, key: String?): Short {
        synchronized(this) {
            return db.getShort(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun getInt(name: String, key: String?): Int {
        synchronized(this) {
            return db.getInt(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun getBoolean(name: String, key: String?): Boolean {
        synchronized(this) {
            return db.getBoolean(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun getDouble(name: String, key: String?): Double {
        synchronized(this) {
            return db.getDouble(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun getLong(name: String, key: String?): Long {
        synchronized(this) {
            return db.getLong(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun getFloat(name: String, key: String?): Float {
        synchronized(this) {
            return db.getFloat(key(name, key))
        }
    }

    //****************************
    //*      KEYS OPERATIONS
    //****************************
    private fun Array<out String>.cleanKeys(name: String): Array<out String> {
        val dbNameKeyPrefix = key(name, "")!!
        return this.map { it.substring(dbNameKeyPrefix.length) }.toTypedArray()
    }

    @Throws(SnappydbException::class)
    fun exists(name: String, key: String?): Boolean {
        synchronized(this) {
            return db.exists(key(name, key))
        }
    }

    @Throws(SnappydbException::class)
    fun findKeys(name: String, prefix: String?): Array<out String> {
        synchronized(this) {
            return db.findKeys(key(name, prefix)).cleanKeys(name)
        }
    }

    @Throws(SnappydbException::class)
    fun findKeys(name: String, prefix: String?, offset: Int): Array<out String> {
        synchronized(this) {
            return db.findKeys(key(name, prefix), offset).cleanKeys(name)
        }
    }

    @Throws(SnappydbException::class)
    fun findKeys(name: String, prefix: String?, offset: Int, limit: Int): Array<out String> {
        synchronized(this) {
            return db.findKeys(key(name, prefix), offset, limit).cleanKeys(name)
        }
    }

    @Throws(SnappydbException::class)
    fun countKeys(name: String, prefix: String?): Int {
        synchronized(this) {
            return db.countKeys(key(name, prefix))
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysBetween(name: String, startPrefix: String?, endPrefix: String?): Array<out String> {
        synchronized(this) {
            return db.findKeysBetween(key(name, startPrefix), key(name, endPrefix)).cleanKeys(name)
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysBetween(name: String, startPrefix: String?, endPrefix: String?, offset: Int): Array<out String> {
        synchronized(this) {
            return db.findKeysBetween(key(name, startPrefix), key(name, endPrefix), offset).cleanKeys(name)
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysBetween(name: String, startPrefix: String?, endPrefix: String?, offset: Int, limit: Int): Array<out String> {
        synchronized(this) {
            return db.findKeysBetween(key(name, startPrefix), key(name, endPrefix), offset, limit).cleanKeys(name)
        }
    }

    @Throws(SnappydbException::class)
    fun countKeysBetween(name: String, startPrefix: String?, endPrefix: String?): Int {
        synchronized(this) {
            return db.countKeysBetween(key(name, startPrefix), key(name, endPrefix))
        }
    }

    //***********************
    //*      ITERATORS
    //***********************
    @Throws(SnappydbException::class)
    fun allKeysIterator(name: String): KeyIterator {
        synchronized(this) {
            return CleanKeyIterator(key(name, "")!!, db.findKeysIterator(name))
        }
    }

    @Throws(SnappydbException::class)
    fun allKeysReverseIterator(name: String): KeyIterator {
        synchronized(this) {
            return CleanKeyIterator(key(name, "")!!, db.findKeysReverseIterator(name))
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysIterator(name: String, prefix: String?): KeyIterator {
        synchronized(this) {
            return CleanKeyIterator(key(name, "")!!, db.findKeysIterator(key(name, prefix)))
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysReverseIterator(name: String, prefix: String?): KeyIterator {
        synchronized(this) {
            return CleanKeyIterator(key(name, "")!!, db.findKeysReverseIterator(key(name, prefix)))
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysBetweenIterator(name: String, startPrefix: String?, endPrefix: String?): KeyIterator {
        synchronized(this) {
            return CleanKeyIterator(key(name, "")!!, db.findKeysBetweenIterator(key(name, startPrefix), key(name, endPrefix)))
        }
    }

    @Throws(SnappydbException::class)
    fun findKeysBetweenReverseIterator(name: String, startPrefix: String?, endPrefix: String?): KeyIterator {
        synchronized(this) {
            return CleanKeyIterator(key(name, "")!!, db.findKeysBetweenReverseIterator(key(name, startPrefix), key(name, endPrefix)))
        }
    }

    //*********************************
    //*      KRYO SERIALIZATION
    //*********************************

    // Allow the user to access the Kryo instance, for eventual customization
    fun getKryoInstance(): Kryo {
        synchronized(this) {
            return db.kryoInstance
        }
    }
}