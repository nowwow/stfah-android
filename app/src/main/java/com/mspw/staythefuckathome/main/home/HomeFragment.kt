package com.mspw.staythefuckathome.main.home

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.challengedetails.ChallengeDetailsActivity
import com.mspw.staythefuckathome.data.challenge.Challenge
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.round

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUi()
        setupViewModel()
        setupObserve()
    }

    private fun setupUi() {
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
        val spacing = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            25f,
            displayMetrics
        ).toInt()
        val displayWidth = displayMetrics.widthPixels
        val imageSize = (displayWidth - spacing) / SPAN_COUNT
        val count = round(displayMetrics.heightPixels / imageSize.toDouble()).toInt()

        for (v in 0..count) {
            val firstItem = createPlaceholder(imageSize, bottomSpacing)
            val secondItem = createPlaceholder(imageSize, bottomSpacing)
            homeFirstPlaceholder.addView(firstItem)
            homeSecondPlaceholder.addView(secondItem)
        }

        challengeList.apply {
            setHasFixedSize(true)
            addItemDecoration(HomeChallengeSpaceDecoration(sideSpacing, bottomSpacing))
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            homeAdapter = HomeAdapter(mutableListOf(), this@HomeFragment, imageSize)
            adapter = homeAdapter
        }
    }

    private fun setupViewModel() {
        val appContainer = (activity?.application as BaseApplication).appContainer
        viewModel = HomeViewModel(appContainer.challengeRepository)
    }

    private fun setupObserve() {
        with (viewModel) {
            isLoading.observe(this@HomeFragment, Observer {
                if (it) {
                    return@Observer
                }

                challengePlaceholder.visibility = View.GONE
                challengeList.visibility = View.VISIBLE
            })
            challenges.observe(this@HomeFragment, Observer {
                homeAdapter.addChallenges(it)
            })
        }
    }

    private fun createPlaceholder(imageSize: Int, bottomSpacing: Int): FrameLayout {
        return FrameLayout(context!!).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                imageSize
            ).apply {
                setMargins(0, 0, 0, bottomSpacing)
            }
            setBackgroundColor(ContextCompat.getColor(context, R.color.darkWhite))
        }
    }

    fun moveToDetails(challenge: Challenge) {
        val intent = Intent(context, ChallengeDetailsActivity::class.java).apply {
            putExtra("challenge", challenge)
        }
        startActivity(intent)
    }

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
        private const val SPAN_COUNT = 2
    }

}
