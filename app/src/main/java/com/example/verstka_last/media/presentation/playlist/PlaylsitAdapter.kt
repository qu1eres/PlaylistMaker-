package com.example.verstka_last.media.presentation.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.databinding.PlaylistItemBinding
import java.io.File

class PlaylistViewHolder(private val binding: PlaylistItemBinding, private val filePath: File, private val onClick: (Playlist) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Playlist) {

        binding.playlistName.text = item.title
        val addText = itemView.resources.getQuantityString(
            R.plurals.countOfTracks,
            item.trackCount.toInt(),
            item.trackCount.toInt()
        )
        binding.playlistTracksCount.text = addText

        val file = File(filePath, "${item.id}.jpg")
        Glide.with(itemView)
            .load(file.toUri().toString())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)))
            .into(binding.playlistImage)
        itemView.setOnClickListener {
            onClick(item)
        }
    }
}

class PlayListAdapter(
    private val filePath: File,
    private val onClick: (Playlist) -> Unit = {}
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        val binding = PlaylistItemBinding.inflate(layoutInspector, parent, false)
        return PlaylistViewHolder(binding, filePath, onClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}