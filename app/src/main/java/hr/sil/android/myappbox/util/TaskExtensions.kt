package hr.sil.android.myappbox.util

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.awaitForResult(): Task<T> {
    return suspendCoroutine<Task<T>> { c ->
        this@awaitForResult.addOnCompleteListener { c.resume(it) }
        this.addOnFailureListener { c.resumeWithException(it) }
    }
}