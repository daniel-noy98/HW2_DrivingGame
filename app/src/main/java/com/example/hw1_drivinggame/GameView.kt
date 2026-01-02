package com.example.hw1_drivinggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()

    private val lanes = 5
    private var carPosition = 2

    private var lives = 3
    private var score = 0
    private var distance = 0

    private var speed = 5f
    private var isGameRunning = false
    private var isPaused = false

    private val obstacles = mutableListOf<Obstacle>()
    private val coins = mutableListOf<Coin>()

    private var laneWidth = 0f
    private var gameWidth = 0f
    private var gameHeight = 0f

    private val handler = Handler(Looper.getMainLooper())
    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private var gameOverCallback: (() -> Unit)? = null
    private var livesUpdateCallback: ((Int) -> Unit)? = null
    private var scoreUpdateCallback: ((Int) -> Unit)? = null
    private var distanceUpdateCallback: ((Int) -> Unit)? = null

    private var speedMode = "SLOW"

    // -------- SOUNDS --------
    private val soundPool: SoundPool
    private val crashSoundId: Int
    private val coinSoundId: Int

    data class Obstacle(var lane: Int, var y: Float, var collided: Boolean = false)
    data class Coin(var lane: Int, var y: Float, var collected: Boolean = false)

    init {
        paint.isAntiAlias = true

        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttrs)
            .build()

        crashSoundId = soundPool.load(context, R.raw.crash, 1)
        coinSoundId = soundPool.load(context, R.raw.coin, 1)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        gameWidth = w.toFloat()
        gameHeight = h.toFloat()
        laneWidth = gameWidth / lanes
    }

    fun setSpeedMode(mode: String) {
        speedMode = mode
    }

    fun setGameOverCallback(callback: () -> Unit) {
        gameOverCallback = callback
    }

    fun setLivesUpdateCallback(callback: (Int) -> Unit) {
        livesUpdateCallback = callback
    }

    fun setScoreUpdateCallback(callback: (Int) -> Unit) {
        scoreUpdateCallback = callback
    }

    fun setDistanceUpdateCallback(callback: (Int) -> Unit) {
        distanceUpdateCallback = callback
    }

    fun startGame() {
        carPosition = 2
        lives = 3
        score = 0
        distance = 0
        speed = 5f
        obstacles.clear()
        coins.clear()
        isGameRunning = true
        isPaused = false

        livesUpdateCallback?.invoke(lives)
        scoreUpdateCallback?.invoke(score)
        distanceUpdateCallback?.invoke(distance)

        gameLoop()
    }

    fun pauseGame() {
        isPaused = true
    }

    fun resumeGame() {
        if (isGameRunning) {
            isPaused = false
            gameLoop()
        }
    }

    fun stopGame() {
        isGameRunning = false
    }

    fun moveLeft() {
        if (isGameRunning && !isPaused && carPosition > 0) carPosition--
    }

    fun moveRight() {
        if (isGameRunning && !isPaused && carPosition < lanes - 1) carPosition++
    }

    fun getScore() = score
    fun getDistance() = distance

    private fun gameLoop() {
        if (!isGameRunning || isPaused) return

        obstacles.iterator().apply {
            while (hasNext()) {
                val o = next()
                o.y += speed
                if (!o.collided &&
                    o.y > gameHeight - 200 &&
                    o.y < gameHeight - 100 &&
                    o.lane == carPosition
                ) {
                    o.collided = true
                    handleCrash()
                }
                if (o.y > gameHeight + 50) remove()
            }
        }

        coins.iterator().apply {
            while (hasNext()) {
                val c = next()
                c.y += speed
                if (!c.collected &&
                    c.y > gameHeight - 200 &&
                    c.y < gameHeight - 100 &&
                    c.lane == carPosition
                ) {
                    c.collected = true
                    score += 10
                    soundPool.play(coinSoundId, 1f, 1f, 1, 0, 1f)
                    scoreUpdateCallback?.invoke(score)
                    remove()
                }
                if (c.y > gameHeight + 50) remove()
            }
        }

        if (Random.nextFloat() < 0.02f)
            obstacles.add(Obstacle(Random.nextInt(lanes), -50f))

        if (Random.nextFloat() < 0.015f)
            coins.add(Coin(Random.nextInt(lanes), -50f))

        speed = minOf(speed + 0.001f, 12f)
        distance += if (speedMode == "FAST") 2 else 1
        distanceUpdateCallback?.invoke(distance)

        invalidate()
        handler.postDelayed({ gameLoop() }, if (speedMode == "FAST") 20 else 35)
    }

    private fun handleCrash() {
        soundPool.play(crashSoundId, 1f, 1f, 1, 0, 1f)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }

        lives--
        livesUpdateCallback?.invoke(lives)

        if (lives <= 0) {
            isGameRunning = false
            gameOverCallback?.invoke()
        }
    }

    override fun onDraw(canvas: Canvas) {
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
        paint.color = Color.DKGRAY
        obstacles.forEach {
            val s = laneWidth * 0.55f
            val l = it.lane * laneWidth + (laneWidth - s) / 2
            canvas.drawRoundRect(
                RectF(l, it.y, l + s, it.y + s),
                8f, 8f, paint
            )
        }

        val coinSize = laneWidth * 0.35f
        paint.color = Color.parseColor("#FACC15")
        coins.forEach { coin ->
            val cx = coin.lane * laneWidth + laneWidth / 2f
            val cy = coin.y + coinSize / 2f
            canvas.drawCircle(cx, cy, coinSize / 2f, paint)
        }


        paint.color = Color.RED
        val carW = laneWidth * 0.65f
        val carL = carPosition * laneWidth + (laneWidth - carW) / 2
        canvas.drawRoundRect(
            RectF(carL, gameHeight - 150, carL + carW, gameHeight - 70),
            12f, 12f, paint
        )
    }
}
