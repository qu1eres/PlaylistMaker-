package com.example.verstka_last.search.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Track

class TrackAdapter(
    private var tracks: List<Track> = emptyList(),
    private var onItemClickListener: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    fun setOnItemClickListener(listener: (Track) -> Unit) {
        onItemClickListener = listener
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(tracks[position])
        }
    }

    override fun getItemCount() = tracks.size
}

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackDuration: TextView = itemView.findViewById(R.id.track_duration)
    private val trackCover: ImageView = itemView.findViewById(R.id.track_cover)


    fun bind(track: Track) {
        trackName.text = track.title.ifEmpty { itemView.context.getString(R.string.unknown_title) }
        artistName.text = track.artist.ifEmpty { itemView.context.getString(R.string.unknown_artist) }
        trackDuration.text = track.duration

        val artworkUrl = track.artworkUrl
        if (artworkUrl.isNullOrEmpty()) {
            trackCover.setImageResource(R.drawable.ic_placeholder)
        } else {
            Glide.with(itemView.context)
                .load(artworkUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .transform(RoundedCorners(8))
                .into(trackCover)
        }
    }
}