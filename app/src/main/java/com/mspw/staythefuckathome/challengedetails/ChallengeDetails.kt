package com.mspw.staythefuckathome.challengedetails

import com.mspw.staythefuckathome.data.Sponsorship
import com.mspw.staythefuckathome.data.video.Video

sealed class ChallengeDetails {

    abstract val key: String

    data class Reward(
        override val key: String = "",
        val tag: String? = null,
        val coupon: String? = null,
        val sponsorship: Sponsorship? = null
    ) : ChallengeDetails()

    data class Content(
        override val key: String = "",
        val videos: List<Video>
    ) : ChallengeDetails()

}