package com.mspw.staythefuckathome.challengedetails

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mspw.staythefuckathome.BaseApplication
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.challenge.Challenge
import com.mspw.staythefuckathome.data.video.Video
import com.mspw.staythefuckathome.playchallenge.PlayChallengeActivity
import com.mspw.staythefuckathome.uploadvideo.UploadVideoActivity
import kotlinx.android.synthetic.main.activity_challenge_details.*
import kotlin.math.round

class ChallengeDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: ChallengeDetailsViewModel

    private lateinit var detailsAdapter: ChallengeDetailsAdapter

    private val challenge by lazy { intent.getParcelableExtra<Challenge?>("challenge") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge_details)
        setupUi()
        setupViewModel()
        setupObserve()
        setupListener()
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
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupViewModel() {
        val appContainer = (application as BaseApplication).appContainer
        viewModel = ChallengeDetailsViewModel(
            appContainer.challengeRepository,
            appContainer.videoRepository
        )

        val id = challenge?.id ?: return
        viewModel.loadData(id)
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

    private fun setupListener() {
        challengeDetailsUploadButton.setOnClickListener {
            startActivity(Intent(this, UploadVideoActivity::class.java))
        }
    }

    private fun setupYoutubeView() {
        challengeDetailsYoutubeView.apply {
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density
            val width = displayMetrics.widthPixels / density
            val height = round((width / TRANSVERSE_RATIO) * SPECIES_RATIO)

            val url = "${BASE_URL}webviews/youtube/" +
                "?width=${width.toInt()}" +
                "&height=${height.toInt()}" +
                "&video_id=${challenge?.videoId}" +
                "&start_seconds=${challenge?.startSeconds ?: 0}" +
                "&end_seconds=0"
            challengeDetailsYoutubeViewContainer.layoutParams.height = (height * density).toInt()
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                mediaPlaybackRequiresUserGesture = false
            }
            webChromeClient = ChallengeDetailsChromeClient(this@ChallengeDetailsActivity)
            loadUrl(url)
        }
    }

    private fun setupContent() {
        challengeDetailsContent.apply {
            layoutManager = LinearLayoutManager(context)
            detailsAdapter = ChallengeDetailsAdapter(mutableListOf(), this@ChallengeDetailsActivity)
            adapter = detailsAdapter
        }
    }

    fun moveToPlayChallenge(video: Video) {
        val intent = Intent(this, PlayChallengeActivity::class.java).apply {
            putExtra("video", video)
        }
        startActivity(intent)
    }

    companion object {
        private val TAG = ChallengeDetailsActivity::class.java.simpleName
        private const val BASE_URL = "https://test.mspw.io/"
        private const val TRANSVERSE_RATIO = 16
        private const val SPECIES_RATIO = 9
    }

}
