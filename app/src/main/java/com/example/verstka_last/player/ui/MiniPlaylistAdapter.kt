package com.example.verstka_last.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.databinding.PlaylistItemMiniBinding
import java.io.File

class MiniPlayListAdapter(
    private val clickListener: (Playlist) -> Unit
) : RecyclerView.Adapter<MiniPlayListViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniPlayListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemMiniBinding.inflate(inflater, parent, false)
        return MiniPlayListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MiniPlayListViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.invoke(playlists[position]) }
    }

    override fun getItemCount(): Int = playlists.size
}

class MiniPlayListViewHolder(
    private val binding: PlaylistItemMiniBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        binding.playlistName.text = item.title

        val tracksCountText = itemView.resources.getQuantityString(
            R.plurals.countOfTracks,
            item.trackCount.toInt(),
            item.trackCount
        )
        binding.tracksCount.text = tracksCountText

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
    }
}