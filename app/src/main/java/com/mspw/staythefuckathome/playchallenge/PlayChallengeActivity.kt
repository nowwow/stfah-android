package com.mspw.staythefuckathome.playchallenge

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.video.Video
import kotlinx.android.synthetic.main.activity_play_challenge.*

class PlayChallengeActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private var video: Video? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_challenge)
        setupUi()
        setupListener()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    private fun setupUi() {
        playChallengeUsername.text = video?.user?.name
        try {
            video = intent.getParcelableExtra("video")
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(playChallengeUserProfile.context)
                .load(video?.user?.image)
                .transition(DrawableTransitionOptions.with(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .placeholder(R.drawable.circle_dark_white)
                .error(R.drawable.circle_dark_white)
                .into(playChallengeUserProfile)
        } catch (exception: Exception) {
        }
    }

    private fun setupListener() {
        playChallengeClose.setOnClickListener { onBackPressed() }
    }

    private fun initializePlayer() {
        try {
            val uri = Uri.parse(video?.url)
            val applicationId = applicationInfo.labelRes
            val applicationName = getString(applicationId)
            val dataSourceFactory = DefaultDataSourceFactory(
                applicationContext,
                Util.getUserAgent(applicationContext, applicationName)
            )
            val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            player = ExoPlayerFactory.newSimpleInstance(applicationContext)
                .apply {
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ALL
                    addListener(object : Player.EventListener {
                        override fun onPlayerError(error: ExoPlaybackException?) {
                            super.onPlayerError(error)
                        }

                        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                            super.onPlayerStateChanged(playWhenReady, playbackState)
                            if (playbackState >= 3) {
//                                viewModel.setLoading(false)
                            }
                        }
                    })
                }
            playerView.player = player
            player?.prepare(mediaSource)
        } catch (exception: NullPointerException) {
//            handleResourceNotFound()
        }
    }

    companion object {
        private val TAG = PlayChallengeActivity::class.java.simpleName
        private const val SPECIES_RATIO = 9
        private const val TRANSVERSE_RATIO = 16
    }


}
