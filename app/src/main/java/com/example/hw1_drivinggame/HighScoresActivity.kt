package com.example.hw1_drivinggame

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HighScoresActivity : AppCompatActivity(),
    HighScoresListFragment.OnScoreClickListener {

    private lateinit var mapFragment: HighScoresMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        applyStatusBarTopPadding()

        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            finish()
        }

        val listFragment = HighScoresListFragment()
        mapFragment = HighScoresMapFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.listContainer, listFragment)
            .replace(R.id.mapContainer, mapFragment)
            .commit()
    }

    override fun onScoreClicked(entry: HighScoreEntry) {
        mapFragment.focusOn(entry)
    }

    private fun applyStatusBarTopPadding() {
        val root = findViewById<View>(android.R.id.content)
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight =
            if (resId > 0) resources.getDimensionPixelSize(resId) else 0

        root.setPadding(
            root.paddingLeft,
            statusBarHeight,
            root.paddingRight,
            root.paddingBottom
        )
    }
}
