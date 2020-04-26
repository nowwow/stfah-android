package com.mspw.staythefuckathome.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.challenge.Challenge
import com.squareup.picasso.Picasso

class HomeAdapter(
    private val challenges: MutableList<Challenge>,
    private val view: HomeFragment,
    private val imageSize: Int
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home, parent, false),
            this@HomeAdapter,
            imageSize
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(challenges[position])
    }

    override fun getItemCount(): Int = challenges.size

    fun addChallenges(items: List<Challenge>) {
        challenges.addAll(items)
        notifyDataSetChanged()
    }

    fun moveToDetails(position: Int) {
        view.moveToDetails(challenges[position])
    }

    class ViewHolder(
        view: View,
        adapter: HomeAdapter,
        private val imageSize: Int
    ) : RecyclerView.ViewHolder(view) {

        private val image: ImageView
        private val title: TextView

        init {
            with (view) {
                this@ViewHolder.image = findViewById<ImageView>(R.id.homeImage).apply {
                    layoutParams.height = imageSize
                }
                this@ViewHolder.title = findViewById(R.id.homeTitle)
                setOnClickListener {
                    val position = adapterPosition
                    if (position == RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }

                    adapter.moveToDetails(position)
                }
            }
        }

        fun bind(challenge: Challenge) {
            try {
                Picasso.get()
                    .load(challenge.image)
                    .resize(imageSize, imageSize)
                    .centerCrop()
                    .placeholder(R.color.darkWhite)
                    .error(R.color.darkWhite)
                    .into(image)
                title.text = challenge.title
            } catch (exception: Exception) {  }
        }

    }

    companion object {
        private val TAG = HomeAdapter::class.java.simpleName
    }


}