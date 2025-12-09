package hr.sil.android.myappbox.fonts

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import hr.sil.android.myappbox.R

internal class EdittextWithFont : EditText {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val attr = context.obtainStyledAttributes(attrs, R.styleable.EdittextWithFont, defStyle, 0)
        val attrName = attr.getString(R.styleable.EdittextWithFont_font_name) ?: ""
        val attrType = attr.getString(R.styleable.EdittextWithFont_font_type) ?: ""
        attr.recycle()

        val font = AppFonts.getFontByAttr(attrName, attrType)
        if (font != null) setTypeface(font)
    }
}


