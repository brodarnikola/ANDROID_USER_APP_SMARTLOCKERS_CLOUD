package hr.sil.android.datacache.util

import android.content.Context
import android.util.Log
import com.snappydb.DB
import hr.sil.android.datacache.CacheDatabase
//import hr.sil.android.util.general.extensions.toHexString
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.security.MessageDigest
import kotlin.reflect.KClass

import hr.sil.android.datacache.toHexString


/**
 * @author mfatiga
 */
object PersistenceClassTracker {
    private const val TAG = "PersistenceClassTracker"

    private fun hashString(text: String, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        val hashedBytes = digest.digest(text.toByteArray(charset("UTF-8")))
        return hashedBytes.toHexString()
    }

    private fun <T : Any> generateSignature(kClass: KClass<T>): String {
        val jClass = kClass.java

        val constructors = jClass.constructors.map(Constructor<*>::toGenericString).sorted()

        val fields = jClass.fields.map(Field::toGenericString)
        val declaredFields = jClass.declaredFields.map(Field::toGenericString)
        val allFields = fields.union(declaredFields).sorted()

        val methods = jClass.methods.map(Method::toGenericString)
        val declaredMethods = jClass.declaredMethods.map(Method::toGenericString)
        val allMethods = methods.union(declaredMethods).sorted()

        val constructorsString = constructors.joinToString("\n")
        val allFieldsString = allFields.joinToString("\n")
        val allMethodsString = allMethods.joinToString("\n")

        val result = "-class:\n${jClass.canonicalName}\n" +
                "-constructors:\n$constructorsString\n" +
                "-fields:\n$allFieldsString\n" +
                "-methods:\n$allMethodsString"

        return hashString(result, "MD5")
    }

    private fun <T : Any> getSignatureKey(kClass: KClass<T>): String =
            "_sig_${kClass.java.canonicalName}"

    private fun removeAllWithKeyPrefix(db: DB, keyPrefix: String) {
        val keysToRemove = db.findKeys(keyPrefix)
        keysToRemove?.forEach { if (it != null) db.del(it) }
    }

    fun <T : Any> checkClass(db: DB, kClass: KClass<T>) {
        synchronized(this) {
            Log.i(TAG, "Checking ${kClass.java.simpleName}")

            val sigKey = getSignatureKey(kClass)

            val signature = generateSignature(kClass)
            val oldSignature = (if (db.exists(sigKey)) db.get(sigKey) else "") ?: ""

            if (oldSignature != signature) {
                Log.i(TAG, "Signature for ${kClass.java.simpleName} changed! Removing invalid cache entries..")

                //remove data
                removeAllWithKeyPrefix(db, CacheKeyConstructor.getDataKeyPrefix(kClass))

                //remove state
                removeAllWithKeyPrefix(db, CacheKeyConstructor.getStateKeyPrefix(kClass))

                //update signature
                db.put(sigKey, signature)
            }
        }
    }

    fun <T : Any> checkClass(context: Context, kClass: KClass<T>) {
        checkClass(CacheDatabase.getCacheDatabase(context), kClass)
    }

    //TODO: find a way to remove cache sig, data, and state entries that are not used anymore
}