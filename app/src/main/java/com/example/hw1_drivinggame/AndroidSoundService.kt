package com.example.hw1_drivinggame

import android.content.Context
import android.media.MediaPlayer

class AndroidSoundService(private val context: Context) : SoundService {

    private var crashPlayer: MediaPlayer? = null
    private var coinPlayer: MediaPlayer? = null

    override fun playCrash() {
        if (crashPlayer == null) crashPlayer = MediaPlayer.create(context, R.raw.crash)
        crashPlayer?.seekTo(0)
        crashPlayer?.start()
    }

    override fun playCoin() {
        if (coinPlayer == null) coinPlayer = MediaPlayer.create(context, R.raw.coin)
        coinPlayer?.seekTo(0)
        coinPlayer?.start()
    }

    override fun release() {
        crashPlayer?.release()
        coinPlayer?.release()
        crashPlayer = null
        coinPlayer = null
    }
}
