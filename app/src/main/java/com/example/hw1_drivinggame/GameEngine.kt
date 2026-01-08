package com.example.hw1_drivinggame

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameEngine(
    private val vibrationService: VibrationService,
    private val soundService: SoundService
) {

    enum class ControlMode { BUTTONS, SENSORS }
    enum class SpeedMode { SLOW, FAST }
    enum class CollisionType { ROCK, COIN }

    data class Obstacle(var lane: Int, var y: Float)
    data class Coin(var lane: Int, var y: Float)

    data class State(
        val lanes: Int,
        val carLane: Int,
        val lives: Int,
        val score: Int,
        val distance: Int,
        val speed: Float,
        val obstacles: List<Obstacle>,
        val coins: List<Coin>,
        val isRunning: Boolean
    )

    interface Listener {
        fun onStateUpdated(state: State)
        fun onCollision(type: CollisionType, state: State)
        fun onGameOver(state: State)
    }

    private var listener: Listener? = null

    private val lanes = 5
    private var carLane = 2

    private var lives = 3
    private var score = 0
    private var distance = 0

    private var speed = 5f
    private var isRunning = false

    private val obstacles = mutableListOf<Obstacle>()
    private val coins = mutableListOf<Coin>()

    private var speedMode = SpeedMode.SLOW

    fun setListener(l: Listener) { listener = l }
    fun setSpeedMode(mode: SpeedMode) { speedMode = mode }

    fun start() {
        carLane = 2
        lives = 3
        score = 0
        distance = 0
        speed = 5f
        obstacles.clear()
        coins.clear()
        isRunning = true
        notifyState()
    }

    fun stop() { isRunning = false }

    fun moveLeft() {
        if (!isRunning) return
        carLane = max(0, carLane - 1)
        notifyState()
    }

    fun moveRight() {
        if (!isRunning) return
        carLane = min(lanes - 1, carLane + 1)
        notifyState()
    }

    fun getScore(): Int = score
    fun getDistance(): Int = distance

    fun currentState(): State {
        return State(
            lanes = lanes,
            carLane = carLane,
            lives = lives,
            score = score,
            distance = distance,
            speed = speed,
            obstacles = obstacles.map { it.copy() },
            coins = coins.map { it.copy() },
            isRunning = isRunning
        )
    }

    fun update(dtSeconds: Float, gameHeight: Float) {
        if (!isRunning) return

        val dy = speed * (dtSeconds * 60f)

        val obstacleIter = obstacles.iterator()
        while (obstacleIter.hasNext()) {
            val o = obstacleIter.next()
            o.y += dy

            if (isCarHit(o.y, gameHeight) && o.lane == carLane) {
                obstacleIter.remove()
                handleRockCollision()
                if (!isRunning) return
            }

            if (o.y > gameHeight + 100f) obstacleIter.remove()
        }

        val coinIter = coins.iterator()
        while (coinIter.hasNext()) {
            val c = coinIter.next()
            c.y += dy

            if (isCarHit(c.y, gameHeight) && c.lane == carLane) {
                coinIter.remove()
                handleCoinCollision()
            } else if (c.y > gameHeight + 100f) {
                coinIter.remove()
            }
        }

        if (Random.nextFloat() < 0.02f) obstacles.add(Obstacle(Random.nextInt(lanes), -100f))
        if (Random.nextFloat() < 0.015f) coins.add(Coin(Random.nextInt(lanes), -100f))

        speed = min(speed + 0.001f * (dtSeconds * 60f), 12f)

        distance += if (speedMode == SpeedMode.FAST) 2 else 1

        notifyState()
    }

    private fun isCarHit(objY: Float, gameHeight: Float): Boolean {
        val carTop = gameHeight - 150f
        val carBottom = carTop + 80f
        val objTop = objY
        val objBottom = objY + 80f
        return objBottom >= carTop && objTop <= carBottom
    }

    private fun handleRockCollision() {
        vibrationService.vibrate(200)
        soundService.playCrash()

        lives -= 1
        val state = currentState()
        listener?.onCollision(CollisionType.ROCK, state)

        if (lives <= 0) {
            isRunning = false
            listener?.onGameOver(currentState())
        } else {
            notifyState()
        }
    }

    private fun handleCoinCollision() {
        soundService.playCoin()
        score += 10
        val state = currentState()
        listener?.onCollision(CollisionType.COIN, state)
        notifyState()
    }

    private fun notifyState() {
        listener?.onStateUpdated(currentState())
    }
}
