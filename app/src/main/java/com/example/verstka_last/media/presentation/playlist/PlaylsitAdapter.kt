package com.example.verstka_last.media.presentation.playlist

import android.annotation.SuppressLint
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import com.example.verstka_last.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.databinding.PlaylistItemBinding
import java.io.File

class PlaylistViewHolder(private val binding: PlaylistItemBinding, private val filePath: File) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: Playlist) {
        binding.playlistName.text = item.title
        val addText = itemView.resources.getQuantityString(
            R.plurals.countOfTracks,
            item.trackCount.toInt(), item.trackCount.toInt()
        )
        binding.playlistTracksCount.text = addText
        val file = File(filePath, "${item.id}.jpg")
        Glide.with(itemView)
            .load(file.toUri().toString())
            .placeholder(R.drawable.ic_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_radius))
            )
            .into(binding.playlistImage)
    }
}

class PlayListAdapter(val data: List<Playlist>) :
    RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val filePath =
            File(parent.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(
            PlaylistItemBinding.inflate(layoutInspector, parent, false),
            filePath
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}