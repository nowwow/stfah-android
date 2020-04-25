package com.mspw.staythefuckathome.challengedetails

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChallengeDetailsVideoSpaceDecoration(
    private val sideSpace: Int,
    private val bottomSpace: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        with(outRect) {
            when (spanIndex) {
                FIRST_VERTICAL_LINE -> {
                    right = sideSpace
                }
                SECOND_VERTICAL_LINE -> {
                    left = sideSpace
                }
            }

            bottom = bottomSpace
        }
    }

    companion object {
        private val TAG = ChallengeDetailsVideoSpaceDecoration::class.java.simpleName
        private const val FIRST_VERTICAL_LINE = 0
        private const val SECOND_VERTICAL_LINE = 1
    }

}