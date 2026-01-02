package com.example.hw1_drivinggame

data class HighScoreEntry(
    val name: String,
    val score: Int,
    val distance: Int,
    val lat: Double,
    val lng: Double,
    val timestamp: Long
)
