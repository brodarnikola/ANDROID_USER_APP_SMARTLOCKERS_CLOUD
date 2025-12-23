package hr.sil.android.datacache.db

import com.esotericsoftware.kryo.Kryo
import com.snappydb.DB
import com.snappydb.KeyIterator
import com.snappydb.SnappydbException
import java.io.Serializable

/**
 * A wrapper for snappy DB that synchronizes database access.
 *
 * @author mfatiga
 */
class DBSync internal constructor(private val name: String) : DB {

    //***********************
    //*      DB MANAGEMENT
    //***********************

    @Throws(SnappydbException::class)
    override fun close() {
        DBMultiFactory.close(name)
    }

    @Throws(SnappydbException::class)
    override fun destroy() {
        DBMultiFactory.destroy(name)
    }

    @Throws(SnappydbException::class)
    override fun isOpen(): Boolean = DBMultiFactory.isOpen(name)

    //***********************
    //*      CREATE
    //***********************
    @Throws(SnappydbException::class)
    override fun put(key: String?, data: ByteArray?) {
        DBMultiFactory.put(name, key, data)
    }

    @Throws(SnappydbException::class)
    override fun put(key: String?, value: String?) {
        DBMultiFactory.put(name, key, value)
    }

    @Throws(SnappydbException::class)
    override fun put(key: String?, value: Serializable?) {
        DBMultiFactory.put(name, key, value)
    }

    @Throws(SnappydbException::class)
    override fun put(key: String?, value: Array<out Serializable>?) {
        DBMultiFactory.put(name, key, value)
    }

    @Throws(SnappydbException::class)
    override fun put(key: String?, `object`: Any?) {
        DBMultiFactory.put(name, key, `object`)
    }

    @Throws(SnappydbException::class)
    override fun put(key: String?, `object`: Array<out Any>?) {
        DBMultiFactory.put(name, key, `object`)
    }

    @Throws(SnappydbException::class)
    override fun putInt(key: String?, `val`: Int) {
        DBMultiFactory.putInt(name, key, `val`)
    }

    @Throws(SnappydbException::class)
    override fun putShort(key: String?, `val`: Short) {
        DBMultiFactory.putShort(name, key, `val`)
    }

    @Throws(SnappydbException::class)
    override fun putBoolean(key: String?, `val`: Boolean) {
        DBMultiFactory.putBoolean(name, key, `val`)
    }

    @Throws(SnappydbException::class)
    override fun putDouble(key: String?, `val`: Double) {
        DBMultiFactory.putDouble(name, key, `val`)
    }

    @Throws(SnappydbException::class)
    override fun putFloat(key: String?, `val`: Float) {
        DBMultiFactory.putFloat(name, key, `val`)
    }

    @Throws(SnappydbException::class)
    override fun putLong(key: String?, `val`: Long) {
        DBMultiFactory.putLong(name, key, `val`)
    }

    //***********************
    //*      DELETE
    //***********************
    @Throws(SnappydbException::class)
    override fun del(key: String?) {
        DBMultiFactory.del(name, key)
    }

    //***********************
    //*      RETRIEVE
    //***********************
    @Throws(SnappydbException::class)
    override fun get(key: String?): String = DBMultiFactory.get(name, key)

    @Throws(SnappydbException::class)
    override fun getBytes(key: String?): ByteArray = DBMultiFactory.getBytes(name, key)

    @Throws(SnappydbException::class)
    override fun <T : Serializable?> get(key: String?, className: Class<T>?): T =
            DBMultiFactory.get(name, key, className)

    @Throws(SnappydbException::class)
    override fun <T : Any?> getObject(key: String?, className: Class<T>?): T =
            DBMultiFactory.getObject(name, key, className)

    @Throws(SnappydbException::class)
    override fun <T : Serializable?> getArray(key: String?, className: Class<T>?): Array<out T> =
            DBMultiFactory.getArray(name, key, className)

    @Throws(SnappydbException::class)
    override fun <T : Any?> getObjectArray(key: String?, className: Class<T>?): Array<out T> =
            DBMultiFactory.getObjectArray(name, key, className)

