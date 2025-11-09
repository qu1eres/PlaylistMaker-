package com.example.verstka_last

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var play: ImageView
    private lateinit var pause: ImageView
    private lateinit var currentPlayTime: TextView

    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val updateTime = Runnable {
        runnable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        // Не понял, почему SDK ругается на вызов getSerializableExtra, ведь Parcelable поддерживается только с API 33+. Я проверяю всё на устройстве с API 31, поэтому решил оставить устаревший метод.
        @Suppress("DEPRECATION")
        val track = intent.getSerializableExtra("track") as? Track
        if (track == null) {
            finish()
            return
        }

        val toolBar: Toolbar = findViewById(R.id.tool_bar)
        val trackImage: ImageView = findViewById(R.id.track_image)
        val trackName: TextView = findViewById(R.id.track_name)
        val artistName: TextView = findViewById(R.id.artist_name)
        val albumText: TextView = findViewById(R.id.album_text)
        val albumName: TextView = findViewById(R.id.album_name)
        val yearText: TextView = findViewById(R.id.year_text)
        val year: TextView = findViewById(R.id.year)
        val genreName: TextView = findViewById(R.id.genre_name)
        val country: TextView = findViewById(R.id.country)
        val trackDuration: TextView = findViewById(R.id.track_duration)

        play = findViewById(R.id.play)
        pause = findViewById(R.id.play_button_active)
        currentPlayTime = findViewById(R.id.current_play_time); currentPlayTime.text = "00:00"

        fun preparePlayer() {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                play.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                handler.removeCallbacks(updateTime); currentPlayTime.text = "00:00"
                play.visibility = View.VISIBLE
                pause.visibility = View.GONE
            }
        }

        preparePlayer()

        toolBar.setNavigationOnClickListener {
            finish()
        }

        trackName.text = track.title
        artistName.text = track.artist

        if (track.collectionName.isNullOrEmpty()) {
            albumText.visibility = View.GONE
            albumName.visibility = View.GONE
        } else {
            albumName.text = track.collectionName
        }

        val releaseYear = track.getReleaseYear()
        if (releaseYear.isNullOrEmpty()) {
            yearText.visibility = View.GONE
            year.visibility = View.GONE
        } else {
            year.text = releaseYear
        }

        genreName.text = track.primaryGenreName

        country.text = track.country

        trackDuration.text = track.duration

        val artworkUrl = track.getHighResArtworkUrl()
        if (artworkUrl != null) {
            Glide.with(this)
                .load(artworkUrl)
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .transform(RoundedCorners(16))
                .into(trackImage)
        } else {
            trackImage.setImageResource(R.drawable.ic_album_placeholder)
        }

        play.setOnClickListener {
            playbackControl()
        }

        pause.setOnClickListener { playbackControl() }

    }

    private fun startPlayer() {
        mediaPlayer.start()
        pause.visibility = View.VISIBLE
        play.visibility = View.GONE
        playerState = STATE_PLAYING
        handler.post(updateTime)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.visibility = View.VISIBLE
        pause.visibility = View.GONE
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTime)

    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun runnable() {
        currentPlayTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.postDelayed(updateTime, 500)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTime)
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTime)
        mediaPlayer.release()
    }


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
}
