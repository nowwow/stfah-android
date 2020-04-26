package com.mspw.staythefuckathome.challengedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mspw.staythefuckathome.data.ListResponse
import com.mspw.staythefuckathome.data.challenge.Challenge
import com.mspw.staythefuckathome.data.challenge.ChallengeRepository
import com.mspw.staythefuckathome.data.video.Video
import com.mspw.staythefuckathome.data.video.VideoRepository
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class ChallengeDetailsViewModel(
    private val challengeRepository: ChallengeRepository,
    private val videoRepository: VideoRepository
) {

    private val _compositeDisposable = CompositeDisposable()

    private val _details = MutableLiveData<List<ChallengeDetails>>()
    val details: LiveData<List<ChallengeDetails>> get() = _details

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private fun findChallengeById(id: String): Flowable<Challenge> {
        return challengeRepository.findOneById(id)
            .toFlowable()
    }

    private fun findVideosByTitle(title: String): Flowable<ListResponse<Video>> {
        return videoRepository.findAllByTitle(title)
    }

    private fun handleLoadData(details: List<ChallengeDetails>) {
        _details.value = details
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLoadDataError(error: Throwable) { }

    fun loadData(id: String, title: String) {
        Flowable.zip(
            findChallengeById(id),
            findVideosByTitle(title),
            BiFunction<Challenge, ListResponse<Video>, Pair<Challenge, ListResponse<Video>>> {
                t1, t2 -> Pair(t1, t2)
            }
        )
        .map {
            val (challenge, videos) = it
            mutableListOf(
                ChallengeDetails.Reward(
                    tag = challenge.title,
                    coupon = challenge.reward
                ),
                ChallengeDetails.Content("", videos.results)
            )
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(::handleLoadData, ::handleLoadDataError)
        .also { _compositeDisposable.add(it) }
    }


}