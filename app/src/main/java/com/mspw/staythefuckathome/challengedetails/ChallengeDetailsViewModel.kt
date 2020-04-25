package com.mspw.staythefuckathome.challengedetails

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mspw.staythefuckathome.data.video.Video
import com.mspw.staythefuckathome.data.video.VideoRepository

class ChallengeDetailsViewModel(private val videoRepository: VideoRepository) {

    private val _details = MutableLiveData<List<ChallengeDetails>>()
    val details: LiveData<List<ChallengeDetails>> get() = _details

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadData()
    }

    fun loadData() {
        videoRepository.findOneById(0)
        Handler().postDelayed({
            _details.value = mutableListOf(
                ChallengeDetails.Reward(
                    tag = "tag",
                    coupon = "coupon"
                ),
                ChallengeDetails.Content("", mutableListOf(Video(), Video(), Video()))
            )
        }, 5000L)
    }


}