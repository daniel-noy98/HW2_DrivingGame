package com.example.hw1_drivinggame

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameView: GameView
    private lateinit var livesContainer: LinearLayout
    private lateinit var tvScore: TextView
    private lateinit var tvDistance: TextView

    private lateinit var btnPauseMenu: Button
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button

    private lateinit var sensorManager: SensorManager
    private var accelSensor: Sensor? = null

    private var controlMode = "BUTTONS"
    private var speedMode = "SLOW"

    private var lastMoveTime = 0L
    private val moveCooldownMs = 180L
    private val tiltThreshold = 2.2f

    private val handler = Handler(Looper.getMainLooper())
    private var lastFrameTimeMs = 0L

    private lateinit var engine: GameEngine
    private lateinit var soundService: SoundService
    private lateinit var vibrationService: VibrationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)
        livesContainer = findViewById(R.id.livesContainer)
        tvScore = findViewById(R.id.tvScore)
        tvDistance = findViewById(R.id.tvDistance)

        btnPauseMenu = findViewById(R.id.btnPauseMenu)
        btnLeft = findViewById(R.id.leftButton)
        btnRight = findViewById(R.id.rightButton)

        val sp = getSharedPreferences(MenuActivity.PREFS, MODE_PRIVATE)
        controlMode = sp.getString(MenuActivity.KEY_CONTROL_MODE, "BUTTONS") ?: "BUTTONS"
        speedMode = sp.getString(MenuActivity.KEY_SPEED_MODE, "SLOW") ?: "SLOW"

        vibrationService = AndroidVibrationService(this)
        soundService = AndroidSoundService(this)
        engine = GameEngine(vibrationService, soundService)

        engine.setSpeedMode(
            if (speedMode == "FAST") GameEngine.SpeedMode.FAST else GameEngine.SpeedMode.SLOW
        )

        engine.setListener(object : GameEngine.Listener {
            override fun onStateUpdated(state: GameEngine.State) {
                runOnUiThread {
                    gameView.setState(state)
                    updateLives(state.lives)
                    tvScore.text = "Score: ${state.score}"
                    tvDistance.text = "Distance: ${state.distance}"
                }
            }

            override fun onCollision(type: GameEngine.CollisionType, state: GameEngine.State) {
                runOnUiThread {
                    when (type) {
                        GameEngine.CollisionType.ROCK ->
                            Toast.makeText(this@MainActivity, "Crash!", Toast.LENGTH_SHORT).show()
                        GameEngine.CollisionType.COIN ->
                            Toast.makeText(this@MainActivity, "+10 Coin", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onGameOver(state: GameEngine.State) {
                runOnUiThread { showSaveScoreDialog() }
            }
        })

        if (controlMode == "SENSORS") {
            btnLeft.visibility = View.GONE
            btnRight.visibility = View.GONE
        } else {
            btnLeft.visibility = View.VISIBLE
            btnRight.visibility = View.VISIBLE
            btnLeft.setOnClickListener { engine.moveLeft() }
            btnRight.setOnClickListener { engine.moveRight() }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        btnPauseMenu.setOnClickListener {
            pauseLoop()
            AlertDialog.Builder(this)
                .setTitle("Return to Menu")
                .setMessage("Stop the game and return to the menu?")
                .setPositiveButton("Yes") { _, _ -> goToMenu() }
                .setNegativeButton("No") { _, _ -> resumeLoop() }
                .setOnCancelListener { resumeLoop() }
                .show()
        }

        updateLives(3)
        tvScore.text = "Score: 0"
        tvDistance.text = "Distance: 0"

        engine.start()
        startLoop()
    }

    override fun onResume() {
        super.onResume()
        if (controlMode == "SENSORS") {
            accelSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
        resumeLoop()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        pauseLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundService.release()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (controlMode != "SENSORS") return

        val x = event.values[0]
        val now = System.currentTimeMillis()
        if (now - lastMoveTime < moveCooldownMs) return

        if (x > tiltThreshold) {
            engine.moveLeft()
            lastMoveTime = now
        } else if (x < -tiltThreshold) {
            engine.moveRight()
            lastMoveTime = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startLoop() {
        lastFrameTimeMs = System.currentTimeMillis()
        handler.post(frameRunnable)
    }

    private fun resumeLoop() {
        lastFrameTimeMs = System.currentTimeMillis()
        handler.removeCallbacks(frameRunnable)
        handler.post(frameRunnable)
    }

    private fun pauseLoop() {
        handler.removeCallbacks(frameRunnable)
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis()
            val dt = (now - lastFrameTimeMs).coerceAtMost(50L) / 1000f
            lastFrameTimeMs = now

            engine.update(dtSeconds = dt, gameHeight = gameView.height.toFloat())

            val delayMs = if (speedMode == "FAST") 20L else 35L
            handler.postDelayed(this, delayMs)
        }
    }

    private fun goToMenu() {
        engine.stop()
        startActivity(
            Intent(this, MenuActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }

    private fun updateLives(lives: Int) {
        livesContainer.removeAllViews()
        repeat(lives) {
            val heart = View(this)
            val params = LinearLayout.LayoutParams(60, 60)
            params.marginEnd = 8
            heart.layoutParams = params
            heart.setBackgroundResource(R.drawable.heart)
            livesContainer.addView(heart)
        }
    }

    private fun showSaveScoreDialog() {
        val finalScore = engine.getScore()
        val finalDistance = engine.getDistance()

        val input = android.widget.EditText(this)
        input.hint = "Your name"

        AlertDialog.Builder(this)
            .setTitle("Save High Score")
            .setMessage("Score: $finalScore | Distance: $finalDistance")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().ifBlank { "Player" }
                val store = HighScoreStore(this)

                store.add(
                    HighScoreEntry(
                        name = name,
                        score = finalScore,
                        distance = finalDistance,
                        lat = Random.nextDouble(0.05, 0.95),
                        lng = Random.nextDouble(0.05, 0.95),
                        timestamp = System.currentTimeMillis()
                    )
                )
                goToMenu()
            }
            .show()
    }
}
