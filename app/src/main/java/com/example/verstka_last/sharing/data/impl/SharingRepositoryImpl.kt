package com.example.verstka_last.sharing.data.impl

import android.content.Context
import com.example.verstka_last.R
import com.example.verstka_last.core.domain.models.Playlist
import com.example.verstka_last.core.domain.models.Track
import com.example.verstka_last.sharing.domain.api.AgreementData
import com.example.verstka_last.sharing.domain.api.ShareData
import com.example.verstka_last.sharing.domain.api.SharingRepository
import com.example.verstka_last.sharing.domain.api.SupportData

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {

    override fun getShareData(): ShareData {
        return ShareData(
            message = context.getString(R.string.share_message),
            link = context.getString(R.string.share_link)
        )
    }

    override fun getSupportData(): SupportData {
        return SupportData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_subject),
            body = context.getString(R.string.support_body)
        )
    }

    override fun getAgreementData(): AgreementData {
        return AgreementData(
            url = context.getString(R.string.agreement_link)
        )
    }

    override fun getPlaylistShareText(playlist: Playlist, tracks: List<Track>): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append(playlist.title).append("\n")

        if (playlist.description.isNotEmpty()) {
            stringBuilder.append(playlist.description).append("\n")
        }

        stringBuilder.append("[${tracks.size}] треков\n")

        tracks.forEachIndexed { index, track ->
            val trackNumber = index + 1
            stringBuilder.append("$trackNumber. ${track.artist} - ${track.title} (${(track.duration)})\n")
        }

        return stringBuilder.toString().trim()
    }
}