package com.mspw.staythefuckathome.challengedetails

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import kotlinx.android.synthetic.main.activity_challenge_details.*
import kotlin.math.round

class ChallengeDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: ChallengeDetailsViewModel

    private lateinit var detailsAdapter: ChallengeDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge_details)
        setupUi()
        setupViewModel()
        setupObserve()
        setupYoutubeView()
        setupContent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    private fun setupUi() {
        setSupportActionBar(challengeDetailsToolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupViewModel() {
        val appContainer = (application as BaseApplication).appContainer
        viewModel = ChallengeDetailsViewModel(appContainer.videoRepository)
    }

    private fun setupObserve() {
        with (viewModel) {
            isLoading.observe(this@ChallengeDetailsActivity, Observer {
                if (it) {
                    challengeDetailsYoutubeViewPlaceholder.visibility = View.VISIBLE
                    challengeDetailsContent.visibility = View.GONE
                    challengeDetailsYoutubeView.visibility = View.GONE
                    return@Observer
                }

                challengeDetailsYoutubeViewPlaceholder.visibility = View.GONE
                challengeDetailsContent.visibility = View.VISIBLE
                challengeDetailsYoutubeView.visibility = View.VISIBLE
            })
            details.observe(this@ChallengeDetailsActivity, Observer {
                detailsAdapter.setDetails(it)
            })
        }
    }

    private fun setupYoutubeView() {
        challengeDetailsYoutubeView.apply {
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density
            val width = displayMetrics.widthPixels / density
            val height = round((width / TRANSVERSE_RATIO) * SPECIES_RATIO)
//            val (startSeconds, endSeconds) = if (youtube.isNotNull() && youtube!!.products.isNotEmpty()) {
//                val product = youtube!!.products[0]
//                Pair(product.startSeconds, product.endSeconds)
//            } else {
//                Pair(0, 0)
//            }

//            val url = "${BASE_URL}webviews/youtube/" +
//                "?width=${width.toInt()}" +
//                "&height=${height.toInt()}" +
//                "&video_id=${youtube?.videoId}" +
//                "&start_seconds=${startSeconds}" +
//                "&end_seconds=${endSeconds}"
            challengeDetailsYoutubeViewContainer.layoutParams.height = (height * density).toInt()
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                mediaPlaybackRequiresUserGesture = false
            }
//            webChromeClient = WatchYoutubeChromeClient(
//                this@WatchYoutubeFragment,
//                mainActivity
//            )
            loadUrl(url)
        }
    }

    private fun setupContent() {
        challengeDetailsContent.apply {
            layoutManager = LinearLayoutManager(context)
            detailsAdapter = ChallengeDetailsAdapter(mutableListOf())
            adapter = detailsAdapter
        }
    }

    companion object {
        private val TAG = ChallengeDetailsActivity::class.java.simpleName
        private const val TRANSVERSE_RATIO = 16
        private const val SPECIES_RATIO = 9
    }

}
