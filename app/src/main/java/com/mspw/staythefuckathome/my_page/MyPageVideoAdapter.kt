package com.mspw.staythefuckathome.my_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.video.Video
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_main_challenge.view.*

class MyPageVideoAdapter(
    private val items: ArrayList<Video>,
    private val listener: OnItemClickedListener
) : RecyclerView.Adapter<MyPageVideoAdapter.ViewHolder>() {
    interface OnItemClickedListener {
        fun onClick(v: View, position: Int, item: Video)
    }

    class ViewHolder(
        v: View,
        listener: OnItemClickedListener,
        items: ArrayList<Video>
    ) : RecyclerView.ViewHolder(v) {
        init {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                v.setOnClickListener {
                    listener.onClick(it, adapterPosition, items[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_main_challenge, parent, false)
        , listener, items
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            Glide.with(context)
                .load(items[position].url)
                .centerCrop()
                .into(image)
            title.text = items[position].challenge?.title
        }
    }
}