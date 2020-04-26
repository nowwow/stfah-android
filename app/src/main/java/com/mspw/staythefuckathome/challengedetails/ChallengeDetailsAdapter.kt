package com.mspw.staythefuckathome.challengedetails

import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mspw.staythefuckathome.R

class ChallengeDetailsAdapter(
    private val details: MutableList<ChallengeDetails>,
    private val view: ChallengeDetailsActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == REWARD_ITEM_VIEW_TYPE) {
            return RewardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_challenge_details_reward, parent, false), this)
        }

        return ContentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_challenge_details_content, parent, false),
            viewPool,
            view
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RewardViewHolder) {
            val reward = details[position] as ChallengeDetails.Reward
            holder.bind(reward)
        } else if (holder is ContentViewHolder) {
            val content = details[position] as ChallengeDetails.Content
            holder.bind(content)
        }
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun getItemViewType(position: Int): Int {
        if (details[position] is ChallengeDetails.Reward) {
            return REWARD_ITEM_VIEW_TYPE
        }

        return CONTENT_ITEM_VIEW_TYPE
    }

    fun setDetails(items: List<ChallengeDetails>) {
        details.clear()
        details.addAll(items)
        notifyDataSetChanged()
    }

    fun moveToUberEats(position: Int) {
        val url = (details[position] as ChallengeDetails.Reward).sponsorship?.external_link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.startActivity(intent)
    }

    class RewardViewHolder(
        view: View,
        adapter: ChallengeDetailsAdapter
    ) : RecyclerView.ViewHolder(view) {

        private val tag: TextView
        private val coupon: TextView
        private val uber: ImageView

        init {
            tag = view.findViewById(R.id.rewardTag)
            coupon = view.findViewById(R.id.rewardCoupon)
            uber = view.findViewById(R.id.uberBtn)
            uber.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    adapter.moveToUberEats(adapterPosition)

            }
        }

        fun bind(reward: ChallengeDetails.Reward) {
            tag.text = reward.tag
            coupon.text = reward.coupon

        }

    }

    class ContentViewHolder(
        view: View,
        pool: RecyclerView.RecycledViewPool,
        acitivity: ChallengeDetailsActivity
    ) : RecyclerView.ViewHolder(view) {

        private val videoList: RecyclerView

        init {
            videoList = view.findViewById<RecyclerView>(R.id.videoList).apply {
                val displayMetrics = resources.displayMetrics
                val sideSpacing = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    7f,
                    displayMetrics
                ).toInt()
                val bottomSpacing = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    18f,
                    displayMetrics
                ).toInt()
                setRecycledViewPool(pool)
                val spacing = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    displayMetrics
                ).toInt()
                val imageSize = (displayMetrics.widthPixels - spacing) / SPAN_COUNT
                addItemDecoration(ChallengeDetailsVideoSpaceDecoration(sideSpacing, bottomSpacing))
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
                adapter = ChallengeDetailsContentAdapter(mutableListOf(), acitivity, imageSize)
            }
        }

        fun bind(content: ChallengeDetails.Content) {
            (videoList.adapter as ChallengeDetailsContentAdapter).setVideos(content.videos)
        }

    }

    companion object {
        private val TAG = ChallengeDetailsAdapter::class.java.simpleName
        private const val REWARD_ITEM_VIEW_TYPE = 1
        private const val CONTENT_ITEM_VIEW_TYPE = 2
        private const val SPAN_COUNT = 2
    }

}