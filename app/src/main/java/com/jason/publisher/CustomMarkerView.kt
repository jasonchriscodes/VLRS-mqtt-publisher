package com.jason.publisher

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable

class CustomMarkerView : Drawable() {

    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        textSize = 32f
    }

    private val path: Path = Path()

    private val rect: RectF = RectF(0f, 0f, 100f, 100f)

    private var text: String? = null

    override fun draw(canvas: Canvas) {
        path.reset()
        path.addRoundRect(rect, 10f, 10f, Path.Direction.CW)

        canvas.drawPath(path, paint)

        if (text != null) {
            canvas.drawText(text!!, rect.centerX(), rect.centerY(), paint)
        }
    }

    override fun setAlpha(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun setColorFilter(p0: ColorFilter?) {
        TODO("Not yet implemented")
    }

    override fun getOpacity(): Int {
        TODO("Not yet implemented")
    }

    override fun getIntrinsicWidth(): Int {
        return 100
    }

    override fun getIntrinsicHeight(): Int {
        return 100
    }

    fun setText(text: String) {
        this.text = text
        invalidateSelf()
    }
}
