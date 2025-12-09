package hr.sil.android.myappbox.fonts

import android.graphics.Typeface
import hr.sil.android.myappbox.App
import java.util.concurrent.ConcurrentHashMap

object AppFonts {

    enum class FontName(val attr: Int, val path: String, val ext: String) {
        UNKNOWN(-1, "", ""),
        METROPOLIS(1,"metropolis", "otf"),
        RAJDHANI(2,"rajdhani", "ttf"),
        BARLOW(3,"barlow", "ttf");

        companion object {
            fun getByAttr(attr: Int?) = values().firstOrNull { it.attr == attr } ?: UNKNOWN
            fun getByPath(path: String?) = values().firstOrNull { it.path == path } ?: UNKNOWN
        }
    }

    enum class FontType(val attr: Int, val path: String) {
        UNKNOWN(-1, ""),
        REGULAR(1,"regular"),
        MEDIUM(2, "medium"),
        BOLD(3, "bold");

        companion object {
            fun getByAttr(attr: Int?) = values().firstOrNull { it.attr == attr } ?: UNKNOWN
            fun getByPath(path: String?) = values().firstOrNull { it.path == path } ?: UNKNOWN
        }
    }

    private val fontCache = ConcurrentHashMap<String, Typeface>()

    fun getFont(name: FontName, type: FontType): Typeface? {
        if (name == FontName.UNKNOWN) return null
        val cleanType = if (type != FontType.UNKNOWN) type else FontType.REGULAR

        val path = "font/${name.path}-${cleanType.path}.${name.ext}"
        return fontCache.getOrPut(path) { Typeface.createFromAsset(App.Companion.ref.assets, path) }
    }

    fun getFontByAttr(attrName: String, attrType: String): Typeface? {
        val attrNameInt = attrName.toIntOrNull()
        val attrTypeInt = attrType.toIntOrNull()

        val name =
            if (attrNameInt != null) FontName.getByAttr(attrNameInt)
            else FontName.getByPath(attrName)

        val type =
            if (attrTypeInt != null) FontType.getByAttr(attrTypeInt)
            else FontType.getByPath(attrType)

        return getFont(name, type)
    }
}