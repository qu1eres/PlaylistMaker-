package com.example.verstka_last.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.databinding.TrackItemBinding
import com.example.verstka_last.search.presentation.TrackViewHolder

class TracksInPlayListAdapter(
    private val clickListener: (Track) -> Unit,
    private val longClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var data: List<Track> = emptyList()

    fun updateData(newData: List<Track>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener {
            clickListener.invoke(data[position])
        }
        holder.itemView.setOnLongClickListener {
            longClickListener.invoke(data[position])
            true
        }
    }

    override fun getItemCount(): Int = data.size
}