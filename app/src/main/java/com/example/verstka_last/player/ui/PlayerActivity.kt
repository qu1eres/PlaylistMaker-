package com.example.verstka_last.player.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.creator.Creator
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.ActivityAudioplayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackJson = intent.getStringExtra(TRACK_KEY)
        val track = Gson().fromJson(trackJson, Track::class.java)

        val factory = Creator.providePlayerViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]

        setupUI(track)

        if (savedInstanceState == null) {
            viewModel.preparePlayer(track.previewUrl)
        }

        binding.play.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.toolBar.setNavigationOnClickListener { finish() }

        viewModel.playerState.observe(this) { state ->
            render(state)
        }

        viewModel.currentTime.observe(this) { time ->
            binding.trackDuration.text = time
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.Default -> {
                binding.play.isEnabled = false
            }
            PlayerState.Prepared -> {
                binding.play.isEnabled = true
                binding.play.setImageResource(R.drawable.ic_button_play)
            }
            PlayerState.Playing -> {
                binding.play.isEnabled = true
                binding.play.setImageResource(R.drawable.ic_active_play_button)
            }
            PlayerState.Paused -> {
                binding.play.isEnabled = true
                binding.play.setImageResource(R.drawable.ic_button_play)
            }
        }
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.title
        binding.artistName.text = track.artist
        binding.trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.duration)
        binding.albumName.text = track.collectionName
        binding.year.text = track.getReleaseYear()
        binding.genreName.text = track.primaryGenreName
        binding.country.text = track.country


        Glide.with(this)
            .load(track.getHighResArtworkUrl())
            .placeholder(R.drawable.ic_album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(binding.trackImage)
    }

    companion object {
        const val TRACK_KEY = "track"
    }
}