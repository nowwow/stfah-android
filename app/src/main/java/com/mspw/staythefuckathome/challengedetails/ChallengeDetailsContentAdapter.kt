package com.mspw.staythefuckathome.challengedetails

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.video.Video
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class ChallengeDetailsContentAdapter(
    private val videos: MutableList<Video>,
    private val imageSize: Int
) : RecyclerView.Adapter<ChallengeDetailsContentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_challenge_details_video, parent, false),
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

    class ViewHolder(
        view: View,
        private val imageSize: Int
    ) : RecyclerView.ViewHolder(view) {

        private val thumnail: ImageView
        private val profile: ImageView
        private val username: TextView

        init {
            thumnail = view.findViewById<ImageView>(R.id.videoThumbnail).apply {
                layoutParams.height = imageSize
            }
            profile = view.findViewById(R.id.videoUserProfile)
            username = view.findViewById(R.id.videoUserName)
        }

        fun bind(video: Video) {
            try {
                Picasso.get()
                    .load(video.thumbnail?.url)
                    .resize(imageSize, imageSize)
                    .centerCrop()
                    .placeholder(R.color.darkWhite)
                    .error(R.color.darkWhite)
                    .into(thumnail)
                Picasso.get()
                    .load("https://d2pzex094z0wfi.cloudfront.net/media/images/products/2020/04/b989f4ce-9594-4d73-bc67-b925ae0d197b.png")
                    .resize(28, 28)
                    .centerCrop()
                    .transform(CropCircleTransformation())
                    .placeholder(R.color.darkWhite)
                    .error(R.color.darkWhite)
                    .into(profile)
                username.text = ""
            } catch (exception: Exception) {
                Log.d("profile", "exception: $exception")
            }
        }

    }

}