package com.example.verstka_last

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity : AppCompatActivity() {

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
        val currentPlayTime: TextView = findViewById(R.id.current_play_time)

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

        currentPlayTime.text = "00:00"
    }
}