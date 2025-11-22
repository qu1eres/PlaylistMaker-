package com.example.verstka_last.search.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.TrackItemBinding
import com.example.verstka_last.R

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
        val binding = TrackItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(tracks[position])
        }
    }

    override fun getItemCount() = tracks.size
}

class TrackViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.trackName.text = track.title.ifEmpty { itemView.context.getString(R.string.unknown_title) }
        binding.artistName.text = track.artist.ifEmpty { itemView.context.getString(R.string.unknown_artist) }
        binding.trackDuration.text = track.duration

        val artworkUrl = track.artworkUrl
        if (artworkUrl.isNullOrEmpty()) {
            binding.trackCover.setImageResource(R.drawable.ic_placeholder)
        } else {
            Glide.with(itemView.context)
                .load(artworkUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .transform(RoundedCorners(8))
                .into(binding.trackCover)
        }
    }
}