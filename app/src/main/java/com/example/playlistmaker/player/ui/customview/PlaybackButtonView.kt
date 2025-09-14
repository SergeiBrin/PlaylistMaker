package com.example.playlistmaker.player.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.result.PlayerState

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var imageBitmap: Bitmap? = null
    private val playButtonBitmap: Bitmap?
    private val pauseButtonBitmap: Bitmap?
    private var rectF = RectF()

    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playButtonBitmap = getDrawable(R.styleable.PlaybackButtonView_playButtonIcon)?.toBitmap()
                pauseButtonBitmap = getDrawable(R.styleable.PlaybackButtonView_pauseButtonIcon)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.apply {
            left = 0f
            top = 0f
            right = w.toFloat()
            bottom = h.toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        imageBitmap?.let {
            canvas.drawBitmap(it, null, rectF, null)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    fun updateBitmap(newState: PlayerState) {
        imageBitmap = when (newState) {
            is PlayerState.StateDefault,
            is PlayerState.StatePrepared,
            is PlayerState.StatePaused -> playButtonBitmap
            is PlayerState.StatePlaying -> pauseButtonBitmap
        }
        invalidate()
    }
}