    @Throws(SnappydbException::class)
    override fun getShort(key: String?): Short = DBMultiFactory.getShort(name, key)

    @Throws(SnappydbException::class)
    override fun getInt(key: String?): Int = DBMultiFactory.getInt(name, key)

    @Throws(SnappydbException::class)
    override fun getBoolean(key: String?): Boolean = DBMultiFactory.getBoolean(name, key)

    @Throws(SnappydbException::class)
    override fun getDouble(key: String?): Double = DBMultiFactory.getDouble(name, key)

    @Throws(SnappydbException::class)
    override fun getLong(key: String?): Long = DBMultiFactory.getLong(name, key)

    @Throws(SnappydbException::class)
    override fun getFloat(key: String?): Float = DBMultiFactory.getFloat(name, key)

    //****************************
    //*      KEYS OPERATIONS
    //****************************
    @Throws(SnappydbException::class)
    override fun exists(key: String?): Boolean = DBMultiFactory.exists(name, key)

    @Throws(SnappydbException::class)
    override fun findKeys(prefix: String?): Array<out String> =
            DBMultiFactory.findKeys(name, prefix)

    @Throws(SnappydbException::class)
    override fun findKeys(prefix: String?, offset: Int): Array<out String> =
            DBMultiFactory.findKeys(name, prefix, offset)

    @Throws(SnappydbException::class)
    override fun findKeys(prefix: String?, offset: Int, limit: Int): Array<out String> =
            DBMultiFactory.findKeys(name, prefix, offset, limit)

    @Throws(SnappydbException::class)
    override fun countKeys(prefix: String?): Int = DBMultiFactory.countKeys(name, prefix)

    @Throws(SnappydbException::class)
    override fun findKeysBetween(startPrefix: String?, endPrefix: String?): Array<out String> =
            DBMultiFactory.findKeysBetween(name, startPrefix, endPrefix)

    @Throws(SnappydbException::class)
    override fun findKeysBetween(startPrefix: String?, endPrefix: String?, offset: Int): Array<out String> =
            DBMultiFactory.findKeysBetween(name, startPrefix, endPrefix, offset)

    @Throws(SnappydbException::class)
    override fun findKeysBetween(startPrefix: String?, endPrefix: String?, offset: Int, limit: Int): Array<out String> =
            DBMultiFactory.findKeysBetween(name, startPrefix, endPrefix, offset, limit)

    @Throws(SnappydbException::class)
    override fun countKeysBetween(startPrefix: String?, endPrefix: String?): Int =
            DBMultiFactory.countKeysBetween(name, startPrefix, endPrefix)

    //***********************
    //*      ITERATORS
    //***********************
    @Throws(SnappydbException::class)
    override fun allKeysIterator(): KeyIterator = DBMultiFactory.allKeysIterator(name)

    @Throws(SnappydbException::class)
    override fun allKeysReverseIterator(): KeyIterator = DBMultiFactory.allKeysReverseIterator(name)

    @Throws(SnappydbException::class)
    override fun findKeysIterator(prefix: String?): KeyIterator =
            DBMultiFactory.findKeysIterator(name, prefix)

    @Throws(SnappydbException::class)
    override fun findKeysReverseIterator(prefix: String?): KeyIterator =
            DBMultiFactory.findKeysReverseIterator(name, prefix)

    @Throws(SnappydbException::class)
    override fun findKeysBetweenIterator(startPrefix: String?, endPrefix: String?): KeyIterator =
            DBMultiFactory.findKeysBetweenIterator(name, startPrefix, endPrefix)

    @Throws(SnappydbException::class)
    override fun findKeysBetweenReverseIterator(startPrefix: String?, endPrefix: String?): KeyIterator =
            DBMultiFactory.findKeysBetweenReverseIterator(name, startPrefix, endPrefix)

    //*********************************
    //*      KRYO SERIALIZATION
    //*********************************

    // Allow the user to access the Kryo instance, for eventual customization
    override fun getKryoInstance(): Kryo = DBMultiFactory.getKryoInstance()
}