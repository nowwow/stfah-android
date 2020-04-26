package com.mspw.staythefuckathome.data.video

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Call

class VideoRepository(
    private val videoService: VideoService
) {

    fun findAllByChallengeId(challengeId: Long): Flowable<ListResponse<Video>> {
        return videoService.findAllByChallengeId(challengeId)
    }

    fun findOneById(id: Long): Single<Video> {
        return Single.just(Video())
    }

    fun findAllVideoByCreatorId(creator:Long): Call<ListResponse<Video>> {
        return videoService.findAllVideoByCreatorId(creator)
    }

}