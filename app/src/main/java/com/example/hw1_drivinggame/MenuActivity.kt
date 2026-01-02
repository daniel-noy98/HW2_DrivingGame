package com.example.hw1_drivinggame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    companion object {
        const val PREFS = "hw1_prefs"
        const val KEY_CONTROL_MODE = "control_mode"
        const val KEY_SPEED_MODE = "speed_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        applyStatusBarTopPadding()

        val rbButtons = findViewById<RadioButton>(R.id.rbButtons)
        val rbSensors = findViewById<RadioButton>(R.id.rbSensors)
        val rbSlow = findViewById<RadioButton>(R.id.rbSlow)
        val rbFast = findViewById<RadioButton>(R.id.rbFast)

        val sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val controlMode = sp.getString(KEY_CONTROL_MODE, "BUTTONS") ?: "BUTTONS"
        val speedMode = sp.getString(KEY_SPEED_MODE, "SLOW") ?: "SLOW"

        if (controlMode == "SENSORS") rbSensors.isChecked = true else rbButtons.isChecked = true
        if (speedMode == "FAST") rbFast.isChecked = true else rbSlow.isChecked = true

        rbButtons.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sp.edit().putString(KEY_CONTROL_MODE, "BUTTONS").apply()
        }
        rbSensors.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sp.edit().putString(KEY_CONTROL_MODE, "SENSORS").apply()
        }
        rbSlow.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sp.edit().putString(KEY_SPEED_MODE, "SLOW").apply()
        }
        rbFast.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sp.edit().putString(KEY_SPEED_MODE, "FAST").apply()
        }

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.btnHighScores).setOnClickListener {
            startActivity(Intent(this, HighScoresActivity::class.java))
        }
    }

    private fun applyStatusBarTopPadding() {
        val root = findViewById<View>(android.R.id.content)
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if (resId > 0) resources.getDimensionPixelSize(resId) else 0
        root.setPadding(root.paddingLeft, statusBarHeight, root.paddingRight, root.paddingBottom)
    }
}
