package com.example.hw1_drivinggame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

class HighScoresMapFragment : Fragment() {

    private var pendingFocus: HighScoreEntry? = null

    private lateinit var root: FrameLayout
    private lateinit var marker: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_high_scores_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        root = view.findViewById(R.id.mapRoot)
        marker = view.findViewById(R.id.markerDot)

        root.post {
            if (pendingFocus != null) {
                focusOn(pendingFocus!!)
                pendingFocus = null
            } else {
                showCenter()
            }
        }
    }

    fun focusOn(entry: HighScoreEntry) {
        if (!this::root.isInitialized) {
            pendingFocus = entry
            return
        }

        val (nx, ny) = normalize(entry.lat, entry.lng)

        val w = root.width.toFloat()
        val h = root.height.toFloat()

        val x = nx * w
        val y = ny * h

        marker.visibility = View.VISIBLE
        marker.x = x - marker.width / 2f
        marker.y = y - marker.height / 2f
    }

    private fun showCenter() {
        val w = root.width.toFloat()
        val h = root.height.toFloat()
        marker.visibility = View.VISIBLE
        marker.x = (w / 2f) - marker.width / 2f
        marker.y = (h / 2f) - marker.height / 2f
    }

    private fun normalize(lat: Double, lng: Double): Pair<Float, Float> {
        if (lat in 0.0..1.0 && lng in 0.0..1.0) {
            return Pair(lat.toFloat(), lng.toFloat())
        }
        val nx = ((lng + 180.0) / 360.0).coerceIn(0.0, 1.0)
        val ny = (1.0 - ((lat + 90.0) / 180.0)).coerceIn(0.0, 1.0)
        return Pair(nx.toFloat(), ny.toFloat())
    }
}
