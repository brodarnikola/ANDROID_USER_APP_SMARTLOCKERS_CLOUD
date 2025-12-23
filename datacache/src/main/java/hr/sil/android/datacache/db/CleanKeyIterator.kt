package hr.sil.android.datacache.db

import com.snappydb.KeyIterator

/**
 * @author mfatiga
 */
class CleanKeyIterator internal constructor(private val dbNameKeyPrefix: String, private val impl: KeyIterator) : KeyIterator {
    override fun close() {
        impl.close()
    }

    override fun byBatch(size: Int): MutableIterable<Array<String>> =
            CleanKeyMutableIterable(size, this)

    override fun next(max: Int): Array<String> =
            impl.next(max).map { it.substring(dbNameKeyPrefix.length) }.toTypedArray()

    override fun hasNext(): Boolean = impl.hasNext()

    class CleanKeyMutableIterable(size: Int, parent: CleanKeyIterator) : MutableIterable<Array<String>> {
        private val cleanKeyMutableIterator = CleanKeyMutableIterator(size, parent)
        override fun iterator(): MutableIterator<Array<String>> = cleanKeyMutableIterator
    }

    class CleanKeyMutableIterator(private val size: Int, private val parent: CleanKeyIterator) : MutableIterator<Array<String>> {
        override fun hasNext(): Boolean = parent.hasNext()

        override fun next(): Array<String> = parent.next(size)

        override fun remove() {
            throw UnsupportedOperationException()
        }
    }
}