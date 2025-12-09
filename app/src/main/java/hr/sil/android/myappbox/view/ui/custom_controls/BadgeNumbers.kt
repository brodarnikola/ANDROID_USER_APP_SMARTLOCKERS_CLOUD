package hr.sil.android.myappbox.view.ui.custom_controls

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.fonts.AppFonts

class BadgeNumbers : AppCompatTextView {

    private var  strokeWidth: Float = 0.toFloat()
    private var  strokeColor: Int = 0
    internal var solidColor: Int = 0
    private var  strokePaint: Paint? = null
    private var circlePaint: Paint? = null

    init {
        circlePaint = Paint()
        circlePaint?.color = ContextCompat.getColor(context, R.color.transparentColor)
        circlePaint?.flags = Paint.ANTI_ALIAS_FLAG

        strokePaint = Paint()
        strokePaint?.color = ContextCompat.getColor(context, R.color.transparentColor)
        strokePaint?.flags = Paint.ANTI_ALIAS_FLAG
    }

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
        val attr = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithFont, defStyle, 0)
        val attrName = attr.getString(R.styleable.TextViewWithFont_font_name) ?: ""
        val attrType = attr.getString(R.styleable.TextViewWithFont_font_type) ?: ""

        val font = AppFonts.getFontByAttr(attrName, attrType)
        if (font != null) setTypeface(font)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeNumbersDesign)
        circlePaint?.color = typedArray.getColor(R.styleable.BadgeNumbersDesign_circle_fill_color, Color.TRANSPARENT)
        typedArray.recycle()
    }

    fun setSolidColor(color: String) {
        solidColor = Color.parseColor(color)
        circlePaint?.color = solidColor

    }

    fun setStrokeWidth(dp: Int) {
        val scale = context.resources.displayMetrics.density
        strokeWidth = dp * scale

    }

    fun setStrokeColor(color: String) {
        strokeColor = Color.parseColor(color)
        strokePaint?.color = strokeColor
    }

    override fun onDraw(canvas: Canvas) {

        val h = this.height
        val w = this.width

        val diameter = if (h > w) h else w
        val radius = diameter / 2

        this.height = diameter
        this.width = diameter

        canvas?.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius.toFloat(), strokePaint!!)

        canvas?.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius - strokeWidth,
            circlePaint!!)

        super.onDraw(canvas)
    }

}