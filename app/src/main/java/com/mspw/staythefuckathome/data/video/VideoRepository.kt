package com.mspw.staythefuckathome.data.video

import com.mspw.staythefuckathome.data.ListResponse
import io.reactivex.Flowable
import io.reactivex.Single

class VideoRepository(
    private val videoService: VideoService
) {

    fun findAllByTitle(title: String): Flowable<ListResponse<Video>> {
        return videoService.findAllByTitle(title)
    }

    fun findOneById(id: Long): Single<Video> {
        return Single.just(Video())
    }

}