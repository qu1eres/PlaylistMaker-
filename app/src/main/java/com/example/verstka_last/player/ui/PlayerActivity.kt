package com.example.verstka_last.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.creator.Creator
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.ActivityAudioplayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        val track = intent.getSerializableExtra(TRACK_KEY) as? Track ?: run {
            finish()
            return
        }

        val factory = Creator.providePlayerViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]

        setupUI(track)

        if (savedInstanceState == null) {
            viewModel.preparePlayer(track.previewUrl)
        }

        binding.play.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.playButtonActive.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.toolBar.setNavigationOnClickListener { finish() }

        viewModel.screenState.observe(this) { state ->
            render(state)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun render(state: PlayerScreenState) {
        when (state.playerState) {
            PlayerState.Default -> {
                binding.play.isVisible = true
                binding.playButtonActive.isVisible = false
                binding.play.isEnabled = true
            }
            PlayerState.Prepared -> {
                binding.play.isVisible = true
                binding.playButtonActive.isVisible = false
                binding.play.isEnabled = true
            }
            PlayerState.Playing -> {
                binding.play.isVisible = false
                binding.playButtonActive.isVisible = true
                binding.playButtonActive.isEnabled = true
            }
            PlayerState.Paused -> {
                binding.play.isVisible = true
                binding.playButtonActive.isVisible = false
                binding.play.isEnabled = true
            }
        }

        binding.currentPlayTime.text = state.currentTime
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.title
        binding.artistName.text = track.artist
        binding.trackDuration.text = track.duration

        if (track.collectionName != null) {
            binding.albumName.text = track.collectionName
            binding.albumName.isVisible = track.collectionName.isNotEmpty()
        } else {
            binding.albumName.isVisible = false
        }

        binding.year.text = track.getReleaseYear() ?: ""
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