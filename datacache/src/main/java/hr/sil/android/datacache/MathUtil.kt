package hr.sil.android.datacache

/**
 * @author mfatiga
 */
object MathUtil {
    tailrec fun greatestCommonDenominator(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return a + b
        return greatestCommonDenominator(b, a % b)
    }
}