package com.example.hw1_drivinggame

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class HighScoreStore(private val context: Context) {

    companion object {
        private const val PREFS = "high_scores_prefs"
        private const val KEY_LIST = "high_scores_list"
        private const val MAX = 10
    }

    fun getTop(): List<HighScoreEntry> {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = sp.getString(KEY_LIST, null) ?: return emptyList()
        val arr = JSONArray(raw)

        val list = mutableListOf<HighScoreEntry>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                HighScoreEntry(
                    name = o.getString("name"),
                    score = o.getInt("score"),
                    distance = o.getInt("distance"),
                    lat = o.getDouble("lat"),
                    lng = o.getDouble("lng"),
                    timestamp = o.getLong("timestamp")
                )
            )
        }
        return list
    }

    fun add(entry: HighScoreEntry) {
        val current = getTop().toMutableList()
        current.add(entry)

        val sorted = current.sortedWith(
            compareByDescending<HighScoreEntry> { it.score }
                .thenByDescending { it.distance }
                .thenByDescending { it.timestamp }
        ).take(MAX)

        val arr = JSONArray()
        for (e in sorted) {
            val o = JSONObject()
            o.put("name", e.name)
            o.put("score", e.score)
            o.put("distance", e.distance)
            o.put("lat", e.lat)
            o.put("lng", e.lng)
            o.put("timestamp", e.timestamp)
            arr.put(o)
        }

        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_LIST, arr.toString()).apply()
    }
}
