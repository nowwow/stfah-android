package com.mspw.staythefuckathome.uploadvideo

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UploadVideoSpaceDecoration(
    private val sideSpacing: Int,
    private val bottomSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        with (outRect) {
            when (spanIndex) {
                FIRST_VERTICAL_LINE -> {
                    right = sideSpacing
                }
                SECOND_VERTICAL_LINE -> {
                    left = sideSpacing
                    right = sideSpacing
                }
                THIRD_VERTICAL_LINE -> {
                    left = sideSpacing
                }
            }

            bottom = bottomSpacing
        }
    }

    companion object {
        private val TAG = UploadVideoSpaceDecoration::class.java.simpleName
        private const val FIRST_VERTICAL_LINE = 0
        private const val SECOND_VERTICAL_LINE = 1
        private const val THIRD_VERTICAL_LINE = 2
    }

}