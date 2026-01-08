package com.example.hw1_drivinggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var state: GameEngine.State? = null

    fun setState(newState: GameEngine.State) {
        state = newState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val s = state ?: return
        val gameWidth = width.toFloat()
        val gameHeight = height.toFloat()
        if (gameWidth <= 0f || gameHeight <= 0f) return

        val lanes = s.lanes
        val laneWidth = gameWidth / lanes

        paint.color = Color.parseColor("#374151")
        canvas.drawRect(0f, 0f, gameWidth, gameHeight, paint)

        paint.color = Color.WHITE
        paint.alpha = 80
        paint.strokeWidth = 4f
        for (i in 1 until lanes) {
            val x = i * laneWidth
            var y = 0f
            while (y < gameHeight) {
                canvas.drawLine(x, y, x, y + 40, paint)
                y += 80
            }
        }
        paint.alpha = 255

        val obstacleSize = laneWidth * 0.55f
        paint.color = Color.parseColor("#4B5563")
        for (o in s.obstacles) {
            val left = o.lane * laneWidth + (laneWidth - obstacleSize) / 2f
            canvas.drawRoundRect(
                RectF(left, o.y, left + obstacleSize, o.y + obstacleSize),
                8f, 8f, paint
            )
        }

        val coinSize = laneWidth * 0.45f
        paint.color = Color.parseColor("#FACC15")
        for (c in s.coins) {
            val cx = c.lane * laneWidth + laneWidth / 2f
            val cy = c.y + coinSize / 2f
            canvas.drawCircle(cx, cy, coinSize / 2f, paint)
        }

        val carW = laneWidth * 0.65f
        val carH = 80f
        paint.color = Color.parseColor("#EF4444")
        val carLeft = s.carLane * laneWidth + (laneWidth - carW) / 2f
        val carTop = gameHeight - 150f
        canvas.drawRoundRect(
            RectF(carLeft, carTop, carLeft + carW, carTop + carH),
            12f, 12f, paint
        )
    }
}
