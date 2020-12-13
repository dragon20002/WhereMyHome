package com.minuminu.haruu.wheremyhome.view.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.minuminu.haruu.wheremyhome.R

class EditTextWithButton : AppCompatEditText, View.OnTouchListener {
    private var drwMinus: Drawable? = null
    private var drwPlus: Drawable? = null

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init() }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        drwMinus = ContextCompat.getDrawable(context, R.drawable.ic_minus)?.let {
            DrawableCompat.wrap(it).apply {
                DrawableCompat.setTintList(this, hintTextColors)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
        }
        drwPlus = ContextCompat.getDrawable(context, R.drawable.ic_add)?.let {
            DrawableCompat.wrap(it).apply {
                DrawableCompat.setTintList(this, hintTextColors)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
        }

        setCompoundDrawables(drwMinus, null, drwPlus, null)

        super.setOnTouchListener(this)
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        event?.let {
            drwMinus?.let { drawable ->
                if ((it.x > paddingStart) and (it.x < paddingStart + drawable.intrinsicWidth)) {
                    when (it.action) {
                        MotionEvent.ACTION_UP -> {
                            val num = text.toString().toIntOrNull() ?: 0
                            setText((num - 1).toString())
                            view?.let {
                                it.requestFocus()
                                if (it.isSoundEffectsEnabled)
                                    it.playSoundEffect(SoundEffectConstants.CLICK)
                            }
                        }
                    }

                    return true
                }
            }
            drwPlus?.let { drawable ->
                if ((it.x > width - paddingEnd - drawable.intrinsicWidth) and (it.x < width - paddingEnd)) {
                    when (it.action) {
                        MotionEvent.ACTION_UP -> {
                            val num = text.toString().toIntOrNull() ?: 0
                            setText((num + 1).toString())
                            view?.let {
                                it.requestFocus()
                                if (it.isSoundEffectsEnabled)
                                    it.playSoundEffect(SoundEffectConstants.CLICK)
                            }
                        }
                    }

                    return true
                }
            }
        }

        return false
    }
}