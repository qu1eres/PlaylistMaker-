package com.example.verstka_last.media.presentation.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.databinding.PlaylistItemBinding
import java.io.File

class PlayListAdapter(private val onClick: (Playlist) -> Unit = {}) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemBinding.inflate(inflater, parent, false)
        return PlaylistViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}

class PlaylistViewHolder(private val binding: PlaylistItemBinding, private val onClick: (Playlist) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        binding.playlistName.text = item.title
        val tracksCountText = itemView.resources.getQuantityString(
            R.plurals.countOfTracks,
            item.trackCount.toInt(),
            item.trackCount.toInt()
        )
        binding.playlistTracksCount.text = tracksCountText

        val coverFile = item.imagePath.takeIf { it.isNotBlank() }?.let { File(it) }
        Glide.with(itemView)
            .load(coverFile)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius))
            )
            .into(binding.playlistImage)

        itemView.setOnClickListener { onClick(item) }
    }
}