package com.mspw.staythefuckathome.playchallenge

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        video = intent.getParcelableExtra("video")
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

    private fun setupListener() {
        playChallengeClose.setOnClickListener { onBackPressed() }
    }

    private fun initializePlayer() {
        try {
            val uri = Uri.parse("https://d23r2dimy698xl.cloudfront.net/media/test.mp4")
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
//                            handleResourceNotFound()
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


}
