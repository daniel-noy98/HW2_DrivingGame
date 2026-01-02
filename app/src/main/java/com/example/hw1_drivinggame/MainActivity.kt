package com.example.hw1_drivinggame

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameView: GameView
    private lateinit var livesContainer: LinearLayout
    private lateinit var tvScore: TextView
    private lateinit var tvDistance: TextView

    private lateinit var btnPauseMenu: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button

    private lateinit var sensorManager: SensorManager
    private var accelSensor: Sensor? = null

    private var controlMode = "BUTTONS"
    private var speedMode = "SLOW"

    private var lastMoveTime = 0L
    private val moveCooldownMs = 180L
    private val tiltThreshold = 2.2f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        applyStatusBarTopPadding()

        gameView = findViewById(R.id.gameView)
        livesContainer = findViewById(R.id.livesContainer)
        tvScore = findViewById(R.id.tvScore)
        tvDistance = findViewById(R.id.tvDistance)

        btnPauseMenu = findViewById(R.id.btnPauseMenu)
        leftButton = findViewById(R.id.leftButton)
        rightButton = findViewById(R.id.rightButton)

        val sp = getSharedPreferences(MenuActivity.PREFS, Context.MODE_PRIVATE)
        controlMode = sp.getString(MenuActivity.KEY_CONTROL_MODE, "BUTTONS") ?: "BUTTONS"
        speedMode = sp.getString(MenuActivity.KEY_SPEED_MODE, "SLOW") ?: "SLOW"

        gameView.setSpeedMode(speedMode)

        if (controlMode == "SENSORS") {
            leftButton.visibility = View.GONE
            rightButton.visibility = View.GONE
        } else {
            leftButton.visibility = View.VISIBLE
            rightButton.visibility = View.VISIBLE
            leftButton.setOnClickListener { gameView.moveLeft() }
            rightButton.setOnClickListener { gameView.moveRight() }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        gameView.setLivesUpdateCallback { lives ->
            runOnUiThread { updateLives(lives) }
        }

        gameView.setScoreUpdateCallback { score ->
            runOnUiThread { tvScore.text = "Score: $score" }
        }

        gameView.setDistanceUpdateCallback { distance ->
            runOnUiThread { tvDistance.text = "Distance: $distance" }
        }

        gameView.setGameOverCallback {
            runOnUiThread { showSaveScoreDialog() }
        }

        btnPauseMenu.setOnClickListener {
            gameView.pauseGame()
            AlertDialog.Builder(this)
                .setTitle("Return to Menu")
                .setMessage("Stop the game and return to the menu?")
                .setPositiveButton("Yes") { _, _ -> goToMenu() }
                .setNegativeButton("No") { _, _ -> gameView.resumeGame() }
                .setOnCancelListener { gameView.resumeGame() }
                .show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToMenu()
            }
        })

        updateLives(3)
        tvScore.text = "Score: 0"
        tvDistance.text = "Distance: 0"

        gameView.startGame()
    }

    override fun onResume() {
        super.onResume()
        if (controlMode == "SENSORS") {
            accelSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        gameView.pauseGame()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (controlMode != "SENSORS") return

        val x = event.values[0]
        val now = System.currentTimeMillis()
        if (now - lastMoveTime < moveCooldownMs) return

        if (x > tiltThreshold) {
            gameView.moveLeft()
            lastMoveTime = now
        } else if (x < -tiltThreshold) {
            gameView.moveRight()
            lastMoveTime = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun goToMenu() {
        gameView.stopGame()
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
        val finalScore = gameView.getScore()
        val finalDistance = gameView.getDistance()

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

    private fun applyStatusBarTopPadding() {
        val root = findViewById<View>(android.R.id.content)
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height = if (resId > 0) resources.getDimensionPixelSize(resId) else 0
        root.setPadding(root.paddingLeft, height, root.paddingRight, root.paddingBottom)
    }
}
