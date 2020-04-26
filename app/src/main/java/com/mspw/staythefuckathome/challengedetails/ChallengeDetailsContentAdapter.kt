package com.mspw.staythefuckathome.challengedetails

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.video.Video

class ChallengeDetailsContentAdapter(
    private val videos: MutableList<Video>,
    private val view: ChallengeDetailsActivity,
    private val imageSize: Int
) : RecyclerView.Adapter<ChallengeDetailsContentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_challenge_details_video, parent, false),
            this,
            imageSize
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun setVideos(items: List<Video>) {
        videos.clear()
        videos.addAll(items)
        notifyDataSetChanged()
    }

    fun moveToPlayChallenge(position: Int) {
        view.moveToPlayChallenge(videos[position])
    }

    class ViewHolder(
        view: View,
        adapter: ChallengeDetailsContentAdapter,
        private val imageSize: Int
    ) : RecyclerView.ViewHolder(view) {

        private val thumbnail: ImageView
        private val profile: ImageView
        private val username: TextView

        init {
            with (view) {
                this@ViewHolder.thumbnail = findViewById<ImageView>(R.id.videoThumbnail).apply {
                    layoutParams.height = imageSize
                }
                this@ViewHolder.profile = findViewById(R.id.videoUserProfile)
                this@ViewHolder.username = findViewById(R.id.videoUserName)
                setOnClickListener {
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }

                    adapter.moveToPlayChallenge(position)
                }
            }
        }

        fun bind(video: Video) {
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
                Glide.with(profile.context)
                    .load(video.user?.image)
                    .transition(DrawableTransitionOptions.with(factory))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.circle_dark_white)
                    .error(R.drawable.circle_dark_white)
                    .into(profile)
                username.text = video.user?.name
            } catch (exception: Exception) {
                Log.d("profile", "exception: $exception")
            }
        }

    }

}