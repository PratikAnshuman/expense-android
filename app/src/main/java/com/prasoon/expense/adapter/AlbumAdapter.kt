package com.prasoon.expense.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.prasoon.expense.R
import com.prasoon.expense.ui.profile.gallery.GalleryAlbums
import kotlinx.android.synthetic.main.album_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class AlbumAdapter(
    private val mAlbumList: ArrayList<GalleryAlbums>,
    private inline val onAlbumClicked: (album: GalleryAlbums) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.MyViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val album = mAlbumList[position]
        holder.albumTitleTv.text = album.name
        holder.photosCountTv.text = album.albumPhotos.size.toString()

        doAsync {
            uiThread {
                Glide.with(context).load(album.coverUri).apply(
                    RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_baseline_profile_24)
                ).into(holder.albumThumbnailIv)
            }
        }

        holder.albumCv.setOnClickListener {
            onAlbumClicked(album)
        }
    }

    override fun getItemCount(): Int = mAlbumList.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var albumThumbnailIv: ImageView = view.albumThumbnailIv
        var albumTitleTv: TextView = view.albumTitleTv
        var photosCountTv: TextView = view.photosCountTv
        var albumCv: CardView = view.albumCv
    }
}