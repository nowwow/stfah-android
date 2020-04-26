package com.mspw.staythefuckathome.uploadvideo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.video.Video

class UploadVideoAdapter(
    private val videos: List<Video>,
    private val view: UploadVideoActivity,
    private val imageSize: Int
) : RecyclerView.Adapter<UploadVideoAdapter.ViewHolder>() {

    private var selectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            imageSize,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_upload_video, parent, false),
            this
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun getSelectedVideo(): Video? {
        return videos.find { it.isSelected }
    }


    fun updateVideo(position: Int) {
        if (selectedItem >= 0) {
            videos[selectedItem].isSelected = videos[selectedItem].isSelected.not()
            notifyItemChanged(selectedItem)
        }

        videos[position].isSelected = videos[position].isSelected.not()
        selectedItem = position
        notifyItemChanged(position)
        view.activeMenuItem()
    }

    class ViewHolder(
        private val imageSize: Int,
        view: View,
        adapter: UploadVideoAdapter
    ) : RecyclerView.ViewHolder(view) {

        private val thumbnail: ImageView

        init {
            thumbnail = view.findViewById<ImageView>(R.id.uploadVideoThumbnail).apply {
                layoutParams.height = imageSize
                setOnClickListener {
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }

                    adapter.updateVideo(position)
                }
            }
        }

        fun bind(video: Video) {
            if (video.isSelected) {
                thumbnail.alpha = 0.5f
            } else {
                thumbnail.alpha = 1f
            }
            thumbnail.layout(0, 0, 0, 0)
            try {
                val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                Glide.with(thumbnail.context)
                    .load(video.url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.with(factory))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.darkWhite)
                    .error(R.color.darkWhite)
                    .into(thumbnail)
            } catch (exception: Exception) {  }
        }

    }

}