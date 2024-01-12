package com.example.exoplayerapp

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.exoplayerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private var player: Player? = null

    lateinit var editText: EditText
    lateinit var button: Button

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        editText = viewBinding.editText
        button = viewBinding.button
        button.setOnClickListener {
            initializePlayer(editText.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer(url: String = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3") {
        player = ExoPlayer.Builder(this)
            .build()
            .also {
                viewBinding.videoView.player = it
                val mediaItem = MediaItem.fromUri(url)
                it.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                it.prepare()
            }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}