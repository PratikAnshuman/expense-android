package com.prasoon.expense.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.prasoon.expense.R
import com.prasoon.expense.custom.SquareLayout
import com.prasoon.expense.ui.profile.gallery.GalleryData
import com.prasoon.expense.ui.profile.gallery.RunOnUiThread
import kotlinx.android.synthetic.main.grid_item.view.*
import org.jetbrains.anko.doAsync

class ImageGridAdapter(
    private val imageList: ArrayList<GalleryData>,
    private inline val onImageClicked: (imageUri: String) -> Unit
) : RecyclerView.Adapter<ImageGridAdapter.MyViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val photo = imageList[position].photoUri
        if (position == 0) {
            holder.imageCamera.visibility = View.VISIBLE
            holder.image.visibility = View.GONE

            holder.imageCamera.setImageResource(R.drawable.ic_camera_background)

        } else {
            holder.image.visibility = View.VISIBLE
            holder.imageCamera.visibility = View.GONE
//            doAsync {
//                RunOnUiThread(context).safely {
                    try {
                        val requestListener: RequestListener<Drawable> =
                            object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    holder.image.alpha = 0.3f
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }
                            }
                        Glide.with(context).load(photo)
                            .apply(RequestOptions().centerCrop().override(100))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .listener(requestListener).into(holder.image)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                }
//            }

        }
        holder.image.setOnClickListener {
            onImageClicked(photo)
        }
        holder.imageCamera.setOnClickListener {
            onImageClicked(photo)
        }

    }

    override fun getItemCount(): Int = imageList.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.image
        var imageCamera: ImageView = view.imagecamera
        var frame: SquareLayout = view.frame
    }

}