package com.example.verstka_last.player.ui

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.databinding.PlaylistItemMiniBinding
import java.io.File
import com.example.verstka_last.R

class MiniPlayListAdapter(
    private val clickListener: (Playlist) -> Unit
) : RecyclerView.Adapter<MiniPlayListViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniPlayListViewHolder {
        val filePath = File(
            parent.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )
        val layoutInspector = LayoutInflater.from(parent.context)
        return MiniPlayListViewHolder(
            PlaylistItemMiniBinding.inflate(layoutInspector, parent, false),
            filePath
        )
    }

    override fun onBindViewHolder(holder: MiniPlayListViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            clickListener.invoke(playlists[position])
        }
    }

    override fun getItemCount(): Int = playlists.size
}

class MiniPlayListViewHolder(
    private val binding: PlaylistItemMiniBinding,
    private val filePath: File
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        binding.playlistName.text = item.title
        val addedText = itemView.resources.getQuantityString(
            R.plurals.countOfTracks,
            item.trackCount.toInt(),
            item.trackCount.toInt()
        )
        binding.tracksCount.text = addedText

        val file = File(filePath, "${item.id}.jpg")
        if (file.exists()) {
            Glide.with(itemView)
                .load(file.toUri().toString())
                .placeholder(R.drawable.ic_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)))
                .into(binding.playlistImage)
        } else {
            binding.playlistImage.setImageResource(R.drawable.ic_placeholder)
        }
    }
}