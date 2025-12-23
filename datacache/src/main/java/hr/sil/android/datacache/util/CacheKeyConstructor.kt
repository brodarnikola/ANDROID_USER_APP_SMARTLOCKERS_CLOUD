package hr.sil.android.datacache.util

import kotlin.reflect.KClass

/**
 * @author mfatiga
 */
internal object CacheKeyConstructor {
    fun <T : Any> getDataKeyPrefix(kClass: KClass<T>): String =
            "_data_${kClass.java.canonicalName}"

    fun <T : Any> getDataKey(kClass: KClass<T>, paramsKey: String): String =
            "${getDataKeyPrefix(kClass)}${if (paramsKey.isNotBlank()) "_$paramsKey" else ""}"

    fun <T : Any> getStateKeyPrefix(kClass: KClass<T>): String =
            "_state_${kClass.java.canonicalName}"

    fun <T : Any> getStateKey(kClass: KClass<T>, paramsKey: String): String =
            "${getStateKeyPrefix(kClass)}${if (paramsKey.isNotBlank()) "_$paramsKey" else ""}"
}