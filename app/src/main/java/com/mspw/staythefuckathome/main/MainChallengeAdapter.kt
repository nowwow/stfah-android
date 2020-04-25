package com.mspw.staythefuckathome.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.Challenge.Challenge
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_main_challenge.view.*

class MainChallengeAdapter(
    private val items: ArrayList<Challenge>,
    private val listener: OnItemClickedListener
) : RecyclerView.Adapter<MainChallengeAdapter.ViewHolder>() {
    interface OnItemClickedListener {
        fun onClick(v:View, position:Int, item:Challenge)
    }

    class ViewHolder(v: View, listener:OnItemClickedListener,items:ArrayList<Challenge>) : RecyclerView.ViewHolder(v) {
        init {
            v.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                    listener.onClick(it, adapterPosition, items[adapterPosition])
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false),
        listener, items
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.run {
            Picasso.get().load(items[position].url).into(image)
        }
    }
}